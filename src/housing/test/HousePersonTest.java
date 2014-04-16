package housing.test;

import java.util.ArrayList;

import interfaces.Building;
import junit.framework.TestCase;
import global.actions.Action;
import global.test.mock.LoggedEvent;
import housing.HouseAgent;
import housing.HousePersonRole;
import housing.test.mock.MockHouse;
import housing.test.mock.MockLandlord;
import housing.test.mock.MockPersonForHouse;


public class HousePersonTest extends TestCase {



	MockHouse house;
	HousePersonRole houseperson; 
	MockLandlord landlord;
	MockPersonForHouse person;
	HouseAgent realhouse;
	ArrayList<Building> b = new ArrayList<Building>();

	ArrayList<Action> ac = new ArrayList<Action>();
	public void setUp() throws Exception{
		super.setUp();
		houseperson = new HousePersonRole();
		house = new MockHouse( "Residence", true);		
		person = new MockPersonForHouse("Lisa ", b, house);
		landlord = new MockLandlord("mockLandlord");
		realhouse = new HouseAgent("Real House",false);
		b.add(house);



	}

	public void testOneComingInHouseWithNoActions() {

		houseperson.setHouse(house);
		house.setHousePerson(houseperson);
		houseperson.setPerson(person);

		//preconditions
		assertTrue("There should be no logs in house ", house.log.size() == 0);

		//test message
		house.msgAtLocation(person, houseperson, ac);
		assertEquals("There should be a log that contains the string recieved msg at location ," , house.log.size(), 1);
		assertEquals("There should be no actions in action list, " ,ac.size(), 0);
		assertTrue("House should have logged msg at location ," , house.log.containsString("Recieved at location"));




		//precoditions for coming into house
		assertTrue("Person action shhould be zero. Its more than zero", ac.size() == 0);

		//test message
		houseperson.msgComeIntoHouse(house, person, ac);
		assertTrue("Action should still be one. Its not", ac.size() == 0);

		//postcondition
		assertFalse("HousePerson scheduler should return false ", houseperson.pickAndExecuteAnAction());


	}
	public void testComingWithActionEatingAtHomeNorm() {
		houseperson.setHouse(house);
		house.setHousePerson(houseperson);
		houseperson.setPerson(person);
		ac.add(new Action("eat"));
		houseperson.hungerLevel = 3;


		//preconditions
		assertTrue("There should be no logs in house ", house.log.size() == 0);
		assertEquals("There should be 5 foods in the house,  ", 4, houseperson.foods.size());
		house.msgAtLocation(person, houseperson, ac);
		assertEquals("There should be a log that contains the string recieved msg at location ," , house.log.size(), 1);
		assertEquals("There should be one action in action list, " ,ac.size(), 1);
		assertTrue("House should have logged msg at location ," , house.log.containsString("Recieved at location"));

		//step 2 going into scheduler which should return true
		assertTrue("Person action shhould be zero. Its not", houseperson.actions.size() == 0);

		//test message
		houseperson.msgComeIntoHouse(house, person, ac);
		assertTrue("Action should still be one. Its not", houseperson.actions.size() == 1);
		assertTrue("Actions should contain a log of eat", houseperson.actions.get(0).equals(ac.get(0)));

		//postcondition
		assertTrue("HousePerson scheduler should return true", houseperson.pickAndExecuteAnAction());
		assertEquals("Action size is now 0 ,. It isnt ", houseperson.actions.size() , 0);
		assertEquals("Food size is now 3", 3, houseperson.foods.size());
		assertEquals(" houseperson hunger level is reset to 0. Its not.", 0, houseperson.getHungerLevel());
		assertFalse("HousePerson scheduler should return false since there is nothing left to do ," , houseperson.pickAndExecuteAnAction());
	}


	public void testtwoLandlordWhileHousePersonPresent() {
		houseperson.setHouse(house);
		house.setLandlord(landlord);
		houseperson.setPerson(person);
		house.setHousePerson(houseperson);
		houseperson.setLandlord(landlord);
		houseperson.getPerson().setMoney(100f);
		//no logs

		//precond
		assertTrue("HousePerson should have no bills. It does ", houseperson.getBills().size() == 0);
		assertTrue("House should have no logs", house.log.size() ==  0);

		house.msgRentDue(landlord, 100);
		assertTrue("HousePerson should have one bill. It does ", houseperson.getBills().size()== 1);
		assertTrue("House contains a log that recieved bill " , house.log.containsString("Received rent due from Landlord"));

		//postconditions house rent due messsage
		assertTrue("HousePersons pickandexecute an action should return true bc there is more than one bill in list"
				+ ". It returns false." , houseperson.pickAndExecuteAnAction());
		assertTrue("Houseperson shouldnt have a bill inside ", houseperson.getBills().isEmpty());
		assertTrue("House persons money is less than it initially was, . Its not less." , (0 < houseperson.getPerson().getMoney()) && (houseperson.getPerson().getMoney() < 100f));
		assertEquals("Landlord has a log ", landlord.log.size(), 1);
		assertTrue("land lord should have just logged message ", landlord.log.containsString("Received rent"));
		assertFalse("Scheduler for houseperson returns false " , houseperson.pickAndExecuteAnAction());
	}

