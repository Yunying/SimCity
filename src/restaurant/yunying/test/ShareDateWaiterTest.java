package restaurant.yunying.test;


import global.PersonAgent;
import restaurant.yunying.gui.CashierGui;
import restaurant.yunying.roles.OrderStand;
import restaurant.yunying.roles.TuCashierRole;
import restaurant.yunying.roles.TuShareDataWaiterRole;
import restaurant.yunying.roles.TuWaiterAgent.MyCustomer;
import restaurant.yunying.test.mock.MockCashier;
import restaurant.yunying.test.mock.MockCook;
import restaurant.yunying.test.mock.MockCustomer;
import restaurant.yunying.test.mock.MockHost;
import restaurant.yunying.test.mock.MockMarket;
import restaurant.yunying.test.mock.MockTruckDriver;
import restaurant.yunying.test.mock.MockWaiter;
import junit.framework.TestCase;

public class ShareDateWaiterTest extends TestCase{
	TuShareDataWaiterRole waiter;
	PersonAgent p;
	OrderStand stand;
	MockCook cook;
	MockCustomer customer;
	MockHost host;
	MockCashier cashier;
	
	
	public void setUp() throws Exception{
		p = new PersonAgent("person", null, null);
		waiter = new TuShareDataWaiterRole();
		waiter.setPerson(p);
		stand = new OrderStand();
		cook = new MockCook("cook");
		stand.setCook(cook);
		customer = new MockCustomer("customer");
		host = new MockHost("host");
		cashier = new MockCashier("cashier");
		waiter.setOrderStand(stand);
		waiter.setCook(cook);
		waiter.setCashier(cashier);
		waiter.setHost(host);
		
		waiter.log.clear();
		host.log.clear();
		cook.log.clear();
		cashier.log.clear();
		stand.log.clear();
	}
	
	public void testOrdinaryWorkingScenarioIncludingWaiterAgent(){
		//Check pre conditions
		assertEquals("The waiter should have no customers in the list now", 
				0, waiter.getCustomers().size());
		assertEquals("The order stand should be set correctly", 
				stand, waiter.getOrderStand());
		assertEquals("The waiter should have set host correctly", 
				host, waiter.getHost());
		assertEquals("The waiter should have set cook correctly", 
				cook, waiter.getCook());
		assertEquals("The waiter should have set cashier correctly", 
				cashier, waiter.getCashier());
		
		//Start by being assigned a customer
		waiter.msgSitAtTable(customer, 1);
		assertEquals("There should be one event in the waiter's log", 
				1, waiter.log.size());
		assertEquals("There should be one customer in the waiter's customer list", 
				1, waiter.getCustomers().size());
		assertEquals("The waiter state should be available at this time", "available",
				waiter.waiterState.name());
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());	
		assertEquals("There should be one event in customer's log saying follow waiter to table,",
				1, customer.log.size());
		assertFalse("Waiter's scheduler should have returned false, but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertEquals("The waiter state should be available at this time", 
				"available", waiter.waiterState.name());
		
		//Customer wants to order
		waiter.msgImReadyToOrder(customer);
		assertEquals("There should be two events in the waiter's log now", 
				2, waiter.log.size());
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());
		assertEquals("There should be two events in the customer's log now", 
				2, customer.log.size());
		assertEquals("The waiter state should be available at this time", 
				"available", waiter.waiterState.name());
		
		//Customer orders and put the order in the order stand
		waiter.msgHereIsMyChoice(customer, "Brownie");
		assertEquals("There should be three events in the waiter's log now", 
				3, waiter.log.size());
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());
		assertEquals("There should be an event in the orderStand's log", 
				1, stand.log.size());
		assertEquals("There should be one event in the cook's log", 
				1, cook.log.size());
		
		//Cook calls and go get order for customer
		waiter.msgOrderIsReady("Brownie", 1);
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());
		assertEquals("There should be three events in the customer's log", 
				3, customer.log.size());
		
		//Customer is ready for check
		waiter.msgImReadyForCheck(customer);
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());
		assertEquals("Cashier should now have one event in its log",
				1, cashier.log.size());
		assertEquals("The waiter state should be available at this time", 
				"available", waiter.waiterState.name());
		
		//The check is ready
		waiter.msgHereIsCheck(7, 1);
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());
		assertEquals("There should be four events in the customer's log", 
				4, customer.log.size());
		assertEquals("The waiter state should be available at this time", 
				"available", waiter.waiterState.name());
		
		//Customer finishes eating
		waiter.msgLeavingTable(customer);
		assertTrue("Waiter's scheduler should have returned true, but didn't.", 
				waiter.pickAndExecuteAnAction());
		assertEquals("There should be one event in the host's log", 
				1, host.log.size());
		assertEquals("There should be no customer in the waiter's customer's list",
				0,waiter.getCustomers().size());
		assertEquals("The waiter state should be available at this time", 
				"available", waiter.waiterState.name());
		
		//Check post conditions
		assertFalse("Waiter's scheduler should have returned false, but didn't.", 
				waiter.pickAndExecuteAnAction());
		
		
		
		
		
	}
}
