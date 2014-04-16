package restaurant.yunying.roles;

import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;

import java.util.ArrayList;
import java.util.List;

import restaurant.yunying.interfaces.Cook;
import restaurant.yunying.interfaces.Waiter;
import restaurant.yunying.Order;

public class OrderStand extends Object{
	private List<Order> orders;
	private Cook cook;
	public EventLog log = new EventLog();
	
	public void setCook(Cook cook){
		this.cook = cook;
	}
	
	public OrderStand(){
		orders = new ArrayList<Order>();
	}
	
	synchronized public void add(Waiter w, String food, int table){
		log.add(new LoggedEvent("add an order to the order stand"));
		Order o = new Order(food, w, table);
		cook.msgHereIsAnOrder();
		orders.add(o);
	}
	
	public synchronized void remove(Order o){
		orders.remove(o);
		log.add(new LoggedEvent("remove order"));
	}
	
	synchronized public Order getOrder(){
		log.add(new LoggedEvent("Cook is getting order from order stand"));
		return orders.get(0);
	}
	
	synchronized public int getSize(){
		return orders.size();
	}
	
	synchronized public List<Order> getOrders(){
		return orders;
	}
	
	/**************For test use**************/
	public Cook getCook(){
		return cook;
	}
	
	
}
