package restaurant.ji.test;

import interfaces.Building;
import interfaces.Business;

import java.util.ArrayList;
import java.util.List;

import bank.interfaces.BankPatron;
import bank.test.mock.MockBank;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.MockPerson;
import restaurant.ji.roles.JiCashierRole;
import restaurant.ji.roles.JiCashierRole.*;
import restaurant.ji.test.mock.*;
import junit.framework.*;

public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	MockJiRestaurant restaurant; MockBank bank;
	JiCashierRole cashier; MockPerson cashierperson;
	MockWaiter waiter; MockPerson waiterperson;
	MockCustomer customer1; MockPerson customer1person;
	MockCustomer customer2; MockPerson customer2person;
	MockTruckDriver driver1; MockPerson driver1person;
	MockTruckDriver driver2; MockPerson driver2person;
	MockCook cook; MockPerson cookperson;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		
		restaurant = new MockJiRestaurant("MockJi");
		bank = new MockBank("bank"); restaurant.bank = bank;
		List<Building> b = new ArrayList<Building>(); b.add(restaurant); b.add(bank);
		
		cashier = new JiCashierRole(); cashierperson = new MockPerson("cashier", b); cashier.setPerson(cashierperson); 	
		customer1 = new MockCustomer(); customer1person = new MockPerson("customer1", b); customer1.setPerson(customer1person);
		customer2 = new MockCustomer(); customer2person = new MockPerson("customer2", b); customer2.setPerson(customer2person);
		waiter = new MockWaiter(); waiterperson = new MockPerson("waiter", b); waiter.setPerson(waiterperson);
		cook = new MockCook(); cookperson = new MockPerson("cook", b); cook.setPerson(cookperson);
		driver1 = new MockTruckDriver(); driver1person = new MockPerson("driver1", b); driver1.setPerson(driver1person);
		driver2 = new MockTruckDriver(); driver2person = new MockPerson("driver2", b); driver2.setPerson(driver2person);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		// some set up for this test
		restaurant.currentAssets = 100f; // give the restaurant starting money
		customer1.cashier = cashier;			
		
		//check preconditions
		assertEquals("the person in the cashier role should be cashierperson, but it's actually " + cashier.getPerson().getName(), cashier.getPerson(), cashierperson);
		assertFalse("cashier shouldn't be working but is", cashier.isWorking());
		assertEquals("cashier's state should be none but is actually " + cashier.getState(), cashier.getState(), State.none);
		assertTrue("Cashier should have 0 bills in it. It doesn't.",cashier.getChecks().isEmpty());
		assertTrue("Cashier should have 0 restaurant bills in it. It doesn't.",cashier.getRestaurantBills().isEmpty());
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgAtJiRestaurant is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		// going to the restaurant to start work
		cashier.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("Cashier should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cashier's state should be arrivedAtJiRestaurant but isn't", cashier.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cashier's restaurant is the mockrestaurant", cashier.getRestaurant(), restaurant);
		assertTrue("cashier's scheduler should have returned true to begin work but didn't", cashier.pickAndExecuteAnAction());
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should be working but isn't", cashier.isWorking());
		assertFalse("cashier should not have gotten paid but somehow he thinks he did", cashier.hasReceivedPaycheck());
		assertEquals("cashier know how much money the restaurant has but doesn't. he thinks it's $" + cashier.getRestaurantMoney(), cashier.getRestaurantMoney(), 100f);
		assertEquals("cashier's company bank is the mockrestaurant's company bank", cashier.getCompanyBank(), bank);
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		
		//waiter asking for bill
		cashier.msgComputeBill("Steak", customer1, waiter); // steak price is 15.99
		//check postconditions for computing a bill
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgComputeBill"));
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.getChecks().size(), 1);
		assertEquals("cashier's 1 bill should be for $15.99. It's something else instead: $" + cashier.getChecks().get(0).due, 
				cashier.getChecks().get(0).due, 15.99f);
		assertEquals("cashier's 1 bill should have no change, but has instead " + cashier.getChecks().get(0).change, cashier.getChecks().get(0).change, 0f);
		assertEquals("cashier's 1 bill should have the right customer in it. It doesn't.", cashier.getChecks().get(0).customer, customer1);
		assertEquals("cashier's 1 bill should have the right waiter in it. It doesn't.", cashier.getChecks().get(0).waiter, waiter);
		assertEquals("cashier's 1 bill should have state == beingCalculated but doesn't, instead it's " + cashier.getChecks().get(0).state, 
				cashier.getChecks().get(0).state, CheckState.beingCalculated);
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 
				waiter.log.size(), 0);
		assertTrue("cashier's scheduler should have returned true to compute the bill but didn't", cashier.pickAndExecuteAnAction());
		assertTrue("waiter should have logged \"Received msgHereIsCheck from cashier\" but didn't, instead logged " + waiter.log.getLastLoggedEvent().toString(),
				waiter.log.containsString("Received msgHereIsCheck from cashier"));
		assertTrue("CashierBill's first bill should have state == waiting. It doesn't.", cashier.getChecks().get(0).state == CheckState.waiting);
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
		
		//customer paying a bill
		cashier.msgPayingCheck(15.99f, customer1);
		//check postconditions
		assertTrue("Cashier should have logged \"Received msgPayingCheck\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgPayingCheck"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("CashierBill should contain a bill with state == payReceived. It doesn't.", cashier.getChecks().get(0).state, CheckState.payReceived);
		assertEquals("cashier's bill should have change = 0 but doesn't, instead: " + cashier.getChecks().get(0).change, cashier.getChecks().get(0).change, 0f);
		//assertEquals("cashier should have added the payment to the restaurant's money but didn't", cashier.getRestaurantMoney(), 100+15.99f);
		assertTrue("restaurant should have logged \"Received msgReceivedPaymentFromCustomer\" but instead says "+restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgReceivedPaymentFromCustomer"));
		assertTrue("cashier's scheduler should have returned true to give change but didn't", cashier.pickAndExecuteAnAction());
		assertTrue("mockcustomer1 should have logged an event for receiving \"msgHereIsChange\" with the correct balance, but his last event logged reads instead: " + customer1.log.getLastLoggedEvent().toString(), 
				customer1.log.containsString("Received msgHereIsChange from cashier. Change = 0.0"));
		assertTrue("cashier should have removed this check from his list but didn't", cashier.getChecks().isEmpty());
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
	}//end one normal customer scenario
	
	
	public void testTwoOneOrderOneMarket(){
		restaurant.currentAssets = 20f; // give the restaurant starting money
		
		//check preconditions
		assertEquals("the person in the cashier role should be cashierperson, but it's actually " + cashier.getPerson().getName(), cashier.getPerson(), cashierperson);
		assertFalse("cashier shouldn't be working but is", cashier.isWorking());
		assertEquals("cashier's state should be none but is actually " + cashier.getState(), cashier.getState(), State.none);
		assertTrue("Cashier should have 0 bills in it. It doesn't.",cashier.getChecks().isEmpty());
		assertTrue("Cashier should have 0 restaurant bills in it. It doesn't.",cashier.getRestaurantBills().isEmpty());
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgAtJiRestaurant is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		// going to the restaurant to start work
		cashier.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("Cashier should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cashier's state should be arrivedAtJiRestaurant but isn't", cashier.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cashier's restaurant is the mockrestaurant", cashier.getRestaurant(), restaurant);
		assertTrue("cashier's scheduler should have returned true to begin work but didn't", cashier.pickAndExecuteAnAction());
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should be working but isn't", cashier.isWorking());
		assertFalse("cashier should not have gotten paid but somehow he thinks he did", cashier.hasReceivedPaycheck());
		assertEquals("cashier know how much money the restaurant has but doesn't. he thinks it's $" + cashier.getRestaurantMoney(), cashier.getRestaurantMoney(), 20f);
		assertEquals("cashier's company bank is the mockrestaurant's company bank", cashier.getCompanyBank(), bank);
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		
		// restaurant needs to pay a bill from the market
		cashier.msgPayMarketBill(20f, driver1); //from the restaurant
		// post-conditions
		assertTrue("Cashier should have logged \"Received msgPayMarketBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgPayMarketBill"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("Cashier RestaurantBills should contain an entry for the market containing the amount of money it needs to pay. It doesn't.",
				cashier.getRestaurantBills().get(driver1), 20f);
		assertTrue("Cashier's scheduler should have returned true to pay the market, but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("restaurant should have logged \"Received msgPaidBillToMarket\" but instead says "+restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgPaidBillToMarket"));
		assertTrue("driver1 should have logged Received msgHereIsBill but instead reads: " + driver1.log.getLastLoggedEvent().toString(),
				driver1.log.containsString("Received msgHereIsBill"));
		assertEquals("Cashier should have no money left, but doesn't", cashier.getRestaurantMoney(), 0f);
		assertFalse("Cashier RestaurantBills should no longer an entry for the market containing the amount of money it needs to pay. But it does.",
				cashier.getRestaurantBills().containsKey(driver1));
		assertTrue("cashier should have no market bills now, but it does", cashier.getRestaurantBills().isEmpty());
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
	}
	
	public void testThreeOneOrderTwoMarkets(){
		restaurant.currentAssets = 100f; // give the restaurant starting money
		
		//check preconditions
		assertEquals("the person in the cashier role should be cashierperson, but it's actually " + cashier.getPerson().getName(), cashier.getPerson(), cashierperson);
		assertFalse("cashier shouldn't be working but is", cashier.isWorking());
		assertEquals("cashier's state should be none but is actually " + cashier.getState(), cashier.getState(), State.none);
		assertTrue("Cashier should have 0 bills in it. It doesn't.",cashier.getChecks().isEmpty());
		assertTrue("Cashier should have 0 restaurant bills in it. It doesn't.",cashier.getRestaurantBills().isEmpty());
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgAtJiRestaurant is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		// going to the restaurant to start work
		cashier.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("Cashier should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cashier's state should be arrivedAtJiRestaurant but isn't", cashier.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cashier's restaurant is the mockrestaurant", cashier.getRestaurant(), restaurant);
		assertTrue("cashier's scheduler should have returned true to begin work but didn't", cashier.pickAndExecuteAnAction());
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should be working but isn't", cashier.isWorking());
		assertFalse("cashier should not have gotten paid but somehow he thinks he did", cashier.hasReceivedPaycheck());
		assertEquals("cashier know how much money the restaurant has but doesn't. he thinks it's $" + cashier.getRestaurantMoney(), cashier.getRestaurantMoney(), 100f);
		assertEquals("cashier's company bank is the mockrestaurant's company bank", cashier.getCompanyBank(), bank);
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		
		// pay market bill
		cashier.msgPayMarketBill(50f, driver1);
		// post-conditions
		assertTrue("Cashier should have logged \"Received msgPayMarketBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgPayMarketBill"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("Cashier RestaurantBills should contain an entry for the market containing the amount of money it needs to pay. It doesn't.",
				cashier.getRestaurantBills().get(driver1), 50f);
		assertTrue("Cashier's scheduler should have returned true to pay the market, but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("restaurant should have logged \"Received msgPaidBillToMarket\" but instead says "+restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgPaidBillToMarket"));
		assertTrue("driver1 should have logged Received msgHereIsBill but instead reads: " + driver1.log.getLastLoggedEvent().toString(),
				driver1.log.containsString("Received msgHereIsBill"));
		assertEquals("Cashier should have $50 now, but doesn't", cashier.getRestaurantMoney(), 50f);
		assertFalse("Cashier RestaurantBills should no longer an entry for the market containing the amount of money it needs to pay. But it does.",
				cashier.getRestaurantBills().containsKey(driver1));
		assertTrue("cashier should have no market bills now, but it does", cashier.getRestaurantBills().isEmpty());
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
		
		// ordering from driver2
		cashier.msgPayMarketBill(50f, driver2);
		// post-conditions
		assertTrue("Cashier should have logged \"Received msgPayMarketBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgPayMarketBill"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("Cashier RestaurantBills should contain an entry for the market containing the amount of money it needs to pay. It doesn't.",
				cashier.getRestaurantBills().get(driver2), 50f);
		assertTrue("Cashier's scheduler should have returned true to pay the market, but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("restaurant should have logged \"Received msgPaidBillToMarket\" but instead says "+restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgPaidBillToMarket"));
		assertTrue("driver2 should have logged Received msgHereIsBill but instead reads: " + driver2.log.getLastLoggedEvent().toString(),
				driver2.log.containsString("Received msgHereIsBill"));
		assertEquals("Cashier should have no money left, but doesn't", cashier.getRestaurantMoney(), 0f);
		assertFalse("Cashier RestaurantBills should no longer an entry for the market containing the amount of money it needs to pay. But it does.",
				cashier.getRestaurantBills().containsKey(driver2));
		assertTrue("cashier should have no market bills now, but it does", cashier.getRestaurantBills().isEmpty());
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
	}
	
	public void testFourOneOrderUnableToPayMarket(){
		restaurant.currentAssets = 40f; // give the restaurant starting money
		
		//check preconditions
		assertEquals("the person in the cashier role should be cashierperson, but it's actually " + cashier.getPerson().getName(), cashier.getPerson(), cashierperson);
		assertFalse("cashier shouldn't be working but is", cashier.isWorking());
		assertEquals("cashier's state should be none but is actually " + cashier.getState(), cashier.getState(), State.none);
		assertTrue("Cashier should have 0 bills in it. It doesn't.",cashier.getChecks().isEmpty());
		assertTrue("Cashier should have 0 restaurant bills in it. It doesn't.",cashier.getRestaurantBills().isEmpty());
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgAtJiRestaurant is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		// going to the restaurant to start work
		cashier.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("Cashier should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cashier's state should be arrivedAtJiRestaurant but isn't", cashier.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cashier's restaurant is the mockrestaurant", cashier.getRestaurant(), restaurant);
		assertTrue("cashier's scheduler should have returned true to begin work but didn't", cashier.pickAndExecuteAnAction());
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should be working but isn't", cashier.isWorking());
		assertFalse("cashier should not have gotten paid but somehow he thinks he did", cashier.hasReceivedPaycheck());
		assertEquals("cashier know how much money the restaurant has but doesn't. he thinks it's $" + cashier.getRestaurantMoney(), cashier.getRestaurantMoney(), 40f);
		assertEquals("cashier's company bank is the mockrestaurant's company bank", cashier.getCompanyBank(), bank);
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		
		// pay market bill
		cashier.msgPayMarketBill(50f, driver1);
		// post-conditions
		assertTrue("Cashier should have logged \"Received msgPayMarketBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgPayMarketBill"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("Cashier RestaurantBills should contain an entry for the market containing the amount of money it needs to pay. It doesn't.",
				cashier.getRestaurantBills().get(driver1), 50f);
		assertTrue("Cashier's scheduler should have returned true to pay the market, but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("restaurant should have logged \"Received msgPaidBillToMarket\" but instead says "+restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgPaidBillToMarket"));
		assertTrue("driver1 should have logged Received msgHereIsBill but instead reads: " + driver1.log.getLastLoggedEvent().toString(),
				driver1.log.containsString("Received msgHereIsBill"));
		assertEquals("Cashier should have no money left, but doesn't", cashier.getRestaurantMoney(), 0f);
		assertEquals("Cashier RestaurantBills should still contain an entry for the market and the amount of money it still needs to pay. But it doesn't or it's not right.",
				cashier.getRestaurantBills().get(driver1), 10f);
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
	}
	
	public void testFiveCustomerCantPay(){
		restaurant.currentAssets = 100f; // give the restaurant starting money
		customer1.cashier = cashier;			
		
		//check preconditions
		assertEquals("the person in the cashier role should be cashierperson, but it's actually " + cashier.getPerson().getName(), cashier.getPerson(), cashierperson);
		assertFalse("cashier shouldn't be working but is", cashier.isWorking());
		assertEquals("cashier's state should be none but is actually " + cashier.getState(), cashier.getState(), State.none);
		assertTrue("Cashier should have 0 bills in it. It doesn't.",cashier.getChecks().isEmpty());
		assertTrue("Cashier should have 0 restaurant bills in it. It doesn't.",cashier.getRestaurantBills().isEmpty());
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgAtJiRestaurant is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		// going to the restaurant to start work
		cashier.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("Cashier should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cashier's state should be arrivedAtJiRestaurant but isn't", cashier.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cashier's restaurant is the mockrestaurant", cashier.getRestaurant(), restaurant);
		assertTrue("cashier's scheduler should have returned true to begin work but didn't", cashier.pickAndExecuteAnAction());
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should be working but isn't", cashier.isWorking());
		assertFalse("cashier should not have gotten paid but somehow he thinks he did", cashier.hasReceivedPaycheck());
		assertEquals("cashier know how much money the restaurant has but doesn't. he thinks it's $" + cashier.getRestaurantMoney(), cashier.getRestaurantMoney(), 100f);
		assertEquals("cashier's company bank is the mockrestaurant's company bank", cashier.getCompanyBank(), bank);
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		
		//waiter asking for bill
		cashier.msgComputeBill("Steak", customer1, waiter); // steak price is 15.99
		//check postconditions for computing a bill
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgComputeBill"));
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.getChecks().size(), 1);
		assertEquals("cashier's 1 bill should be for $15.99. It's something else instead: $" + cashier.getChecks().get(0).due, 
				cashier.getChecks().get(0).due, 15.99f);
		assertEquals("cashier's 1 bill should have no change, but has instead " + cashier.getChecks().get(0).change, cashier.getChecks().get(0).change, 0f);
		assertEquals("cashier's 1 bill should have the right customer in it. It doesn't.", cashier.getChecks().get(0).customer, customer1);
		assertEquals("cashier's 1 bill should have the right waiter in it. It doesn't.", cashier.getChecks().get(0).waiter, waiter);
		assertEquals("cashier's 1 bill should have state == beingCalculated but doesn't, instead it's " + cashier.getChecks().get(0).state, 
				cashier.getChecks().get(0).state, CheckState.beingCalculated);
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 
				waiter.log.size(), 0);
		assertTrue("cashier's scheduler should have returned true to compute the bill but didn't", cashier.pickAndExecuteAnAction());
		assertTrue("waiter should have logged \"Received msgHereIsCheck from cashier\" but didn't, instead logged " + waiter.log.getLastLoggedEvent().toString(),
				waiter.log.containsString("Received msgHereIsCheck from cashier"));
		assertTrue("CashierBill's first bill should have state == waiting. It doesn't.", cashier.getChecks().get(0).state == CheckState.waiting);
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
		
		//customer can't pay the bill
		cashier.msgICantPay(customer1);
		//check postconditions
		assertTrue("Cashier should have logged \"Received msgICantPay\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgICantPay"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should have removed the bill from his list but didn't", cashier.getChecks().isEmpty());
		assertEquals("mockcustomer1 should have an empty event log still. Instead, the mockcustomer1's event log reads: " + customer1.log.toString(), 
				customer1.log.size(), 0);
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
	}
	
	public void testSixTwoCustomersOnePayingOneNot(){
		restaurant.currentAssets = 100f; // give the restaurant starting money
		customer1.cashier = cashier;
		customer2.cashier = cashier;
		
		//check preconditions
		assertEquals("the person in the cashier role should be cashierperson, but it's actually " + cashier.getPerson().getName(), cashier.getPerson(), cashierperson);
		assertFalse("cashier shouldn't be working but is", cashier.isWorking());
		assertEquals("cashier's state should be none but is actually " + cashier.getState(), cashier.getState(), State.none);
		assertTrue("Cashier should have 0 bills in it. It doesn't.",cashier.getChecks().isEmpty());
		assertTrue("Cashier should have 0 restaurant bills in it. It doesn't.",cashier.getRestaurantBills().isEmpty());
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgAtJiRestaurant is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		// going to the restaurant to start work
		cashier.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("Cashier should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cashier's state should be arrivedAtJiRestaurant but isn't", cashier.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cashier's restaurant is the mockrestaurant", cashier.getRestaurant(), restaurant);
		assertTrue("cashier's scheduler should have returned true to begin work but didn't", cashier.pickAndExecuteAnAction());
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should be working but isn't", cashier.isWorking());
		assertFalse("cashier should not have gotten paid but somehow he thinks he did", cashier.hasReceivedPaycheck());
		assertEquals("cashier know how much money the restaurant has but doesn't. he thinks it's $" + cashier.getRestaurantMoney(), cashier.getRestaurantMoney(), 100f);
		assertEquals("cashier's company bank is the mockrestaurant's company bank", cashier.getCompanyBank(), bank);
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		
		//waiter asking for bill
		cashier.msgComputeBill("Steak", customer1, waiter); // steak price is 15.99
		//check postconditions for computing a bill
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgComputeBill"));
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.getChecks().size(), 1);
		assertEquals("cashier's 1 bill should be for $15.99. It's something else instead: $" + cashier.getChecks().get(0).due, 
				cashier.getChecks().get(0).due, 15.99f);
		assertEquals("cashier's 1 bill should have no change, but has instead " + cashier.getChecks().get(0).change, cashier.getChecks().get(0).change, 0f);
		assertEquals("cashier's 1 bill should have the right customer in it. It doesn't.", cashier.getChecks().get(0).customer, customer1);
		assertEquals("cashier's 1 bill should have the right waiter in it. It doesn't.", cashier.getChecks().get(0).waiter, waiter);
		assertEquals("cashier's 1 bill should have state == beingCalculated but doesn't, instead it's " + cashier.getChecks().get(0).state, 
				cashier.getChecks().get(0).state, CheckState.beingCalculated);
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 
				waiter.log.size(), 0);
		assertTrue("cashier's scheduler should have returned true to compute the bill but didn't", cashier.pickAndExecuteAnAction());
		assertTrue("waiter should have logged \"Received msgHereIsCheck from cashier\" but didn't, instead logged " + waiter.log.getLastLoggedEvent().toString(),
				waiter.log.containsString("Received msgHereIsCheck from cashier"));
		assertTrue("CashierBill's first bill should have state == waiting. It doesn't.", cashier.getChecks().get(0).state == CheckState.waiting);
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
		
		
		//waiter asking for second bill
		cashier.msgComputeBill("Steak", customer2, waiter); // steak price is 15.99
		//check postconditions for computing a bill
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgComputeBill"));
		assertEquals("Cashier should have 2 bills in it. It doesn't.", cashier.getChecks().size(), 2);
		assertEquals("cashier's 2nd bill should be for $15.99. It's something else instead: $" + cashier.getChecks().get(1).due, 
				cashier.getChecks().get(1).due, 15.99f);
		assertEquals("cashier's 2nd bill should have no change, but has instead " + cashier.getChecks().get(1).change, cashier.getChecks().get(1).change, 0f);
		assertEquals("cashier's 2nd bill should have the right customer in it. It doesn't.", cashier.getChecks().get(1).customer, customer2);
		assertEquals("cashier's 2nd bill should have the right waiter in it. It doesn't.", cashier.getChecks().get(1).waiter, waiter);
		assertEquals("cashier's 2nd bill should have state == beingCalculated but doesn't, instead it's " + cashier.getChecks().get(1).state, 
				cashier.getChecks().get(1).state, CheckState.beingCalculated);
		assertEquals("MockWaiter should have an event log with only one other log before the Cashier's scheduler is called. Instead, the MockWaiter's last log reads: " + waiter.log.getLastLoggedEvent().toString(), 
				waiter.log.size(), 1);
		assertTrue("cashier's scheduler should have returned true to compute the bill but didn't", cashier.pickAndExecuteAnAction());
		assertTrue("waiter should have logged \"Received msgHereIsCheck from cashier\" but didn't, instead logged " + waiter.log.getLastLoggedEvent().toString(),
				waiter.log.containsString("Received msgHereIsCheck from cashier"));
		assertTrue("CashierBill's second bill should have state == waiting. It doesn't.", cashier.getChecks().get(1).state == CheckState.waiting);
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
		
		//customer paying a bill
		cashier.msgPayingCheck(15.99f, customer1);
		//customer can't pay the bill
		cashier.msgICantPay(customer2);
		
		//check postconditions of check being paid
		assertTrue("Cashier should have logged \"Received msgPayingCheck\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgPayingCheck"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("CashierBill should contain a bill with state == payReceived. It doesn't.", cashier.getChecks().get(0).state, CheckState.payReceived);
		assertEquals("cashier's bill should have change = 0 but doesn't, instead: " + cashier.getChecks().get(0).change, cashier.getChecks().get(0).change, 0f);
		//assertEquals("cashier should have added the payment to the restaurant's money but didn't", cashier.getRestaurantMoney(), 100+15.99f);
		assertTrue("restaurant should have logged \"Received msgReceivedPaymentFromCustomer\" but instead says "+restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgReceivedPaymentFromCustomer"));

		//check postconditions of not being able to pay
		assertTrue("Cashier should have logged \"Received msgICantPay\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgICantPay"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("cashier should have removed the bill from his list so now it only has 1 bill. but he didn't", cashier.getChecks().size(), 1);
		assertEquals("mockcustomer2 should have an empty event log still. Instead, the mockcustomer1's event log reads: " + customer2.log.toString(), 
				customer2.log.size(), 0);
		assertTrue("cashier's scheduler should have returned true, needs to calculate change for customer1", cashier.pickAndExecuteAnAction());
		
		// even most post-conditions of check being paid
		assertTrue("mockcustomer1 should have logged an event for receiving \"msgHereIsChange\" with the correct balance, but his last event logged reads instead: " + customer1.log.getLastLoggedEvent().toString(), 
				customer1.log.containsString("Received msgHereIsChange from cashier. Change = 0.0"));
		assertTrue("cashier should have removed this check from his list but didn't", cashier.getChecks().isEmpty());
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
	}
	
	public void testSevenOneNormalCustomerOneNormalMarketOrder(){
		restaurant.currentAssets = 100f; // give the restaurant starting money
		customer1.cashier = cashier;			
		
		//check preconditions
		assertEquals("the person in the cashier role should be cashierperson, but it's actually " + cashier.getPerson().getName(), cashier.getPerson(), cashierperson);
		assertFalse("cashier shouldn't be working but is", cashier.isWorking());
		assertEquals("cashier's state should be none but is actually " + cashier.getState(), cashier.getState(), State.none);
		assertTrue("Cashier should have 0 bills in it. It doesn't.",cashier.getChecks().isEmpty());
		assertTrue("Cashier should have 0 restaurant bills in it. It doesn't.",cashier.getRestaurantBills().isEmpty());
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgAtJiRestaurant is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		// going to the restaurant to start work
		cashier.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("Cashier should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cashier's state should be arrivedAtJiRestaurant but isn't", cashier.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cashier's restaurant is the mockrestaurant", cashier.getRestaurant(), restaurant);
		assertTrue("cashier's scheduler should have returned true to begin work but didn't", cashier.pickAndExecuteAnAction());
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should be working but isn't", cashier.isWorking());
		assertFalse("cashier should not have gotten paid but somehow he thinks he did", cashier.hasReceivedPaycheck());
		assertEquals("cashier know how much money the restaurant has but doesn't. he thinks it's $" + cashier.getRestaurantMoney(), cashier.getRestaurantMoney(), 100f);
		assertEquals("cashier's company bank is the mockrestaurant's company bank", cashier.getCompanyBank(), bank);
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		
		//waiter asking for bill
		cashier.msgComputeBill("Steak", customer1, waiter); // steak price is 15.99
		//check postconditions for computing a bill
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("Cashier should have logged \"Received msgComputeBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgComputeBill"));
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.getChecks().size(), 1);
		assertEquals("cashier's 1 bill should be for $15.99. It's something else instead: $" + cashier.getChecks().get(0).due, 
				cashier.getChecks().get(0).due, 15.99f);
		assertEquals("cashier's 1 bill should have no change, but has instead " + cashier.getChecks().get(0).change, cashier.getChecks().get(0).change, 0f);
		assertEquals("cashier's 1 bill should have the right customer in it. It doesn't.", cashier.getChecks().get(0).customer, customer1);
		assertEquals("cashier's 1 bill should have the right waiter in it. It doesn't.", cashier.getChecks().get(0).waiter, waiter);
		assertEquals("cashier's 1 bill should have state == beingCalculated but doesn't, instead it's " + cashier.getChecks().get(0).state, 
				cashier.getChecks().get(0).state, CheckState.beingCalculated);
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 
				waiter.log.size(), 0);
		assertTrue("cashier's scheduler should have returned true to compute the bill but didn't", cashier.pickAndExecuteAnAction());
		assertTrue("waiter should have logged \"Received msgHereIsCheck from cashier\" but didn't, instead logged " + waiter.log.getLastLoggedEvent().toString(),
				waiter.log.containsString("Received msgHereIsCheck from cashier"));
		assertTrue("CashierBill's first bill should have state == waiting. It doesn't.", cashier.getChecks().get(0).state == CheckState.waiting);
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
		
		// restaurant needs to pay a bill from the market
		cashier.msgPayMarketBill(20f, driver1); //from the restaurant
		// post-conditions
		assertTrue("Cashier should have logged \"Received msgPayMarketBill\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgPayMarketBill"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("Cashier RestaurantBills should contain an entry for the market containing the amount of money it needs to pay. It doesn't.",
				cashier.getRestaurantBills().get(driver1), 20f);
		assertTrue("Cashier's scheduler should have returned true to pay the market, but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("restaurant should have logged \"Received msgPaidBillToMarket\" but instead says "+restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgPaidBillToMarket"));
				assertTrue("driver1 should have logged Received msgHereIsBill but instead reads: " + driver1.log.getLastLoggedEvent().toString(),
						driver1.log.containsString("Received msgHereIsBill"));
		assertEquals("Cashier should have the right amount of money left, but doesn't", cashier.getRestaurantMoney(), 100-20f);
		assertFalse("Cashier RestaurantBills should no longer an entry for the market containing the amount of money it needs to pay. But it does.",
				cashier.getRestaurantBills().containsKey(driver1));
		assertTrue("cashier should have no market bills now, but it does", cashier.getRestaurantBills().isEmpty());
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
		
		
		//customer paying a bill
		cashier.msgPayingCheck(15.99f, customer1);
		//check postconditions
		assertTrue("Cashier should have logged \"Received msgPayingCheck\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgPayingCheck"));
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertEquals("CashierBill should contain a bill with state == payReceived. It doesn't.", cashier.getChecks().get(0).state, CheckState.payReceived);
		assertEquals("cashier's bill should have change = 0 but doesn't, instead: " + cashier.getChecks().get(0).change, cashier.getChecks().get(0).change, 0f);
		//assertEquals("cashier should have added the payment to the restaurant's money but didn't", cashier.getRestaurantMoney(), 100-20+15.99f);
		assertTrue("restaurant should have logged \"Received msgReceivedPaymentFromCustomer\" but instead says "+restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgReceivedPaymentFromCustomer"));
		assertTrue("cashier's scheduler should have returned true to give change but didn't", cashier.pickAndExecuteAnAction());
		assertTrue("mockcustomer1 should have logged an event for receiving \"msgHereIsChange\" with the correct balance, but his last event logged reads instead: " + customer1.log.getLastLoggedEvent().toString(), 
				customer1.log.containsString("Received msgHereIsChange from cashier. Change = 0.0"));
		assertTrue("cashier should have removed this check from his list but didn't", cashier.getChecks().isEmpty());
		assertFalse("cashier's scheduler should have returned false, nothing to do", cashier.pickAndExecuteAnAction());
	}
	
	public void testEightGoingToBank(){
		restaurant.currentAssets = 500f; // give the restaurant starting money
		
		//check preconditions
		assertEquals("cashier doesn't have money to deposit", cashier.getMoneyToDeposit(), 0f);
		assertEquals("the person in the cashier role should be cashierperson, but it's actually " + cashier.getPerson().getName(), cashier.getPerson(), cashierperson);
		assertFalse("cashier shouldn't be working but is", cashier.isWorking());
		assertEquals("cashier's state should be none but is actually " + cashier.getState(), cashier.getState(), State.none);
		assertTrue("Cashier should have 0 bills in it. It doesn't.",cashier.getChecks().isEmpty());
		assertTrue("Cashier should have 0 restaurant bills in it. It doesn't.",cashier.getRestaurantBills().isEmpty());
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgAtJiRestaurant is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
		
		// going to the restaurant to start work
		cashier.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("Cashier should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cashier.log.getLastLoggedEvent().toString(), 
				cashier.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cashier's state should be arrivedAtJiRestaurant but isn't", cashier.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cashier's restaurant is the mockrestaurant", cashier.getRestaurant(), restaurant);
		assertTrue("cashier's scheduler should have returned true to begin work but didn't", cashier.pickAndExecuteAnAction());
		assertEquals("cashier's state should be working but isn't", cashier.getState(), State.working);
		assertTrue("cashier should be working but isn't", cashier.isWorking());
		assertFalse("cashier should not have gotten paid but somehow he thinks he did", cashier.hasReceivedPaycheck());
		assertEquals("cashier know how much money the restaurant has but doesn't. he thinks it's $" + cashier.getRestaurantMoney(), cashier.getRestaurantMoney(), 500f);
		assertEquals("cashier's company bank is the mockrestaurant's company bank", cashier.getCompanyBank(), bank);
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
		
		cashier.msgDepositExcessFunds(200f);
		// post conditions of being given restaurant money to take to the bank
		assertEquals("cashier's moneyToDeposit should be 200 but is " + cashier.getMoneyToDeposit(), cashier.getMoneyToDeposit(), 200f );
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
		
		
		float moneyBefore = cashier.getPerson().getMoney();
		cashier.msgStopWorkingGoHome();
		//post conditions of leaving work
		Role r = cashier.getPerson().getRole("BankPatron");	BankPatron b = (BankPatron) r;
		
		assertEquals("cashier's state should be leavingWork but isn't", cashier.getState(), State.leavingWork);
		assertFalse("cashier isn't working", cashier.isWorking());
		assertTrue("cashier's scheduler returns true, leaving work", cashier.pickAndExecuteAnAction());
		assertTrue("cashier is going to the bank", cashier.getMoneyToDeposit() > 0);
		assertEquals("cashier added moneytodeposit to his own money", cashier.getPerson().getMoney(), moneyBefore+200f);
		//assertTrue("cashier added task to person to make him go to bank", cashier.getPerson());
//		
//		assertTrue("cashier is accessing bank patron role", b != null);
//		assertTrue("bank patron role is active", r.isActive);
//		assertTrue("bank patron's list doesn't have the right deposit action", r.hasAction("deposit$200.0"));
//		assertEquals("cashier's person has wrong company: + " + b.getCompany().getName(), b.getCompany(), (Building)restaurant);
//		assertEquals("cashier's person has wrong account num: + " + b.getCompanyAccountNum(), b.getCompanyAccountNum(), restaurant.getBankAccount());
		assertTrue("restaurant should have logged \"Received msgLeavingWork\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgLeavingWork"));
		assertTrue("person should have logged \"Received msgLeavingLocation\" but didn't. Instead, it logged " + cashierperson.log.getLastLoggedEvent().toString(),
				cashierperson.log.containsString("Received msgLeavingLocation"));
		assertFalse("Cashier's scheduler should have returned false. nothing to do.", cashier.pickAndExecuteAnAction());
	}
	
}
