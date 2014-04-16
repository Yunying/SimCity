package restaurant.cammarano.interfaces;

import java.util.HashMap;

import restaurant.cammarano.roles.CammaranoHostRole.Table;

public interface CammaranoCustomer {
	public abstract void msgIAmReadyForYouToPay();
	public abstract void msgHereIsYourCheck();
	public abstract void msgPaymentReceived();
	public abstract void msgHereIsYourChange(float change);
	public abstract void msgHereIsYourFood(String choice);
	public abstract void msgYouDidNotPayEnough();
	public abstract void msgYouHaveToReorder(String choice);
	public abstract void msgWhatDoYouWant();
	public abstract void msgSitAtTable(CammaranoWaiter waiterAgent, Table table, HashMap<String, Float> menu);
	public abstract void msgRestaurantIsFull();

	public abstract String getName();
}