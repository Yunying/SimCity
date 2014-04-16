package restaurant.redland.UtilityClasses;

import restaurant.redland.interfaces.RedlandWaiter;

public class Order {
	public enum CookingState {pending, cooking, doneCooking, done, recievedByWaiter};
	RedlandWaiter waiter;
	String choice;
	int table;
	CookingState s;
	
	public Order( RedlandWaiter w, String newChoice, int table){
		this.waiter = w;
		choice = newChoice;
		this.table = table;
		this.s = CookingState.pending;
	}
	
	public void setState(CookingState s){
		this.s = s;
	}
	
	public CookingState getState(){
		return this.s;
	}
	
	public RedlandWaiter getWaiter(){
		return this.waiter;
	}
	
	public void setWaiter( RedlandWaiter waiter ){
		this.waiter = waiter;
	}

	public String getChoice(){
		return this.choice;
	}
	
	public int getTable(){
		return this.table;
	}
}
