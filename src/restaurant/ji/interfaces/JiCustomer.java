package restaurant.ji.interfaces;

import interfaces.Building;
import interfaces.Person;
import restaurant.ji.Menu;

public interface JiCustomer {
        
	// from restaurant
	public abstract void msgDirectingYouToHost(JiHost host);
	public abstract void msgWeAreClosed();
	
	// from host
    public abstract void msgTablesAreFull();
	
    // from waiter
    public abstract void msgFollowMe(JiWaiter w, Menu m, int x, int y);
	public abstract void msgWhatWouldYouLike();
	public abstract void msgWeAreOut(String choice);
	public abstract void msgHeresYourFood(String c, JiCashier cashier);
	public abstract void msgHereIsCheck(Float amount);
	
	// from cashier
	public abstract void msgHereIsChange(Float change);
	
	// from gui and animation
	public abstract void gotHungry();
	public abstract void msgAnimationFinishedGoToSeat();
	public abstract void msgAnimationFinishedLeaveRestaurant();
	
	//accessors
	public abstract String getName();
	public abstract void msgAtBuilding(Building destination);
	public abstract Person getPerson();


}