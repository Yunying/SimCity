package market.interfaces;

import java.util.List;

public interface MarketCustomer {

	/** Message from Market */
	public abstract void msgGoToShoppingArea( MarketManager manager );
	
	/** Messages from MarketManager */
	public abstract void msgWelcomeToMarket( MarketManager manager, float carBill );
	public abstract void msgHereIsYourChange( float change );
	
	/** Messages from MarketEmployee */
	public abstract void msgWhatDoYouWant( MarketEmployee employee );
	public abstract void msgHereAreYourItemsAndBill( MarketEmployee employee, List<String> items, float bill );
}
