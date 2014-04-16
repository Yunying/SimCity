package market.interfaces;

import interfaces.Person;

import java.util.List;

import market.Inventory;

public interface MarketEmployee {

	/** Messages from MarketManager */
	public abstract void msgBeginWorking( MarketManager manager, Inventory inventory );
	public abstract void msgHelpThisCustomer( MarketCustomer customer, Person person );
	public abstract void msgMarketIsClosing();
	
	/** Messages from MarketCustomer */
	public abstract void msgIWantTheseItems( MarketCustomer cust, List<String> items );
	public abstract void setManager(MarketManager manager);
	
}
