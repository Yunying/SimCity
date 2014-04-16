package restaurant.ji.test.mock;

import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import restaurant.ji.interfaces.JiCustomer;
import restaurant.ji.interfaces.JiHost;
import restaurant.ji.interfaces.JiWaiter;

public class MockHost extends MockRole implements JiHost{
	public EventLog log = new EventLog();
	
	@Override
	public void msgStopWorkingGoHome() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasReceivedPaycheck() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void msgCanIGoOnBreak(JiWaiter waiter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBackFromBreak(JiWaiter waiter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTableIsFree(int tableNum) {
		log.add(new LoggedEvent("Received msgTableIsFree"));
	}

	@Override
	public void msgTableForOne(JiCustomer cust) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIDontWantToWait(JiCustomer cust) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIWillWait(JiCustomer cust) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addWaiter(JiWaiter r) {
		// TODO Auto-generated method stub
		
	}

}
