package restaurant.yunying.test.mock;

import market.interfaces.Market;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import restaurant.yunying.interfaces.Cashier;
import restaurant.yunying.interfaces.Cook;
import restaurant.yunying.interfaces.Waiter;

public class MockCook extends MockRole implements Cook {
	Waiter w;
	String food;
	int table;

	public MockCook(String name) {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgMarketOutOfStock(Market m, String choice,
			int storage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgHereIsAnOrder(Waiter tuWaiterRole, String hisChoice,
			int table) {
		// TODO Auto-generated method stub

	}

	@Override
	public Cashier getCashier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgHereIsOrderedStorage(String choice, int amt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgHereIsAnOrder() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("here is an order from orderStand"));
	}

}
