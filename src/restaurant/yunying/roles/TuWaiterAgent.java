package restaurant.yunying.roles;

import global.roles.Role;
import global.test.mock.LoggedEvent;
import interfaces.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import agent.Agent;
import restaurant.yunying.Food;
import restaurant.yunying.TuRestaurantAgent;
import restaurant.yunying.gui.waiterGui;
import restaurant.yunying.interfaces.Cashier;
import restaurant.yunying.interfaces.Cook;
import restaurant.yunying.interfaces.Customer;
import restaurant.yunying.interfaces.Host;
import restaurant.yunying.interfaces.Waiter;


public abstract class TuWaiterAgent extends Role implements Waiter {

	protected List<MyCustomer> customers
	= Collections.synchronizedList(new ArrayList<MyCustomer>());
	public enum WaiterState {working, available};
	public WaiterState waiterState = WaiterState.available;
	protected int customerNum = 0;
	protected boolean rest;
	protected OrderStand orderStand;
	
	protected float salary;

	public enum CustomerState {none, waiting, seated, readyToOrder, ordering, 
		ordered, waitingForFood, foodCooking,foodCooked, foodGot, foodDelivered, 
		served, leaving,reOrder,
		readyForCheck, checking, receiveCheck, eating};
	public class MyCustomer{
		public MyCustomer (Customer customer, int tableNo){
			cust=customer;
			table = tableNo;
			state = CustomerState.waiting;
		}

		Customer cust;
		int table;
		String hisChoice;
		CustomerState state;	
		double payment;
	}
	
	protected List<Food> menu = Collections.synchronizedList(new ArrayList<Food>());
	public enum BreakState {none, asking,asked,replied, onBreak};
	protected BreakState breakState = BreakState.none;

	protected Host h = null;
	protected Cook cook = null;
    protected Cashier cashier = null;

	protected  String name;
	protected Semaphore atTable = new Semaphore(0,true);

	public waiterGui wG = null;
	
	protected boolean onBreak = false;
	protected boolean asked = false;
	
	protected boolean readyToLeave = false;
	TuRestaurantAgent tr;

	public TuWaiterAgent() {
		super();
		menu.add(new Food("Crepe",7));
		menu.add(new Food("Cheesecake",6));
		menu.add(new Food("Waffle",5));
		menu.add(new Food("Brownie",4));
		menu.add(new Food("Gelato",2));
	}
	
	public void setPerson(Person p){
		super.setPerson(p);
		this.name = p.getName();
	}
	
	public float getSalary(){
		return 10;
	}
	
	public void setSalary(float a){
		salary = a;
	}
	
	public void setRest(boolean r){
		rest = r;
	}
	
	public void offBreak(){
		rest = false;
		onBreak = false;
		wG.offBreak();
	}
	
	public boolean isResting(){
		return rest;
	}

	public void setHost(Host host){
		h=host;
	}
	
	public void setCashier(Cashier cashier){
		this.cashier = cashier;
	}

