package restaurant.ji.test.mock;

import market.interfaces.TruckDriver;
import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import restaurant.ji.interfaces.*;

public class MockCashier extends MockRole implements JiCashier {

	public EventLog log = new EventLog();
	
	@Override
	public void msgStopWorkingGoHome() {
		log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		log.add(new LoggedEvent("Received msgHeresYourPaycheck"));
	}

	@Override
	public boolean hasReceivedPaycheck() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void msgComputeBill(String choice, JiCustomer customer,
			JiWaiter waiter) {
		log.add(new LoggedEvent("Received msgComputeBill"));
	}

	@Override
	public void msgPayingCheck(float money, JiCustomer customer) {
		log.add(new LoggedEvent("Received msgPayingCheck"));
	}

	@Override
	public void msgICantPay(JiCustomer customer) {
		log.add(new LoggedEvent("Received msgICantPay"));
	}

	@Override
	public void msgPayMarketBill(float owed, TruckDriver driver) {
		log.add(new LoggedEvent("Received msgPayMarketBill"));
	}

	@Override
	public void msgDepositExcessFunds(float f) {
		log.add(new LoggedEvent("Received msgDepositExcessFunds"));
	}

}
