package restaurant.ji;

import global.test.mock.LoggedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import restaurant.ji.interfaces.JiCook;
import restaurant.ji.interfaces.JiWaiter;
import restaurant.ji.roles.JiCashierRole.Check;

public class RevolvingStand {
	private static List<CustomerOrder> orders;
	private JiCook cook;
	
	public RevolvingStand(){
		orders = Collections.synchronizedList(new ArrayList<CustomerOrder>());
	};
	
	public void addOrder(String choice, JiWaiter waiter, int tableNum){
		CustomerOrder o = new CustomerOrder(choice, waiter, tableNum);
//		if (cook != null){
//			cook.msgNextOrderFromStand(o);
//		}
//		else{
			synchronized(orders){
				orders.add(o);
//			}
		}
	};
	
	public void getOrder(JiCook cook){
		synchronized(orders){
			Iterator<CustomerOrder> i = orders.iterator();
			if (i.hasNext()){
				cook.msgNextOrderFromStand(i.next());
				i.remove();
			}
		}
	}
	
	public List<CustomerOrder> getOrders(){
		return orders;
	}
	
	public CustomerOrder get(int i){
		try{ return orders.get(i); }
		catch(IndexOutOfBoundsException e){ return null;}
	}
	public boolean isEmpty(){
		return orders.isEmpty();
	}
	public void setCook(JiCook c){
		this.cook = c;
	}
}
