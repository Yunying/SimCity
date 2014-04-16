/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package restaurant.cammarano.test;

import bank.BankAgent;
import global.PersonAgent;
import global.test.mock.MockPerson;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;
import market.MarketAgent;
import restaurant.cammarano.CammaranoRestaurantAgent;
import restaurant.cammarano.roles.*;
import restaurant.cammarano.roles.base.CammaranoRevolvingStand;

/**
 *
 * @author CMCammarano
 */
public class RestaurantTest extends TestCase {
	
	public CammaranoRestaurantAgent restaurant;
	public MarketAgent market;
	public BankAgent bank;
	
	public CammaranoHostRole host;
	public CammaranoCashierRole cashier;
	public CammaranoCookRole cook;
	public CammaranoWaiterRole waiter;
	public CammaranoSharedWaiterRole shared;
	public CammaranoCustomerRole customer;

	public List<MockPerson> people;
	public CammaranoRevolvingStand stand;
	
	public void setUp() throws Exception {
		super.setUp();		
		// Setting up the buildings
		bank = new BankAgent("bank");
		market = new MarketAgent("market", "loc");
		restaurant = new CammaranoRestaurantAgent("rest", market, bank);
		
		host = new CammaranoHostRole();
		cashier = new CammaranoCashierRole();
		cook = new CammaranoCookRole();
		waiter = new CammaranoWaiterRole();
		shared = new CammaranoSharedWaiterRole();
		customer = new CammaranoCustomerRole();
		
		stand = new CammaranoRevolvingStand();
		
		people = new ArrayList<MockPerson>();
		for (int cnt = 0; cnt < 6; cnt++) {
			people.add(new MockPerson("person" + cnt, null));
		}
		
		host.setPerson(people.get(0));
		host.setRestaurant(restaurant);
		
		cashier.setPerson(people.get(1));
		cashier.setRestaurant(restaurant);
		
		cook.setPerson(people.get(2));
		cook.setRestaurant(restaurant);
		cook.setStand(stand);
		
		waiter.setPerson(people.get(3));
		waiter.setRestaurant(restaurant);
		
		shared.setPerson(people.get(4));
		shared.setRestaurant(restaurant);
		shared.setStand(stand);
		
		customer.setPerson(people.get(5));
		customer.setHost(host);
		customer.setCashier(cashier);
	}	
	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 * @throws Exception 
	 */
	public void testAssigningRestaurantPeople() throws Exception {
		setUp(); // runs first before this test!		
		
		// Check preconditions
		assertEquals("The restaurant has no people inside of it. It doesn't.", restaurant.people.size(), 0);		
		assertEquals("PersonAgent should have an empty event log before the Person's msgUpdateTime is called. Instead, the Person's event log reads: " + restaurant.log.toString(), 0, restaurant.log.size());
		
		// Step 1 of the test (Testing adding a host to the list)
		restaurant.msgAtLocation(people.get(0), host, null);
		assertEquals("The restaurant should have 1 person in it. It doesn't.", restaurant.people.size(), 1);
		assertFalse("The host's scheduler should have returned false, but didn't.", host.pickAndExecuteAnAction());
		assertEquals("The restaurant should have an empty event log someone is added. Instead, the Person's event log reads: " + restaurant.log.toString(), 0, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"AssignHost called\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("AssignHost called"));
		assertFalse("The host's scheduler should have returned false, but didn't.", host.pickAndExecuteAnAction());
		
		// Step 2 of the test (Testing adding a new host to the list and removing the old host)
		host = new CammaranoHostRole();
		host.setPerson(people.get(0));
		host.setRestaurant(restaurant);
		restaurant.msgAtLocation(people.get(0), host, null);
		assertEquals("The restaurant should have 2 people in it. It doesn't.", restaurant.people.size(), 2);
		assertFalse("The host's scheduler should have returned false, but didn't.", host.pickAndExecuteAnAction());
		assertEquals("The restaurant should have a log with one entry. Instead, the Person's event log reads: " + restaurant.log.toString(), 1, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"AssignHost called\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("AssignHost called"));
		assertFalse("The host's scheduler should have returned false, but didn't.", host.pickAndExecuteAnAction());
				
		// Step 3 of the test (Testing adding a cashier to the list)
		restaurant.msgAtLocation(people.get(1), cashier, null);
		assertEquals("The restaurant should have 3 people in it. It doesn't.", restaurant.people.size(), 3);
		assertFalse("The cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("The restaurant should have a log with 3 entries. Instead, the Person's event log reads: " + restaurant.log.toString(), 3, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"AssignCashier called\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("AssignCashier called"));
		assertFalse("The cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		
		// Step 4 of the test (Testing adding a cook to the list)
		restaurant.msgAtLocation(people.get(2), cook, null);
		assertEquals("The restaurant should have 4 people in it. It doesn't.", restaurant.people.size(), 4);
		assertFalse("The cook's scheduler should have returned false, but didn't.", cook.pickAndExecuteAnAction());
		assertEquals("The restaurant should have a log with 4 entries. Instead, the Person's event log reads: " + restaurant.log.toString(), 4, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"AssignCook called\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("AssignCook called"));
		assertFalse("The cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
				
		// Step 5 of the test (Testing adding a waiter to the list)
		restaurant.msgAtLocation(people.get(3), waiter, null);
		assertEquals("The restaurant should have 5 people in it. It doesn't.", restaurant.people.size(), 5);
		assertEquals("The restaurant should have a log with 5 entries. Instead, the Person's event log reads: " + restaurant.log.toString(), 5, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"AssignWaiter called\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("AssignWaiter called"));
		
		// Step 5 of the test (Testing adding a waiter to the list)
		restaurant.msgAtLocation(people.get(4), shared, null);
		assertEquals("The restaurant should have 6 people in it. It doesn't.", restaurant.people.size(), 6);
		assertEquals("The restaurant should have a log with 6 entries. Instead, the Person's event log reads: " + restaurant.log.toString(), 6, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"AssignSharedWaiter called\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("AssignSharedWaiter called"));
	
		// Step 6 of the test (Adding a customer to the closed restaurant)
		restaurant.msgAtLocation(people.get(5), customer, null);
		assertEquals("The restaurant should have 7 people in it. It doesn't.", restaurant.people.size(), 7);
		assertFalse("The customer's scheduler should have returned false, but didn't.", customer.pickAndExecuteAnAction());
		assertEquals("The restaurant should have a log with 7 entries. Instead, the Person's event log reads: " + restaurant.log.toString(), 7, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"Restaurant closed\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("Restaurant closed"));
		assertFalse("The customer's scheduler should have returned false, but didn't.", customer.pickAndExecuteAnAction());
		
		// Step 6 of the test (Adding a customer to the open restaurant)
		restaurant.setOpen(true);
		restaurant.msgAtLocation(people.get(5), customer, null);
		assertEquals("The restaurant should have 8 people in it. It doesn't.", restaurant.people.size(), 8);
		assertFalse("The customer's scheduler should have returned false, but didn't.", customer.pickAndExecuteAnAction());
		assertEquals("The restaurant should have a log with 9 entries. Instead, the Person's event log reads: " + restaurant.log.toString(), 9, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"AssignCustomer called\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("AssignCustomer called"));
		assertFalse("The customer's scheduler should have returned false, but didn't.", customer.pickAndExecuteAnAction());
	}
}
