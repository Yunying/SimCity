package global.test;


import gui.animation.PersonGUI;
import bank.BankAgent;
import java.util.*;

import junit.framework.*;
import global.*;
import global.PersonAgent.*;
import global.enumerations.JobEnum;
import interfaces.*;
import housing.*;
import restaurant.cammarano.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class PersonTestUpdatingTime extends TestCase {
	//these are instantiated for each test separately via the setUp() method.
	PersonAgent person;
	PersonGUI gui;
	HouseAgent house;
	List<Building> buildings;
	
	JobEnum jobEnum;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		// Setting up the buildings
		buildings = new ArrayList<>();
		buildings.add(new CammaranoRestaurantAgent("rest", null, null));
		
		// Setting up the house
		house = new HouseAgent("home", false);
		house.setLocation("111 stadium way");
		
		// Setting up our person
		person = new PersonAgent("person", buildings, house);
		gui = new PersonGUI(person);
		person.setGui(gui);
		person.testingDisableAnimationCalls = true;
		
		person.AddJob(jobEnum.CammaranoHostRole, 16, 42);
	}	
	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 * @throws Exception 
	 */
	public void testUpdatingTime() throws Exception {
		setUp(); // runs first before this test!		
		
		// Check preconditions
		assertEquals("Person should have 0 times in it. It doesn't.", person.times.size(), 0);		
		assertEquals("PersonAgent should have an empty event log before the Person's msgUpdateTime is called. Instead, the Person's event log reads: " + person.log.toString(), 0, person.log.size());
		assertTrue("The person should be in testing mode. It is not", person.testingDisableAnimationCalls == true);
		
		// Step 1 of the test (Testing adding to the list)
		person.msgUpdateTime(0, 0);
		assertEquals("Person should have 1 time in it. It doesn't.", person.times.size(), 1);
		assertFalse("Cashier's scheduler should have returned false, but didn't.", person.pickAndExecuteAnAction());
		assertEquals("PersonAgent should have an empty event log when the Person's msgUpdateTime is called. Instead, the Person's event log reads: " + person.log.toString(), 0, person.log.size());
		
		// Step 2 of the test (Waking the person up)
		person.msgUpdateTime(14, 0);
		assertEquals("Person should have 2 times in it. It doesn't.", person.times.size(), 2);
		assertTrue("Person's scheduler should have returned true, but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person should have logged \"AddTaskWakeUpAndGetReady called\"  but didn't. His log reads instead: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("AddTaskWakeUpAndGetReady called"));
		assertTrue("Times should contain a time logged at 0 at element 0. It contains something else instead: " + person.times.get(0).getTime(), person.times.get(0).getTime() == 0);
		assertTrue("Times should contain a time logged at 14 at element 1. It contains something else instead: " + person.times.get(1).getTime(), person.times.get(1).getTime() == 14);
		assertTrue("Times should be active at element 0. It isn't.", person.times.get(0).hasPassed == false);
		assertTrue("Times should be inactive at element 1. It isn't.", person.times.get(1).hasPassed == true);
		assertTrue("Person's scheduler should have returned true (HousePersonRole is active), but didn't.", person.pickAndExecuteAnAction());
		person.roles.get(0).isActive = false;
		assertFalse("Person's scheduler should have returned false, but didn't.", person.pickAndExecuteAnAction());
		
		
		// Step 2 of the test (Getting lunch)
		person.msgUpdateTime(24, 0);
		assertEquals("Person should have 3 times in it. It doesn't.", person.times.size(), 3);
		assertTrue("Person's scheduler should have returned true, but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person should have logged \"AddTaskGetFood called\"  but didn't. His log reads instead: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("AddTaskGetFood called"));
		assertTrue("Times should contain a time logged at 0 at element 0. It contains something else instead: " + person.times.get(0).getTime(), person.times.get(0).getTime() == 0);
		assertTrue("Times should contain a time logged at 14 at element 1. It contains something else instead: " + person.times.get(1).getTime(), person.times.get(1).getTime() == 14);
		assertTrue("Times should contain a time logged at 24 at element 2. It contains something else instead: " + person.times.get(2).getTime(), person.times.get(2).getTime() == 24);
		assertTrue("Times should be active at element 0. It isn't.", person.times.get(0).hasPassed == false);
		assertTrue("Times should be inactive at element 1. It isn't.", person.times.get(1).hasPassed == true);
		assertTrue("Times should be inactive at element 2. It isn't.", person.times.get(2).hasPassed == true);
		assertTrue("Person's scheduler should have returned true (A role, Restaurant or House is active), but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person should have logged \"GoToRestaurantToEat called\"  but didn't. His log reads instead: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("GoToRestaurantToEat called"));
		person.roles.get(person.roles.size() - 1).isActive = false;
		assertFalse("Person's scheduler should have returned false, but didn't.", person.pickAndExecuteAnAction());
		
		// Step 3 of the test (Getting dinner)
		person.msgUpdateTime(34, 0);
		assertEquals("Person should have 4 times in it. It doesn't.", person.times.size(), 4);
		assertTrue("Person's scheduler should have returned true, but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person should have logged \"AddTaskGetFood called\"  but didn't. His log reads instead: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("AddTaskGetFood called"));
		assertTrue("Times should contain a time logged at 0 at element 0. It contains something else instead: " + person.times.get(0).getTime(), person.times.get(0).getTime() == 0);
		assertTrue("Times should contain a time logged at 14 at element 1. It contains something else instead: " + person.times.get(1).getTime(), person.times.get(1).getTime() == 14);
		assertTrue("Times should contain a time logged at 24 at element 2. It contains something else instead: " + person.times.get(2).getTime(), person.times.get(2).getTime() == 24);
		assertTrue("Times should contain a time logged at 34 at element 2. It contains something else instead: " + person.times.get(3).getTime(), person.times.get(3).getTime() == 34);
		assertTrue("Times should be active at element 0. It isn't.", person.times.get(0).hasPassed == false);
		assertTrue("Times should be inactive at element 1. It isn't.", person.times.get(1).hasPassed == true);
		assertTrue("Times should be inactive at element 2. It isn't.", person.times.get(2).hasPassed == true);
		assertTrue("Times should be inactive at element 3. It isn't.", person.times.get(3).hasPassed == true);
		assertTrue("Person's scheduler should have returned true (A role, Restaurant or House is active), but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person should have logged \"GoToRestaurantToEat called\"  but didn't. His log reads instead: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("GoToRestaurantToEat called"));
		person.roles.get(person.roles.size() - 1).isActive = false;
		assertFalse("Person's scheduler should have returned false, but didn't.", person.pickAndExecuteAnAction());
		
	
		// Step 3 of the test (Going to work)
		person.msgUpdateTime(16, 0);
		assertEquals("Person should have 5 times in it. It doesn't.", person.times.size(), 5);
		assertTrue("Person's scheduler should have returned true, but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person should have logged \"AddTaskGoToWork called\"  but didn't. His log reads instead: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("AddTaskGoToWork called"));
		assertTrue("Times should contain a time logged at 0 at element 0. It contains something else instead: " + person.times.get(0).getTime(), person.times.get(0).getTime() == 0);
		assertTrue("Times should contain a time logged at 14 at element 1. It contains something else instead: " + person.times.get(1).getTime(), person.times.get(1).getTime() == 14);
		assertTrue("Times should contain a time logged at 24 at element 2. It contains something else instead: " + person.times.get(2).getTime(), person.times.get(2).getTime() == 24);
		assertTrue("Times should contain a time logged at 34 at element 3. It contains something else instead: " + person.times.get(3).getTime(), person.times.get(3).getTime() == 34);
		assertTrue("Times should contain a time logged at 16 at element 4. It contains something else instead: " + person.times.get(4).getTime(), person.times.get(4).getTime() == 16);
		assertTrue("Times should be active at element 0. It isn't.", person.times.get(0).hasPassed == false);
		assertTrue("Times should be inactive at element 1. It isn't.", person.times.get(1).hasPassed == true);
		assertTrue("Times should be inactive at element 2. It isn't.", person.times.get(2).hasPassed == true);
		assertTrue("Times should be inactive at element 3. It isn't.", person.times.get(3).hasPassed == true);
		assertTrue("Times should be inactive at element 4. It isn't.", person.times.get(4).hasPassed == true);
		assertTrue("Person's scheduler should have returned true (A working role is active), but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person should have logged \"GoToWork called\"  but didn't. His log reads instead: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("GoToWork called"));
		person.roles.get(person.roles.size() - 1).isActive = false;
		assertFalse("Person's scheduler should have returned false, but didn't.", person.pickAndExecuteAnAction());	
		
		// Step 3 of the test (Going to bed)
		person.msgUpdateTime(42, 0);
		assertEquals("Person should have 6 times in it. It doesn't.", person.times.size(), 6);
		assertTrue("Person's scheduler should have returned true, but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Times should contain a time logged at 0 at element 0. It contains something else instead: " + person.times.get(0).getTime(), person.times.get(0).getTime() == 0);
		assertTrue("Times should contain a time logged at 14 at element 1. It contains something else instead: " + person.times.get(1).getTime(), person.times.get(1).getTime() == 14);
		assertTrue("Times should contain a time logged at 24 at element 2. It contains something else instead: " + person.times.get(2).getTime(), person.times.get(2).getTime() == 24);
		assertTrue("Times should contain a time logged at 34 at element 3. It contains something else instead: " + person.times.get(3).getTime(), person.times.get(3).getTime() == 34);
		assertTrue("Times should contain a time logged at 16 at element 4. It contains something else instead: " + person.times.get(4).getTime(), person.times.get(4).getTime() == 16);
		assertTrue("Times should contain a time logged at 42 at element 5. It contains something else instead: " + person.times.get(5).getTime(), person.times.get(5).getTime() == 42);
		assertTrue("Times should be active at element 0. It isn't.", person.times.get(0).hasPassed == false);
		assertTrue("Times should be inactive at element 1. It isn't.", person.times.get(1).hasPassed == true);
		assertTrue("Times should be inactive at element 2. It isn't.", person.times.get(2).hasPassed == true);
		assertTrue("Times should be inactive at element 3. It isn't.", person.times.get(3).hasPassed == true);
		assertTrue("Times should be inactive at element 4. It isn't.", person.times.get(4).hasPassed == true);
		assertTrue("Times should be inactive at element 5. It isn't.", person.times.get(5).hasPassed == true);
		assertTrue("Person's scheduler should have returned true (HousePersonRole), but didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person should have logged \"GoHome called\"  but didn't. His log reads instead: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("GoHome called"));
		person.roles.get(person.roles.size() - 1).isActive = false;
		assertFalse("Person's scheduler should have returned false, but didn't.", person.pickAndExecuteAnAction());	
			
	}
}