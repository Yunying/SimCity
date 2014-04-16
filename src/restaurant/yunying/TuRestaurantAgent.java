package restaurant.yunying;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import bank.interfaces.Bank;
import market.interfaces.Market;
import market.interfaces.TruckDriver;
import agent.Agent;
import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import gui.animation.building.restaurant.TuRestaurantGUI;
import interfaces.Building;
import interfaces.Person;
import restaurant.yunying.interfaces.Cashier;
import restaurant.yunying.roles.*;
import restaurant.yunying.roles.TuWaiterAgent.MyCustomer;

public class TuRestaurantAgent extends BusinessAgent implements Building {
	List<TuCustomerRole> customers = Collections.synchronizedList(new ArrayList<TuCustomerRole>());
	List<TuWaiterAgent> waiters = Collections.synchronizedList(new ArrayList<TuWaiterAgent>());
	List<Role> people = Collections.synchronizedList(new ArrayList<Role>());
	enum RestaurantState{readyToOpen, open, readyToClose, closed, closeRestaurant};
	RestaurantState state;
	TuHostRole host;
	TuCookRole cook;
	TuCashierRole cashier;
	int time;
	boolean active;
	String location;
	OrderStand orderStand;
	String name;
	int start;
	int close;
	Market market;
	Bank bank;
	float capital;
	TuRestaurantGUI gui;
	
	public TuRestaurantAgent(String name, Market m, Bank b){
		super();
		this.name = name;
		orderStand = new OrderStand();
		market = m;
		bank = b;
		capital = 1000;
		start = 15;
		close = 40;
		people.clear();
		bankAccount = 720;
	}

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		// TODO Auto-generated method stub
		if (r instanceof TuCustomerRole){
			customers.add( (TuCustomerRole) r);
			stateChanged();
		}
		
		else if (r instanceof TuHostRole){
			print("Tu restaurant has host now");
			host = (TuHostRole) r;
			host.setRestaurant(this);
			for (TuWaiterAgent w: waiters){
				w.setHost(host);
			}
			people.add(r);
			host.isActive = true;
			stateChanged();
		}
		
		else if (r instanceof TuCookRole){
			print("Tu restaurant has cook now");
			cook = (TuCookRole) r;
			people.add(cook);
			cook.setOrderStand(orderStand);
			cook.setCashier(cashier);
			cook.addMarket(market);
			cook.setRest(this);
			orderStand.setCook(cook);
		}
		
		else if (r instanceof TuCashierRole){
			print("Tu restaurant has cashier now");
			cashier = (TuCashierRole) r;
			cashier.setBank(bank);
			cashier.setCapital(capital);
			cashier.setRest(this);
			people.add(cashier);
			cashier.isActive = true;
		}
		
		else if (r instanceof TuWaiterRole){
			print("Tu restaurant has waiter now");
			TuWaiterRole w = (TuWaiterRole) r;
			w.setCook(cook);
			w.setCashier(cashier);
			w.setHost(host);
			w.setRestaurant(this);
			//w.setOrderStand(orderStand);
			try {host.addWaiter(w);
			} catch(java.lang.NullPointerException e){
				
			}
			host.addWaiter(w);
			people.add(w);
			waiters.add(w);
		}
		
