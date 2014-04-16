package restaurant.yunying.interfaces;

import market.interfaces.Market;
import market.interfaces.TruckDriver;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;
import global.test.mock.EventLog;


public interface Cashier {
	public abstract void msgHereIsPayment(Customer c, int table, double amount, double owe);
	
	public abstract void msgComputeBill(Waiter w, String s, int table);
	
	public abstract void msgHereIsBill(Market m, TruckDriver d, double price);
	
	abstract boolean pickAndExecuteAnAction();
	
}
