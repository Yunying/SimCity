package restaurant.mcneal.test.mock;

import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import restaurant.mcneal.McNealCheck;
import restaurant.mcneal.interfaces.McNealCustomer;
import restaurant.mcneal.interfaces.McNealHost;
import restaurant.mcneal.interfaces.McNealWaiter;
import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.mcneal.roles.WaiterAgent.Menu;

public class MockCustomer extends MockRole implements McNealCustomer {

	public MockCustomer(String string) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHereIsChange(double change) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Here is your change"));
		
	}

	@Override
	public void msgNotEnoughMoney() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved not enough Money from Cashier"));
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table getTableNum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public McNealWaiter getWaiter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWaiter(McNealWaiter w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFoodChoice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgHereIsBill(McNealCheck check) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Here is Bill from waiter"));
	}

	@Override
	public void msgWhatWouldYouLike() {
		log.add(new LoggedEvent("Recieved what would you like"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFollowMe(Table table, McNealWaiter waiterRole, Menu menu) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved Follow Me by Waiter"));
		
	}

	@Override
	public void msgHereIsYourFood() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Here is your food"));
	}

	@Override
	public void msgTableFull() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Msg Table is full"));
	}

	@Override
	public void msgMeetHost(McNealHost host) {
		log.add(new LoggedEvent("Meet Your Host"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWhatWouldYouLikeToo(Menu g) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("What Would You Like To Eat"));
		
	}

	@Override
	public void setHost(McNealHost host) {
		// TODO Auto-generated method stub
		
	}

}
