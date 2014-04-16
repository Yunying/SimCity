package restaurant.mcneal.test.mock;

import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import restaurant.mcneal.interfaces.McNealCook;
import restaurant.mcneal.interfaces.McNealRestaurant;
import restaurant.mcneal.interfaces.McNealWaiter;
import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.mcneal.roles.Order;

public class MockCook extends MockRole implements McNealCook {

	@Override
	public void msgHereisAnOrder(McNealWaiter wa, String choice, Table t) {
		log.add(new LoggedEvent("Recieved an order"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFoodisDone(Order o) {
		log.add(new LoggedEvent("Food is finished"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgInventoryOut(String food) {
		log.add(new LoggedEvent("Recieved inventory out by Market"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHeresMoreFood() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Here More Food!"));
		
	}

	@Override
	public void setRestaurantAgent(McNealRestaurant building) {
		// TODO Auto-generated method stub
		
	}

}
