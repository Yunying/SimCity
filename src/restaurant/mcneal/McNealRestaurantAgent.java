package restaurant.mcneal;

import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import gui.animation.building.restaurant.McNealRestaurantGUI;
import interfaces.Employee;
import interfaces.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;





import java.util.Map;

import bank.interfaces.Bank;
import interfaces.Building;
import market.TruckDriverRole;
import market.interfaces.Market;
import restaurant.cammarano.roles.CammaranoCashierRole;
import restaurant.mcneal.interfaces.McNealCashier;
import restaurant.mcneal.interfaces.McNealCook;
import restaurant.mcneal.interfaces.McNealCustomer;
import restaurant.mcneal.interfaces.McNealHost;
import restaurant.mcneal.interfaces.McNealRestaurant;
import restaurant.mcneal.interfaces.McNealWaiter;








public class McNealRestaurantAgent extends BusinessAgent implements McNealRestaurant, Building  {
	
	List<Role> presentEmployees = new ArrayList<Role>();
	List<McNealWaiter> waiters = new ArrayList<McNealWaiter>();
	McNealCook cook;
	McNealHost host;
	McNealCashier cashier;

	List<McNealCustomer> customers = new ArrayList<McNealCustomer>();
	List<Market> markets = new ArrayList<Market>();
	public String location = "McNealRestaurant";
	boolean open;
	int start = 14;
	int closed = 39;
	int time;
	boolean payDay;
	public Map<Role, Float> salary = new HashMap<Role, Float>();
	
	float currentmoney = 300;
	
	
	static final float workingCapital = 300f;
	public float currentAssets = 300f;
	String name;
	
	McNealRestaurantGUI gui;
	
	public McNealRestaurantAgent(String name, Market m, Bank b){
		super();
		this.name = name;
		markets.add(m);
		this.bank = b;
		
	}
	enum RestaurantState{readyToOpen,operating, readyToClose,closed};
	RestaurantState state;
	public class RestOrder{
	  String choice;
	  int table;
	  McNealWaiter w;
	  public RestOrder(String c, int t, McNealWaiter w){
		  choice = c;
		  table = t;
		  this.w = w;  
	  }
	}
	List<RestOrder> orders;

	public void msgOrderDelivered(Map<String,Integer> order, Market market, TruckDriverRole driver, float bill) {
	
	}
	
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
	     
