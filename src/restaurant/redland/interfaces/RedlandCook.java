package restaurant.redland.interfaces;

//import restaurant.gui.Order;

//TODO: put in restaurant order
public interface RedlandCook {

	public abstract void msgHereIsOrder(RedlandWaiter w, String choice, int table);
	
	public abstract void msgGotOrder(RedlandWaiter w, String choice, int table);
	
	//public abstract void msgFoodIsDone(Order o);
	
	public abstract void msgHereAreSupplies( String food, int amount, float price );
	
	public abstract void msgOutOfOrder( String food );;

	//public abstract void setHost(RedlandHostAgent hostAgent);

	public abstract void setCashier(RedlandCashier cashier);
}
