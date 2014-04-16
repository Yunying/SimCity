package restaurant.ji.roles;

import interfaces.Building;

import java.util.*;

import global.roles.Role;
import global.test.mock.LoggedEvent;
import restaurant.ji.JiRestaurantAgent.Table;
import restaurant.ji.interfaces.*;

/**
 * Restaurant Host Agent
 * The agent that assigns new customers to waiter-table combinations, and marks tables as empty when customers leave.
 * A Host is the manager of a restaurant who sees that all is proceeded as he wishes.
 */

public class JiHostRole extends Role implements JiHost{
	
	private JiRestaurant restaurant;
	private List<Table> tables;
	
	private List<WaitingCustomer> waitingCustomers	= Collections.synchronizedList(new ArrayList<WaitingCustomer>()); //Notice that we implement waitingCustomers using ArrayList, but type it with List semantics.
	private static JiWaiter assignedWaiter;
    private static List<JiWaiter> waiters;
	private static List<JiWaiter> waitersAboutToGoOnBreak = new ArrayList<JiWaiter>();
	
	public enum WaitingCustomerState {ENTEREDRESTAURANT, NOTIFIEDOFWAIT, DECIDEDTOWAIT, DECIDEDTOLEAVE};
	
    public enum State {arrivedAtJiRestaurant, working, leavingWork, none};
	private State state;
	
    boolean receivedPaycheck;
    boolean working;
    
	public JiHostRole() {
		super();
		receivedPaycheck = false;
		working = false;
		state = State.none;
	}
	
	// Messages
	@Override
	public void msgAtBuilding(Building rest){
		log.add(new LoggedEvent("Received msgAtJiRestaurant"));
		state = State.arrivedAtJiRestaurant;
		this.restaurant = (JiRestaurant) rest;
		stateChanged();
	}
	
	@Override
	public void msgTableForOne(JiCustomer cust){
		synchronized(waitingCustomers){
			state = State.working;
			waitingCustomers.add(new WaitingCustomer(cust));
			Do("Adding to list. " + new Integer(waitingCustomers.size()).toString() + " customer(s) in list");
			stateChanged();
		}
	}
	
	@Override
	public void msgCanIGoOnBreak(JiWaiter waiter) {	
		waitersAboutToGoOnBreak.add(waiter);
		stateChanged();
	}
	
	@Override
	public void msgBackFromBreak(JiWaiter waiter) {
		if (!waiters.contains(waiter)){
			waiters.add(waiter);
		}
		stateChanged();
	}
	
	/*
	@Override
	public void msgIWantFood(JiCustomer cust) {
		synchronized(waitingCustomers){
			waitingCustomers.add(new WaitingCustomer(cust));
			Do("Adding to list. " + new Integer(waitingCustomers.size()).toString() + " customer(s) in list");
			stateChanged();
		}
	}
	*/
	
	@Override
	public void msgIDontWantToWait(JiCustomer cust){
		synchronized(waitingCustomers){	
			state = State.working;
			for (WaitingCustomer c : waitingCustomers){
				if (c.customer == cust){
					Do("removed " + cust.getName());
					c.state = WaitingCustomerState.DECIDEDTOLEAVE;
					waitingCustomers.remove(c);
					stateChanged();
					break;
				}
			}
		}
	}
	@Override
	public void msgIWillWait(JiCustomer cust){
		synchronized(waitingCustomers){
			state = State.working;
			for (WaitingCustomer c : waitingCustomers){
				if (c.customer == cust){
					Do(cust.getName() + " is waiting");
					c.state = WaitingCustomerState.DECIDEDTOWAIT;
					stateChanged();
					break;
				}
			}
		}
	}

	@Override
	public void msgTableIsFree(int tableNum){
		state = State.working;
		for(Table t: tables)
		{
			if (t.getTableNumber() == tableNum){
				t.setUnoccupied();
			}
		}
		stateChanged();
	}
	