		else if (r instanceof TuShareDataWaiterRole){
			print("Tu restaurant has share-data waiter now");
			TuShareDataWaiterRole w = (TuShareDataWaiterRole) r;
			w.setCook(cook);
			w.setCashier(cashier);
			w.setHost(host);
			w.setOrderStand(orderStand);
			w.setRestaurant(this);
			host.addWaiter(w);
			people.add(w);
			waiters.add(w);
		}
	}
	
	public void msgLeavingWork(Role r){
		if (r instanceof TuCustomerRole){
			r.isActive = false;
			r.getPerson().AddTaskGoHome();
			customers.remove( (TuCustomerRole) r);
		}
		
		else if (r instanceof TuHostRole){
			r.getPerson().AddTaskGoHome();
			r.isActive = false;
			people.remove(host);
			host = null;
		}
		
		else if (r instanceof TuCookRole){
			r.getPerson().AddTaskGoHome();
			r.isActive = false;
			people.remove(cook);
			cook = null;
		}
		
		else if (r instanceof TuCashierRole){
			r.getPerson().AddTaskGoHome();
			r.isActive = false;
			people.remove(cashier);
			cashier = null;
		}
		
		else if (r instanceof TuWaiterRole){
			r.getPerson().AddTaskGoHome();
			r.isActive = false;
			people.remove(r);
			waiters.remove( (TuWaiterRole) r);
		}
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		this.time = time;
		if (time == start){
			print("7:00 ready to open");
			state = RestaurantState.readyToOpen;
			stateChanged();
		}
		else if (time == close){
			print("READY TO CLOSE");
			state = RestaurantState.readyToClose;
			stateChanged();
		}
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String l) {
		location = l;
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		//print("Restaurant scheduler");
		if (state == RestaurantState.readyToOpen && host != null){
			print("Scheduler ready to open");
			openRestaurant();
			return true;
		}
		
		if (state == RestaurantState.readyToClose){
			payEmployees();
		}
		
		if (state == RestaurantState.closeRestaurant){
			closeRestaurant();
		}
		
		
		if (active && !customers.isEmpty()){
			letCustomerMeetHost();
			return true;
		}
		else{
			tellCustomerToLeave();
		}
		return false;
	}
	
	private void openRestaurant(){
		//print("open restaurant");
		active = true;
		state = RestaurantState.open;
	}
	
	private void closeRestaurant(){
		active = false;
		state = RestaurantState.closed;
	}
	
	private void payEmployees(){
		if (cashier != null){
			for (TuWaiterAgent w: waiters){
				print("Pay waiter");
				w.getPerson().ChangeMoney(w.getSalary());
				cashier.reduceCapital(w.getSalary());
				w.msgLeaveWork();
			}
			if (host != null){
				print("Pay host");
				host.getPerson().ChangeMoney(host.getSalary());
				cashier.reduceCapital(host.getSalary());
				host.msgLeaveWork();
			}
			if (cook != null){
				print("Pay cook");
				cook.getPerson().ChangeMoney(host.getSalary());
				cashier.reduceCapital(cook.getSalary());
				cook.msgLeaveWork();
			}
			cashier.getPerson().ChangeMoney(host.getSalary());
			print("Pay cashier");
			cashier.reduceCapital(cashier.getSalary());
			//Bank interaction
			cashier.depositMoney();
			cashier.msgLeaveWork();
		}
		state = RestaurantState.closeRestaurant;
	}
	
	private void letCustomerMeetHost(){
		print("Let customers meet the host");
		synchronized (customers){
			try{
				for (TuCustomerRole t: customers){
					t.msgMeetTheHost(host, cashier);	
					customers.remove(t);
				}
			}catch(java.util.ConcurrentModificationException e){
				
			}
		}
	}
	
	private void tellCustomerToLeave(){
		try{
		for (TuCustomerRole t: customers){
			t.msgPleaseLeave();
			customers.remove(t);
		}
		}catch(java.util.ConcurrentModificationException e){
			tellCustomerToLeave();
		}
	}

	@Override
	public int getStartTime() {

		return start;
	}

	@Override
	public void setStartTime(int t) {
		start = t;
	}

	@Override
	public int getCloseTime() {
		
		return close;
	}

	@Override
	public void setCloseTime(int t) {

		close = t;

	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		
		return people;

	}
	
	public String getName(){
		return name;
	}
	
	public void msgOrderDelivered(Map<String, Integer> order, Building market, TruckDriver driver,float bill){
		//do stuff here to add items to your inventory and update money
		//note, this is normative only, does not account for restaurant not being able to pay
		for (String f : order.keySet()){
    		cook.msgHereIsOrderedStorage(f, order.get(f));
    		stateChanged();
    	}
		Market m = (Market) market;
    	cashier.msgHereIsBill(m, driver, bill);
	}

	public void setGui(TuRestaurantGUI trGUI) {
		// TODO Auto-generated method stub
		gui = trGUI;
	}
	
	public int getBankAccount(){
		return bankAccount;
	}
	
	public Cashier getCashier(){
		return cashier;
	}

}
