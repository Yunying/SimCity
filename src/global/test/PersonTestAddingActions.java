package global.test;


import java.util.*;

import bank.BankPatronRole;
import market.MarketCustomerRole;
import junit.framework.*;
import global.*;
import global.actions.*;
import global.PersonAgent.*;
import global.enumerations.JobEnum;
import global.test.mock.MockRole;
import global.test.persontest.*;
import interfaces.*;
import housing.*;
import restaurant.cammarano.*;
import restaurant.cammarano.roles.CammaranoCustomerRole;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class PersonTestAddingActions extends TestCase {
	//these are instantiated for each test separately via the setUp() method.
	PersonAgent person;
	MockBank bank;
	MockHouse house;
	MockMarket market;
	MockRestaurant restaurant;
	
	MockRole role;
	
	JobEnum jobEnum;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		
		// Setting up the buildings
		bank = new MockBank("bank");
		house = new MockHouse("house");
		market = new MockMarket("market");
		restaurant = new MockRestaurant("restaurant");
		
		// Manually creating roles
		role = new MockRole();
		role.setPerson(person);
		
		// Setting up our person
		person = new PersonAgent("person", null, null);
		person.testingDisableAnimationCalls = true;
	}	
	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 * @throws Exception 
	 */
	public void testAddingTask() throws Exception {
		setUp(); // runs first before this test!		
		
		// Check preconditions
		assertEquals("Person should have 0 tasks in it. It doesn't.", person.actionsToComplete.size(), 0);		
		assertEquals("PersonAgent should have an empty event log before the Person's scheduler is called. Instead, the Person's event log reads: " + person.log.toString(), 0, person.log.size());
		assertTrue("The person should be in testing mode. It is not", person.testingDisableAnimationCalls == true);
		
		// Step 1 of the test (Adding the basic task to go home)
		person.actionsToComplete.add(person.new PersonAction("gohome", house, role, new ArrayList<Action>()));
		assertEquals("Person should have 1 action in it. It doesn't.", person.actionsToComplete.size(), 1);
		assertTrue("The new action in the list should be active. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getIsActive());
		assertEquals("PersonAgent should have an empty event log when the Person's scheduler is first called. Instead, the Person's event log reads: " + person.log.toString(), 0, person.log.size());
		assertEquals("MockHouse should have an empty event log when the MockHouse scheduler is first called. Instead, the MockHouse event log reads: " + house.log.toString(), 0, house.log.size());
		assertTrue("Person scheduler should have returned true (new action added), but didn't.", person.pickAndExecuteAnAction());
		assertFalse("The new action in the list should be inactive since the role is now running. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getIsActive());
		assertTrue("The role inside of our list should now be active. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getRole().isActive == true);
		assertTrue("MockHouse should have logged \"msgAtLocation Received\"  but didn't. His log reads instead: " + house.log.getLastLoggedEvent().toString(), house.log.containsString("msgAtLocation Received"));
		person.actionsToComplete.get(person.actionsToComplete.size() - 1).getRole().isActive = false; // We now set the role as inactive
		assertFalse("Person scheduler should have returned false (no active roles), but didn't.", person.pickAndExecuteAnAction());
				
		// Step 2 of the test (Adding go to bank task)
		person.actionsToComplete.add(person.new PersonAction("bank", bank, role, new ArrayList<Action>()));
		assertEquals("Person should have 2 action in it. It doesn't.", person.actionsToComplete.size(), 2);
		assertTrue("The new action in the list should be active. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getIsActive());
		assertEquals("MockBank should have an empty event log when the MockBank scheduler is first called. Instead, the MoMockBankckHouse event log reads: " + bank.log.toString(), 0, bank.log.size());
		assertTrue("Person scheduler should have returned true (new action added), but didn't.", person.pickAndExecuteAnAction());
		assertFalse("The new action in the list should be inactive since the role is now running. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getIsActive());
		assertTrue("The role inside of our list should now be active. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getRole().isActive == true);
		assertTrue("MockBank should have logged \"msgAtLocation Received\"  but didn't. His log reads instead: " + bank.log.getLastLoggedEvent().toString(), bank.log.containsString("msgAtLocation Received"));
		person.actionsToComplete.get(person.actionsToComplete.size() - 1).getRole().isActive = false; // We now set the role as inactive
		assertFalse("Person scheduler should have returned false (no active roles), but didn't.", person.pickAndExecuteAnAction());
		
		// Step 3 of the test (Getting lunch)
		person.actionsToComplete.add(person.new PersonAction("eatrest", restaurant, role, new ArrayList<Action>()));
		assertEquals("Person should have 3 action in it. It doesn't.", person.actionsToComplete.size(), 3);
		assertTrue("The new action in the list should be active. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getIsActive());
		assertEquals("MockRestaurant should have an empty event log when the scheduler is first called. Instead, the MockRestaurant event log reads: " + restaurant.log.toString(), 0, restaurant.log.size());
		assertTrue("Person scheduler should have returned true (new action added), but didn't.", person.pickAndExecuteAnAction());
		assertFalse("The new action in the list should be inactive since the role is now running. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getIsActive());
		assertTrue("The role inside of our list should now be active. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getRole().isActive == true);
		assertTrue("MockRestaurant should have logged \"msgAtLocation Received\"  but didn't. His log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("msgAtLocation Received"));
		person.actionsToComplete.get(person.actionsToComplete.size() - 1).getRole().isActive = false; // We now set the role as inactive
		assertFalse("Person scheduler should have returned false (no active roles), but didn't.", person.pickAndExecuteAnAction());
		
		// Step 3 of the test (Getting dinner)
		person.actionsToComplete.add(person.new PersonAction("market", market, role, new ArrayList<Action>()));
		assertEquals("Person should have 4 action in it. It doesn't.", person.actionsToComplete.size(), 4);
		assertTrue("The new action in the list should be active. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getIsActive());
		assertEquals("MockMarket should have an empty event log when the scheduler is first called. Instead, the MockMarket event log reads: " + market.log.toString(), 0, market.log.size());
		assertTrue("Person scheduler should have returned true (new action added), but didn't.", person.pickAndExecuteAnAction());
		assertFalse("The new action in the list should be inactive since the role is now running. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getIsActive());
		assertTrue("The role inside of our list should now be active. It isn't.", person.actionsToComplete.get(person.actionsToComplete.size() - 1).getRole().isActive == true);
		assertTrue("MockMarket should have logged \"msgAtLocation Received\"  but didn't. His log reads instead: " + market.log.getLastLoggedEvent().toString(), market.log.containsString("msgAtLocation Received"));
		person.actionsToComplete.get(person.actionsToComplete.size() - 1).getRole().isActive = false; // We now set the role as inactive
		assertFalse("Person scheduler should have returned false (no active roles), but didn't.", person.pickAndExecuteAnAction());
		
	}
}