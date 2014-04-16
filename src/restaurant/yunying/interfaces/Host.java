package restaurant.yunying.interfaces;

import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;
import global.test.mock.EventLog;


public interface Host {

	void msgImLeaving(Customer tuCustomerRole);

	void msgIWantFood(Customer tuCustomerRole);

	void msgBackToWork(Waiter tuWaiterRole);

	void msgWantToGoOnBreak(Waiter tuWaiterRole);

	void msgTableIsFree(Waiter tuWaiterRole, int table);

}
