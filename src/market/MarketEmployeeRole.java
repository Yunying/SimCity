package market;

import interfaces.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import market.MarketAgent.CustomerState;
import market.MarketAgent.EmployeeState;
import market.MarketAgent.TruckDriverState;
import market.interfaces.MarketCustomer;
import market.interfaces.MarketEmployee;
import market.interfaces.MarketManager;
import global.roles.Role;

public class MarketEmployeeRole extends Role implements MarketEmployee {

	/** Data */
	private List<MyCustomer> customers;
	private MarketManager manager;
	private Inventory inventory;
	private Semaphore atCustomer = new Semaphore( 0, true );//this is for gui, to be released when you reach customer
	private Semaphore collectItems = new Semaphore( 0, true );//this is for gui, to be released when order items are acquired
	
	EmployeeState state;
	boolean isMarketOpen;
	
	
	public MarketEmployeeRole(){
		super();
		
		this.state = EmployeeState.pending;
		customers = new ArrayList<MyCustomer>();
		manager = null;
		inventory = null;
		isMarketOpen = false;
	}
	
	/** Messages */	
	public void msgBeginWorking( MarketManager manager, Inventory inventory ){
		this.state = EmployeeState.beginWorking;
		this.manager = manager;
		this.inventory = inventory;
		isMarketOpen = true;
		stateChanged();
	}
	
	public void msgHelpThisCustomer( MarketCustomer customer, Person person ) {
		customers.add( new MyCustomer( customer, person ) );
		stateChanged();
	}
	
	public void msgIWantTheseItems( MarketCustomer cust, List<String> items ) {
		for( MyCustomer customer : customers ){
			if( customer.customer == cust ){
				customer.state = CustomerState.ordered;
				customer.items = items;
			}
		}
		stateChanged();
	}
	
	public void msgMarketIsClosing() {
		isMarketOpen = false;
		stateChanged();
	}

	/** Scheduler */
	public boolean pickAndExecuteAnAction() {
		
		//rules pertaining to market
		
		
		//rules pertaining to customers
		for( MyCustomer customer : customers ){
			if( customer.state == CustomerState.pending ){
				GoHelpCustomer( customer );
				return true;
			}
		}
		for( MyCustomer customer : customers ){
			if( customer.state == CustomerState.ordered ){
				GetCustomersOrder( customer );
				return true;
			}
		}
		
		DoGoToHomePosition();
		
		if( !isMarketOpen && (customers.size() == 0 ) ){
			LeaveMarket();
			return true;
		}
		return false;
	}
	
	/** Actions */
	
	private void GoHelpCustomer( MyCustomer customer ){
		customer.state = CustomerState.askedForOrder;
		DoGoToCustomer( customer );//go to customer
		try{
			atCustomer.acquire();//make sure to release by gui
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.customer.msgWhatDoYouWant( this );//then ask customer what they want
	}
	
	private void GetCustomersOrder( MyCustomer customer ){
		//TODO: this is just normative, no running out of items
		customer.state = CustomerState.hasItems;//to prevent loop, is this necessary?
		//first we need to calculate customer bill
		List<String> newItemsList = new ArrayList<String>(); //this will account for running out of food
		for( String item : customer.items ){
			if( inventory.isAvailable( item ) ){
				newItemsList.add( item );
				customer.bill += inventory.sell( item );
			}
			else{
				print( "Market out of item: " + item );
				//make note of not adding this item to newItemsList
				//TODO: send message to customer about depleted food
			}
		}
		customer.items = newItemsList;//update list if out of items
		
		DoCollectItems( customer.items );//now to collect items for customer
		try{
			collectItems.acquire();//make sure to release by gui
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		DoGoToCustomer( customer );//now to bring items to customer and his/her bill
		try{
			atCustomer.acquire();//make sure to release by gui
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.customer.msgHereAreYourItemsAndBill( this, customer.items, customer.bill);
		
		customers.remove( customer );//customer will now got to manager to pay and is out of our hands
	}
	
	private void LeaveMarket(){
		state = EmployeeState.shiftOver;
		if (manager == null){
			return;
		}
		manager.msgLeavingMarket( this );
		this.person.msgLeavingLocation( this );//set role inactive
	}
	
	/** GUI Calls */
	
	private void DoGoToCustomer( MyCustomer customer ){
		//do this in gui
		atCustomer.release();
	}
	
	private void DoCollectItems( List<String> items ){
		//do this in gui
		collectItems.release();
	}
	
	private void DoGoToHomePosition(){
		
	}
	
	public void setManager(MarketManager m){
		manager = m;
	}
	
	/** Utilities */
	
	
	/** MyCustomer
	 * @author Jeff Redland
	 * Private class to MarketEmployeeRole to keep track of customers.
	 */
	private class MyCustomer{
		public MarketCustomer customer;
		public Person person;
		//public enum CustomerState {pending, shopping, askedForOrder, ordered, hasItems, leaving};
		public CustomerState state;
		public float bill;
		public boolean wantsCar;
		public List<String> items;

		
		public MyCustomer( MarketCustomer customer, Person person ){
			this.customer = customer;
			this.person = person;
			this.state = CustomerState.pending;
			this.bill = 0.0f;
			this.wantsCar = false;
			this.items = new ArrayList<String>();
		}
	}
}
