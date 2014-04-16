/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package restaurant.redland;

import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import interfaces.Building;
import interfaces.Person;
import restaurant.redland.UtilityClasses.*;
import restaurant.redland.interfaces.*;
import restaurant.redland.roles.RedlandCashierRole;
import restaurant.redland.roles.RedlandCookRole;
import restaurant.redland.roles.RedlandCustomerRole;
import restaurant.redland.roles.RedlandHostRole;
import restaurant.redland.roles.RedlandWaiterRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import market.MarketAgent;
import market.TruckDriverRole;
import market.MarketAgent.ManagerState;
import market.MarketAgent.MarketState;
import market.interfaces.Market;
import bank.BankAgent;

/**Redland Restaurant
 * @author Redland
 */
public class RedlandRestaurantAgent extends BusinessAgent implements Building {

	/** Data */
	
	public int currentTime;
	public String name;
	public float balance;
	public float startingWage;
	public boolean isOpen;
	
	public MarketAgent market;
	public BankAgent bank;
	public List<MyCustomer> customers;
	public List<MyWaiter> waiters;
	public MyHost host;
	public MyCook cook;
	public MyCashier cashier;
	public List<Role> peopleInTheBuilding;
	
	public enum RedlandHostState {Pending, Waiting, AskedForBreak, OnBreak, GoingOnBreak, Working, ShiftOver, NotPresent};
	public enum RedlandCookState {Pending, Waiting, AskedForBreak, OnBreak, GoingOnBreak, Working, ShiftOver, NotPresent};
	public enum RedlandCashierState {Pending, Waiting, AskedForBreak, OnBreak, GoingOnBreak, Working, ShiftOver, NotPresent};
	public enum RedlandWaiterState {Pending, Waiting, AskedForBreak, OnBreak, GoingOnBreak, Working, ShiftOver, NotPresent};
	public enum RedlandCustomerState {Pending, Waiting, BeingSeated, Seated, ReadyToOrder, AskedToOrder, Ordered, Eating, Leaving, NotPresent};
	public enum RedlandRestaurantState {pending, closing, closed, opening, checkIfOpen, open}
	RedlandRestaurantState state;
	
	public RedlandRestaurantAgent( String name, MarketAgent market, BankAgent bank){
		super();
		
		this.name = name;
		//initialize variables
		currentTime = 0;
		balance = 1000.0f;
		startingWage = 10.0f;
		this.market = market;
		this.bank = bank;
		customers = new ArrayList<MyCustomer>();
		waiters = new ArrayList<MyWaiter>();
		host = null;
		cook = null;
		cashier = null;
		peopleInTheBuilding = new ArrayList<Role>();
		isOpen = false;
		this.state = RedlandRestaurantState.closed;
	}
	
