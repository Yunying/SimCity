package restaurant.mcneal.interfaces;



import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.mcneal.roles.Order;

public interface McNealCook {
	public abstract void msgHereisAnOrder(McNealWaiter wa, String choice, Table t);
	public abstract void msgFoodisDone(Order o);
	public abstract void msgInventoryOut(String food);
	public abstract void msgHeresMoreFood();
	public abstract void setRestaurantAgent(McNealRestaurant building);

}
