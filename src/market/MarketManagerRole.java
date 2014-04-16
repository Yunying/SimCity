package market;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import interfaces.Person;
import market.MarketAgent.CustomerState;
import market.MarketAgent.EmployeeState;
import market.MarketAgent.ManagerState;
import market.MarketAgent.MarketState;
import market.MarketAgent.TruckDriverState;
import market.RestaurantOrder.orderState;
import market.interfaces.Market;
import market.interfaces.MarketCustomer;
import market.interfaces.MarketEmployee;
import market.interfaces.MarketManager;
import market.interfaces.TruckDriver;
import global.PersonAgent;
import global.roles.Role;

public class MarketManagerRole extends Role implements MarketManager{

	/** Data */
	private Market market;
	private List<MyCustomer> customers;
	private List<MyEmployee> employees;
	private List<RestaurantOrder> orders;
	private MyDriver driver;
	private Inventory inventory;
	
	//private enum ManagerState {pending, working, onBreak, shiftOver, notPresent};
	private ManagerState state;
	//public enum MarketState {pending, closing, closed, opening, checkIfOpen, open}
	private MarketState marketState;
	
	
	public MarketManagerRole(){
		super();
		
		state = ManagerState.pending;
		marketState = MarketState.pending;
		customers = new ArrayList<MyCustomer>();
		employees = new ArrayList<MyEmployee>();
		orders = new ArrayList<RestaurantOrder>();
		driver = null;
	}

	/** Messages */
	public void msgBeginWorking( Market market, MarketState state, Inventory inventory ){
		this.market = market;
		this.state = ManagerState.beginWorking;
		marketState = state;
		this.inventory = inventory;
		stateChanged();
	}
	
	public void msgNewCustomer( MarketCustomer customer, Person person, List<String> wantedItems, boolean wantsCar ) {
		customers.add( new MyCustomer( customer , person, wantedItems, wantsCar ) );
		stateChanged();
	}	

	public void msgNewEmployee(MarketEmployee employee, Person person ) {
		employees.add( new MyEmployee( employee, person ) );
		stateChanged();
	}
	
	public void msgNewTruckDriver( TruckDriver driver, Person person ){
		this.driver = new MyDriver( driver, person );
		stateChanged();
	}
	
	public void msgIWantACar( MarketCustomer customer, float cash ){
		for( MyCustomer cust : customers ){
			if( cust.customer == customer ){
				if( cash > 1000 ){//this is very arbitrary
					cust.bill = 1000;//check CheckOutCustomer
					( (PersonAgent) cust.person).setHasCar( true );
					//cust.person.ChangeHasCar( true );
					cust.state = CustomerState.paid;
				}
				else{
					print( "You can't affor a car fool." );
					cust.person.msgLeavingLocation( (MarketCustomerRole) cust.customer );
					customers.remove( cust );
					market.msgPersonHasLeft( cust.person, (MarketCustomerRole) cust.customer );
				}
			}
		}
		stateChanged();
	}
	
	public void msgReadyToPay( MarketCustomer customer, float bill, float cash ) {
		for( MyCustomer cust : customers ){
			if( cust.customer == customer ){
				cust.bill = bill;
				cust.cashPaid = cash;
				cust.state = CustomerState.paid;
			}
		}
		stateChanged();
	}

	public void msgLeavingMarket( MarketCustomer customer ){
		for( MyCustomer cust : customers ){
			if( cust.customer == customers ){
				cust.state = CustomerState.leaving;
			}
		}
		stateChanged();
	}

	public void msgLeavingMarket( MarketEmployee employee ){
		for( MyEmployee emp : employees ){
			if( emp == employee ){
				emp.state = EmployeeState.shiftOver;
			}
		}
		stateChanged();
	}

	public void msgLeavingMarket( TruckDriver driver ){
		//if there were a list of drivers check through list
		this.driver.state = TruckDriverState.shiftOver;
		stateChanged();
	}
	
	public void msgNewRestaurantOrder(RestaurantOrder o) {
		orders.add( o );
		stateChanged();
	}

