package restaurant.ji.interfaces;

import java.util.List;

import interfaces.Building;
import interfaces.Employee;
import restaurant.ji.JiMyTable;
import restaurant.ji.RevolvingStand;

public interface JiWaiter extends Employee{

	// from host
	public abstract void msgHeresANewCustomer(JiCustomer cust, int tableNum, int tableX, int tableY);
	public abstract void msgGoOnBreak();
	public abstract void msgNoBreak();
	
	// from customers
	public abstract void msgImReadyToOrder(JiCustomer cust);
	public abstract void msgHeresMyOrder(JiCustomer cust, String choice);
	public abstract void msgIWantMyCheck(JiCustomer customer);
	public abstract void msgDoneEatingAndLeaving(JiCustomer cust);
	
	// from cook
	public abstract void msgOrderIsReady(int tableNum);
	public abstract void msgOutOfThis(String choice);
	
	// from cashier
	public abstract void msgHereIsCheck(Float due, JiCustomer customer);
	
	// from the gui and animation
	public abstract void msgAtWaitingArea();
	public abstract void msgAtTable();
	public abstract void msgAtCook();
	public abstract void askToGoOnBreak();
	public abstract void finishBreak();
	
	// accessor
	public abstract String getName();
	public abstract void msgAtBuilding(Building building);
	public abstract void setCook(JiCook cook);
	public abstract void setCashier(JiCashier cashier);
	public abstract void setHost(JiHost host);
	public abstract List<JiMyTable> getMyTables();
	public abstract void setOrderStand(RevolvingStand stand);
	
}
