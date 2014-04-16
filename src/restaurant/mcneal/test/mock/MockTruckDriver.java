package restaurant.mcneal.test.mock;

import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import interfaces.Building;
import market.RestaurantOrder;
import market.interfaces.Market;
import market.interfaces.MarketManager;
import market.interfaces.TruckDriver;

public class MockTruckDriver extends MockRole implements TruckDriver{

	public MockTruckDriver(String string) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgBeginWork(Market market, MarketManager manager) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void msgGoToWaitingPosition() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDeliverOrder(RestaurantOrder o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgMarketIsClosing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsBill(float bill, Building restaurant) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Here is Bill"));
	}

	

}