	public void msgOrderDelivered( RestaurantOrder o ){
		driver.state = TruckDriverState.waiting;
		market.msgUpdateBalance( o.getMoneyGiven() );
		o.setState( orderState.delivered );
		stateChanged();
	}

	public void msgAskManagerIfReady(){
		marketState = MarketState.checkIfOpen;
		stateChanged();
	}
	
	public void msgBeginClosingMarket() {
		marketState = MarketState.closing;
		stateChanged();
	}

	/** Scheduler */
	public boolean pickAndExecuteAnAction() {
		//rules pertaining to market
		if( marketState == marketState.checkIfOpen ){
			CheckIfMarketIsOpen();
			return true;
		}
		if( marketState == MarketState.closing ){
			PrepareMarketToClose();
			return true;
		}
		
		
		//rules pertaining to manager
		if( state == ManagerState.beginWorking ){
			BeginWorking();
			return true;
		}
		
		//rules pertaining to employees
		for( MyEmployee employee : employees ){
			if( employee.state == EmployeeState.pending ){
				TellEmployeeToWork( employee );
				return true;
			}
		}
		for( MyEmployee employee : employees ){
			if( employee.state == EmployeeState.shiftOver ){
				RelieveEmployee( employee );
				return true;
			}
		}
		
		//rules pertaining to customers
		for( MyCustomer customer : customers ){
			if( customer.state == CustomerState.pending ){
				if( customer.wantsCar ){
					SellCustomerCar( customer );
				}
				else{
					AssignCustomerToEmployee( customer );
				}
				return true;
			}
		}
		for( MyCustomer customer : customers ){
			if( customer.state == CustomerState.paid ){
				CheckOutCustomer( customer );
				return true;
			}
		}
		for( MyCustomer customer : customers ){
			if( customer.state == CustomerState.leaving ){
				RemoveCustomer( customer );
				return true;
			}
		}
		
		//rules pertaining to driver and orders
		try{
			if ( driver.state == TruckDriverState.pending ){
				PrepareTruckDriver();
				return true;
			}
			if ( driver.state == TruckDriverState.shiftOver ){
				RelieveTruckDriver();
				return true;
			}
		}catch(java.lang.NullPointerException e){
			return false;
		}
		for( RestaurantOrder order : orders ){
			if( order.getState() == orderState.processing && driver.state == TruckDriverState.waiting ){
				SendOrderToDriver( order );
				return true;
			}
		}
		for( RestaurantOrder order : orders ){
			if( order.getState() == orderState.delivered ){
				RemoveOrder( order );
				return true;
			}
		}
		
		return false;
	}

	/** Actions */
	private void BeginWorking(){
		state = ManagerState.working;
		//need a semaphore acquire here?
		DoGoToRegister( );
	}
	
	private void SellCustomerCar( MyCustomer customer ){
		float carBill = 1000.0f;
		customer.state = CustomerState.buyingCar;
		customer.customer.msgWelcomeToMarket( this, carBill );
	}
	
	private void AssignCustomerToEmployee( MyCustomer customer ){
		if( marketState == MarketState.closing ){//if market is closing
			print( "Market is closing sorry." );
			customer.person.msgLeavingLocation( (MarketCustomerRole) customer.customer );
			customers.remove( customer );
			market.msgPersonHasLeft( customer.person, (MarketCustomerRole) customer.customer );
		}
		customer.state = CustomerState.shopping;
		MyEmployee employeeToPick = employees.get( 0 ); //TODO: use smarter algorithm to pick employees
		
		employeeToPick.numCustomers++;
		employeeToPick.employee.msgHelpThisCustomer( customer.customer , customer.person );
	}
	
	private void CheckOutCustomer( MyCustomer customer ){
		customer.state = CustomerState.leaving;
		//update customers money
		//( (PersonAgent) customer.person ).ChangeMoney( -(customer.bill) ); //is done in MarketCustomer.msgHereIsYourChange( float )
		//update market money
		( (MarketAgent) market).msgUpdateBalance( customer.bill );
		customer.customer.msgHereIsYourChange( customer.cashPaid - customer.bill );
	}
	
	private void RemoveCustomer( MyCustomer customer ){
		customer.state = CustomerState.notPresent;
		market.msgPersonHasLeft( customer.person, (MarketCustomerRole) customer.customer );
		customers.remove( customer );
	}
	
