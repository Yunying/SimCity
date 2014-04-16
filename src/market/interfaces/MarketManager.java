package market.interfaces;

import interfaces.Person;
import java.util.List;
import market.Inventory;
import market.MarketAgent.MarketState;
import market.RestaurantOrder;

public interface MarketManager {

	/** Messages from MarketCustomer*/
	public abstract void msgReadyToPay( MarketCustomer customer, float bill, float cash );
	public abstract void msgIWantACar( MarketCustomer customer, float cash );
	public abstract void msgLeavingMarket( MarketCustomer customer );
	
	/** Messages from MarketEmployee*/
	public abstract void msgLeavingMarket( MarketEmployee employee );
	
	/** Messages from RestaurantAgent*/
	public abstract void msgBeginWorking( Market market, MarketState state, Inventory inventory );
	public abstract void msgNewCustomer( MarketCustomer customer, Person person, List<String> wantedItems, boolean wantsCar );
	public abstract void msgNewEmployee( MarketEmployee employee, Person person );
	public abstract void msgNewTruckDriver( TruckDriver driver, Person person );
	public abstract void msgNewRestaurantOrder( RestaurantOrder o );
	public abstract void msgOrderDelivered( RestaurantOrder o );
	public abstract void msgAskManagerIfReady();
	public abstract void msgBeginClosingMarket();
	
	/** Message from TruckDriver */
	public abstract void msgLeavingMarket( TruckDriver driver );
}
