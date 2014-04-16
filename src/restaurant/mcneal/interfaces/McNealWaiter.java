package restaurant.mcneal.interfaces;

import java.util.List;

import restaurant.mcneal.McNealFood;
import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.mcneal.roles.McNealWaiterRole;
import restaurant.mcneal.roles.WaiterAgent.myCustomer;



public interface McNealWaiter {
	
	public abstract void  msgComputedBill(double cost, Table t);
	public abstract int getXPos();
	
	public abstract Table getTable();
	public abstract void msgReadyToOrder(Table tableNum);
	public abstract void msgCantAffordAndLeave(Table tableNum);
	public abstract void msgHereIsmyChoice(String choice, Table tableNum);
	public abstract void msgDoneEatingandPaying(Table tableNum);
	public abstract void msgreadyforbill(String foodChoice, Table tableNum);
	public abstract void msgSitAtTable(McNealWaiter wa,
			McNealCustomer customer, Table table);
	public abstract void setwithCustomer(boolean b);
	public abstract void msgOutOFFood(McNealFood f, Table table);
	public abstract void msgOrderIsReady(McNealWaiter waiter,
			String stringChoice, Table table);
	public abstract void setOnBreak(boolean b);
	public abstract String getName();
	public abstract void msgBreakReply(boolean b);
	public abstract List<myCustomer> getWaitingCustomers();
	public abstract boolean onBreak();
	public abstract void setHost(McNealHost host);
	public abstract void setCook(McNealCook cook);
		
	

}
