package restaurant.yunying.test;

import junit.framework.TestCase;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.roles.TuCashierRole;
import restaurant.yunying.roles.TuCashierRole.CheckState;
import restaurant.yunying.test.*;
import global.PersonAgent;
import global.roles.Role;
import restaurant.yunying.test.mock.*;
import global.test.mock.*;


public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	TuCashierRole cashier;
	CashierGui cashierGui;
	MockWaiter waiter;
	MockWaiter waiter2;
	MockCustomer customer;
	MockCustomer customer2;
	MockMarket market;
	MockMarket market2;
	MockTruckDriver driver;
	MockTruckDriver driver2;
	PersonAgent p;
	PersonAgent p2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		p = new PersonAgent("person",null,null);
		cashier = new TuCashierRole();	
		cashier.setPerson(p);
		customer = new MockCustomer("mockcustomer");	
		customer2 = new MockCustomer("mockcustomer2");
		waiter = new MockWaiter("mockwaiter");
		waiter2 = new MockWaiter("mockwaiter2");
		market = new MockMarket("market");
		market2 = new MockMarket("market2");
		driver = new MockTruckDriver("driver");
		driver2 = new MockTruckDriver("driver2");
		//market2.cashier = cashier;
		//market.cashier = cashier;
		customer.cashier = cashier;
		customer2.cashier = cashier;
		waiter.cashier = cashier;
		waiter2.cashier = cashier;
		cashierGui = new CashierGui(cashier);
		cashier.setGui(cashierGui);
	}	
	
	public void testCashierAndOneMarketScenario(){
		System.out.println("Test: Cashier and one market. One Bill paid in full");
		market.log.clear();
		cashier.savings = 100;
		
		//Check Preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockMarket should have an empty event log before the Cashier's shceduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		//Market Call cashier
		cashier.msgHereIsBill(market, driver, 10);
		
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.bills.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have one event in the log after the scheduler is called. Instead, the Cashier event log reads: "
						+ cashier.log.toString(), 1, cashier.log.size());
			
		assertTrue("Cashier should have logged \"I have enough money\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("I have enough money"));
		
		assertEquals("Cashier should now have 90 in his savings. Instead, the cashier has "+ cashier.savings, (double)90.0, (double)cashier.savings);
		
		assertEquals(
				"MockMarket should have one event in the log after the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertEquals("The truck driver should have on event in the log after the scheduler is called", 1, driver.log.size());
		
		
		
		//Check postconditions
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
		
		assertEquals("Cashier should now have no bills in its list. but it doesn't. ", 0, cashier.bills.size());
		
		System.out.println();
	}
	
	public void testCashierAndTwoMarkets(){
		System.out.println("Test: Cashier and one market. Two Bills paid in full");
		cashier.savings = 100;
		market.log.clear();
		market2.log.clear();
		
		//Check Preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockMarket should have an empty event log before the Cashier's shceduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("MockMarket2 should have an empty event log before the Cashier's shceduler is called. Instead, the MockMarket's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		
		//Market1 call cashier
		cashier.msgHereIsBill(market, driver, 10);
		
		assertEquals("Cashier should now have one bill in its list. but it doesn't. ", 1, cashier.bills.size());
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have one event in the log after the scheduler is called. Instead, the Cashier event log reads: "
						+ cashier.log.toString(), 1, cashier.log.size());
		
		assertTrue("Cashier should have logged \"I have enough money\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("I have enough money"));
		
		assertEquals("Cashier should now have 90 in his savings. Instead, the cashier has "+ cashier.savings, (double)90.0, (double)(cashier.savings));
		
		assertEquals(
				"MockMarket1 should have one event in the log after the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertEquals("Truck driver should have one event in the log", 1, driver.log.size());
		
		//Market2 call cashier
		cashier.msgHereIsBill(market2, driver2, 20);
		
		assertEquals("Cashier should now have one bill in its list. but it doesn't. ", 1, cashier.bills.size());
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have two events in the log after the scheduler is called. Instead, the Cashier event log reads: "
						+ cashier.log.toString(), 2, cashier.log.size());
		
		assertTrue("Cashier should have logged \"I have enough money\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("I have enough money"));
		
		assertEquals("Cashier should now have 70 in his savings. Instead, the cashier has "+ cashier.savings, (double)70.0, (double)cashier.savings);
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	

		
		//Check if each market received correct amount of money
//		assertEquals("MockMarket1 should have 10 dollars in its savings. Instead, the MockMarket has " 
//					+ market.savings, 10.0, market.savings);
//		
//		assertEquals("MockMarket2 should have 20 dollars in its savings. Instead, the MockMarket has " 
//				+ market2.savings, 20.0, market2.savings);
//		
		//Check postconditions
		
		assertEquals("Cashier should now have no bills in its list. but it doesn't. ", 0, cashier.bills.size());
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
		
		System.out.println();
				
	}
	
	public void testCashierCannotPayTheMarket(){
		System.out.println("Test: Cashier and one market. Cannot pay the bill");
		cashier.savings = 10;
		market.log.clear();
		
		//Check Preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockMarket should have an empty event log before the Cashier's shceduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		//Market Call The Cashier
		//Market1 call cashier
		cashier.msgHereIsBill(market, driver, 30);
				
		assertEquals("Cashier should now have one bill in its list. but it doesn't. ", 1, cashier.bills.size());
				
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
				
		assertEquals(
				"Cashier should have one event in the log after the scheduler is called. Instead, the Cashier event log reads: "
						+ cashier.log.toString(), 1, cashier.log.size());
				
		assertTrue("Cashier should have logged \"I don't have enough money\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("I don't have enough money"));
		
		assertEquals("Cashier should now have one debts. But it didn't", 1, cashier.debts.size());
		
		assertEquals("Cashier should now have a debt of 20 dollars. Instead, it has " + cashier.debts.get(market), (double)20.0, (double)cashier.debts.get(market));
				
		assertEquals("Cashier should now have 0 in his savings. Instead, the cashier has "+ cashier.savings, (double)0.0, (double)cashier.savings);
				
		assertEquals(
				"MockMarket should have one event in the log after the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertEquals("The truck driver should have one event in the log after the cashier's scheduler is called", 1, driver.log.size());
		
	
		//assertEquals("MockMarket should received 10 dollars. But instead it receives " + market.savings, 10.0, market.savings); 
		
		//check postconditions
		assertEquals("Cashier should now have no bills in its list. but it doesn't. ", 0, cashier.bills.size());
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
		
		System.out.println();
		
	}
	

	public void testOneWaiterOneCustomerWithEnoughMoneyWithOneMarketScenario()
	{	waiter.customer = customer;	
		cashier.savings = 100;
		market.log.clear();
		waiter.log.clear();
		customer.log.clear();
		
		System.out.println("Test: One waiter, one customer with enough money, one market interfering scenario");
		
		//check preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("Waiter should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Customer should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		
		//Waiter Calls The Cashier
		cashier.msgComputeBill(waiter, "Gelato", 1);//send the message from a waiter
		
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have one event in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 1, cashier.log.size());
		
		assertEquals(
				"MockWaiter should have one event in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have one event in the log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 1, customer.log.size());
		
		assertEquals("The price of the check should be 2.0. Instead it is " + cashier.checks.get(0).price, (double)cashier.checks.get(0).price, (double)2.0);

		assertTrue("CashierBill should contain a bill with state == processing. It doesn't.",
				cashier.checks.get(0).orderState == CheckState.processing);
		
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeBill"));
		
		assertTrue("CashierBill should contain a bill with the right waiter in it. It doesn't.", 
					cashier.checks.get(0).w == waiter);
		
		//Market Interfering
		cashier.msgHereIsBill(market,driver, 30);
		
		assertEquals("Cashier should now have one bill in its list. but it doesn't. ", 1, cashier.bills.size());
		
		//Customer Pays the check
		
		cashier.msgHereIsPayment(customer, 1, 20, 0);
		
		assertTrue("Cashier should contain a check with state == paying. It doesn't.",
				cashier.checks.get(0).orderState == CheckState.paying
				);
		
		assertEquals("Cashier should have a check whose payment is 20.0. But it doesnet.", 20.0, cashier.checks.get(0).payment);
		
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgHereIsPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have two event in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 2, cashier.log.size());
		
		assertEquals(
				"MockCustomer should have two events in the log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 2, customer.log.size());

		assertTrue("Cashier should have logged \"Received HereIsPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsPayment"));

		assertEquals("Cashier should compute the change for the check. The change that customer receives should be 18.0, but instead it is " + customer.change, (double)18.0, (double)customer.change);
		
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("HereIsYourChange"));
		
		assertEquals("Cashier should now have 102.0 in its savings. but instead it has " + cashier.savings, (double)102.0, (double)cashier.savings);
	
		
		//Pay Market
		assertEquals("There should be no checks in the checklist since the transaction has finished. It doesn't.",
				0, cashier.checks.size());
		
		assertTrue("Cashier's scheduler should have returned true (to pay market bill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have three events in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 3, cashier.log.size());
		
		assertTrue("Cashier should have logged \"I have enough money\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("I have enough money"));
		
		assertEquals("Cashier should now have 72 in his savings. Instead, the cashier has "+ cashier.savings, (double)72.0, (double)cashier.savings);
		
		assertEquals(
				"MockMarket should have one event in the log after the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		//assertEquals("MockMarket should have 30 dollars in its savings. Instead, the MockMarket has " + market.savings, 30.0, market.savings);
		
		
		//Check postconditions
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
		
		assertEquals("Cashier should now have no bills in its list. but it doesn't. ", 0, cashier.bills.size());
		
		System.out.println();
		
	}
	
	public void testOneWaiterOneCustomerWithoutEnoughMoneyAndDebtWithOneMarketScenario(){
		waiter.customer = customer;	
		cashier.savings = 100;
		market.log.clear();
		waiter.log.clear();
		customer.log.clear();
		
		System.out.println("Test: One waiter, one customer with enough money, one market interfering scenario");
		
		//check preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("Waiter should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Customer should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		
		//Waiter Calls The Cashier
		cashier.msgComputeBill(waiter, "Gelato", 1);//send the message from a waiter
		
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have one event in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 1, cashier.log.size());
		
		assertEquals(
				"MockWaiter should have one event in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have one event in the log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 1, customer.log.size());
		
		assertEquals("The price of the check should be 2.0. Instead it is " + cashier.checks.get(0).price, (double)cashier.checks.get(0).price, (double)2.0);

		assertTrue("CashierBill should contain a bill with state == processing. It doesn't.",
				cashier.checks.get(0).orderState == CheckState.processing);
		
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeBill"));
		
		assertTrue("CashierBill should contain a bill with the right waiter in it. It doesn't.", 
					cashier.checks.get(0).w == waiter);
		
		//Market Interfering
		cashier.msgHereIsBill(market, driver, 30);
		
		assertEquals("Cashier should now have one bill in its list. but it doesn't. ", 1, cashier.bills.size());
		
		//Customer Pays the check
		
		cashier.msgHereIsPayment(customer, 1, 10, 8);
		
		assertTrue("Cashier should contain a check with state == paying. It doesn't.",
				cashier.checks.get(0).orderState == CheckState.paying
				);
		
		assertEquals("Cashier should have a check whose payment is 10.0. But it doesnet.", 10.0, cashier.checks.get(0).payment);
		
		assertEquals("Cashier should have a check whose debt is 8.0. But it doesnet.", 8.0, cashier.checks.get(0).debt);
		
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgHereIsPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have two event in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 2, cashier.log.size());
		
		assertEquals(
				"MockCustomer should have two events in the log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 2, customer.log.size());

		assertTrue("Cashier should have logged \"Received HereIsPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsPayment"));

		assertEquals("Cashier should compute the change for the check. The change 0, which is a debt, but instead it is " + customer.change, (double)0, (double)(0-customer.change));
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received You Owe Us from cashier\" with the correct change, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received You Owe Us from cashier"));
		
		assertEquals("Cashier should now have 110 in its savings. but instead it has " + cashier.savings, (double)110.0, (double)cashier.savings);
	
		
		//Pay Market
		assertEquals("There should be no checks in the checklist since the transaction has finished. It doesn't.",
				0, cashier.checks.size());
		
		assertTrue("Cashier's scheduler should have returned true (to pay market bill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have three events in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 3, cashier.log.size());
		
		assertTrue("Cashier should have logged \"I have enough money\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("I have enough money"));
		
		assertEquals("Cashier should now have 80 in his savings. Instead, the cashier has "+ cashier.savings, (double)80.0, (double)cashier.savings);
		
		assertEquals(
				"MockMarket should have one event in the log after the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		//assertEquals("MockMarket should have 30 dollars in its savings. Instead, the MockMarket has " + market.savings, 30.0, market.savings);
		
		
		//Check postconditions
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
		
		assertEquals("Cashier should now have no bills in its list. but it doesn't. ", 0, cashier.bills.size());
		
		System.out.println();
	}
	
	public void testTwoWaiterTwoCustomerWithoutEnoughMoneyWithOneMarketScenario()
	{	waiter.customer = customer;	
		waiter2.customer = customer2;
		cashier.savings = 100;
		market.log.clear();
		waiter.log.clear();
		customer.log.clear();
		customer.change = 0;
		//market.savings = 0;
		
		System.out.println("Test: One waiter, two customers, one with enough money and the other without enough money, one market interfering scenario");
		
		//check preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's computeBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("Waiter should have an empty event log. Instead, the Cashier's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Customer should have an empty event log. Instead, the Cashier's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		assertEquals("Waiter2 should have an empty event log. Instead, the Cashier's event log reads: "
				+ waiter2.log.toString(), 0, waiter2.log.size());
		assertEquals("Customer2 should have an empty event log. Instead, the Cashier's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals("Cashier should have no change. The change that customer has should be 0.0, but instead it is " + customer.change, 0.0, customer.change);

		
		//Two waiters Call The Cashier
		cashier.msgComputeBill(waiter, "Brownie", 1);//send the message from a waiter
		
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		
		assertEquals(
				"Cashier should have one event in the log. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 1, cashier.log.size());
		
		cashier.msgComputeBill(waiter2, "Gelato", 2);
		
		assertEquals(
				"Cashier should have two events in the log. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 2, cashier.log.size());
		
		assertEquals("Cashier should have 2 checks in it. It doesn't.", cashier.checks.size(), 2);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		
		assertEquals(
				"MockWaiter should have one event in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have one event in the log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 1, customer.log.size());
		
		assertEquals("The price of the check should be 4.0. Instead it is " + cashier.checks.get(0).price, (double)cashier.checks.get(0).price, (double)4.0);

		assertTrue("CashierBill should contain a check with state == processing. It doesn't.",
				cashier.checks.get(0).orderState == CheckState.processing);
		
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeBill"));
		
		assertTrue("CashierBill should contain a bill with the right waiter in it. It doesn't.", 
					cashier.checks.get(0).w == waiter);
		
		//Process the other check
		assertEquals("Cashier should have 2 checks in it. It doesn't.", cashier.checks.size(), 2);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
				
		assertEquals("The price of the check should be 2. Instead it is " + cashier.checks.get(1).price, (double)cashier.checks.get(1).price, (double)2.0);

		assertTrue("CashierBill should contain a bill with state == processing. It doesn't.",
						cashier.checks.get(1).orderState == CheckState.processing);
				
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeBill"));
				
		assertTrue("CashierBill should contain a bill with the right waiter in it. It doesn't.", 
					cashier.checks.get(1).w == waiter2);
		
		//Market Interfering
		cashier.msgHereIsBill(market, driver, 130);
		
		assertEquals("Cashier should now have one bill in its list. but it doesn't. ", 1, cashier.bills.size());
		
		//The Customer(with enough money) pays the check
		
		cashier.msgHereIsPayment(customer, 1, 20, 0);
		
		assertTrue("Cashier should contain a check with state == paying. It doesn't.",
				cashier.checks.get(0).orderState == CheckState.paying
				);
		
		assertEquals("Cashier should have a check whose payment is 20.0. But it doesnet.", 20.0, cashier.checks.get(0).payment);
		
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgHereIsPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have two event in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 3, cashier.log.size());
		
		assertEquals(
				"MockCustomer should have two events in the log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 2, customer.log.size());

		assertTrue("Cashier should have logged \"Received HereIsPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsPayment"));

		assertEquals("Cashier should compute the change for the check. The change that customer receives should be 16.0, but instead it is " + customer.change, (double)16.0, (double)customer.change);
		
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("HereIsYourChange"));
		
		assertEquals("Cashier should now have 104.0 in its savings. but instead it has " + cashier.savings, (double)104.0, (double)cashier.savings);
		
		assertEquals("There should be one check in the checklist since the transaction has finished. It doesn't.",
				1, cashier.checks.size());
		
		
		//The Customer(without enough money) pays the check
		cashier.msgHereIsPayment(customer2, 2, 10, 8);
		
		assertTrue("Cashier should contain a check with state == paying. It doesn't.",
				cashier.checks.get(0).orderState == CheckState.paying
				);
		
		assertEquals("Cashier should have a check whose payment is 10.0. But it doesnet.", 10.0, cashier.checks.get(0).payment);
		
		assertEquals("Cashier should have a check whose debt is 8.0. But it doesnet.", 8.0, cashier.checks.get(0).debt);
		
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgHereIsPayment), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have four events in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 4, cashier.log.size());
		

		assertTrue("Cashier should have logged \"Received HereIsPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsPayment"));

		assertEquals("Cashier should compute the change for the check. The change 0, which is a debt, but instead it is " + customer2.change, (double)0, (double)0-customer2.change);
		
		assertTrue("MockCustomer should have logged an event for receiving \"Received You Owe Us from cashier\" with the correct change, but his last event logged reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received You Owe Us from cashier"));
		
		assertEquals("Cashier should now have 114.0 in its savings. but instead it has " + cashier.savings, (double)114.0, (double)cashier.savings);
		
		//Pay Market
		assertEquals("There should be no checks in the checklist since the transaction has finished. It doesn't.",
				0, cashier.checks.size());
		
		assertTrue("Cashier's scheduler should have returned true (to pay market bill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"Cashier should have five events in the log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ cashier.log.toString(), 5, cashier.log.size());
		
		assertTrue("Cashier should have logged \"I don't have enough money\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("I don't have enough money"));
		
		assertEquals("Cashier should now have one debts. But it didn't", 1, cashier.debts.size());
		
		assertEquals("Cashier should now have a debt of 16.0 dollars. Instead, it has " + cashier.debts.get(market), (double)16.0, (double)Math.floor(cashier.debts.get(market)));
				
		assertEquals("Cashier should now have 0 in his savings. Instead, the cashier has "+ cashier.savings, (double)0.0, (double)cashier.savings);
				
		assertEquals(
				"MockMarket should have 0 event in the log after the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		//assertEquals("MockMarket should received 125.99 dollars. But instead it receives " + market.savings, 125.99, market.savings); 
		
		
		//Check postconditions
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
		
		assertEquals("There should be no checks in the checklist since the transaction has finished. It doesn't.",
				0, cashier.checks.size());
		
		assertEquals("Cashier should now have no bills in its list. but it doesn't. ", 0, cashier.bills.size());
		
		System.out.println();
		
	}
	
	
	
	
}
