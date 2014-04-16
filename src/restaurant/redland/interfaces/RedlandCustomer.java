package restaurant.redland.interfaces;

import java.util.ArrayList;

//import restaurant.redland.CashierAgent;
//import restaurant.redland.CustomerAgent.AgentEvent;


public interface RedlandCustomer {
	
	public abstract void gotHungry();
	
	public abstract void msgFollowMeToTable( RedlandWaiter waiter, int table, ArrayList<String> menu);

	public abstract void msgWhatWouldYouLike();
	
	public abstract void msgHereIsYourFood();
	
	public abstract void msgAnimationFinishedGoToSeat();
	
	public abstract void msgAnimationFinishedLeaveRestaurant();
	
	public abstract void msgHereIsYourBill( RedlandCashier cashier );
	
	public abstract void msgHereIsYourChange( float change );
	
	//Non-normative scenario
	public abstract void msgReOrder( String lastOrder );
}