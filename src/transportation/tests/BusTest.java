package transportation.tests;

import java.util.ArrayList;
import java.util.List;

import transportation.BusAgent;
import transportation.TransportationMediator;
import transportation.TransportationRole;
import transportation.Interfaces.Bus;
import transportation.Interfaces.Mediator;
import transportation.Interfaces.Passenger;
import transportation.tests.mock.MockBuilding;
import transportation.tests.mock.MockBus;
import transportation.tests.mock.MockBusStop;
import transportation.tests.mock.MockMediator;
import transportation.tests.mock.MockPassenger;
import junit.framework.TestCase;

public class BusTest extends TestCase{
	MockPassenger person;
	MockPassenger person2;
	MockPassenger person3;
	MockPassenger person4;
	MockBusStop bs1;
	MockBusStop bs2;
	MockBusStop bs3;
	MockBusStop bs4;
	BusAgent bus;
	MockMediator mediator;
	
	
	public void setUp() throws Exception{
		mediator = new MockMediator("mediator");
		bus = new BusAgent(mediator,"bus");
		person = new MockPassenger("person");
		person2 = new MockPassenger("person2");
		person3 = new MockPassenger("person3");
		person4 = new MockPassenger("person4");
		
		bs1 = new MockBusStop("bs1", 0, 100, 100, bus);
		bs2 = new MockBusStop("bs2", 0, 200, 200, bus);
		bs3 = new MockBusStop("bs3", 0, 300, 300, bus);
		bs4 = new MockBusStop("bs4", 0, 400, 400, bus);
		
		bus.addBusStop(bs1);
		bus.addBusStop(bs2);
		bus.addBusStop(bs3);
		bus.addBusStop(bs4);
		
		person.setStop(bs1, bs2);
		person2.setStop(bs1, bs3);
		person3.setStop(bs3, bs4);
		
		person.log.clear();
		person2.log.clear();
		person3.log.clear();
		bus.log.clear();
		bs1.log.clear();
		bs2.log.clear();
		bs3.log.clear();
		mediator.log.clear();
		
	}
	
	public void testBusStartOperating(){
		System.out.println("Test: Start operating in the morning");
		
		//Check pre conditions
		assertEquals("There should be no event in bus's log", 
				0, bus.log.size());
		assertEquals("There should be no event in person's log", 
				0, person.log.size());
		assertEquals("The person should start at bs1", 
				bs1, person.getStartStop());
		assertEquals("The person should get off at bs2", 
				bs2, person.getEndStop());
		assertEquals("The bus's current carry should be 0", 
				0, bus.getCurrentCarry());
		assertEquals("The mediator's log should be clean", 
				0, mediator.log.size());
		
		//Bus starts operation
		bus.msgUpdateTime(10);
		assertEquals("Bus should now have one event in its log",
				1,bus.log.size());
		assertEquals("Bus's state should now be readyToOperate", 
				"readyToOperate", bus.state.name());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertTrue("Bus should now be active", 
				bus.isActive());
		assertEquals("There should be one event in mediator's log", 
				1, mediator.log.size());
		assertEquals("The bus's current stop should be the first stop", 
				bs1, bus.getCurrentStop());
		assertEquals("The bus's next stop should be the second stop", 
				bs2, bus.getNextStop());
		assertEquals("There should be one event in the first stop's log", 
				1, bs1.log.size());
		assertEquals("The bus's state should be atStop", 
				"atStop", bus.state.name());
	}
	
