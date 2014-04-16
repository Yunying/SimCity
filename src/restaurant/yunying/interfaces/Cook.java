package restaurant.yunying.interfaces;

import market.interfaces.Market;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;
import global.test.mock.EventLog;


public interface Cook {

	void msgMarketOutOfStock(Market tuMarketRole, String choice,
			int storage);

	void msgHereIsAnOrder(Waiter tuWaiterRole, String hisChoice, int table);

	Cashier getCashier();

	void msgHereIsOrderedStorage(String choice, int amt);

	void msgAnimation();
	
	void msgHereIsAnOrder();

}
