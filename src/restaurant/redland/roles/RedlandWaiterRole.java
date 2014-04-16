package restaurant.redland.roles;

import interfaces.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import restaurant.redland.UtilityClasses.Table;
import restaurant.redland.interfaces.RedlandCashier;
import restaurant.redland.interfaces.RedlandCook;
import restaurant.redland.interfaces.RedlandCustomer;
import restaurant.redland.interfaces.RedlandHost;
import restaurant.redland.interfaces.RedlandWaiter;
import global.roles.Role;

public class RedlandWaiterRole extends Role implements RedlandWaiter{
	//A global for the number of tables. Use this for map{Table, coordinates} ?
	static final int NTABLES = 4;
	boolean onBreak = false;
	private String name;
	//public WaiterGui waiterGui = null;
	
	Timer timer = new Timer();
	Random generator = new Random();//For determining break length
	
	private RedlandHost host;
	private RedlandCook cook;//Change to cook list when we implement multiple cooks
	private RedlandCashier cashier;
	
	//list of all the customers this waiter is taking control of
	public List<MyCustomer> myCustomers = new ArrayList<MyCustomer>();
	
	//Enum's for MyCustomer and scheduler. Put in MyCustomer class?
	public enum RedlandCustomerState
	{Waiting, BeingSeated, Seated, ReadyToOrder, AskedToOrder, Ordered, Eating, Leaving};
	public enum CustomerEvent
	{Seated, AsksToOrder, Ordered, FoodIsReady, DoneEating,
		OutOfOrder}
	
	//menu to give to customers
	String[] foods = new String[] {"Steak", "Chicken", "Salad", "Soup"};
	private ArrayList<String> menu = new ArrayList<String>(Arrays.asList(foods));
	
	private Semaphore customerAtTable = new Semaphore(0,true);
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atCook = new Semaphore(0,true);

	
	public RedlandWaiterRole() {
		super();
		
		onBreak = false;
	}
	
	
	/********** Messages **********/

	public void msgSitAtTable(RedlandCustomerRole cust, Table table, Person person){//message 2
		//TODO: Need to pass int tableNumber and not Table class
		//print( "Got new " + cust );
		myCustomers.add( new MyCustomer( cust, table.getNumber(), person ) );
		stateChanged();
	}
	
	public void msgImAtTable(RedlandCustomer cust){//releases waiter from waiting at table
		//print("msgImAtTable() called");
		customerAtTable.release();
		for (MyCustomer customer : myCustomers){
			if (customer.getCustomer() == cust) {
				//print( "customer " + cust.getName() + " is seated" );
				customer.setEvent(CustomerEvent.Seated);
				customer.setState(RedlandCustomerState.Seated);
			}
		}
		stateChanged();
	}
	
	public void msgImReadyToOrder(RedlandCustomer cust){//message 4
		//print( "msgImReadyToOrder received" );
		for (MyCustomer customer : myCustomers){
			if (customer.getCustomer() == cust) {
				//print ( "customer " + cust.getName() + " wants to order" );
				customer.setEvent(CustomerEvent.AsksToOrder);
			}
		}
		stateChanged();
	}
	
	public void msgHereIsMyChoice(RedlandCustomer cust, String choice){//message 6
		for (MyCustomer customer : myCustomers){
			if (customer.getCustomer() == cust) {
				customer.setEvent(CustomerEvent.Ordered);
				customer.setChoice(choice);
			}
		}
		stateChanged();
	}
	
	public void msgOrderIsReady(String choice, int table){//message 8
		for (MyCustomer customer : myCustomers){
			if (customer.getTable() == table){
				if( customer.getChoice() == choice ){
					customer.setEvent(CustomerEvent.FoodIsReady);
				}
			}
		}
		stateChanged();
	}
	
	public void msgDoneEating(RedlandCustomer cust) {//message 10
		for (MyCustomer customer : myCustomers){
			if (customer.getCustomer() == cust) {
				customer.setEvent(CustomerEvent.DoneEating);
			}
		}
		stateChanged();
	}

	public void msgOutOfOrder( String choice, int table ){
	//TODO: finish this non-normative scenario
		for (MyCustomer customer : myCustomers){
			if (customer.getTable() == table ) {
				customer.setEvent(CustomerEvent.OutOfOrder);
				customer.setChoice(choice);
			}
		}
		stateChanged();
	}
	
	public void msgAtTable(){
		/*Very similar to msgImAtTable called by customer
		 * This, however, is called by WaiterGui when we need to get customer order
		 */
		//print("msgAtTable() called");
		atTable.release();
		stateChanged();
	}
	
	public void msgAtCook(){
		//Like msgAtTable, called by WaiterGui when at cook
		//print( "At cook" );
		atCook.release();
		stateChanged();
	}
	