	private void TellEmployeeToWork( MyEmployee employee ){
		employee.state = EmployeeState.working;
		employee.employee.msgBeginWorking( this, inventory );//tells employee to go to start location
	}
	
	private void RelieveEmployee( MyEmployee employee ){
		employee.state = EmployeeState.notPresent;
		market.msgPersonHasLeft( employee.person, (MarketEmployeeRole) employee.employee );
		employees.remove( employee );
	}
	
	private void SendOrderToDriver( RestaurantOrder order ){
		//does not yes implement non-normative mode of running out
		order.setState( orderState.givenToDriver );
		
		//first we need to calculate bill
		float bill = 0;
		for (Map.Entry<String, Integer> entry : order.getOrder().entrySet()){
			if( inventory.isAvailable( entry.getKey() ) ){
				//newItemsList.add( entry.getKey() );
				bill += inventory.getPrice( entry.getKey() ) * entry.getValue();// bill = price * amount
			}
			else{
				print( "Market out of item: " + entry.getKey() );
				//make note of not adding this item to newItemsList
				//make sure restaurant knows this item is out
			}
		}
		order.setBill( bill );
		driver.driver.msgDeliverOrder( order );
	}
	
	private void RemoveOrder( RestaurantOrder o ){
		orders.remove( o );
	}
	
	private void CheckIfMarketIsOpen(){
		//make sure not to use this to check if closed, only to check if done opening
		
		if( ( employees.size() > 0 ) && ( driver.person != null ) ){
			marketState = MarketState.open;
		}
		else{
			marketState = MarketState.opening;
		}
		
		market.msgHereIsMarketState( marketState );
	}
	
	private void PrepareMarketToClose(){
		//employees are paid as they leave, they initiate that on their own
		driver.driver.msgMarketIsClosing();
		for( MyEmployee employee : employees ){
			employee.employee.msgMarketIsClosing();
		}
		//deposit money
		//TODO: need to deposit market's money
	}
	
	private void PrepareTruckDriver(){
		driver.state = TruckDriverState.waiting;
		driver.driver.msgBeginWork( market, this );
	}
	
	private void RelieveTruckDriver(){
		driver.state = TruckDriverState.notPresent;
		market.msgPersonHasLeft( driver.person, (TruckDriverRole) driver.driver );
		driver.person = null;
	}
	
	
	/** GUI Calls */
	
	private void DoGoToRegister(){
		//TODO: make animation call here
	}
	
	/** Utilities */
	
	
	/** MyCustomer
	 * Private class to MarketManagerRole to keep track of customers.
	 * @author Jeff Redland
	 */
	private class MyCustomer{
		public MarketCustomer customer;
		public Person person;
		public CustomerState state;
		public float bill;
		public float cashPaid;
		public boolean wantsCar;
		public List<String> wantedItems;

		
		public MyCustomer( MarketCustomer customer, Person person, List<String> wantedItems, boolean wantsCar ){
			this.customer = customer;
			this.person = person;
			this.wantedItems = wantedItems;
			this.state = CustomerState.pending;
			this.bill = 0.0f;
			this.cashPaid = 0.0f;
			this.wantsCar = wantsCar;
		}
	}
	
	/** MyEmployee
	 * Private class to MarketManagerRole to keep track of employees.
	 * @author Jeff Redland
	 *
	 */
	private class MyEmployee{
		public MarketEmployee employee;
		public Person person;
		public EmployeeState state;
		public int numCustomers;
		
		public MyEmployee( MarketEmployee employee , Person person ){
			this.employee = employee;
			this.person = person;
			this.state = EmployeeState.pending;
			this.numCustomers = 0;
		}
	}
	
	/** MyDriver
	 * Private class to MarketManager to keep track of driver.
	 * @author Jeff Redland
	 *
	 */
	private class MyDriver{
		public TruckDriver driver;
		public Person person;
		public TruckDriverState state;
		
		public MyDriver( TruckDriver driver, Person person ){
			this.driver = driver;
			this.person = person;
			this.state = TruckDriverState.pending;
		}
	}
}
