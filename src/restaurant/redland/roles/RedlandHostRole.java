package restaurant.redland.roles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import global.roles.Role;
import restaurant.redland.RedlandRestaurantAgent;
import restaurant.redland.RedlandRestaurantAgent.RedlandCashierState;
import restaurant.redland.RedlandRestaurantAgent.RedlandHostState;
import restaurant.redland.RedlandRestaurantAgent.RedlandWaiterState;
import restaurant.redland.UtilityClasses.*;
import restaurant.redland.interfaces.RedlandCashier;
import restaurant.redland.interfaces.RedlandCook;
import restaurant.redland.interfaces.RedlandCustomer;
import restaurant.redland.interfaces.RedlandHost;
import restaurant.redland.interfaces.RedlandWaiter;
import restaurant.redland.roles.RedlandCustomerRole.AgentState;

public class RedlandHostRole extends Role implements RedlandHost{
	
	/********** Data **********/
	
	public RedlandRestaurantAgent restaurant;
	public Collection<Table> tables;
	public List<MyWaiter> waiters = new ArrayList<MyWaiter>();
	public RedlandCook cook;//this takes place of cooks List
	public RedlandCashier cashier;
	public List<RedlandCustomerRole> waitingCustomers = new ArrayList<RedlandCustomerRole>();
	public RedlandHostState state;
	
	public enum WaiterEvent {None, AsksForBreak, LeftForBreak, ReadyToWork};
	


	private String name;
	static final int NTABLES = 4;//a global for the number of tables.
    private final int TABLEX = 100;
    private final int TABLEY = 150;
    //public HostGui hostGui = null;
    
	public RedlandHostRole() {
		super();
		//this.name = name;
		
		this.state = RedlandHostState.Pending;
		//make tables
		tables = new ArrayList<Table>(NTABLES);
		int tableNumber = 0;
		//TODO: This code doesn't work properly for odd number of tables
		for (int i = 0; i < (int)Math.ceil(NTABLES/2.0); i++){
			for (int j = 0; j < (int)Math.floor(NTABLES/2.0); j++){
				tableNumber++;
		        tables.add(new Table(tableNumber, TABLEX + 200*i, TABLEY + 200*j));
		    }
		}
	}

	
	
	/********* Messages **********/
	//welcome message from restaurant upon first arriving
	public void msgGetReadyForWork( RedlandRestaurantAgent restaurant, RedlandCookRole cook, RedlandCashierRole cashier, List<RedlandWaiterRole> theWaiters ){
		this.restaurant = restaurant;
		this.state = RedlandHostState.Waiting;
		this.cook = cook;
		this.cashier = cashier;
		for( RedlandWaiterRole waiter : theWaiters ){
			waiters.add( new MyWaiter( waiter ) );
		}
	}
	
	//message from customer when they become hungry
	public void msgIWantFood( RedlandCustomerRole cust) {//message 1
		//print( cust + " wants food.");
		waitingCustomers.add(cust);
		stateChanged();
	}
	
	// message from waiter saying a table is free
	public void msgTableIsFree(int tableNum, RedlandWaiter waiter) {//message 11
		//maybe change these to Events enum and remove table in an action via scheduler
		//print( "Table " + tableNum + " is free" );
		for (MyWaiter wait : waiters){
			if ( wait.getWaiter() == waiter ){
				wait.removeCustomer();
			}
		}
		for (Table table : tables){
			if ( table.getNumber() == tableNum ){
				table.setUnoccupied();
			}
		}
		stateChanged();
	}

	public void msgAddWaiter( RedlandWaiterRole waiter){
		waiter.setHost( this );//host must be present
		try{
			waiter.setCashier( this.cashier );
		} catch( NullPointerException e ){ }//there is no current cashier
		try{
			waiter.setCook( this.cook );
		} catch( NullPointerException e ){ }//there is no current cook
		MyWaiter newWaiter = new MyWaiter( waiter );
		waiters.add( newWaiter );
		stateChanged();
	}
	
	//message from Restaurant Panel adding a cook to cooks list
	public void msgAddCook( RedlandCookRole cook){
		//TODO: change for multiple cooks
		//print( "Got new cook " + cook.getName() );
		cook.setHost( (RedlandHost) this);//is this needed
		cook.setCashier( this.cashier );
		this.cook = cook;
		stateChanged();
	}
	
	public void msgAddCashier( RedlandCashierRole cashier ){
		//print( "Got new cashier " + cashier.getName() );
		this.cashier = cashier;
		stateChanged();
	}
	
