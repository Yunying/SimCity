package housing.test;


import interfaces.Building;









import java.util.ArrayList;
import java.util.List;

import sun.util.logging.resources.logging;

import global.actions.Action;
import housing.HouseAgent;
import housing.HousePersonRole;
import housing.interfaces.*;
import housing.test.mock.MockHousePerson;
import housing.test.mock.MockLandlord;
import housing.test.mock.MockPersonForHouse;
import junit.framework.TestCase;

public class HouseTest extends TestCase {
	
	
	HouseAgent house;
	HousePerson hp;
	MockHousePerson houseperson; 
	MockLandlord landlord;
	MockPersonForHouse person;
	ArrayList<Building> b = new ArrayList<Building>();
	ArrayList<Action> action = new ArrayList<Action>();
	public void setUp() throws Exception{
		super.setUp();
		houseperson = new MockHousePerson("mockhouseperson");
		hp = new HousePersonRole();

		house = new HouseAgent( "Residence", false);		
		person = new MockPersonForHouse("Poo", b, house);
		b.add(house);
		landlord = new MockLandlord("mockLandlord");
	}
	
	public void testPersonEnteringHouse() {
		houseperson.house = house;
		houseperson.setPerson(person);
		
	  //preconditions for person walking in house
		assertEquals("Person should have 0 logs in it. It does", houseperson.log.size(), 0);
		assertTrue("House should have no people waiting to enter in it " , house.waitingPeople.isEmpty());
		assertTrue("Action list is empty", house.actionsForHouse.isEmpty());
		
		//person messages house
		action.add(new Action("eat"));
		house.msgAtLocation(houseperson.getPerson(), houseperson, action);
		assertEquals("Houses waitingpeople list should have one person in it it. It doesnt ", house.waitingPeople.size(), 1);
		assertTrue("One action should have been added to the list. Its not" , house.actionsForHouse.size() == 1);
		
		//Houses scheduler after msgAtLocation
		assertTrue("House's scheduler should return true. It doesnt ", house.pickAndExecuteAnAction());
		assertTrue("Houses waitingpeople list should be empty. Its not empty", house.waitingPeople.isEmpty());
		assertTrue("House should have cleared their actions for house list. It didnt ", house.actionsForHouse.isEmpty());
		

		//msg to test whether person comes in house
		houseperson.msgComeIntoHouse(house, person, action);
		assertTrue("HousePerson should have a log that contains recieving a message " + houseperson.log.containsString("Recieved Message to Come into house"),  houseperson.log.containsString("Recieved Message to Come into house"));
		assertTrue("There should still be one action in action list. Theres not", action.size() == 1);

		//post conidtion When person has entered the house, there should be nothing left to do
		assertFalse("Scheduler for house shouldbe false. Its true", house.pickAndExecuteAnAction());
		
		
	}
	
	public void testRentBeingDueWhilePersonIsntThere() {
		house.setLandlord(landlord);		
		house.setHousePerson(houseperson);
		houseperson.setHouse(house);
		int rentrate = 30;
		
		List<Integer> bills = new ArrayList<Integer>();
		
		//preconditions
		assertEquals("Landlord should have 0 logs in it. It does", landlord.log.size(), 0);
		assertTrue("HousePersons bills should be empty", houseperson.getBills().isEmpty());
		
		//msgToTheLandlord
		house.msgRentDue(landlord, rentrate);
		assertEquals("HousePersons bills should have one, it doesnt" , houseperson.getBills().size() , 1);
		
		//post conditions
        assertFalse("Scheduler should retun false since there is noone waiting", house.pickAndExecuteAnAction());	
	}
	
	public void testRentBeingDueWhilePersonPresent() {
		house.setLandlord(landlord);		
		house.setHousePerson(houseperson);
		houseperson.setHouse(house);
		int rentrate = 30;
		house.waitingPeople.add(new MockHousePerson("dude"));

		
		//preconditions
		assertEquals("Landlord should have 0 logs in it. It does", landlord.log.size(), 0);
		assertTrue("HousePersons bills should be empty", houseperson.getBills().isEmpty());
		assertTrue("House should have one person waiting ", house.waitingPeople.size() == 1);
		
		//msgToTheLandlord
		house.msgRentDue(landlord, rentrate);
	
		//scheduler after message
		assertEquals("HousePersons bills should have one, it doesnt" , houseperson.getBills().size() , 1);
        assertTrue("Scheduler should retun true since there is someone waiting", house.pickAndExecuteAnAction());
        
        
        //postcondition person should have been removed from waiting list
        assertTrue("House should have removed waiting person from list ", house.waitingPeople.isEmpty());
        
        //post condition
        assertFalse("HousePerson scheduler returns false because there is nothing else for it todo", houseperson.pickAndExecuteAnAction());
        
        //precondition
        assertTrue("Houseperson should have 0 logs in it", house.log.size() == 0);
       
        
        houseperson.msgComeIntoHouse(house, person, action);
        
        //post condition
        assertTrue("HousePerson should have logged that they should come into house " , houseperson.log.containsString("Recieved Message to Come into house") == true);
		assertTrue("houseperson should see that have on bill, it contains a log " , houseperson.log.containsString("Recieved Message to Come into house") == true);
        
	}
	
	public void testMsgLeavingLocation() {
		//pre conditions
		house.setHousePerson(houseperson);
		houseperson.setHouse(house);
		houseperson.isActive = true;
		
		
		
		//houseperson leaving location
		house.msgLeavingLocation(houseperson);
		
		//post conditions
		assertTrue("Role should be inactive ", (!houseperson.isActive));
		assertFalse("Scheduler should return false ", house.pickAndExecuteAnAction());
		
		
	 }
	
	}

	
	

