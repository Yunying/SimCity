package restaurant.redland.interfaces;

import interfaces.Person;
import restaurant.redland.UtilityClasses.Table;
import restaurant.redland.roles.RedlandCustomerRole;

//import restaurant.gui.Table;

public interface RedlandWaiter {

	public abstract void msgSitAtTable( RedlandCustomerRole customer, Table table, Person person );
	
	public abstract void msgImAtTable(RedlandCustomer cust);
	
	public abstract void msgImReadyToOrder(RedlandCustomer cust);
	
	public abstract void msgHereIsMyChoice(RedlandCustomer cust, String choice);
	
	public abstract void msgOrderIsReady(String choice, int table);
	
	public abstract void msgDoneEating(RedlandCustomer cust);

	public abstract void msgOutOfOrder( String choice, int table );
	
	public abstract void msgAtTable();
	
	public abstract void msgAtCook();
	
	public abstract void msgAskForBreak();
	
	public abstract void msgGoOnBreak();
	
	public abstract void msgHereIsBill( RedlandCashier cashier );

	public abstract void setCook(RedlandCook cook);

	public abstract void setCashier(RedlandCashier cashier);

	public abstract void setHost(RedlandHost hostAgent);
}
