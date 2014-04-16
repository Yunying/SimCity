package restaurant.ji.interfaces;

import market.interfaces.TruckDriver;
import interfaces.Building;
import interfaces.Employee;

public interface JiCashier extends Employee{

	// from waiters
	public abstract void msgComputeBill(String choice, JiCustomer customer, JiWaiter waiter);
	
	// from customers
	public abstract void msgPayingCheck(float money, JiCustomer customer);
	public abstract void msgICantPay(JiCustomer customer);
	
	// from restaurant
	public abstract void msgPayMarketBill(float owed, TruckDriver driver);
	public abstract void msgDepositExcessFunds(float f);

	public abstract void msgAtBuilding(Building building);
}
