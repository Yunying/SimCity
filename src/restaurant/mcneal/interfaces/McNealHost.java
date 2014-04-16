package restaurant.mcneal.interfaces;

import java.util.ArrayList;
import java.util.List;

import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.mcneal.roles.WaiterAgent;

public interface McNealHost {	
	public abstract List<McNealWaiter> getWaiterList();
	public abstract void msgIWantFood(McNealCustomer customer);
	public abstract void msgTableFree(Table t, McNealWaiter wa);
	public abstract void msgGoOnBreakPlease(); //(RestaurantPanel r);
	public abstract void msgOffBreak();
	public abstract void setRestaurant(
			McNealRestaurant mcNealRest);
	

}
