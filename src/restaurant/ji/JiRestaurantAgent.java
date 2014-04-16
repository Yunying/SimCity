package restaurant.ji;

import java.util.*;

import bank.interfaces.Bank;
import market.interfaces.*;
import restaurant.ji.interfaces.*;
import restaurant.ji.roles.JiCookRole.CooksPantry;
import restaurant.ji.roles.JiSharedWaiterRole;
import interfaces.*;
import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import gui.animation.base.GUI;
import gui.animation.building.restaurant.JiRestaurantGUI;

public class JiRestaurantAgent extends BusinessAgent implements JiRestaurant {
	
	//**********Data************//
	
	List<JiWaiter> waiters = new ArrayList<JiWaiter>();
	List<JiCustomer> arrivingCustomers= new ArrayList<JiCustomer>();
	JiCook cook;
	JiHost host;
	JiCashier cashier;
	List<Market> markets = new ArrayList<Market>();
	
	static final float workingCapital = 350f;
	float currentAssets;
	String name;
	
	public enum RestaurantState {
		closed, open, openNoEmployees, PayingAndClosing, 
		};
	RestaurantState state;
	RevolvingStand orderstand = new RevolvingStand();
	JiRestaurantGUI gui;
	
	public static final int xTable = 50;
    public static final int yTable = 150;
    public static int NTABLES = 3;//a global for the number of tables.
	private List<Table> tables = new ArrayList<Table>(NTABLES);
	
	public JiRestaurantAgent(String name, Market m, Bank b){
		super();
		this.name = name;
		this.bank = b;
		this.bankAccount = 500;
		
		state = RestaurantState.closed;
		markets.add(m);
		
		currentAssets = 400f;		
		
		wages.put("JiHost", 10f);
		wages.put("JiCashier", 10f);
		wages.put("JiWaiter", 15f);
		wages.put("JiCook", 20f);
		
		startTime = 15;
		closeTime = 47;
		
		// make tables
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix, xTable+(ix-1)*100, yTable));
		}
		
	}

	//**********Messages************//
	
	@Override
	public void msgUpdateTime(int time, int day) {
		this.currentTime = time;
		if (currentTime == startTime){
			if (haveEmployeesToOperate()){
				state = RestaurantState.open;
				Do("JiRestaurant is opening!");
			}
			else
				state = RestaurantState.openNoEmployees;
		}
		else if (haveEmployeesToOperate() && state == RestaurantState.openNoEmployees){
			state = RestaurantState.open;
			Do("JiRestaurant is opening (employees came late)");
		}
		else if (currentTime == closeTime){
			if (state == RestaurantState.openNoEmployees){
				state = RestaurantState.closed;
				Do("JiRestaurant closed... couldn't open today");
			}
			else if (state == RestaurantState.open){
				state = RestaurantState.PayingAndClosing;
				Do("JiRestaurant is closing. Customers finishing up: " + customers.size());
			}	
		}
		stateChanged();
	}
	
	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		if (r instanceof JiCustomer){
			customers.add(r);
			arrivingCustomers.add((JiCustomer) r);
		}
		else{
			employees.add(r);
			if (r instanceof JiHost){
				host = (JiHost) r;
				for (JiWaiter w : waiters){
					w.setHost(host);
				}
			}
			else if (r instanceof JiCook){
				cook = (JiCook) r;
				cook.setOrderStand(orderstand);
				orderstand.setCook(cook);
				CooksPantry.addMarkets(markets);
				for (JiWaiter w : waiters){
					w.setCook(cook);
				}
			}
			else if (r instanceof JiCashier){
				cashier = (JiCashier) r;
				for (JiWaiter w : waiters){
					w.setCashier(cashier);
				}
			}
			else if (r instanceof JiWaiter){
				JiWaiter w = (JiWaiter) r;
				waiters.add(w);
				if (r instanceof JiSharedWaiterRole) ((JiSharedWaiterRole)r).setOrderStand(orderstand);
				if (host!= null) w.setHost(host);
				if (cook!=null) w.setCook(cook);
				if (cashier!=null) w.setCashier(cashier);
			}
		}
		stateChanged();
	}
	
	@Override
	public void msgLeavingWork(Role employee){
		getEmployees().remove(employee);
		if (employee instanceof JiWaiter){
			waiters.remove(employee);
		}
		else if (employee instanceof JiHost){
			host = null;
		}
		else if (employee instanceof JiCook){
			cook = null;
		}
		else if (employee instanceof JiCashier){
			cashier = null;
		}
		stateChanged();
	}
	
