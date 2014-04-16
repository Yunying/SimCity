package restaurant.ji.interfaces;

import java.util.List;

import market.interfaces.Market;
import global.test.mock.MockPerson;
import interfaces.Building;
import interfaces.Employee;
import interfaces.Person;
import restaurant.ji.CustomerOrder;
import restaurant.ji.Food;
import restaurant.ji.RevolvingStand;
import restaurant.ji.roles.JiCookRole.State;

public interface JiCook extends Employee{
	
	// from waiters and revolving stands
	public abstract void msgCookThisOrder(String choice, JiWaiter w, int tableNum);
	public abstract void msgNextOrderFromStand(CustomerOrder customerOrder);
	
	// from markets -- remove/refactor once new Market is up
	public abstract void msgCanOnlyDeliverPartial(Market market, Food food, int amountDeliverable);
	public abstract void msgMarketOutOfOrderedItem(Food food, Market market);
	
	// from restaurant
	public abstract void msgOrderCameIn(String f, int amount);
	
	// from the gui
	public abstract void msgAtFridge();
	public abstract void msgAtPlating();
	public abstract void msgAtStove();
	public abstract void msgAtBuilding(Building building);
	public abstract void setOrderStand(RevolvingStand orderstand);
	public abstract String getName();
	public abstract void setPerson(Person person);
	public abstract boolean isWorking();
	public abstract State getState();
	public abstract Person getPerson();
	public abstract List<CustomerOrder> getIncomingOrders();
	public abstract RevolvingStand getOrderStand();

}
