package restaurant.yunying.roles;

import agent.Agent;
import restaurant.yunying.Food;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;

import java.awt.Dimension;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Waiter Agent
 */

public class TuWaiterRole extends TuWaiterAgent{

	public TuWaiterRole() {
		super();
	}

	@Override
	protected void takeOrderToCook(MyCustomer c){
		print ("Take Order To Cook");
		c.state = CustomerState.waitingForFood;
//		DoTakeOrderToCook(c.hisChoice);
//		try {
//			//print("acquire");
//			atTable.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		cook.msgHereIsAnOrder(this, c.hisChoice,c.table);
		waiterState = WaiterState.available;
		stateChanged();
	}

	
}


