package market.interfaces;

import java.util.Map;

import market.MarketAgent.MarketState;
import market.RestaurantOrder;
import global.roles.Role;
import interfaces.Building;
import interfaces.Person;

public interface Market extends Building {

	/** Messages from Restaurants */
	public abstract void msgHereIsOrder( Map<String, Integer> order, Building restaurant );
	
	/** Messages from Timer */
	
	/** Messages from TruckDriver */
	public abstract void msgOrderDelivered( TruckDriver driver, RestaurantOrder o );
	
	/** Messages from MarketManager */
	public abstract void msgHereIsMarketState( MarketState state );
	public abstract void msgMarketOpen();
	public abstract void msgMarketClosed();
	public abstract void msgUpdateBalance( float bill );
	public abstract void msgPersonHasLeft( Person person, Role r );
}