	public void setCook(Cook c){
		cook = c;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List<MyCustomer> getCustomers() {
		return customers;
	}

	public boolean isOnBreak(){
		return onBreak;
	}

    public void setOnBreak(){
    	breakState = BreakState.asking;
    	stateChanged();
    }
    
    public void msgLeaveWork(){
    	readyToLeave = true;
    }

	public void msgSitAtTable(Customer c, int t){
		print("sit at table");
		log.add(new LoggedEvent("msg sit at table"));
		MyCustomer cust = new MyCustomer(c,t);
		customers.add(cust);
		customerNum++;
		stateChanged();
	}

	public void msgImReadyToOrder(Customer c){
		log.add(new LoggedEvent("msg I'm ready to order"));
		print("customer ready to order");
		synchronized(customers){
			for (MyCustomer mycust:customers){
				if (mycust.cust.equals(c)){
					mycust.state = CustomerState.readyToOrder;
				}
			}
		}
		stateChanged();
	}

	public void msgHereIsMyChoice(Customer c,String choice){
		log.add(new LoggedEvent("Here is my choice"));
		print("Here is customer's choice "+choice);
		synchronized(customers){	
			for (MyCustomer mycust:customers){
				if (mycust.cust.equals(c)){
					mycust.hisChoice = choice;
					mycust.state = CustomerState.ordered;
				}
			}
		}
		stateChanged();
	}

	public void msgOrderIsReady(String choice,int table){
		synchronized(customers){
			for (MyCustomer mycust:customers){
				if (mycust.table == table){
					mycust.state = CustomerState.foodCooked;
				}
			}
		}
		stateChanged();
	}

	protected void msgFoodGotFromCook(MyCustomer c){
		c.state = CustomerState.foodGot;
		stateChanged();
	}

	protected void msgFoodDelivered(MyCustomer c){
		c.state = CustomerState.foodDelivered;
		stateChanged();
	}

	public void msgLeavingTable(Customer cust) {
		for (MyCustomer mycust:customers){
			if (mycust.cust.equals(cust)){
				mycust.state = CustomerState.leaving;
			}
		}
		stateChanged();
	}

	public void msgAtTable() {
		//print("release");
		atTable.release();
	}
	
	public void msgOutOfFood(int table){
		//print("Receive msg out of food");
		for (MyCustomer c:customers){
			if (c.table == table){
				c.state = CustomerState.reOrder;
				stateChanged();
			}
		}
	}
	
	public void msgBreakReply(boolean br){
		onBreak = br;
		if(br){
		  breakState = BreakState.replied;
		}
		else{
			breakState = BreakState.none;
			wG.setBreakGui();
		}
	}
	
	public void msgBackToWork(){
		onBreak = false;
		breakState = BreakState.none;
		h.msgBackToWork(this);
	}
	
	public void msgImReadyForCheck(Customer c){
		for (MyCustomer mc:customers){
			if (mc.cust == c){
				mc.state = CustomerState.readyForCheck;
				stateChanged();
			}
		}
	}
	
	public void msgHereIsCheck(double money, int table){
		for (MyCustomer mc:customers){
			if (mc.table == table){
				mc.state = CustomerState.receiveCheck;
				mc.payment = money;
				stateChanged();
			}
		}
	}
	

	public void setGui(waiterGui gui) {
		wG = gui;
	}

	public waiterGui getGui() {
		return wG;
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		  try{
			if (!customers.isEmpty()){
				if (waiterState == WaiterState.working){
					return false;
				}
				
				for (MyCustomer c:customers){
					if (c.state == CustomerState.reOrder){
						c.state = CustomerState.ordering;
						waiterState = WaiterState.working;
						askCustomerToReorder(c);
						return true;
					}
				}
				
				for (MyCustomer c:customers){	
					if (c.state == CustomerState.ordered){
						waiterState = WaiterState.working;
						takeOrderToCook(c);
						return true;
					}
				}
				
				for (MyCustomer c:customers){
					if (c.state == CustomerState.foodDelivered){
						serveCustomer(c);
						return true;
					}
				}
				
				for (MyCustomer c:customers){
					if (c.state == CustomerState.foodGot){
						waiterState = WaiterState.working;
						deliverFoodToCustomer(c);
						return true;
					}
				}
				for (MyCustomer c:customers){
					if (c.state == CustomerState.foodCooked){
						waiterState = WaiterState.working;
						takeOrderToTable(c);
						return true;
					}
				}
				
				for (MyCustomer c:customers){
					if (c.state == CustomerState.readyForCheck){
						waiterState = WaiterState.working;
						GetCheckFromCashier(c);
						return true;
					}
				}
				
				for (MyCustomer c:customers){
					if (c.state == CustomerState.receiveCheck){
						waiterState = WaiterState.working;
						GiveCheckToCustomer(c);
						return true;
					}
				}
				
				for (MyCustomer c:customers){
					if (c.state == CustomerState.waiting){
						waiterState = WaiterState.working;
						seatCustomer(c);
						return true;
					}
				}
				
				for (MyCustomer c:customers){
					if (c.state == CustomerState.readyToOrder){
						c.state = CustomerState.ordering;
						waiterState = WaiterState.working;
						askForOrder(c);
						return true;
					}
				}
				
				for (MyCustomer c:customers){	
					if (c.state == CustomerState.ordering){
						return false;
					}
				}
				
				for (MyCustomer c:customers){
					if (c.state == CustomerState.leaving){
						waiterState = WaiterState.working;
						cleanTable(c);
						return true;
					}
			    }
				

				for (MyCustomer c:customers){
					if (c.state == CustomerState.waitingForFood){
						//wG.DoLeaveCustomer();
						return true;
					}
				}
			    
			}
		  }catch(java.util.ConcurrentModificationException c){
			  return true;
		  }

			    if (breakState == BreakState.asking){
			    	print("Asking");
					h.msgWantToGoOnBreak(this);
					breakState = BreakState.asked;
					return true;
				}

				if (waiterState == WaiterState.available && (customerNum!=0 || !onBreak)){
					//wG.DoLeaveCustomer();
				}
				else if (waiterState == WaiterState.available && onBreak && customerNum == 0){
					print("Go to break");
					GoToBreak();
				}
				
				if (readyToLeave){
					leaveWork();
				}
			return false;
		}

		// Actions
	
		protected void leaveWork(){
			print("I'm leaving work");
			tr.msgLeavingWork(this);
			person.msgLeavingLocation(this);
		}
		
		protected void GoToBreak(){
//			wG.DoGoToBreak();
//			try {
//				//print("acquire");
//				atTable.acquire();
//
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			rest = true;
		}
		
		protected void GetCheckFromCashier(MyCustomer c){
			print("Go get check from cashier");
			c.state = CustomerState.ordering;
			//wG.DoGoToCashier();
//			try {
//				//print("acquire");
//				atTable.acquire();
//
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			if (cashier == null){
				cashier = tr.getCashier();
			}
			cashier.msgComputeBill(this, c.hisChoice, c.table);
			stateChanged();
			waiterState = WaiterState.available;
		}
		
		protected void GiveCheckToCustomer(MyCustomer c){
			print("Give check cto customer");
			c.state = CustomerState.eating;
//			wG.DoBringToTable(c.table);
//			try {
//				//print("acquire");
//				atTable.acquire();
//
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			c.cust.msgHereIsCheck(c.payment);
			waiterState = WaiterState.available;
			stateChanged();
		}
		
		protected void askCustomerToReorder(MyCustomer c){
			print("Ask for a reorder");
//			DoAskForOrder(c.table);
//			try {
//				//print("acquire");
//				atTable.acquire();
//
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			c.cust.msgPleaseReorder(c.hisChoice);
			stateChanged();
			waiterState = WaiterState.available;
		}

		protected void seatCustomer(MyCustomer customer) {
			
//			wG.doSeatCustomer1(customer.cust.getWaitingNumber());
//			try {
//				//print("acquire");
//				atTable.acquire();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			customer.cust.msgFollowMeToTable(this,customer.table, menu);
			
//			DoSeatCustomer(customer, customer.table);
//			try {
//				//print("acquire");
//				atTable.acquire();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}

			//wG.DoLeaveCustomer();
			waiterState = WaiterState.available;
			customer.state = CustomerState.seated;
		}

		protected void askForOrder(MyCustomer c){
			print("Ask for order");
//			DoAskForOrder(c.table);
//			try {
//				//print("acquire");
//				atTable.acquire();
//
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			c.cust.msgWhatWouldYouLike();
			stateChanged();
			waiterState = WaiterState.available;
		}

		protected void DoAskForOrder(int table){
			wG.DoAskForAnOrder(table);
		}

//		protected void takeOrderToCook(MyCustomer c){
//			print ("Take Order To Cook");
//			c.state = CustomerState.waitingForFood;
//			DoTakeOrderToCook(c.hisChoice);
//			try {
//				//print("acquire");
//				atTable.acquire();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			cook.msgHereIsAnOrder(this, c.hisChoice,c.table);
//			waiterState = WaiterState.available;
//		}
		
		protected abstract void takeOrderToCook(MyCustomer c);

		protected void DoTakeOrderToCook(String c){
			wG.takeOrderToCook(c);
		}

		protected void takeOrderToTable(MyCustomer c){
			print ("Take Order To Table");
//			DoTakeOrderToTable();
//			try {
//				//print("acquire");
//				//System.out.println("Acquire");
//				atTable.acquire();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			waiterState = WaiterState.available;
			msgFoodGotFromCook(c);
			
		}

		protected void DoTakeOrderToTable(){
			wG.DoGoToCook();
		}

		protected void deliverFoodToCustomer(MyCustomer c){
			//print ("Delivering food");
//			DoDeliverFood(c.table, c.hisChoice);
//			try {
//				//print("acquire");
//				atTable.acquire();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			waiterState = WaiterState.available;
			msgFoodDelivered(c);
		}

		protected void DoDeliverFood(int table, String customerChoice){
			wG.takeFoodToCustomer(table, customerChoice);
		}

		protected void serveCustomer(MyCustomer c){
			print ("customer can now start to eat");
			//wG.foodDeliveredToCustomer();
			c.cust.msgHereIsYourFood(c.hisChoice);
			c.state = CustomerState.served;
		}

		protected void cleanTable(MyCustomer c){
			print ("Clean Table");
//			DoCleanTable(c.table);
//			try {
//				//print("acquire");
//				atTable.acquire();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			wG.isCleaned(c.table);
			customerNum--;
			h.msgTableIsFree(this, c.table);
			customers.remove(c);
			waiterState = WaiterState.available;
		}

		protected void DoCleanTable(int table){
			wG.DoCleanTable(table);
		}

		protected void DoSeatCustomer(MyCustomer c, int table) {
			print("Seating " + " at " + table);
			wG.DoBringToTable(table); 

		}
		
		public void setOrderStand(OrderStand os){
			orderStand = os;
		}
		
		public Cashier getCashier(){
			return cashier;
		}
		
		public Cook getCook(){
			return cook;
		}

		public Host getHost(){
			return h;
		}
		
		public void setRestaurant(TuRestaurantAgent t){
			tr = t;
		}


}