	public void msgIWantBreak( RedlandWaiterRole wait){
		for (MyWaiter waiter : waiters){
			if ( wait == waiter.getWaiter() ){
				waiter.setEvent(WaiterEvent.AsksForBreak);
				stateChanged();
			}
		}
	}
	
	public void msgImOnBreak( RedlandWaiter wait ){
		for (MyWaiter waiter : waiters){
			if ( wait == waiter.getWaiter() ){
				waiter.setState( RedlandWaiterState.OnBreak );
				stateChanged();
			}
		}
	}
	
	public void msgImOffBreak( RedlandWaiter wait ){
		for (MyWaiter waiter : waiters){
			if ( wait == waiter.getWaiter() ){
				waiter.setState( RedlandWaiterState.Waiting );
				stateChanged();
			}
		}
	}
	

	
	/********** Scheduler **********/
	
	public boolean pickAndExecuteAnAction() {
		for ( MyWaiter waiter : waiters ){
			//makes sure waiter is not eligible for break unless they are already working
			if ( waiter.getEvent() == WaiterEvent.AsksForBreak && waiter.getState() == RedlandWaiterState.Working ){
				//print( "Waiter " + waiter.getWaiter().getName() + " asks for break");
				waiter.setState( RedlandWaiterState.AskedForBreak );
				return true;
			}
			if ( waiter.getState() == RedlandWaiterState.AskedForBreak && waiter.numberCustomers() == 0 ){
				//can only go on break if more than 1 waiter
				if ( waiters.size() > 1 ){
					PutWaiterOnBreak(waiter);
					return true;
				}
			}
		}
		
		//Rules pertaining to customers
		for( RedlandCustomerRole customer : waitingCustomers ){
			if( !waiters.isEmpty() ){
				for ( Table table : tables ){
					if( !table.isOccupied() ){
						assignWaiterCustomer( customer, table );
						return true;
					}
				}
			}
		}
		return false;
	}
	

	/********** Actions **********/
	
	//Assign each customer to a waiter
	private void assignWaiterCustomer( RedlandCustomerRole c, Table t){
		//If only one waiter, first waiter will take all customers, cannot go on break
		MyWaiter waiterToPick = waiters.get(0);
		int mostCustomers = waiterToPick.numberCustomers();
		//pick waiter with least customers
		for (MyWaiter waiter : waiters ){
			//print( "number customers " + waiter.getWaiter().getName() + " : " + waiter.numberCustomers() );
			if ( (waiter.getState() != RedlandWaiterState.AskedForBreak) && (waiter.getState() != RedlandWaiterState.OnBreak) ){
				if ( waiter.numberCustomers() <= mostCustomers ){
					waiterToPick = waiter;
					mostCustomers = waiter.numberCustomers();
					//print( "waiter to pick is " + waiter.getWaiter().getName() );
				}
			}
		}
		
		//print("Assign " + c + " to waiter " + waiterToPick.getWaiter().getName() + " at table " + t.getNumber() );
		waiterToPick.setState( RedlandWaiterState.Working );
		//c.setWaiter( waiterToPick.getWaiter() );
		t.setOccupant( c );
		waiterToPick.addCustomer();//keeps track of number of customers, increments numberCustomers
		waiterToPick.waiter.msgSitAtTable( c, t, c.getPerson() );
		waitingCustomers.remove(c);
	}
	
	private void PutWaiterOnBreak( MyWaiter waiter ){
		//TODO: This is where null pointer is change final
		//print( "Waiter " + waiter.getWaiter().getName() + " going on break" );

		waiter.getWaiter().msgGoOnBreak();
		waiter.setState( RedlandWaiterState.GoingOnBreak );
	}

	/********** Utilities **********/
	
	public String getHostName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	/*GUI STUFF
	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	*/
	
	private class MyWaiter{
		private RedlandWaiterRole waiter;
		private RedlandWaiterState s;
		private WaiterEvent e;
		int numberCustomers;
		
		public MyWaiter( RedlandWaiterRole w ){
			this.waiter = w;
			this.s = RedlandWaiterState.Waiting;
			this.e = WaiterEvent.None;
			numberCustomers = 0;
		}
		
		public int numberCustomers(){
			return numberCustomers;
		}
		
		public void addCustomer(){//change to list of customers
			numberCustomers++;
		}
		
		public void removeCustomer(){
			numberCustomers--;
		}
		
		public RedlandWaiter getWaiter(){
			return this.waiter;
		}
		
		public void setState( RedlandWaiterState state ){
			this.s = state;
		}
		
		public RedlandWaiterState getState(){
			return this.s;
		}
		
		private void setEvent( WaiterEvent e) {
			this.e = e;
		}
		
		public WaiterEvent getEvent(){
			return this.e;
		}
	}
}
