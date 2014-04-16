package restaurant.yunying.interfaces;

import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;
import global.test.mock.EventLog;


public interface Waiter {
	
	public void msgSitAtTable(Customer c, int t);
	
	public void msgImReadyToOrder(Customer c);
	
	public void msgHereIsMyChoice(Customer c,String choice);
	
	public void msgOrderIsReady(String choice,int table);
	
	public void msgLeavingTable(Customer cust);
	
	public void msgOutOfFood(int table);
	
	public void msgBreakReply(boolean br);
	
	public void msgBackToWork();
	
	public void msgImReadyForCheck(Customer c);
	
	public void msgHereIsCheck(double money, int table);

	public String getName();

	public boolean isOnBreak();

	public boolean isResting();

	public void offBreak();

	public void setOnBreak();

	public void msgAtTable();


}