	public void testActionAndNoFood() {
		System.err.println("THIS TEST");

		houseperson.setHouse(house);

		houseperson.foods.remove(houseperson.foods.get(0));
		houseperson.foods.remove(houseperson.foods.get(0));
		houseperson.foods.remove(houseperson.foods.get(0));
		houseperson.foods.remove(houseperson.foods.get(0));
		


		house.setHousePerson(houseperson);
		houseperson.setPerson(person);
		ac.add(new Action("eat"));
		houseperson.hungerLevel = 3;

		//preconditions
		System.out.println("HELLO WORLD");
		assertTrue("Foods list is empty" , houseperson.foods.isEmpty());
		
		//mesage test
		houseperson.msgComeIntoHouse(house, person, ac);
		assertTrue("Size o person actions is 1", houseperson.actions.size() == 1);
		assertTrue("Actions should contain a log of eat", houseperson.actions.get(0).equals(ac.get(0)));


		//postcondition
		assertTrue("HousePerson scheduler should return true", houseperson.pickAndExecuteAnAction());
		assertEquals("Action size is still 1 ,. It isnt ", houseperson.actions.size() , 1);
		assertEquals("Food size is 0",  houseperson.foods.size(), 0);
		assertFalse(" housepersons hunger should stay the same. It doesn't. ", 2  == houseperson.getHungerLevel());
		assertTrue("Houseperson role now inactive ", houseperson.isActive == false);
	


		System.err.println("END");
	}

	public void testTimeToClean(){
		houseperson.setHouse(house);
		houseperson.setPerson(person);
		house.setHousePerson(houseperson);	
		houseperson.timeToClean = 6;

		//preconditions for house
		assertTrue("HousePerson list of actions is empty " , houseperson.actions.isEmpty());

		houseperson.msgUpdateTime(6, 0);
		assertEquals("There should now be one action inside the action list. There isnt. " , houseperson.actions.size(),1);
		assertTrue("There is an action such that contains the string 'clean' ", houseperson.actions.get(0).getTask().equals("clean"));

		//test the scheduler
		assertTrue("HousePersons scheduler will return ", houseperson.pickAndExecuteAnAction());
		
		//postcondition
		assertTrue("No more tasks in houseperson ", houseperson.actions.isEmpty());
		assertFalse("HousePerson doesnt have anythings else to do ," , houseperson.pickAndExecuteAnAction());
		assertTrue("HousePerson role now inactive ", houseperson.isActive == false);

	}
	public void testGroceriestoputinhouse() {
		houseperson.setHouse(house);
		houseperson.setPerson(person);
		house.setHousePerson(houseperson);	
		
		for(int i = 0; i < 4; i++ ){

			houseperson.foods.remove(houseperson.foods.get(0));
		}
		ac.add(new Action("eat"));
		//preconditions for house 
		assertTrue("HousePerson list of actions is empty " , houseperson.foods.isEmpty());
		assertEquals("House should have 0 logs " , house.log.size(), 0);

		ac.add(new Action("Steak"));
		ac.add(new Action("Soup"));
		ac.add(new Action("Pizza"));
		ac.add(new Action("Salad"));

		house.msgAtLocation(person, houseperson, ac);
	    assertTrue("House should have a log " , house.log.size() == 1);
		
		houseperson.msgComeIntoHouse(house, person, ac);
		assertEquals("Persons foodslist should not be empty. It is ", houseperson.foods.size(), 4);
		assertEquals("Persons actionlist should not be empty. It is ", houseperson.actions.size(), 1);
		assertTrue("Housepersons scheduler returns true ", houseperson.pickAndExecuteAnAction());
		
		//post conditions
		assertTrue("HousePerson should now have 0 actions for he just finished eating. There are still actions inside ", 
				houseperson.actions.isEmpty());
		assertTrue("House person should exit role", houseperson.isActive == false);
	}

}


