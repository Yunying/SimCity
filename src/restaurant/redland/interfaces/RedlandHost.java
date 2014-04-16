package restaurant.redland.interfaces;

import java.util.List;

import restaurant.redland.RedlandRestaurantAgent;
import restaurant.redland.roles.RedlandCashierRole;
import restaurant.redland.roles.RedlandCookRole;
import restaurant.redland.roles.RedlandCustomerRole;
import restaurant.redland.roles.RedlandWaiterRole;

public interface RedlandHost {

	public abstract void msgGetReadyForWork( RedlandRestaurantAgent restaurant, RedlandCookRole cook, RedlandCashierRole cashier, List<RedlandWaiterRole> theWaiters );
	
	public abstract void msgIWantFood(RedlandCustomerRole cust);
	
	public abstract void msgTableIsFree(int tableNum, RedlandWaiter waiter);

	public abstract void msgAddWaiter( RedlandWaiterRole waiter);
	
	public abstract void msgAddCook( RedlandCookRole cook);
	
	public abstract void msgAddCashier( RedlandCashierRole cashier );
	
	public abstract void msgIWantBreak(RedlandWaiterRole wait);
	
	public abstract void msgImOnBreak( RedlandWaiter wait );
	
	public abstract void msgImOffBreak( RedlandWaiter wait );
}