//	public void msgIWantFood(JiCustomer customer) {
//		customers.add(customer);
//	}

	@Override
	public void msgOrderDelivered(Map<String, Integer> order, Building market, TruckDriver driver, float bill){
    	for (String f : order.keySet()){
    		cook.msgOrderCameIn(f, order.get(f));
    	}
    	cashier.msgPayMarketBill(bill, driver);
    	stateChanged();
    }
    
    @Override
	public void msgPaidBillToMarket(float expense) {
		currentAssets -= expense;
		stateChanged();
	}
    
    @Override
	public void msgReceivedPaymentFromCustomer(float income) {
    	Do("current money is $" + currentAssets);
		currentAssets += income;
		stateChanged();
	}
    
    @Override
    public void msgLeavingAsCustomer(JiCustomer customer) {
		Do("removing " + customer.getName() + "from list");
    	customers.remove(customer);
    	stateChanged();
    }

	//**********Scheduler************//

	@Override
	public boolean pickAndExecuteAnAction() {
		if (!customers.isEmpty()){
			if (!arrivingCustomers.isEmpty()){
				if (state == RestaurantState.closed || state == RestaurantState.openNoEmployees){
					tellCustomersToLeave(arrivingCustomers.get(0));
					return true;
				}
				else if (state == RestaurantState.open){
					directCustomerToHost(arrivingCustomers.get(0), host);
					return true;
				}
			}
			return false;
		}
		else if (state == RestaurantState.PayingAndClosing){
				for (Role employee : getEmployees()){
					Employee e = (Employee) employee;
					if (!e.hasReceivedPaycheck()){
						payEmployee(employee);
						return true;
					}
				}
				if (currentAssets > workingCapital){
					depositMoneyToBank();
					return true;
				}

				close();
				return false;
			}
		return false;
	}
	
	

	//**********Actions************//
	
	private void depositMoneyToBank() {
		Do("Go deposit this money $" + (currentAssets-workingCapital));
		cashier.msgDepositExcessFunds(currentAssets - workingCapital);
		currentAssets -= workingCapital;
	}

	void tellCustomersToLeave(JiCustomer customer){
		Do("Sorry we're not open. state = " + state.toString());
		customer.msgWeAreClosed();
		arrivingCustomers.remove(customer);
		customers.remove(customer);
	}
	
	void directCustomerToHost(JiCustomer customer, JiHost host){
		//host.msgGreetCustomer(customer);
		Do("WELCOME CUSTOMER " + customer.getName()  + "!!!!!!");
		customer.msgDirectingYouToHost(host);
		arrivingCustomers.remove(customer);
	}

	void close(){
		state = RestaurantState.closed;
		Do("Everyone go home");
		if(getEmployees() != null) {
			for (Role employee : getEmployees()){
				Employee e = (Employee) employee;
				e.msgStopWorkingGoHome();

				if (employee instanceof JiWaiter)
					waiters.remove(employee);
				else if (employee instanceof JiCook)
					cook = null;
				else if (employee instanceof JiHost)
					host = null;
				else if (employee instanceof JiCashier)
					cashier = null;
			}
			getEmployees().clear();
		}
	}
	
	void payEmployee(Role employee){
		Employee e = (Employee) employee;
		if (!e.hasReceivedPaycheck()){
			float pay = 0;
			if (employee instanceof JiWaiter){
				pay = wages.get("JiWaiter");
			}
			else if (employee instanceof JiCook){
				pay = wages.get("JiCook");
			}
			else if (employee instanceof JiHost){
				pay = wages.get("JiHost");
			}
			else if (employee instanceof JiCashier){
				pay = wages.get("JiCashier");
			}
			Do("Paying " + employee.getPerson().getName() + " $" + pay);
			e.msgHeresYourPaycheck(pay);
		}
	}

	
	// utilities
	public class Table {
		JiCustomer occupiedBy;
		private int tableNumber;
		int x;
		int y;

		public Table(int tableNumber, int x, int y) {
			this.occupiedBy = null;
			this.setTableNumber(tableNumber);
			this.x = x;
			this.y = y;
		}
		
		public int getX(){	return x;	}
		public int getY(){	return y;	}
		public void setOccupant(JiCustomer cust) {	occupiedBy = cust;	}
		//Customer getOccupant() {	return occupiedBy;	}
		public void setUnoccupied() {	occupiedBy = null;	}
		public boolean isAvailable() {	return occupiedBy == null;	}
		@Override public String toString() {	return "table " + getTableNumber();	}

		public int getTableNumber() {
			return tableNumber;
		}

		public void setTableNumber(int tableNumber) {
			this.tableNumber = tableNumber;
		}
		
	} // end Table class


	
	//**********Accessors************//
	
	public boolean haveEmployeesToOperate(){
		return (!waiters.isEmpty() && cook!= null && host!= null && cashier!= null );
	}
	
	public void addMarket(Market m){ markets.add(m); CooksPantry.addMarket(m);}
	@Override public float getCurrentAssets(){ return currentAssets; }
	@Override public String getName(){ return name; }
	public void setGUI(GUI gui){ this.gui = (JiRestaurantGUI) gui; }
	//public GUI getGUI(){ return gui; }
	@Override public List<JiWaiter> getWaiters(){ return waiters;}
	@Override public JiCook getCook(){ return cook; }
	@Override public JiHost getHost(){ return host; }
	@Override public JiCashier getCashier(){ return cashier; }
	@Override public RevolvingStand getOrderStand() { return orderstand; }
	@Override public int getBankAccount(){return bankAccount;}
	public List<Table> getTables(){ return tables; }

	
	
}
