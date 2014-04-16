package restaurant.mcneal.interfaces;


import interfaces.Building;

import java.util.Map;

import restaurant.mcneal.McNealCheck;


public interface McNealCashier {
	
	
	public abstract void msgBillForCustomer(String choice, McNealCustomer c);
	public abstract void setRestaurant(McNealRestaurant r);
	public abstract void msgHeresmyPayment(McNealCheck check, int cash);
	
	public abstract void msgHereIsChange(double change, McNealCheck c);
	public abstract void msgOrderDelivered(Map<String, Integer> order, Building market, float bill);
	public abstract void msgDepositFunds();
		
	
}
