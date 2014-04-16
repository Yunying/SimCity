package restaurant.yunying.roles;

import agent.Agent;
import restaurant.yunying.TuRestaurantAgent;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;
import gui.animation.role.restaurant.yunying.TuHostGUI;
import interfaces.Person;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */

public class TuHostRole extends Role implements Host {
	static final int NTABLES = 3;
	public List<Customer> waitingCustomers
	= Collections.synchronizedList(new ArrayList<Customer>());
	public static Collection<Table> tables;
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public class MyWaiter{
		public MyWaiter (Waiter waiter){
			customerNumber = 0;
			w=waiter;
		}

		int customerNumber;
		Waiter w;
	}
	TuRestaurantAgent tr;

	int emptyTable;

	private String name;
	private float salary;

	public TuHostGUI hostGui = null;
	boolean readyToLeave = false;
	
	
	public TuHostRole() {
		super();
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}
	
	@Override
	public void setPerson(Person p){
		super.setPerson(p);
		name = p.getName();
	}
	
	public float getSalary(){
		return 20;
	}
	
	public void setSalary(float a){
		salary = a;
	}


	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}
	
	public void addWaiter(Waiter w){
		MyWaiter newWaiter = new MyWaiter(w);
		synchronized (waiters) {waiters.add(newWaiter);}
		stateChanged();
	}

	public Collection getTables() {
		return tables;
	}
	
//	public void setWaiter(Waiter waiter){
//		this.w=waiter;
//	}
	
	// Messages

	public void msgIWantFood(Customer cust) {
		synchronized (waitingCustomers) {waitingCustomers.add(cust);}
		cust.msgHereIsYourNumber(waitingCustomers.size());
		print("Here is your number" + waitingCustomers.size());
		if (waiters.size() == 0){
			print("I don't have any waiter");
		}
		stateChanged();
	}

	public void msgTableIsFree(Waiter wt, int t) {
		for (Table table:tables){
			if (table.tableNumber == t){
				table.setUnoccupied();
				stateChanged();
			}
		}
		for (MyWaiter waiter:waiters){
			if (waiter.w.equals(wt)){
				waiter.customerNumber--;
				stateChanged();
			}
		}
		
	}
	
	public void msgWantToGoOnBreak(Waiter wt){
		if (waiters.size() == 1){
			print("Do not allow");
			wt.msgBreakReply(false);
			return;
		}
		else {
			try{
				synchronized (waiters){
				for (MyWaiter waiter:waiters){
					if (waiter.w.equals(wt)){
						print("Allow waiter " + waiter.w.getName() + " a break");
						wt.msgBreakReply(true);
				        waiters.remove(waiter);
				        return;
					}
				}
				}
			}catch(java.util.ConcurrentModificationException c){
				msgWantToGoOnBreak(wt);
			}
		}
	}

	public void msgBackToWork(Waiter w){
		synchronized (waiters){
		waiters.add(new MyWaiter(w));}
		stateChanged();
	}
	
	public void msgImLeaving(Customer c){
		synchronized (waitingCustomers){
			for (Customer customer: waitingCustomers){
				if (customer.equals(c)){
					waitingCustomers.remove(customer);
					return;
				}
			}
		}
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//print("Host scheduler");
		if (waiters.isEmpty()){
			return false;
		}
		for (Table table : tables) {
			if (!table.isOccupied()) {
				if (!waitingCustomers.isEmpty()) {
					print("seat customer");
					seatCustomer(waitingCustomers.get(0),table.tableNumber);
						return true;
				}
			}
		}
		
		if (!waitingCustomers.isEmpty()){
			for (Customer wc:waitingCustomers){
				sendMsgTablesAreFull(wc);
				return true;
			}
			
		}
		
		if (readyToLeave){
			leaveWork();
		}
		return false;

	}

	// Actions
	private void leaveWork(){
		tr.msgLeavingWork(this);
	}
	
	private void sendMsgTablesAreFull(Customer c){
		c.msgTablesAreFull();
	}

	private void seatCustomer(Customer customer,int table) {
		int chooseWaiter = 100;
		MyWaiter tempW = null;
		for (MyWaiter w:waiters){
			if (w.customerNumber<chooseWaiter){
				chooseWaiter = w.customerNumber;
				tempW = w;
			}
		}
		tempW.w.msgSitAtTable(customer,table);
		tempW.customerNumber++;
		synchronized (waitingCustomers) {waitingCustomers.remove(customer);}
		for (Table atable:tables){
			if (atable.tableNumber == table){
				atable.setOccupant(customer);
			}
		}
	}


	//utilities

	public void setGui(TuHostGUI gui) {
		hostGui = gui;
	}

	public TuHostGUI getGui() {
		return hostGui;
	}

	private class Table {
		Customer occupiedBy;
		public int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Customer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Customer getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}

	public void setRestaurant(TuRestaurantAgent tuRestaurantAgent) {
		// TODO Auto-generated method stub
		tr = tuRestaurantAgent;
	}

	public void msgLeaveWork() {
		// TODO Auto-generated method stub
		readyToLeave = true;
	}
}

