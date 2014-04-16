package restaurant.mcneal.test.mock;

import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import interfaces.Building;

import java.util.Map;

import restaurant.mcneal.McNealCheck;
import restaurant.mcneal.interfaces.McNealCashier;
import restaurant.mcneal.interfaces.McNealCustomer;
import restaurant.mcneal.interfaces.McNealRestaurant;

public class MockCashier extends MockRole implements McNealCashier {

	@Override
	public void msgBillForCustomer(String choice, McNealCustomer c) {
		log.add(new LoggedEvent("Recieved Bill for customer"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRestaurant(McNealRestaurant r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHeresmyPayment(McNealCheck check, int cash) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved payment"));
		
	}

	@Override
	public void msgHereIsChange(double change, McNealCheck c) {
		log.add(new LoggedEvent("Received change"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderDelivered(Map<String, Integer> order, Building market,
			float bill) {
		log.add(new LoggedEvent("Delivered Map"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDepositFunds() {
		log.add(new LoggedEvent("Receieved need to deposit funds"));
		// TODO Auto-generated method stub
		
	}

}
