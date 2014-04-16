package restaurant.ji.interfaces;

import interfaces.Building;
import interfaces.Employee;

public interface JiHost extends Employee{

	// from restaurant
//	public abstract void msgGreetCustomer(JiCustomer cust);
	
	// from waiters
	public abstract void msgCanIGoOnBreak(JiWaiter waiter);
	public abstract void msgBackFromBreak(JiWaiter waiter);
	public abstract void msgTableIsFree(int tableNum);
	
	// from customers
	public abstract void msgTableForOne(JiCustomer cust);
	public abstract void msgIDontWantToWait(JiCustomer cust);
	public abstract void msgIWillWait(JiCustomer cust);

	// from gui
	public abstract void msgAtBuilding(Building rest);
	public abstract void addWaiter(JiWaiter r);
	
}