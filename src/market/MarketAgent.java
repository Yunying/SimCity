package market;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import agent.Agent;
import interfaces.Building;
import interfaces.Person;
import global.actions.Action;
import global.roles.Role;
import gui.animation.building.MarketGUI;
import market.RestaurantOrder.orderState;
import market.interfaces.Market;
import market.interfaces.MarketCustomer;
import market.interfaces.MarketEmployee;
import market.interfaces.MarketManager;
import market.interfaces.TruckDriver;



public class MarketAgent extends Agent implements Market {

	/** Data */
	public int currentTime;
	public int currentDay;
	public int startTime;
	public int closeTime;
	public float balance;
	public float startingWage;
	public String location;
	public String name;
	
	public List<RestaurantOrder> orders;//list of current restaurant orders
	public List<MyCustomer> customers;//retained list of all customers
	public List<MyEmployee> employees;//retained list of all employees
	public MyDriver truckDriver;//for now this driver works all day
	public MyManager manager;//for now this manager works all day
	public Inventory inventory;
	public List<Role> peopleInTheBuilding;
	
	public enum CustomerState {pending, buyingCar, shopping, askedForOrder, ordered, hasItems, paid, leaving, notPresent};
	public enum EmployeeState {pending, beginWorking, working, onBreak, shiftOver, notPresent}
	public enum ManagerState {pending, beginWorking, working, onBreak, shiftOver, notPresent}
	public enum TruckDriverState {pending, waiting, enRoute, shiftOver, notPresent}
	public enum MarketState {pending, closing, closed, opening, checkIfOpen, open}
	MarketState state;
	
	private MarketGUI gui;
	
	public MarketAgent( String name, String location ){
		super( );
		
		this.name = name;
		this.location = location;
		balance = 1000;
		currentTime = 0;
		currentDay = 0;
		startTime = 16;
		closeTime = 40;
		startingWage = 8;
		state = MarketState.closed;
		
		orders = new ArrayList<RestaurantOrder>();
		customers = new ArrayList<MyCustomer>();
		employees = new ArrayList<MyEmployee>();
		truckDriver = null;
		manager = null;
		peopleInTheBuilding = new ArrayList<Role>();
		
		//create inventory, possibly have ability to add food item through gui
		int startingAmounts = 100;//for normative
		inventory = new Inventory( startingAmounts );
		inventory.AddFoodItem( "pizza", startingAmounts, (float) 6.00 );
		inventory.AddFoodItem( "steak", startingAmounts, (float) 7.00 );
		inventory.AddFoodItem( "salad", startingAmounts, (float) 5.00 );
		inventory.AddFoodItem( "soup", startingAmounts, (float) 5.00 );
		inventory.AddFoodItem( "chicken", startingAmounts, (float) 6.50 );
			//for dessert shop
		inventory.AddFoodItem( "brownie", startingAmounts, (float) 4.00 );
		inventory.AddFoodItem( "cheesecake", startingAmounts, (float) 6.00 );
		inventory.AddFoodItem( "gelato", startingAmounts, (float) 2.00 );
		inventory.AddFoodItem( "crepe", startingAmounts, (float) 7.00 );
		inventory.AddFoodItem( "waffle", startingAmounts, (float) 5.00 );
	}
	