	public void testBusStopOperating(){
		System.out.println("Test: Stop operating at night");
		
		//Check pre conditions
		assertEquals("There should be no event in bus's log",
				0, bus.log.size());
		assertEquals("There should be no event in person's log", 
				0, person.log.size());
		assertEquals("The person should start at bs1", 
				bs1, person.getStartStop());
		assertEquals("The person should get off at bs2", 
				bs2, person.getEndStop());
		assertEquals("The bus's current carry should be 0", 
				0, bus.getCurrentCarry());
		assertEquals("The mediator's log should be clean",
				0, mediator.log.size());
		
		//Test begins
		//First ensure it's working
		bus.msgUpdateTime(10);
		assertEquals("Bus should now have one event in its log",
				1,bus.log.size());
		assertEquals("Bus's state should now be readyToOperate", 
				"readyToOperate", bus.state.name());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertTrue("Bus should now be active", 
				bus.isActive());
		assertEquals("There should be one event in mediator's log", 
				1, mediator.log.size());
		assertEquals("The bus's current stop should be the first stop", 
				bs1, bus.getCurrentStop());
		assertEquals("The bus's next stop should be the second stop", 
				bs2, bus.getNextStop());
		assertEquals("There should be one event in the first stop's log", 
				1, bs1.log.size());
		assertEquals("The bus's state should be atStop", 
				"atStop", bus.state.name());
		
		//Then what if the day ends; directly call move to next stop
		bus.msgUpdateTime(40);
		bus.setCurrentStop(bs4);
		assertEquals("There should be two events in the bus's log now", 
				2, bus.log.size());
		assertTrue("readyToPark should be set true",
				bus.readyToPark);
		assertEquals("The current stop should be bs4", 
				bs4, bus.getCurrentStop());
		assertEquals("bs4 should be last one in the bus's stop list", 
				bus.stops.size()-1, bus.stops.indexOf(bs4));
		
		bus.setNextStop(bs1);
		assertEquals("bs1 should be the first one in the bus's stop list", 
				bus.stops.get(0), bs1);
		assertEquals("bs1 should be the bus's next stop", 
				bus.getNextStop(), bs1);
		
		bus.moveToNextStop();
		assertEquals("There should be three events in the bus's log now", 
				3, bus.log.size());
		assertEquals("The bus's current stop should be bs1", 
				bus.getCurrentStop(), bs1);
		assertEquals("The index of the current stop should be 0", 
				bus.stops.indexOf(bs1), 0);
		assertTrue("readyToPark should be set true",
				bus.readyToPark);
		assertEquals("The bus state should be readyToRest", 
				"readyToRest", bus.state.name());
		
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("The bus's current stop should be null", 
				bus.getCurrentStop(), null);
		assertEquals("The bus's next stop should be null", 
				bus.getNextStop(), null);
		assertEquals("There should be four events in the bus's log now", 
				4, bus.log.size());
		assertEquals("The bus state should be atParkingLot", 
				"atParkingLot", bus.state.name());
		assertEquals("The bus should be inactive now", 
				bus.isActive(), false);
	}
	
	public void testNormalSituationOnePassengerFromStartToDestination(){
		System.out.println("Test: Normal Operating with one passenger");
		
		//Check pre conditions
		assertEquals("There should be no event in bus's log",
				0, bus.log.size());
		assertEquals("There should be no event in person's log", 
				0, person.log.size());
		assertEquals("The person should start at bs1", 
				bs1, person.getStartStop());
		assertEquals("The person should get off at bs2", 
				bs2, person.getEndStop());
		assertEquals("The bus's current carry should be 0", 
				0, bus.getCurrentCarry());
		assertEquals("The mediator's log should be clean",
				0, mediator.log.size());
		
		//Update time to get the bus work
		//Bus starts operation
		bus.msgUpdateTime(10);
		assertEquals("Bus should now have one event in its log",
						1,bus.log.size());
		assertEquals("Bus's state should now be readyToOperate", 
						"readyToOperate", bus.state.name());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
						bus.pickAndExecuteAnAction());
		assertTrue("Bus should now be active", 
						bus.isActive());
		assertEquals("There should be one event in mediator's log", 
						1, mediator.log.size());
		assertEquals("The bus's current stop should be the first stop", 
						bs1, bus.getCurrentStop());
		assertEquals("The bus's next stop should be the second stop", 
						bs2, bus.getNextStop());
		assertEquals("There should be one event in the first stop's log", 
						1, bs1.log.size());
		assertEquals("The bus's state should be atStop", 
						"atStop", bus.state.name());
		
		//At stop
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("Bus should now have two events in its log",
				2,bus.log.size());
		assertEquals("The bus should now have any passengers on the bus at this time",
				0, bus.getPassengers().size());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertFalse("The bus's numberTold boolean should be false", 
				bus.getNumberTold());
		