		if (r instanceof McNealCustomer){
			customers.add( (McNealCustomer) r);
			print("yay " + r.getPerson().getName() + " has been added");
			stateChanged();
		}
		else if (!presentEmployees.contains(r)){
			presentEmployees.add(r);
			if (r instanceof McNealHost){
				host = (McNealHost) r;
				host.setRestaurant(this);
				salary.put(r, (float) 9);
				print("yay " + r.getPerson().getName() + " has been added");
				stateChanged();
			}
			else if (r instanceof McNealCook){
				cook = (McNealCook) r;
				cook.setRestaurantAgent(this);  
				salary.put(r, (float) 10);
				
				stateChanged();
			}
			else if (r instanceof McNealWaiter){
				waiters.add( (McNealWaiter) r);
				for(McNealWaiter w : waiters){
					w.setHost(host);
				}
				try {
					host.getWaiterList().add((McNealWaiter) r);
				}
				catch(java.lang.NullPointerException e){
					
				}
				salary.put(r, (float) 7);
				stateChanged();
			} 
			else if (r instanceof McNealCashier){
				cashier = ((McNealCashier) r);
				cashier.setRestaurant(this);
				salary.put(r, (float)8);
				stateChanged();
			}
		}
	
	}

	
	
	public void msgUpdateTime(int time, int day) {
		this.time = time;
		if (time == start){
			print("Restaurant is open yay!");
			state = RestaurantState.readyToOpen;
			stateChanged();
		}
		else if (time == closed){
			print("Restaurant closed");
			state = RestaurantState.readyToClose;
			stateChanged();
		}
	}
	
	
	public void msgLeavingBuilding(Role r, float moneyToDeposit) {
		print(r.getPerson().getName() + " is leaving the restaurant.");
		r.isActive = false;
		
		if(r instanceof CammaranoCashierRole) {
			print(r.getPerson().getName() + " is going to deposit our capital");
			r.getPerson().AddTaskDepositEarnings(this, (float)(moneyToDeposit / 10));
		}
		
		r.getPerson().AddTaskGoHome();
	}
	/*public void msgLeavingWork(Role r) {
		
	
		 if (!presentEmployees.contains(r)){
			presentEmployees.remove(r);
			if (r instanceof McNealHost){
				
			}
			else if (r instanceof McNealCook){
				cook = (McNealCook) r;
				cook.setRestaurantAgent(this);  
				salary.put(r, (float) 10);
			}
			else if (r instanceof McNealWaiter){
				waiters.add( (McNealWaiter) r);
				salary.put(r, (float) 7);
			} 
			else if (r instanceof McNealCashier){
				cashier = ((McNealCashier) r);
				cashier.setRestaurant(this);
				salary.put(r, (float)8);
			}
		}
		
	}*/
	public boolean pickAndExecuteAnAction() {
//		print("Restaurant scheduler");
		if(!waiters.isEmpty()) {
			print(" waiters size " + waiters.size());
			//return true;
		}
		if ((state == RestaurantState.readyToOpen) && (host != null) && (!waiters.isEmpty() && (cook != null) 
				&& (cashier != null))) {
			state = RestaurantState.operating;
			print("Open restaurant");
			  openRestaurant();
			  return true;
		}
		if(open && state == RestaurantState.readyToClose) {
			payEmployee();
			return true;
		}
			if (open && state == RestaurantState.closed){
			  closeRestaurant();
			  return true;
	    }
			if(open && !customers.isEmpty() && host != null) {
				print("Yeah boy we got a customer!");
				haveCustomerMeetHost(customers.get(0));
				return true;
			}
			
	
	return false;
	
	

}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getStartTime() {

		return start;
	}

	@Override
	public void setStartTime(int t) {
		
		start = t;
	}

	@Override
	public int getCloseTime() {
		
		return closed;
	}

	@Override
	public String getName() {
		return name; //To change body of generated methods, choose Tools | Templates.
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public McNealRestaurantGUI getGui() {
		return gui;
	}

	public void setGui(McNealRestaurantGUI gui) {
		this.gui = gui;
	}

	@Override
	public void setCloseTime(int t) {
		closed = t;

	}
	private void openRestaurant() {
		Do("Restaurant Open for buisness");
		for(McNealWaiter w : waiters) {
			w.setCook(cook);
		}
		open = true;
		state = RestaurantState.operating;
		
		
	}
	private void haveCustomerMeetHost(McNealCustomer c) {
		c.msgMeetHost(host);
		c.setHost(host);
		customers.remove(c);
	}
	private void closeRestaurant() {
		open = false;
		state = RestaurantState.closed;
		salary.clear();
		cashier.msgDepositFunds();
		// TODO Auto-generated method stub
		
	}
	 private void payEmployee(){
		for (Role employee : getEmployees()){
		float pay = 0;
		if (employee instanceof McNealWaiter){
			pay = salary.get("McNealWaiter");
		}
		else if (employee instanceof McNealCook){
			pay = salary.get("McNealCook");
		}
		else if (employee instanceof McNealHost){
			pay = salary.get("McNealHost");
		}
		else if (employee instanceof McNealCashier){
			pay = salary.get("McNealCashier");
		}
		Employee e = (Employee) employee;
		e.msgHeresYourPaycheck(pay);
		}
		state = RestaurantState.closed;
		stateChanged();
	}



	@Override
	public void setCurrentAssets(int cash) {
		this.currentAssets += cash;
		// TODO Auto-generated method stub
		
	}



	@Override
	public float getAssets() {
		// TODO Auto-generated method stub
		return (float) currentAssets;
	}

}