	/** Messages */
	public void msgUpdateTime(int time, int day) {
		//TODO: this code may have error if market is created after openingTime
		currentDay = day;
		currentTime = time;
		
		if( currentTime == startTime ){
			state = MarketState.opening;
		}
		if( currentTime == closeTime ){
			state = MarketState.closing;
		}
		stateChanged();
	}

	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		if( r instanceof MarketCustomerRole ){
			//create wanted items list
			List<String> items = new ArrayList<String>();
			boolean wantsCar = false;
			for( Action action : actions ){
				if( action.getTask().toLowerCase().contains("car") ){
					wantsCar = true;
					break;
				}
				items.add( action.getTask().toLowerCase() );//add action.task "pizza" to wanted foods list
			}
			
			boolean flag = true;
			for( MyCustomer customer : customers ){
				if( customer.person == p ){
						flag = false;//to check if this is a new customer
						customer.state = CustomerState.pending;
						customer.wantedItems = items;
						customer.wantsCar = wantsCar;
						break;
				}
			}
			if( flag ){
				customers.add( new MyCustomer( (MarketCustomerRole) r, p, items, wantsCar ) );
			}
		}
		if( r instanceof MarketEmployeeRole ){
			boolean flag = true;
			((MarketEmployeeRole) r).setManager(manager.manager);
			if (manager.manager == null){
				print("Manager is null");
			}
			for( MyEmployee employee : employees ){
				if( employee.person == p ){
						flag = false;//to check if this is a new employee
						employee.state = EmployeeState.pending;
						break;
				}
			}
			if( flag ){
				employees.add( new MyEmployee( (MarketEmployeeRole) r, p, startingWage ) );
			}
		}
		if( r instanceof MarketManagerRole ){
			print("Manager is here");
			if( manager == null ) manager = new MyManager( (MarketManagerRole) r, p, startingWage );//better way to do this?
			if( manager.person == p ){
				manager.state = ManagerState.pending;
			}
			else manager = new MyManager( (MarketManagerRole) r, p, startingWage );
		}
		if( r instanceof TruckDriverRole ){
			if( truckDriver == null ) truckDriver = new MyDriver( (TruckDriver) r, p, startingWage );//better way to do this?
			if( truckDriver.person == p ){
				truckDriver.state = TruckDriverState.pending;
			}
			else truckDriver = new MyDriver( (TruckDriver) r, p, startingWage );
		}
		peopleInTheBuilding.add( r );
		stateChanged();
	}

	public void msgHereIsOrder( Map<String, Integer> order, Building restaurant) {
		orders.add( new RestaurantOrder( restaurant, order ) );
		stateChanged();
	}
	
	public void msgOrderDelivered(TruckDriver driver, RestaurantOrder o) {
		truckDriver.state = TruckDriverState.waiting;
		driver.msgGoToWaitingPosition();
		o.setState( orderState.delivered );//is this state needed
		balance += o.getBill();//add cost of order to market's balance
		orders.remove( o );
		stateChanged();
	}
	
	public void msgHereIsMarketState( MarketState state ){
		this.state = state;
		stateChanged();
	}

	public void msgMarketOpen() {
		state = MarketState.open;
		stateChanged();
	}


	public void msgMarketClosed() {
		state = MarketState.closed;
		stateChanged();
	}
	
	public void msgUpdateBalance( float bill ){
		this.balance = this.balance + bill;
		//TODO: what if final balance is 0 or negative
		/*
		  if( balance =< 0 ){
		       this.state = MarketState.outOfMoney;
		  }
		*/
		stateChanged();
	}
	
	public void msgPersonHasLeft( Person p, Role r ){
		if( r instanceof MarketCustomerRole ){
			for( MyCustomer customer : customers ){
				if( customer.person == p ){
					customer.state = CustomerState.leaving;
					break;
				}
			}
		}
		if( r instanceof MarketEmployeeRole ){
			for( MyEmployee employee : employees ){
				if( employee.person == p ){
					employee.state = EmployeeState.shiftOver;
					break;
				}
			}
		}
		if( r instanceof MarketManagerRole ){
			manager.state = ManagerState.shiftOver;
			if( balance > 1000 ){
				float cashToDeposit = this.balance - 1000;//anything over 1000
				manager.person.AddTaskDepositEarnings( this, cashToDeposit );
				this.balance = 1000.0f;
				//keep track of bank account?
			}
		}
		if( r instanceof TruckDriverRole ){
			truckDriver.state = TruckDriverState.shiftOver;
		}
		peopleInTheBuilding.remove( r );
		stateChanged();
	}
	
	/** Scheduler */
	public boolean pickAndExecuteAnAction() {
		
		//Rules pertaining to market status
		if( state == MarketState.opening ){
//			if( manager.state == ManagerState.working ){
//				AskManagerIfReady();
//				state = MarketState.pending;
//				return true;
//			}
		}
		if( state == MarketState.closing ){
			TellManagerToClose();
			//anything else/
			return true;
		}
		
		
		//Rules pertaining to customers
		for( MyCustomer customer : customers ){
			if( customer.state == CustomerState.pending ){
				TellManagerNewCustomer( customer );
				return true;
			}
		}
		for( MyCustomer customer : customers ){
			if( customer.state == CustomerState.leaving ){
				RemoveCustomer( customer );
				return true;
			}
		}
		
		//Rules pertaining to employees
		for( MyEmployee employee : employees ){
			if( employee.state == EmployeeState.pending){
				TellManagerNewEmployee( employee );
				return true;
			}
		}
		for( MyEmployee employee : employees ){
			if( employee.state == EmployeeState.shiftOver){
				RelieveEmployee( employee );
				return true;
			}
		}
		
		
		//Rules pertaining to manager
		try{
			if( manager.state == ManagerState.pending ){
				TellManagerToWork();
				return true;
			}
			if( manager.state == ManagerState.shiftOver ){
				RelieveManager();
				return true;
			}
		} catch( NullPointerException e ) {
			//manager is null;
		}
		
		//Rules pertaining to RestaurantOrder's and TruckDriver
		for( RestaurantOrder order : orders ){
			if( order.getState() == orderState.pending ){
				TellManagerNewOrder( order );
				return true;
			}
		}
		try{
			if( truckDriver.state == TruckDriverState.pending ){
				TellManagerNewDriver();
				return true;
			}
			if( truckDriver.state == TruckDriverState.shiftOver ){
				RelieveDriver();
				return true;
			}
		} catch( NullPointerException e ) {
			//truckDriver is null;
		}
		return false;
	}
	
	/** Actions */
	private void TellManagerToWork(){
		manager.state = ManagerState.working;
		manager.manager.msgBeginWorking( this, state, inventory );
	}
	
	private void RelieveManager(){
		manager.state = ManagerState.notPresent;
		manager.person.ChangeMoney( manager.wage );//pay manager for the day
	}
	
	private void TellManagerNewCustomer( MyCustomer customer ){
		customer.state = CustomerState.shopping;
		customer.customer.msgGoToShoppingArea( manager.manager );
		manager.manager.msgNewCustomer( customer.customer, customer.person, customer.wantedItems, customer.wantsCar );
	}
	
	private void RemoveCustomer( MyCustomer customer ){
		customer.state = CustomerState.notPresent;
		customer.wantsCar = false;
		customer.wantedItems = null;
		//not removing customer because we keep a log of all customers in MarketAgent
	}
	
	private void TellManagerNewEmployee( MyEmployee employee ){
		employee.state = EmployeeState.working;
		manager.manager.msgNewEmployee( employee.employee, employee.person );
		if (manager == null){
			print("first manager is null");
		}
		else if (manager.manager == null){
			print("second manager is null");
		}
		else if (employee == null){
			print("Employee is null");
		}
		employee.setManager(manager);
	}
	
	private void RelieveEmployee( MyEmployee employee ){
		employee.state = EmployeeState.notPresent;
		employee.person.ChangeMoney( employee.wage );
	}
	
	private void TellManagerNewOrder( RestaurantOrder order ){
		order.setState( orderState.processing );
		manager.manager.msgNewRestaurantOrder( order );
	}
	
	private void TellManagerNewDriver(){
		truckDriver.state = TruckDriverState.waiting;
		manager.manager.msgNewTruckDriver( truckDriver.driver, truckDriver.person );
	}
	
	private void RelieveDriver(){
		truckDriver.state = TruckDriverState.notPresent;
		truckDriver.person.ChangeMoney( manager.wage );//pay manager for the day
	}
	
	private void AskManagerIfReady(){
		state = MarketState.checkIfOpen;
		manager.manager.msgAskManagerIfReady();
	}
	
	private void TellManagerToClose(){
		if (manager != null && manager.manager != null)
			manager.manager.msgBeginClosingMarket();
	}
	
	/** Utilities */
	public String getLocation() {
		return location;
	}

	public void setLocation(String l) {
		this.location = l;
	}

	public boolean isOpen(){
		return ( state == MarketState.open );
	}
	
	public void setName( String name ){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int t) {
		startTime = t;
	}

	public int getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(int t) {
		closeTime = t;
	}

	public List<Role> getPeopleInTheBuilding() {
		return peopleInTheBuilding;
	}

	public MarketGUI getGui() {
		return gui;
	}

	public void setGui(MarketGUI gui) {
		this.gui = gui;
	}
	
	/** MyCustomer
	 * Private class to MarketAgent to keep track of customers. Is retained as history.
	 * Would allow for keeping track of customer debts.
	 * @author Jeff Redland
	 */
	private class MyCustomer{
		public MarketCustomer customer;
		public Person person;
		public CustomerState state;
		public float debt;//could be used if a customer can not pay
		public boolean wantsCar;
		List<String> wantedItems;

		
		public MyCustomer( MarketCustomer customer, Person person, List<String> items, boolean wantsCar ){
			this.customer = customer;
			this.person = person;
			this.debt = 0;
			this.wantsCar = false;
			this.state = CustomerState.pending;
			this.wantedItems = items;
			this.wantsCar = wantsCar;
		}
	}
	
	/** MyEmployee
	 * Private class to MarketAgent to keep track of employees. Is retained as history.
	 * Has the potential to implement promotions, vacation time, schedules.
	 * @author Jeff Redland
	 *
	 */
	private class MyEmployee{
		public MarketEmployee employee;
		public Person person;
		public EmployeeState state;
		public float wage; //used to pay employee

		
		public MyEmployee( MarketEmployee employee , Person person, float startingWage ){
			this.employee = employee;
			this.person = person;
			this.state = EmployeeState.pending;
			this.wage = startingWage;
		}


		public void setManager(MyManager manager) {
			// TODO Auto-generated method stub
			employee.setManager(manager.manager);
		}
	}
	
	/** MyDriver
	 * Private class to MarketAgent to keep track of driver. Is retained as history.
	 * Has the potential to implement promotions, vacation time, schedules.
	 * @author Jeff Redland
	 *
	 */
	private class MyDriver{
		public TruckDriver driver;
		public Person person;
		public TruckDriverState state;
		public float wage; //used to pay employee

		
		public MyDriver( TruckDriver driver, Person person, float startingWage ){
			this.driver = driver;
			this.person = person;
			this.state = TruckDriverState.pending;
			this.wage = startingWage;
		}
	}
	
	/** MyManager
	 * Private class to MarketAgent to keep track of manager. Is retained as history.
	 * Has the potential to implement promotions, vacation time, schedules.
	 * @author Jeff Redland
	 *
	 */
	private class MyManager{
		public MarketManager manager;
		public Person person;
		public ManagerState state;
		public float wage; //used to pay employee

		
		public MyManager( MarketManagerRole manager, Person person, float startingWage ){
			this.manager = manager;
			this.person = person;
			this.state = ManagerState.pending;
			this.wage = startingWage;
		}
	}
}
