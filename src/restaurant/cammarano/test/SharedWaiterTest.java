/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package restaurant.cammarano.test;

import java.util.HashMap;

import bank.BankAgent;
import global.test.mock.MockPerson;
import junit.framework.TestCase;
import market.MarketAgent;
import restaurant.cammarano.CammaranoRestaurantAgent;
import restaurant.cammarano.roles.*;
import restaurant.cammarano.roles.CammaranoHostRole.Table;
import restaurant.cammarano.roles.base.*;

/**
 *
 * @author CMCammarano
 */
public class SharedWaiterTest extends TestCase {
	
	// Buildings
	public BankAgent bank;
	public MarketAgent market;
	public CammaranoRestaurantAgent restaurant;
	
	// Roles
	public CammaranoCookRole cook;
	public CammaranoHostRole host;
	public CammaranoSharedWaiterRole waiter;
	public CammaranoCustomerRole customer;
	
	// People
	public MockPerson mockcook;
	public MockPerson mockwaiter;
	public MockPerson mockcustomer;
	
	// Misc
	public CammaranoRevolvingStand stand;
	
	
	public void setUp() throws Exception {
		super.setUp();
		
		bank = new BankAgent("bank");
		market = new MarketAgent("market", "loc");
		
		restaurant = new CammaranoRestaurantAgent("restaurant", market, bank);
		
		stand  = new CammaranoRevolvingStand();
		
		mockwaiter = new MockPerson("mockwaiter", null);
		mockcook = new MockPerson("mockcook", null);
		mockcustomer = new MockPerson("mockcustomer", null);
		
		host = new CammaranoHostRole();
		
		cook = new CammaranoCookRole();
		cook.setPerson(mockcook);
		cook.setStand(stand);
		cook.setRestaurant(restaurant);
		
		waiter = new CammaranoSharedWaiterRole();
		waiter.setPerson(mockwaiter);
		waiter.setStand(stand);
		waiter.setCook(cook);
		
		customer = new CammaranoCustomerRole();
		customer.setPerson(mockcustomer);
	}
	
	public void testSharedDataWaiter() throws Exception {
		setUp();
		
		// Check preconditions before running tests
		assertEquals("The cook has no orders in it. It does.", cook.orders.size(), 0);	
		assertEquals("The waiter has no customers in it. It does.", waiter.customers.size(), 0);	
		assertEquals("The cook should not have any logged events. Instead, the cook's event log reads: " + cook.log.toString(), 0, cook.log.size());
		assertEquals("The waiter should not have any logged events. Instead, the waiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		assertEquals("The stand should not have any logged events. Instead, the stand event log reads: " + stand.log.toString(), 0, stand.log.size());
		
		// Adding a customer to the waiter and setting them to order. This should cover the entire scenario.
		waiter.msgSitCustomer(customer, host.new Table(0, 0, 0), new HashMap<String, Float>());
		assertEquals("The waiter should have 1 customer in it. It doesn't.", waiter.customers.size(), 1);
		assertTrue("The waiter's scheduler should have returned true, but didn't.", waiter.pickAndExecuteAnAction());
		assertFalse("The cook's scheduler should have returned false, but didn't.", cook.pickAndExecuteAnAction());
		waiter.msgReadyToOrder(customer);
		assertTrue("The waiter's scheduler should have returned true, but didn't.", waiter.pickAndExecuteAnAction());
		waiter.msgHereIsMyChoice(customer, "steak");
		assertTrue("The waiter's scheduler should have returned true, but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("The stand should have logged \"Order steak added to the stand\"  but didn't. Its log reads instead: " + stand.log.getLastLoggedEvent().toString(), stand.log.containsString("Order steak added to the stand."));
		assertTrue("The cook's scheduler should have returned true, but didn't.", cook.pickAndExecuteAnAction());
		assertTrue("The cook's scheduler should have returned true, but didn't.", cook.pickAndExecuteAnAction());		
	}
}
