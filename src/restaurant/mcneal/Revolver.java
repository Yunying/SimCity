package restaurant.mcneal;

import java.util.ArrayList;
import java.util.List;

import restaurant.mcneal.interfaces.McNealWaiter;
import restaurant.mcneal.roles.McNealCookRole;
import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.mcneal.roles.Order;

public class Revolver extends Object {
	
	
	private McNealCookRole cook;
	private List<Order> orders;
	public void setCook(McNealCookRole cook){
		this.cook = cook;
	}
	
	public Revolver(){
		orders = new ArrayList<Order>();
	}
	
	synchronized public void add(McNealWaiter w, String food,Table table){
		orders.add(new Order(w, food,  table));
		 cook.msgHereisAnOrder(w, food,  table);
		
	}
	
	synchronized void remove(Order o){
		orders.remove(o);
	}
	
	synchronized public Order getOrder(){
		return orders.get(0);
	}
	
	synchronized public int getSize(){
		return orders.size();
	}

}
