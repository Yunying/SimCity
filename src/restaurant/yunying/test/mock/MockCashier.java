package restaurant.yunying.test.mock;

import market.interfaces.Market;
import market.interfaces.TruckDriver;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import restaurant.yunying.interfaces.Cashier;
import restaurant.yunying.interfaces.Customer;
import restaurant.yunying.interfaces.Waiter;

public class MockCashier extends MockRole implements Cashier {
	String name;

	public MockCashier(String name) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	@Override
	public void msgHereIsPayment(Customer c, int table, double amount,
			double owe) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgComputeBill(Waiter w, String s, int table) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("msg compute bill"));
	}

	@Override
	public void msgHereIsBill(Market m, TruckDriver d, double price) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

}
