package restaurant.cammarano.interfaces;

import restaurant.cammarano.roles.CammaranoHostRole.Table;

public interface CammaranoCook {

	public abstract void msgHereIsTheOrder(CammaranoWaiter w, Table t, String choice);
	public abstract void msgOrderOnTheStand();
	//public void msgHereIsTheFoodYouNeed(String f, int a);
	//public void msgHereIsTheFoodYouNeedAndWeAreOut(String f, int a, Market m);
	//public void msgWeCanNotFullfillTheOrder(String f, Market m);
	public abstract void msgAtDestination();
	
	public abstract String getName();
}
