package restaurant.yunying.test;

import global.PersonAgent;
import restaurant.yunying.Order;
import restaurant.yunying.roles.OrderStand;
import restaurant.yunying.test.mock.MockCook;
import restaurant.yunying.test.mock.MockWaiter;
import junit.framework.TestCase;

public class OrderStandTest extends TestCase{
	OrderStand stand;
	MockCook cook;
	MockWaiter waiter;
	PersonAgent p;
	
	public void setUp(){
		stand = new OrderStand();
		cook = new MockCook("cook");
		waiter = new MockWaiter("waiter");
		stand.setCook(cook);
		
		stand.log.clear();
		cook.log.clear();
		waiter.log.clear();
		
	}
	
	public void testOrderStandTest(){
		//Check pre conditions
		assertEquals("The cook should be set up correctly", 
				stand.getCook(), cook);
		assertEquals("There should be no orders in the order list", 
				0, stand.getOrders().size());
		assertEquals("There should be no events in the cook's log", 
				0, cook.log.size());
		assertEquals("There should be no events in the waiter's log", 
				0, waiter.log.size());
		assertEquals("There should be no events in the stand's log", 
				0, stand.log.size());
		
		//Waiter put an order into order stand
		stand.add(waiter, "Brownie", 1);
		assertEquals("There should be one order in the order stand", 
				1, stand.getOrders().size());
		assertEquals("There should be one event in the stand's log", 
				1, stand.log.size());
		assertEquals("There should be one event in the cook's log now",
				1, cook.log.size());
		
		//Cook comes to get order
		Order o = stand.getOrder();
		assertEquals("There should be two events in the stand's log", 
				2, stand.log.size());
		assertEquals("The order should have the correct food name", 
				"Brownie", o.choice);
		assertEquals("The order should have the correct waiter", 
				waiter, o.w);
		assertEquals("The order should have the correct table number", 
				1, o.tableNum);
		
		//Cook remove order from order stands
		stand.remove(o);
		assertEquals("There should be no orders in the order stand now", 
				0, stand.getOrders().size());
		assertEquals("There should be three events in the stand's log", 
				3, stand.log.size());
		
		
		
	}
}
