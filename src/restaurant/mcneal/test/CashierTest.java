package restaurant.mcneal.test;


import restaurant.mcneal.roles.McNealCashierRole;
import restaurant.mcneal.test.mock.MockCustomer;
import restaurant.mcneal.test.mock.MockTruckDriver;
import restaurant.mcneal.test.mock.MockWaiter;
import junit.framework.TestCase;

public class CashierTest extends TestCase {
	
	McNealCashierRole cashier;
	
	MockWaiter waiter;
	MockWaiter waiter2;
	MockCustomer customer;
	MockCustomer customer2;
	//MockMarket market;
	//MockMarket market2;
	MockTruckDriver driver;
	MockTruckDriver driver2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new McNealCashierRole();		
		customer = new MockCustomer("mockcustomer");	
		customer2 = new MockCustomer("mockcustomer2");
		waiter = new MockWaiter("mockwaiter");
		waiter2 = new MockWaiter("mockwaiter2");
	//	market = new MockMarket("market");
	//	market2 = new MockMarket("market2");
		driver = new MockTruckDriver("driver");
		driver2 = new MockTruckDriver("driver2");
		//market2.cashier = cashier;
		//market.cashier = cashier;
		
		//customer
	//	customer.cashier = cashier;
	//	customer2.cashier = cashier;
		//waiter.cashier = cashier;
		//waiter2.cashier = cashier;
		
	}	

}