	public void msgAskForBreak(){
		//Message sent from WaiterGui saying waiter wants break, initiated by check box
		//print( "I want break" );
		host.msgIWantBreak(this);
		stateChanged();
	}
	
	public void msgGoOnBreak(){
		onBreak = true;
		stateChanged();
	}
	
	private void msgBreakIsDone(){
		onBreak = false;
		SetOffBreak();
		stateChanged();
	}
	
	public void msgHereIsBill( RedlandCashier cashier ){
		//TODO: finish
		//do i need to "physically" pass anything?
	}
	
	
	/******** Scheduler ********/
	public boolean pickAndExecuteAnAction() {
		for (MyCustomer cust : myCustomers){
			//print ( "customer list: " + cust.getCustomer().getName() );
			if (cust.getState() == RedlandCustomerState.Waiting) {
				cust.setState(RedlandCustomerState.BeingSeated);
				SeatCustomer(cust);
				return true;
			}
		}
		for (MyCustomer cust : myCustomers){
			if (cust.getState() == RedlandCustomerState.BeingSeated && cust.getEvent() == CustomerEvent.Seated){
				cust.setState(RedlandCustomerState.Seated);
				return true;
			}
		}
		for (MyCustomer cust : myCustomers){
			if (cust.getState() == RedlandCustomerState.Seated && cust.getEvent() == CustomerEvent.AsksToOrder){
				//print ( cust.getCustomer().getName() + " wants to order" );
				AskCustomerForOrder(cust);
				return true;
			}
		}
		for (MyCustomer cust : myCustomers){
			if (cust.getState() == RedlandCustomerState.AskedToOrder && cust.getEvent() == CustomerEvent.Ordered){
				cust.setState(RedlandCustomerState.Ordered);
				SendOrderToCook(cust);
				return true;
			}
		}
		for (MyCustomer cust : myCustomers){
			if (cust.getState() == RedlandCustomerState.Ordered && cust.getEvent() == CustomerEvent.FoodIsReady){
				cust.setState(RedlandCustomerState.Eating);
				GiveOrderToCustomer(cust);
				return true;
			}
		}
		for (MyCustomer cust : myCustomers){
			if (cust.getState() == RedlandCustomerState.Eating && cust.getEvent() == CustomerEvent.DoneEating){
				cust.setState(RedlandCustomerState.Leaving);
				CustomerIsLeaving(cust);
				return true;
			}
		}
			//Non-Normative Scenario
				//This rule is for when the cook is out of a certain order
		for (MyCustomer cust : myCustomers){
			if (cust.getState() == RedlandCustomerState.Ordered && cust.getEvent() == CustomerEvent.OutOfOrder){
				AskCustomerToReOrder(cust);
				return true;
			}
		}
		
		//For setting waiter on break
		if ( onBreak ){
			//print("I'm set on break");
			SetOnBreak();
			return true;
		}
		/*
		if ( !onBreak ){//this will be unnecessarily called a lot
			SetOffBreak();
			return true;
		}
		*/
		
		return false;
	}

	/******** Actions ********/