	@Override
	public void msgStopWorkingGoHome() {
		state = State.leavingWork;
		working = false;
		stateChanged();
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		person.ChangeMoney(paycheck);
		receivedPaycheck = true;
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		if (state == State.arrivedAtJiRestaurant){
			beginWork();
			return true;
		}
		else if (state == State.working){
			synchronized(waitingCustomers){
				if (!waitersAboutToGoOnBreak.isEmpty()){
					allowBreaks(waitersAboutToGoOnBreak.get(0));
				}
				if (!waitingCustomers.isEmpty()){
					boolean seatAvailable = false;
					for (Table table : tables) {
						if (table.isAvailable()) {
							seatAvailable = true;
							assignAndSeatCustomer(waitingCustomers.get(0), table);//the action
							return true;//return true to the abstract agent to reinvoke the scheduler.
						}
					}
					if (!seatAvailable){
						for (WaitingCustomer customer : waitingCustomers){
							if (customer.state == WaitingCustomerState.ENTEREDRESTAURANT){
								informOfWait(customer);
							}
						}
						return true;
					}
				}
			}
		}
		else if (state == State.leavingWork){
			leaveWork();
			return true;
		}
	
		return false;
		//we have tried all our rules and found nothing to do. So return false to main loop of abstract agent and wait.
	}
	
	// Actions

	private void beginWork(){
		state = State.working;
		Do("Working as host now!");
		working = true;
		receivedPaycheck = false;
		restaurant.msgAtLocation(person, this, null); // change this message in Building so we're not always passing in unnecessary information
		waiters = restaurant.getWaiters();
		tables = restaurant.getTables();
	}
	
	private void assignAndSeatCustomer(WaitingCustomer c, Table t){
		synchronized(waitingCustomers){
			assignedWaiter = waiters.get(0); // selects array with fewest customers and assigns him the new customers
			Do("Assigning " + waitingCustomers.get(0).getName() + " to " + assignedWaiter.getName());
		
			t.setOccupant(c.customer);
			assignedWaiter.msgHeresANewCustomer(c.customer, t.getTableNumber(), t.getX(), t.getY());
			waitingCustomers.remove(c);
			Collections.sort(waiters, new fewerCustomerCompare()); // sorts array so waiter with fewest customers is in the front
		}
	}
	
	private void informOfWait(WaitingCustomer c){
		Do("All tables are full. There is a wait.");
		c.customer.msgTablesAreFull();
		c.state = WaitingCustomerState.NOTIFIEDOFWAIT;
	}
	
	private void allowBreaks(JiWaiter w){
		if (waiters.size() > 1)
		{
			w.msgGoOnBreak();
			waiters.remove(w);
			Do("Yes, take a break when you're done. " + waiters.size() + " waiter(s) on duty");
			Collections.sort(waiters, new fewerCustomerCompare()); // sorts array so waiter with fewest customers is in the front
		}
		else{
			Do("no breaks allowed");
			w.msgNoBreak();
		}
		waitersAboutToGoOnBreak.remove(w);
	}
	
	void leaveWork(){
		state = State.none;
		Do("Done working bye");
		restaurant.msgLeavingWork(this);
		//gui.DoLeaveBuilding(); // Role should have a gui
		person.msgLeavingLocation(this); // inactivate Role
	}
	
	

	//utilities
	
	class fewerCustomerCompare implements Comparator<JiWaiter> {
	    @Override
	    public int compare(JiWaiter a, JiWaiter b) {
	        return (a.getMyTables().size() < b.getMyTables().size()) ? -1 : 1;
	    }
	}
	
	
	
	
	private class WaitingCustomer{
		JiCustomer customer;
		WaitingCustomerState state;
		
		public WaitingCustomer(JiCustomer customer){
			this.customer = customer;
			this.state = WaitingCustomerState.ENTEREDRESTAURANT;
		}

		public String getName() {
			return customer.getName();
		}
	}

	
	@Override public String getName() { return person.getName(); }
	@Override public boolean hasReceivedPaycheck() { return receivedPaycheck; }
	@Override public void addWaiter(JiWaiter w) { waiters.add(w); }
	public boolean isWorking() { return working; }
	public void setRestaurant(JiRestaurant jiRestaurantAgent) { restaurant = jiRestaurantAgent; }
	public List<WaitingCustomer> getWaitingCustomers() { return waitingCustomers; }
	public List<Table> getTables() { return tables; }
	public void setTables(List<Table> tables) { this.tables = tables; }
	
}

