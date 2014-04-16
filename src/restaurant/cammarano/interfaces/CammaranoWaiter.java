package restaurant.cammarano.interfaces;

import java.util.HashMap;

import restaurant.cammarano.roles.CammaranoHostRole.Table;

public interface CammaranoWaiter {
	public abstract void msgHereIsTheCheck(CammaranoCustomer c);
	public abstract void msgOutOfFood(String choice, Table table);
	public abstract void msgOrderDone(String choice, Table table);
	public abstract void msgHereIsMyChoice(CammaranoCustomer customerAgent, String choice);
	public abstract void msgSitCustomer(CammaranoCustomer customer, Table table, HashMap<String, Float> menu);
	public abstract void msgReadyToOrder(CammaranoCustomer customerAgent);
	public abstract void msgLeavingTable(CammaranoCustomer customerAgent);
	public abstract void msgReadyToPay(CammaranoCustomer customerAgent);
	public abstract void msgBreakDenied();
	public abstract void msgBreakApproved();
	
	public abstract String getName();
}
