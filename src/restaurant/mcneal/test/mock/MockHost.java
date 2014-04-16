package restaurant.mcneal.test.mock;

import java.util.List;

import restaurant.mcneal.interfaces.McNealCustomer;
import restaurant.mcneal.interfaces.McNealHost;
import restaurant.mcneal.interfaces.McNealRestaurant;
import restaurant.mcneal.interfaces.McNealWaiter;
import restaurant.mcneal.roles.McNealHostRole.Table;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;

public class MockHost extends MockRole implements McNealHost {

	@Override
	public List<McNealWaiter> getWaiterList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgIWantFood(McNealCustomer customer) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved I'm Hungry From Customer"));
	}

	@Override
	public void msgTableFree(Table t, McNealWaiter wa) {
		log.add(new LoggedEvent("Recieved that message is free"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoOnBreakPlease() {
		log.add(new LoggedEvent("Received go on break yet"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOffBreak() {
		log.add(new LoggedEvent("Recieved off break"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRestaurant(McNealRestaurant mcNealRest) {
		// TODO Auto-generated method stub
		
	}

}
