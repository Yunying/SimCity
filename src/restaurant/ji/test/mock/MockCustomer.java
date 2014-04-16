package restaurant.ji.test.mock;


import restaurant.ji.Menu;
import global.test.mock.*;
import restaurant.ji.interfaces.JiCashier;
import restaurant.ji.interfaces.JiCustomer;
import restaurant.ji.interfaces.JiHost;
import restaurant.ji.interfaces.JiWaiter;

public class MockCustomer extends MockRole implements JiCustomer {
	
	public EventLog log = new EventLog();

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public JiCashier cashier;

	public MockCustomer() {
		super();

	}

	@Override
	public void msgTablesAreFull() {
		log.add(new LoggedEvent("Received msgTablesAreFull from host"));
	}

	@Override
	public void msgFollowMe(JiWaiter w, Menu m, int x, int y) {
		log.add(new LoggedEvent("Received msgFollowMe from waiter, going to: " + x + ", " + y));
		
	}

	@Override
	public void msgWhatWouldYouLike() {
		log.add(new LoggedEvent("Received msgWhatWouldYouLike from waiter"));
		
	}

	@Override
	public void msgWeAreOut(String choice) {
		log.add(new LoggedEvent("Received msgWeAreOut from waiter. Out of: " + choice));
		
	}

	@Override
	public void msgHeresYourFood(String c, JiCashier cashier) {
		log.add(new LoggedEvent("Received msgHeresYourFood from waiter. Got: " + c));
		
	}

	@Override
	public void msgHereIsCheck(Float amount) {
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Total = " + amount));
		
	}

	@Override
	public void msgHereIsChange(Float change) {
		log.add(new LoggedEvent("Received msgHereIsChange from cashier. Change = " + change));
		
	}

	@Override
	public void msgAnimationFinishedGoToSeat() {
		log.add(new LoggedEvent("Received msgAnimationFinishedGoToSeat from customergui"));
	}

	@Override
	public void msgAnimationFinishedLeaveRestaurant() {
		log.add(new LoggedEvent("Received msgAnimationFinishedLeaveRestaurant from customergui"));
		
	}
	
	public String getName(){
		return null;
	}

	@Override
	public void msgDirectingYouToHost(JiHost host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWeAreClosed() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void gotHungry() {
		// TODO Auto-generated method stub
		
	}

}
