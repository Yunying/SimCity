package restaurant.yunying.test.mock;


import java.util.List;

import restaurant.yunying.Food;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import global.test.mock.*;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends MockRole implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	String name;

	public MockCustomer(String name) {
		super();
		this.name=name;

	}
	
	public double change = 0;

	@Override
	public void msgHereIsChange(double total) {
		change += total;
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ total));
	}

	@Override
	public void msgYouNeedMoreMoney(double remaining_cost) {
		change += remaining_cost;
		log.add(new LoggedEvent("Received You Owe Us from cashier. Debt = "+ remaining_cost));
	}

	@Override
	public void msgHereIsYourNumber(int num) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTablesAreFull() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWhatWouldYouLike() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("what would you like"));
	}

	@Override
	public void msgHereIsYourFood(String choice) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("here is your food"));
	}

	@Override
	public void msgPleaseReorder(String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsCheck(double amount) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Receive check " + amount));
	}

	@Override
	public int getWaitingNumber() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void msgAnimationFinishedGoToSeat() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doneWorking() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotHungry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CustomerGui getGui() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgAnimationFinishedAdjusting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationFinishedGoToRestaurant() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationFinishedGoToCashier() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationFinishedLeaveRestaurant() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgFollowMeToTable(Waiter tuWaiterRole, int table,
			List<Food> menu) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("follow waiter to table"));
	}



}
