package restaurant.yunying.test.mock;

import global.test.mock.*;
import global.test.mock.MockRole;
import restaurant.yunying.interfaces.Customer;
import restaurant.yunying.interfaces.Host;
import restaurant.yunying.interfaces.Waiter;

public class MockHost extends MockRole implements Host {
	String name;
	public EventLog log = new EventLog();

	public MockHost(String name) {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgImLeaving(Customer tuCustomerRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgIWantFood(Customer tuCustomerRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgBackToWork(Waiter tuWaiterRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgWantToGoOnBreak(Waiter tuWaiterRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgTableIsFree(Waiter tuWaiterRole, int table) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("table is free"));
	}

}
