package restaurant.ji.interfaces;

import java.util.List;
import java.util.Map;

import restaurant.ji.JiRestaurantAgent.Table;
import restaurant.ji.RevolvingStand;
import market.interfaces.TruckDriver;
import bank.interfaces.Bank;
import global.roles.Role;
import interfaces.Building;

public interface JiRestaurant extends Building {

	// in building: public abstract void msgAtLocation(Person p, Role r, List<Action> actions);
	
	// from waiters, cooks, cashiers, and hosts
	public abstract void msgLeavingWork(Role employee);
	
	// from truck drivers
	public abstract void msgOrderDelivered(Map<String, Integer> order, Building market, TruckDriver driver, float bill);
	
	// from cashier
	public abstract void msgPaidBillToMarket(float expense);
	public abstract void msgReceivedPaymentFromCustomer(float income);
	
	// accessors
	public abstract Bank getBank();
	public abstract int getBankAccount();
	public abstract float getCurrentAssets();
	
	public abstract List<JiWaiter> getWaiters();
	public abstract JiCook getCook();
	public abstract JiHost getHost();
	public abstract JiCashier getCashier();
	public abstract RevolvingStand getOrderStand();
	public abstract void msgLeavingAsCustomer(JiCustomer customer);
	public abstract List<Table> getTables();
}