	private void SeatCustomer(MyCustomer customer) {
		//tell customer to sit at table, should i pass table or just coordinates?
		//print("Seating " + customer.getCustomer() + " at " + customer.getTable());
		customer.c.msgFollowMeToTable( this, customer.getTable(), menu);
		DoSeatCustomer(customer);
		try {
			atTable.acquire();//released by waiterGui when at table msgAtTable()
			customerAtTable.acquire();//released by customer when customer is at table msgImAtTable()
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*GUI STUFF
		waiterGui.DoLeaveCustomer();
		*/
	}

	private void AskCustomerForOrder(MyCustomer cust){
		//print( "Going to get " + cust.getCustomer() + "'s order" );
		DoGoToCustomer(cust);
		try {
			atTable.acquire();//released WaiterGui when at table
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//After we get to table we ask customer for order
		//print( cust.c.getName() + ", what would you like");
		cust.c.msgWhatWouldYouLike();
		cust.setState(RedlandCustomerState.AskedToOrder);
		//Then we leave
		/*GUI STUFF
		waiterGui.DoLeaveCustomer();
		*/
	}
	
	private void SendOrderToCook(MyCustomer cust){
		//print( "Sending " + cust.getCustomer() + "'s order to cook");
		DoGoToCook();
		try {
			atCook.acquire();//released by cook when at cook
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//After we get to cook we give cook the order
		cook.msgHereIsOrder(this, cust.getChoice(), cust.getTable());
		//Then we leave
		/*GUI STUFF
		waiterGui.DoLeaveCook();
		*/
	}
	
	private void GiveOrderToCustomer(MyCustomer cust){
		//first give bill to cashier TODO make separate action
		//Do animation?
		cashier.msgComputeBill( cust.getChoice(), this, cust.getCustomer() );
		
		print( "Getting " + cust.getCustomer() + "'s order" );
		DoGoToCook();
		try {
			atCook.acquire();//released by cook when at cook
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print( "Got " + cust.getChoice() + " for " + cust.getCustomer() );
		//Confirm with cook we got the order
		cook.msgGotOrder(this, cust.getChoice(), cust.getTable() );
		//After we get to cook we get the order, this is just for animation
		/*GUI STUFF
		waiterGui.DoLeaveCook();//to release atCook semaphore
		*/
		//Then we go to customer
		DoGoToCustomer( cust );
		try {
			atTable.acquire();//released WaiterGui when at table
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//After we get to table we give customer order
		cust.c.msgHereIsYourBill( cashier );
		cust.c.msgHereIsYourFood();
		//Then we leave
		/*GUI STUFF
		waiterGui.DoLeaveCustomer();
		*/
	}
	
	private void CustomerIsLeaving(MyCustomer cust){
		//print( cust.getCustomer() + " is leaving " + cust.getTable() );
		cust.person.msgLeavingLocation( cust.c );
		host.msgTableIsFree( cust.getTable(), this);
		myCustomers.remove(cust);
	}
	
	private void SetOnBreak(){
		/*
		 * This is not implemented in normative sim city for this restaurant
		 */
		
		/*
		//randomly generate break length from 5-10 seconds
		int breakLength = 10 - generator.nextInt(5);
		
		host.msgImOnBreak( this );
		waiterGui.DoGoOnBreak();
		
		timer.schedule(new TimerTask() {
			public void run() {				
				print("I'm off break");
				onBreak = false;
				SetOffBreak();
				stateChanged();
			}
		},
		breakLength * 1000);
		*/
	}
	
	private void SetOffBreak(){
		host.msgImOffBreak( this );
		//waiterGui.setOnBreak( false );
	}
	
	/**Non-normative actions*/
	private void AskCustomerToReOrder( MyCustomer cust ){
		//cust.setState( CustomerState.Seated );
		DoGoToCustomer(cust);
		try {
			atTable.acquire();//released WaiterGui when at table
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//After we get to table we ask customer to re-order
		//print( cust.c.getName() + ", what would you like");
		//Make sure customer does not order this same item
		cust.c.msgReOrder( cust.getChoice() );
		cust.setState(RedlandCustomerState.AskedToOrder);
		//Then we leave
		/*GUI STUFF
		 * waiterGui.DoLeaveCustomer();
		 */
	}
	
	
	/******** GUI Calls ********/
	private void DoSeatCustomer(MyCustomer customer) {
		//waiterGui.DoBringToTable( customer.getTable() ); 
	}
	
	private void DoGoToCustomer(MyCustomer customer) {
		//print("Going to " + customer.getCustomer() + " at " + customer.getTable());
		//waiterGui.DoGoToCustomer( customer.getTable() ); 
		atTable.release();
	}

	private void DoGoToCook(){
		//TODO: add cook parameter when multiple cooks
		//print( "Going to cook " + cook.getName() );
		//TODO: change this to get cook coordinates, not magic number
		//waiterGui.DoGoToCook(-20, -20);
		atCook.release();
	}
	
	
	/******** Utilities ********/
	public void setHost( RedlandHost host){
		this.host = host;
	}
	
	public void setCook( RedlandCook cook){
		//TODO: change this to addCook(...) when we implement multiple cooks
		this.cook = cook;
	}

	public String getName() {
		return name;
	}
	
	public void setCashier( RedlandCashier cashier ){
		this.cashier = cashier;
	}
	
	public RedlandCashier getCashier(){
		return this.cashier;
	}
	
	/*GUI STUFF
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	*/
	
	public class MyCustomer{
		public RedlandCustomerRole c;
		public RedlandCustomerState s;
		public CustomerEvent e;
		public int t;
		public String choice;
		public Person person;

		
		public MyCustomer( RedlandCustomerRole c, int t, Person person ){
			this.c = c;
			this.t = t;
			this.person = person;
			s = RedlandCustomerState.Waiting;
		}
		
		public int getTable(){
			return this.t;
		}
		
		public RedlandCustomer getCustomer(){
			return this.c;
		}
		
		public RedlandCustomerState getState(){
			return this.s;
		}
		
		public synchronized void setState(RedlandCustomerState state){
			this.s = state;
		}
		
		public CustomerEvent getEvent(){
			return this.e;
		}
		
		public synchronized void setEvent(CustomerEvent event){
			this.e = event;
		}
		
		public String getChoice(){
			return this.choice;
		}
		
		public void setChoice(String choice){
			this.choice = choice;
		}
	}
}
