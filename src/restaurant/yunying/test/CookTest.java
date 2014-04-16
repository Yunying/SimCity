package restaurant.yunying.test;

import global.PersonAgent;
import restaurant.yunying.roles.OrderStand;
import restaurant.yunying.roles.TuCookRole;
import restaurant.yunying.test.mock.MockWaiter;
import junit.framework.TestCase;

public class CookTest extends TestCase{
	PersonAgent p;
	TuCookRole cook;
	MockWaiter waiter;
	OrderStand stand;
	
	public void setUp(){
		p = new PersonAgent("person", null, null);
		cook = new TuCookRole();
		cook.setPerson(p);
		waiter = new MockWaiter("waiter");
		stand = new OrderStand();
		cook.setOrderStand(stand);
		stand.setCook(cook);
		
		waiter.log.clear();
		cook.log.clear();
		stand.log.clear();
		
	}
	
	public void testWithShareDataWaiter(){
		//Check pre conditions
		assertEquals("The order stand should be set up correctly", 
				stand, cook.getOrderStand());
		assertEquals("The boolean os should be set as false now", 
				false, cook.getOs());
		assertEquals("There should be nothing in the orders list", 
				0, cook.getOrders().size());
		
		//When an order is added to the order stand
		stand.add(waiter, "Brownie", 1);
		assertEquals("There should be one order in the orders of order stand",
				1, stand.getOrders().size());
		assertEquals("There should be one event in the stand's log",
				1, stand.log.size());
		assertEquals("There should be one event in the cook's log",
				1,cook.log.size());
		assertTrue("The cook's os should be set to true", 
				cook.getOs());
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
				cook.pickAndExecuteAnAction());	
		assertFalse("The cook's os should be set to false",
				cook.getOs());
		assertEquals("There should be two events in the stand's log",
				2,stand.log.size());
		assertEquals("There should be no order in the order stand now",
				0,stand.getOrders().size());
		assertEquals("There should be one order in the cook's orders list",
				1,cook.getOrders().size());
		assertEquals("The cook should have the correct order. The name should be brownie", 
				"Brownie", cook.getOrders().get(0).choice);
		assertEquals("The cook should have the correct order. The waiter should be set correctly", 
				waiter, cook.getOrders().get(0).w);
		assertEquals("The cook should have the correct order. The table number should be 1",
				1,cook.getOrders().get(0).tableNum);
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
				cook.pickAndExecuteAnAction());	
		assertEquals("The order state should be cooking", "cooking", cook.getOrders().get(0).orderState.name());
		
		//Check post conditions
		assertFalse("Cashier's scheduler should have returned false, but didn't.", 
				cook.pickAndExecuteAnAction());	
	
		
		
	}
}
