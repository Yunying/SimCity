package market.interfaces;

import interfaces.Building;
import market.RestaurantOrder;

public interface TruckDriver {

	/** Messages from MarketManager */
	public abstract void msgBeginWork( Market market, MarketManager manager );
	public abstract void msgGoToWaitingPosition();
	public abstract void msgDeliverOrder( RestaurantOrder o );
	public abstract void msgMarketIsClosing();
	
	/** Messages from Restaurant */
	public abstract void msgHereIsBill( float bill, Building restaurant );
}
