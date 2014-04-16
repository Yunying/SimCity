package restaurant.yunying;

import restaurant.yunying.interfaces.Waiter;

public class Order {
	public enum OrderState {none, pending, cooking, cooked};
	public Order(String c, Waiter waiter, int table){
		choice = c;
		w = waiter;
		tableNum=table;
	}
	public Waiter w;
	public String choice;
	public OrderState orderState = OrderState.none;
	public int tableNum;
}