	/** Messages */
	
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		if( r instanceof RedlandCustomer ){
			boolean flag = true;
			for( MyCustomer customer : customers ){
				if( customer.person == p ){
						flag = false;//to check if this is a new customer
						customer.state = RedlandCustomerState.Pending;
						break;
				}
			}
			if( flag ){
				customers.add( new MyCustomer( (RedlandCustomerRole) r, p ) );
			}
		}
		if( r instanceof RedlandWaiterRole ){
			boolean flag = true;
			for( MyWaiter waiter : waiters ){
				if( waiter.person == p ){
						flag = false;//to check if this is a new employee
						waiter.state = RedlandWaiterState.Pending;
						break;
				}
			}
			if( flag ){
				waiters.add( new MyWaiter( (RedlandWaiterRole) r, p, startingWage ) );
			}
		}
		if( r instanceof RedlandHostRole ){
			if( host == null ) host = new MyHost( (RedlandHostRole) r, p, startingWage );//better way to do this?
			if( host.person == p ){
				host.state = RedlandHostState.Pending;
			}
			else host = new MyHost( (RedlandHostRole) r, p, startingWage );
		}
		if( r instanceof RedlandCookRole ){
			if( cook == null ) cook = new MyCook( (RedlandCookRole) r, p, startingWage );//better way to do this?
			if( cook.person == p ){
				cook.state = RedlandCookState.Pending;
			}
			else cook = new MyCook( (RedlandCookRole) r, p, startingWage );
		}
		if( r instanceof RedlandCashierRole ){
			if( cashier == null ) cashier = new MyCashier( (RedlandCashierRole) r, p, startingWage );//better way to do this?
			if( cashier.person == p ){
				cashier.state = RedlandCashierState.Pending;
			}
			else cashier = new MyCashier( (RedlandCashierRole) r, p, startingWage );
		}
		peopleInTheBuilding.add( r );
		stateChanged();
	}

	public void msgOrderDelivered( Map<String,Integer> order, Market market, TruckDriverRole driver, float bill ){
		//TODO: finish
	}
	
	public void msgUpdateTime( int time ) {
		this.currentTime = time;
		if( currentTime == startTime ) state = RedlandRestaurantState.opening;
		if( currentTime == closeTime ) state = RedlandRestaurantState.closing;
		if( currentTime < startTime || currentTime > closeTime ){
			state = RedlandRestaurantState.closed;
			isOpen = false;
		}
		else{
			state = RedlandRestaurantState.open;
			isOpen = true;
		}
		stateChanged();
	}
	
	public void msgPersonHasLeft( Person p, Role r ){
		if( r instanceof RedlandCustomerRole ){
			for( MyCustomer customer : customers ){
				if( customer.person == p ){
					customer.state = RedlandCustomerState.Leaving;
					break;
				}
			}
		}
		if( r instanceof RedlandWaiterRole ){
			for( MyWaiter waiter : waiters ){
				if( waiter.person == p ){
					waiter.state = RedlandWaiterState.ShiftOver;
					break;
				}
			}
		}
		if( r instanceof RedlandCashierRole ){
			host.state = RedlandHostState.ShiftOver;
			if( balance > 1000 ){
				float cashToDeposit = this.balance - 1000;//anything over 1000
				host.person.AddTaskDepositEarnings( this, cashToDeposit );
				this.balance = 1000.0f;
				//keep track of bank account?
			}
		}
		if( r instanceof RedlandCookRole ){
			cook.state = RedlandCookState.ShiftOver;
		}
		if( r instanceof RedlandHostRole ){
			host.state = RedlandHostState.ShiftOver;
		}
		peopleInTheBuilding.remove( r );
		stateChanged();
	}
	
	
	
	
	/********** Scheduler **********/
	
	public boolean pickAndExecuteAnAction(){
		//Rules pertaining to restaurant
		if( state == RedlandRestaurantState.opening ){
			if( host.state == RedlandHostState.Working ){
				AskHostIfReady();
				state = RedlandRestaurantState.pending;
				return true;
			}
		}
		if( state == RedlandRestaurantState.closing ){
			TellHostToClose();
			//anything else?
			return true;
		}
		
		//Rules pertaining to host
		if( host.state == RedlandHostState.Pending ){
			PrepareHostForWork();
			return true;
		}
		
		//Rules pertaining to customers
		for( MyCustomer customer : customers ){
			if( customer.state == RedlandCustomerState.Pending ){
				TellHostNewCustomer( customer );
				return true;
			}
		}
		
		//Rules pertaining to waiters
		for( MyWaiter waiter : waiters ){
			if( waiter.state == RedlandWaiterState.Pending ){
				TellHostNewWaiter( waiter );
				return true;
			}
		}
		
		//Rules pertaining to cooks
		if( cook.state == RedlandCookState.Pending ){
			TellHostNewCook();
			return true;
		}
		
		//Rules pertaining to cashiers
		if( cashier.state == RedlandCashierState.Pending ){
			TellHostNewCashier();
			return true;
		}
		
		return false;
	}
	
	/********** Actions **********/
	
	private void TellHostNewCustomer( MyCustomer customer ){
		customer.state = RedlandCustomerState.Waiting;
		host.host.msgIWantFood( customer.customer );
	}
	
	private void TellHostNewWaiter( MyWaiter waiter ){
		waiter.state = RedlandWaiterState.Waiting;
		host.host.msgAddWaiter( waiter.waiter );
	}
	
	private void TellHostNewCook(){
		cook.state = RedlandCookState.Waiting;
		host.host.msgAddCook( cook.cook );
	}
	
	private void TellHostNewCashier(){
		cashier.state = RedlandCashierState.Waiting;
		host.host.msgAddCashier( cashier.cashier );
	}
	
	private void PrepareHostForWork(){
		//send list of waiters
		List<RedlandWaiterRole> theWaiters = new ArrayList<RedlandWaiterRole>();
		for( MyWaiter waiter : waiters ){
			theWaiters.add( waiter.waiter );
		}
		
		host.state = RedlandHostState.Working;
		host.host.msgGetReadyForWork( this, cook.cook, cashier.cashier, theWaiters );
	}
	
	private void TellHostToClose(){
		
	}
	
	private void AskHostIfReady(){
		
	}
	
	/********** Utilities **********/
	
	public String getLocation() {
		return this.location;
	}

	public String getName() {
		return this.name;
	}

	public int getStartTime() {
		return this.startTime;
	}

	public void setStartTime(int t) {
		this.startTime = t;
	}

	public int getCloseTime() {
		return this.closeTime;
	}

	public void setCloseTime(int t) {
		this.closeTime = t;
	}

	public List<Role> getPeopleInTheBuilding() {
		return this.peopleInTheBuilding;
	}
	
	public RedlandCashierRole getCashier(){
		return this.cashier.cashier;
	}
	
	public RedlandHostRole getHost(){
		return this.host.host;
	}
	
	public RedlandCookRole getCook(){
		return this.cook.cook;
	}
	
	private class MyCustomer{
		RedlandCustomerRole customer;
		Person person;
		float debt;
		RedlandCustomerState state;
		
		public MyCustomer( RedlandCustomerRole customer, Person person ){
			this.customer = customer;
			this.person = person;
			debt = 0.0f;
			state = RedlandCustomerState.Pending;
		}
	}
	
	private class MyWaiter{
		RedlandWaiterRole waiter;
		Person person;
		float wage;
		RedlandWaiterState state;
		
		public MyWaiter( RedlandWaiterRole waiter, Person person, float wage ){
			this.waiter = waiter;
			this.person = person;
			this.wage = wage;
			state = RedlandWaiterState.Pending;
		}
	}
	
	private class MyHost{
		RedlandHostRole host;
		Person person;
		float wage;
		RedlandHostState state;
		
		public MyHost( RedlandHostRole host, Person person, float wage ){
			this.host = host;
			this.person = person;
			this.wage = wage;
			state = RedlandHostState.Pending;
		}
	}
	
	private class MyCook{
		RedlandCookRole cook;
		Person person;
		float wage;
		RedlandCookState state;
		
		public MyCook( RedlandCookRole cook, Person person, float wage ){
			this.cook = cook;
			this.person = person;
			this.wage = wage;
			state = RedlandCookState.Pending;
		}
	}
		
	private class MyCashier{
		RedlandCashierRole cashier;
		Person person;
		float wage;
		RedlandCashierState state;
		
		public MyCashier( RedlandCashierRole cashier, Person person, float wage ){
			this.cashier = cashier;
			this.person = person;
			this.wage = wage;
			state = RedlandCashierState.Pending;
		}
	}
}
