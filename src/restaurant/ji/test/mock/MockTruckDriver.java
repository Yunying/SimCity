package restaurant.ji.test.mock;

import interfaces.Building;
import global.test.mock.LoggedEvent;
import global.test.mock.Mock;
import market.RestaurantOrder;
import market.interfaces.Market;
import market.interfaces.MarketManager;
import market.interfaces.TruckDriver;

public class MockTruckDriver extends Mock implements TruckDriver {

	@Override
	public void msgDeliverOrder(RestaurantOrder o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgMarketIsClosing() {
		// TODO Auto-generated method stub
		
	}
	
	public void msgHereIsBill(float bill, Building rest){
		log.add(new LoggedEvent("Received msgHereIsBill"));
	}

	@Override
	public void msgBeginWork(Market market, MarketManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoToWaitingPosition() {
		// TODO Auto-generated method stub
		
	}

}