		bus.msgTotalWaitingPeople(1);
		assertEquals("The bus should set the boarding people number correctly", 1, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", "unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgYouCanGo();
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		
		//Bus-Passenger interaction
		bus.msgCanIGetOnToBus(person);
		assertEquals("There should be one event in the person's log now", 
				1,person.log.size());
		assertEquals("Bus's current carry shouls still be 0 since the person has not come aboard yet", 
				0, bus.getCurrentCarry());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgImAboard(person);
		assertEquals("There should be one person in bus's passenger list now", 
				1, bus.getPassengers().size());
		assertEquals("Boarding people left should be zero now", 
				0, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertEquals("Bus's stop count should be zero", 
				0 , bus.getStopCount());
		assertEquals("Bus's current carry should be zero since it's not updated yet", 
				0, bus.getCurrentCarry());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Bus moving now
		assertEquals("Bus should now have three events in its log",
				3,bus.log.size());
		//All initialization before arriving at next stop
		assertFalse("Bus's numberTold should be initialized as false", 
				bus.getNumberTold());
		assertEquals("Bus's current carry should now be 1", 
				1, bus.getPassengers().size());
		assertEquals("Bus's stopCount should now be initialized to 0", 
				0, bus.getStopCount());
		assertFalse("bus's readyToGo should be initialized to false", 
				bus.readyToGo);
		bus.msgAnimation();
		assertEquals("There should be two events in the first stop's log now", 
				2, bs1.log.size());
		assertEquals("There should be two events in the passenger's log now saying we are going to next", 
				2, person.log.size());
		assertEquals("The current stop should be set to bs2", 
				bs2, bus.getCurrentStop());
		assertFalse("readyToPark should be set to equal to false", 
				bus.readyToPark);
		assertEquals("bs2 should now have one message in the log", 
				1, bs2.log.size());
		assertEquals("The bus's state should be atStop now", 
				"atStop", bus.state.name());
		
		//At next stop unloading and all stuffs
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("Bus should now have three events in its log",
				4,bus.log.size());
		assertEquals("The bus should now have one passenger on the bus at this time",
				1, bus.getCurrentCarry());
		assertEquals("There should be three events in the passenger's log now", 
				3, person.log.size());
		assertEquals("The bus should have told the correct corrent stop", 
				bs2, person.getCurrentStop());
		assertEquals("The should should have told the corrent next stop", 
				bs3, person.getNextStop());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Passenger wants to leave bus
		bus.msgLeavingBus(person);
		assertEquals("The bus's stopCount should be 1", 
				1, bus.getStopCount());
		assertEquals("The bus should now have no passengers on the bus", 
				0, bus.getPassengers().size());
		assertEquals("The bus should have a current carry number of 1 since it has not been updated",
				1, bus.getCurrentCarry());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Bus-BusStop interaction
		assertFalse("The bus's numberTold boolean should be false", 
				bus.getNumberTold());
		bus.msgTotalWaitingPeople(0);
		assertEquals("The bus should set the boarding people number correctly", 
				0, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgYouCanGo();
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertEquals("Bus's boarding people should be 0",
				0,bus.getBoardingPeople());
		assertTrue("The bus's stopCount should be the same as currentCarry",
				bus.getCurrentCarry() == bus.getStopCount());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Check post conditions
		assertEquals("Bus's current carry should be zero",
				0 , bus.getCurrentCarry());
		assertEquals("Bus's current size of passenger should be 0", 
				0, bus.getPassengers().size());
	}
	
	public void testOnePassengerStayingAndLeaving(){
		System.out.println("Test: Normal Operating with one passenger staying and leaving");
		
		//Check pre conditions
		assertEquals("There should be no event in bus's log",
				0, bus.log.size());
		assertEquals("There should be no event in person's log", 
				0, person2.log.size());
		assertEquals("The person should start at bs1", 
				bs1, person2.getStartStop());
		assertEquals("The person should get off at bs3", 
				bs3, person2.getEndStop());
		assertEquals("The bus's current carry should be 0", 
				0, bus.getCurrentCarry());
		assertEquals("The mediator's log should be clean",
				0, mediator.log.size());
		
		//Update time to get the bus work
		//Bus starts operation
		bus.msgUpdateTime(10);
		assertEquals("Bus should now have one event in its log",
						1,bus.log.size());
		assertEquals("Bus's state should now be readyToOperate", 
						"readyToOperate", bus.state.name());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
						bus.pickAndExecuteAnAction());
		assertTrue("Bus should now be active", 
						bus.isActive());
		assertEquals("There should be one event in mediator's log", 
						1, mediator.log.size());
		assertEquals("The bus's current stop should be the first stop", 
						bs1, bus.getCurrentStop());
		assertEquals("The bus's next stop should be the second stop", 
						bs2, bus.getNextStop());
		assertEquals("There should be one event in the first stop's log", 
						1, bs1.log.size());
		assertEquals("The bus's state should be atStop", 
						"atStop", bus.state.name());
		
		//At stop
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("Bus should now have two events in its log",
				2,bus.log.size());
		assertEquals("The bus should now have any passengers on the bus at this time",
				0, bus.getPassengers().size());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertFalse("The bus's numberTold boolean should be false", 
				bus.getNumberTold());
		
		bus.msgTotalWaitingPeople(1);
		assertEquals("The bus should set the boarding people number correctly", 1, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", "unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgYouCanGo();
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		
		//Bus-Passenger interaction
		bus.msgCanIGetOnToBus(person2);
		assertEquals("There should be one event in the person's log now", 
				1,person2.log.size());
		assertEquals("Bus's current carry shouls still be 0 since the person has not come aboard yet", 
				0, bus.getCurrentCarry());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgImAboard(person2);
		assertEquals("There should be one person in bus's passenger list now", 
				1, bus.getPassengers().size());
		assertEquals("Boarding people left should be zero now", 
				0, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertEquals("Bus's stop count should be zero", 
				0 , bus.getStopCount());
		assertEquals("Bus's current carry should be zero since it's not updated yet", 
				0, bus.getCurrentCarry());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Bus moving now
		assertEquals("Bus should now have three events in its log",
				3,bus.log.size());
		//All initialization before arriving at next stop
		assertFalse("Bus's numberTold should be initialized as false", 
				bus.getNumberTold());
		assertEquals("Bus's current carry should now be 1", 
				1, bus.getPassengers().size());
		assertEquals("Bus's stopCount should now be initialized to 0", 
				0, bus.getStopCount());
		assertFalse("bus's readyToGo should be initialized to false", 
				bus.readyToGo);
		bus.msgAnimation();
		assertEquals("There should be two events in the first stop's log now", 
				2, bs1.log.size());
		assertEquals("There should be two events in the passenger's log now saying we are going to next", 
				2, person2.log.size());
		assertEquals("The current stop should be set to bs2", 
				bs2, bus.getCurrentStop());
		assertFalse("readyToPark should be set to equal to false", 
				bus.readyToPark);
		assertEquals("bs2 should now have one message in the log", 
				1, bs2.log.size());
		assertEquals("The bus's state should be atStop now", 
				"atStop", bus.state.name());
		
		//At next stop unloading and all stuffs
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("Bus should now four three events in its log",
				4,bus.log.size());
		assertEquals("The bus should now have one passenger on the bus at this time",
				1, bus.getCurrentCarry());
		assertEquals("There should be one events in the passenger's log now", 
				3, person2.log.size());
		assertEquals("The bus should have told the correct corrent stop", 
				bs2, person2.getCurrentStop());
		assertEquals("The should should have told the corrent next stop", 
				bs3, person2.getNextStop());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Passenger wants to stay on bus
		bus.msgLeavingBus(person2);
		assertEquals("The bus's stopCount should be 1", 
				1, bus.getStopCount());
		assertEquals("The bus should now have no passengers on the bus", 
				0, bus.getPassengers().size());
		assertEquals("The bus should have a current carry number of 1 since it has not been updated",
				1, bus.getCurrentCarry());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Bus-BusStop interaction
		assertFalse("The bus's numberTold boolean should be false", 
				bus.getNumberTold());
		bus.msgTotalWaitingPeople(0);
		assertEquals("The bus should set the boarding people number correctly", 
				0, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgYouCanGo();
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertEquals("Bus's boarding people should be 0",
				0,bus.getBoardingPeople());
		assertTrue("The bus's stopCount should be the same as currentCarry",
				bus.getCurrentCarry() == bus.getStopCount());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Check post conditions
		assertEquals("Bus's current carry should be zero",
				0 , bus.getCurrentCarry());
		assertEquals("Bus's current size of passenger should be 0", 
				0, bus.getPassengers().size());
	}
	
	public void testTwoPassengers(){
System.out.println("Test: Normal Operating with one passenger");
		
		//Check pre conditions
		assertEquals("There should be no event in bus's log",
				0, bus.log.size());
		assertEquals("There should be no event in person's log", 
				0, person.log.size());
		assertEquals("There should be no event in person3's log", 
				0, person3.log.size());
		assertEquals("The person should start at bs1", 
				bs1, person.getStartStop());
		assertEquals("The person should get off at bs2", 
				bs2, person.getEndStop());
		assertEquals("The person should start at bs3", 
				bs3, person3.getStartStop());
		assertEquals("The person should get off at bs4", 
				bs4, person3.getEndStop());
		assertEquals("The bus's current carry should be 0", 
				0, bus.getCurrentCarry());
		assertEquals("The mediator's log should be clean",
				0, mediator.log.size());
		
		//Update time to get the bus work
		//Bus starts operation
		bus.msgUpdateTime(10);
		assertEquals("Bus should now have one event in its log",
						1,bus.log.size());
		assertEquals("Bus's state should now be readyToOperate", 
						"readyToOperate", bus.state.name());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
						bus.pickAndExecuteAnAction());
		assertTrue("Bus should now be active", 
						bus.isActive());
		assertEquals("There should be one event in mediator's log", 
						1, mediator.log.size());
		assertEquals("The bus's current stop should be the first stop", 
						bs1, bus.getCurrentStop());
		assertEquals("The bus's next stop should be the second stop", 
						bs2, bus.getNextStop());
		assertEquals("There should be one event in the first stop's log", 
						1, bs1.log.size());
		assertEquals("The bus's state should be atStop", 
						"atStop", bus.state.name());
		
		//At stop
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("Bus should now have two events in its log",
				2,bus.log.size());
		assertEquals("The bus should now have any passengers on the bus at this time",
				0, bus.getPassengers().size());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertFalse("The bus's numberTold boolean should be false", 
				bus.getNumberTold());
		
		bus.msgTotalWaitingPeople(1);
		assertEquals("The bus should set the boarding people number correctly", 
				1, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgYouCanGo();
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		
		//Bus-Passenger interaction
		bus.msgCanIGetOnToBus(person3);
		assertEquals("There should be one event in the person's log now", 
				1,person3.log.size());
		assertEquals("Bus's current carry shouls still be 0 since the person has not come aboard yet", 
				0, bus.getCurrentCarry());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgImAboard(person3);
		assertEquals("There should be one person in bus's passenger list now", 
				1, bus.getPassengers().size());
		assertEquals("Boarding people left should be zero now", 
				0, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertEquals("Bus's stop count should be zero", 
				0 , bus.getStopCount());
		assertEquals("Bus's current carry should be zero since it's not updated yet", 
				0, bus.getCurrentCarry());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Bus moving now
		assertEquals("Bus should now have three events in its log",
				3,bus.log.size());
		//All initialization before arriving at next stop
		assertFalse("Bus's numberTold should be initialized as false", 
				bus.getNumberTold());
		assertEquals("Bus's current carry should now be 1", 
				1, bus.getPassengers().size());
		assertEquals("Bus's stopCount should now be initialized to 0", 
				0, bus.getStopCount());
		assertFalse("bus's readyToGo should be initialized to false", 
				bus.readyToGo);
		bus.msgAnimation();
		assertEquals("There should be two events in the first stop's log now", 
				2, bs1.log.size());
		assertEquals("There should be two events in the passenger's log now saying we are going to next", 
				2, person3.log.size());
		assertEquals("The current stop should be set to bs2", 
				bs2, bus.getCurrentStop());
		assertFalse("readyToPark should be set to equal to false", 
				bus.readyToPark);
		assertEquals("bs2 should now have one message in the log", 
				1, bs2.log.size());
		assertEquals("The bus's state should be atStop now", 
				"atStop", bus.state.name());
		
		//At next stop unloading and all stuffs
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("Bus should now have three events in its log",
				4,bus.log.size());
		assertEquals("The bus should now have one passenger on the bus at this time",
				1, bus.getCurrentCarry());
		assertEquals("There should be three events in the passenger's log now", 
				3, person3.log.size());
		assertEquals("The bus should have told the correct corrent stop", 
				bs2, person3.getCurrentStop());
		assertEquals("The should should have told the corrent next stop", 
				bs3, person3.getNextStop());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Passenger wants to leave bus
		bus.msgLeavingBus(person);
		assertEquals("The bus's stopCount should be 1", 
				1, bus.getStopCount());
		assertEquals("The bus should now have one passenger on the bus", 
				1, bus.getPassengers().size());
		assertEquals("The bus should have a current carry number of 1 since it has not been updated",
				1, bus.getCurrentCarry());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Bus-BusStop interaction
		assertFalse("The bus's numberTold boolean should be false", 
				bus.getNumberTold());
		bus.msgTotalWaitingPeople(1);
		assertEquals("The bus should set the boarding people number correctly", 
				1, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgYouCanGo();
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		
		//Bus-Passenger interaction
		bus.msgCanIGetOnToBus(person);
		assertEquals("There should be one event in the person's log now", 
				1,person.log.size());
		assertEquals("Bus's current carry shouls still be 1 since the person has not come aboard yet", 
				1, bus.getCurrentCarry());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgImAboard(person);
		assertEquals("There should be two people in bus's passenger list now", 
				2, bus.getPassengers().size());
		assertEquals("Boarding people left should be zero now", 
				0, bus.getBoardingPeople());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertEquals("Bus's stop count should be one", 
				1 , bus.getStopCount());
		assertEquals("Bus's current carry should be zero since it's not updated yet", 
				1, bus.getCurrentCarry());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		//Bus moving now
		assertEquals("Bus should now have three events in its log",
				5,bus.log.size());
		//All initialization before arriving at next stop
		assertFalse("Bus's numberTold should be initialized as false", 
				bus.getNumberTold());
		assertEquals("Bus's current carry should now be 1", 
				2, bus.getPassengers().size());
		assertEquals("Bus's stopCount should now be initialized to 0", 
				0, bus.getStopCount());
		assertFalse("bus's readyToGo should be initialized to false", 
				bus.readyToGo);
		bus.msgAnimation();
		assertEquals("There should be two events in the first stop's log now", 
				2, bs1.log.size());
		assertEquals("There should be two events in the passenger's log now saying we are going to next", 
				1, person.log.size());
		assertFalse("readyToPark should be set to equal to false", 
				bus.readyToPark);
		assertEquals("bs2 should now have one message in the log", 
				1, bs3.log.size());
		assertEquals("The bus's state should be atStop now", 
				"atStop", bus.state.name());
		
		bus.msgYouCanGo();
		assertTrue("Bus's readyToGo boolean should be set true", 
				bus.readyToGo);
		assertEquals("Bus's boarding people should be 0",
				0,bus.getBoardingPeople());
		assertEquals("The bus's state should now be atStop", 
				"atStop", bus.state.name());
		assertTrue("Bus's scheduler should have returned true, but didn't.", 
				bus.pickAndExecuteAnAction());
		
		bus.msgLeavingBus(person3);
		assertEquals("The bus's stopCount should be 1", 
				1, bus.getStopCount());
		assertEquals("The bus should now have one passenger on the bus", 
				1, bus.getPassengers().size());
		assertEquals("The bus should have a current carry number of 2 since it has not been updated",
				2, bus.getCurrentCarry());
		assertEquals("The bus's state should now be unloading", 
				"unloading", bus.state.name());
		assertFalse("Bus's scheduler should have returned false, but didn't.", 
				bus.pickAndExecuteAnAction());

	}
}
