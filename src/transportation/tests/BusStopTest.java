package transportation.tests;

import transportation.BusStop;
import transportation.TransportationRole;
import transportation.tests.mock.MockBus;
import transportation.tests.mock.MockBusStop;
import transportation.tests.mock.MockMediator;
import transportation.tests.mock.MockPassenger;
import junit.framework.TestCase;

public class BusStopTest extends TestCase{
	MockPassenger person;
	MockPassenger person2;
	MockPassenger person3;
	MockPassenger person4;
	BusStop stop;
	MockBus bus;
	MockBus bus2;
	MockMediator mediator;
	
	public void setUp() throws Exception{
		person = new MockPassenger("person");
		person2 = new MockPassenger("person2");
		stop = new BusStop(mediator, "bus stop");
		bus = new MockBus("bus");
		bus2 = new MockBus("bus2");
		mediator = new MockMediator("mediator");
		mediator.addBus(bus);
		mediator.addStop(stop);
		stop.setMediator(mediator);
		
		stop.setStartTime(6);
		stop.setCloseTime(22);
		
		person.log.clear();
		person2.log.clear();
		stop.log.clear();
		bus.log.clear();
		bus2.log.clear();
		mediator.log.clear();
	}
	
	public void testBusStopClosedTellPassengerToLeave(){
		System.out.println("Test: Bus Stop closed. Tell passenger to leave.");
		//Chekc pre conditions
		assertEquals("There should be no bus at bus stop now", null, stop.getBus());
		assertEquals("There should be no event in the bus stop log", 0, stop.log.size());
		assertEquals("There should be no event in the passenger's log", 0, person.log.size());
		assertEquals("There should be no event in the bus's log", 0 , bus.log.size());
		assertEquals("There should be a transportation mediator in the bus stop", mediator, stop.getMediator());
		
		
		//When the busStop is closed...it should say no to the passenger
		stop.msgUpdateTime(1);
		
		assertFalse("The bus stop should not work at this time. But instead it is still active",
				stop.isActive());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("There should be no person at bus stop now", 0, stop.waitingPassengers.size());
		
		//A person comes. The bus stop tells the passenger to leave
		stop.msgAtLocation(person);
		
		assertEquals("The bus stop should now have one person. Bus instead it has "+ stop.waitingPassengers.size(),
				1, stop.waitingPassengers.size());
		assertEquals("There should be one event in the busStop's log saying the passenger is at location.", 
				1, stop.log.size());
		
		//Set mediator tells the bus stop that there is no bus running so the passenger has to leave.
		mediator.setReturnValue(false);
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("There should be one event in the mediator's log", 1, mediator.log.size());
		assertEquals("The passenger should now have one event in his log saying he must leave.",
				1, person.log.size());
		assertTrue("The log should say that the passenger has to leave. Instead it says "+ person.log.getLastLoggedEvent().toString(),
				person.log.getLastLoggedEvent().toString().contains("You have to leave"));
		//The person tells the bus stop he's going to leave
		stop.msgLeavingStop(person);
		assertEquals("The bus stop should now have no person.", 0, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		
	}
	
	public void testBusStopClosedPassengerWaitingForNextBus(){
		System.out.println("Test: Transportation stops but person should still wait for next bus");
		stop.msgUpdateTime(1);
		person.log.clear();
		stop.log.clear();
		mediator.log.clear();
		stop.msgUpdateTime(1);
		mediator.setReturnValue(true);
		
		//Check pre conditions
		assertFalse("The bus stop should not work at this time. But instead it is still active",
				stop.isActive());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("Person log should be clean", 0, person.log.size());
		assertEquals("Bus Stop log should be clean", 0, stop.log.size());
		assertEquals("Mediator log should be clean", 0, mediator.log.size());
		
		
		//A person arrives. The mediator find that the bus is still running so he
		//tells the person to stay
		stop.msgAtLocation(person);
		
		assertEquals("The bus stop should now have one person. Bus instead it has "+ stop.waitingPassengers.size(),
				1, stop.waitingPassengers.size());
		assertEquals("There should be one event in the busStop's log saying the passenger is at location.", 
				1, stop.log.size());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("Person log should be clean.", 0, person.log.size());
		
		//When the bus comes, the stop should still release the passenger
		stop.msgAtLocation(bus);
		assertEquals("Person log should be clean.", 0, person.log.size());
		assertEquals("The stop should now have two event in the log saying the bus is here",
					2, stop.log.size());
		assertEquals("The stop should correctly set the bus", bus, stop.getBus());
		assertEquals("Person log should be clean.", 0, person.log.size());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		mediator.setReturnValue(true);
		assertEquals("There should be one event in the mediator's log", 1, mediator.log.size());
		assertEquals("The passenger should now have three events in his log saying the bus is here and he is asked." 
					+ person.log.getLastLoggedEvent().toString(),
				3, person.log.size());
		assertEquals("There should be one event in the bus log saying got the number of waiting passengers",
				1, bus.log.size());
		assertTrue("the bus should now set the numberTold to true", bus.getNumberTold());
		assertEquals("There should be three event in the passenger's log",
				3, person.log.size());
		assertTrue("the last logged event in passenger's log should be I have been asked",
				person.log.getLastLoggedEvent().toString().contains("I have been asked"));
		
		//The bus is still not null, so the bus stop should ask again
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("There should be four events in the passenger's log because he is asked again if he has been asked",
				4, person.log.size());
		assertEquals("There should be two events in the bus log saying he can go",
				2, bus.log.size());
		assertEquals("The bus pointer should be null in the bus stop now",
				null, stop.getBus());
		
		//The person tells the bus stop he's going to leave
		stop.msgLeavingStop(person);
		assertEquals("There should be no waiting passengers at the bus stop now", 
				0, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());	
	}
	
	public void testNormalSituationOnePersonOneBus(){
		System.out.println("Test: Normal bus day situation. Person comes then bus comes");
		stop.msgUpdateTime(10);
		person.log.clear();
		stop.log.clear();
		mediator.log.clear();
		bus.log.clear();
		mediator.setReturnValue(true);
		
		//Check pre conditions
		assertTrue("The bus stop should be active at this time",
				stop.isActive());
		assertEquals("There should be no person at the bus stop", 
				0, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("Person log should be clean", 0, person.log.size());
		assertEquals("Bus Stop log should be clean", 0, stop.log.size());
		assertEquals("Mediator log should be clean", 0, mediator.log.size());
		
		//A person is here
		stop.msgAtLocation(person);
		assertEquals("The bus stop should now have one person. Bus instead it has "+ stop.waitingPassengers.size(),
				1, stop.waitingPassengers.size());
		assertEquals("There should be one event in the busStop's log saying the passenger is at location.", 
				1, stop.log.size());
		assertTrue("The bus stop should be active at this time",
				stop.isActive());
		assertFalse("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("Person log should be clean.", 0, person.log.size());
		
		//A bus is here
		stop.msgAtLocation(bus);
		assertEquals("Person log should be clean.", 0, person.log.size());
		assertEquals("The stop should now have two event in the log saying the bus is here",
					2, stop.log.size());
		assertEquals("The stop should correctly set the bus", bus, stop.getBus());
		assertEquals("Person log should be clean.", 0, person.log.size());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		
		//Release passengers
		assertEquals("The bus should have one event in its log saying here is the total waiting number of people", 
				1, bus.log.size());
		assertEquals("The bus should be told the correct number of waiting people", 
				1, bus.waitingPeople);
		assertEquals("There should be three events in the person's log", 
				3, person.log.size());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("The person should have four events in his log"+person.log.getLastLoggedEvent().toString(),
				4, person.log.size());
		assertEquals("The bus should have two events in its log saying he can go", 
				2, bus.log.size());
		assertEquals("The bus pointer in the bus stop should be null now", null, stop.getBus());
		
		//Passenger leave
		stop.msgLeavingStop(person);
		assertEquals("The bus should have no passenger waiting now", 
				0, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());		
	}
	
	public void testBusComingWithoutPassengerWaiting(){
		System.out.println("Test: normal situation. Bus comes then passenger comes");
		stop.msgUpdateTime(10);
		person.log.clear();
		stop.log.clear();
		mediator.log.clear();
		bus.log.clear();
		mediator.setReturnValue(true);
		
		//Check pre conditions
		assertTrue("The bus stop should be active at this time",
				stop.isActive());
		assertEquals("There should be no person at the bus stop", 
				0, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("Person log should be clean", 0, person.log.size());
		assertEquals("Bus Stop log should be clean", 0, stop.log.size());
		assertEquals("Mediator log should be clean", 0, mediator.log.size());
		
		//A bus comes and leaves
		stop.msgAtLocation(bus);
		assertEquals("There should be one event in the stop's log now", 
				1, stop.log.size());
		assertEquals("The stop should set the bus correctly", 
				bus, stop.getBus());
		assertTrue("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("The bus should be told the correct number of waiting people", 
				0, bus.waitingPeople);
		assertEquals("The bus should be told that he can go since there is not people waiting", 
				2, bus.log.size());
		assertEquals("The bus pointer should be set as null", null, stop.getBus());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		
		//A passenger comes
		stop.msgAtLocation(person);
		assertEquals("The bus stop should now have one person. Bus instead it has "+ stop.waitingPassengers.size(),
				1, stop.waitingPassengers.size());
		assertEquals("There should be two events in the busStop's log saying the passenger is at location.", 
				2, stop.log.size());
		assertEquals("Person log should be clean.", 0, person.log.size());
		assertTrue("The bus stop should be active at this time",
				stop.isActive());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("There should still be one person waiting at bus stop", 1, stop.waitingPassengers.size());
		
		//Another bus comes
		stop.msgAtLocation(bus2);
		assertEquals("There should be three event in the stop's log now", 
				3, stop.log.size());
		assertEquals("The stop should set the bus correctly", 
				bus2, stop.getBus());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("The bus should be told the correct number of waiting people", 
				1, bus2.waitingPeople);
		assertEquals("There should be three events in the person's log", 
				3, person.log.size());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("The person should have four events in his log"+person.log.getLastLoggedEvent().toString(),
				4, person.log.size());
		assertEquals("The bus should have two events in its log saying he can go", 
				2, bus2.log.size());
		assertEquals("The bus pointer in the bus stop should be null now", null, stop.getBus());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		
		//Person leaves stop
		stop.msgLeavingStop(person);
		assertEquals("The bus should have no passenger waiting now", 
				0, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());			
	}
	
	public void testMultiplePassengersOneBus(){
		System.out.println("Test: normal situation. Multiple passengers at one bus stop");
		stop.msgUpdateTime(10);
		person.log.clear();
		person2.log.clear();
		stop.log.clear();
		mediator.log.clear();
		bus.log.clear();
		mediator.setReturnValue(true);
		
		//Check pre conditions
		assertTrue("The bus stop should be active at this time",
				stop.isActive());
		assertEquals("There should be no person at the bus stop", 
				0, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("Person log should be clean", 0, person.log.size());
		assertEquals("Person2's log should be clean", 0, person2.log.size());
		assertEquals("Bus Stop log should be clean", 0, stop.log.size());
		assertEquals("Mediator log should be clean", 0, mediator.log.size());
		assertEquals("Bus log should be clean",0,bus.log.size());
		
		//Two person comes
		stop.msgAtLocation(person);
		assertEquals("The bus stop should now have one person. Bus instead it has "+ stop.waitingPassengers.size(),
				1, stop.waitingPassengers.size());
		assertEquals("There should be one event in the busStop's log saying the passenger is at location.", 
				1, stop.log.size());
		assertTrue("The bus stop should be active at this time",
				stop.isActive());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("Person log should be clean.", 0, person.log.size());
		
		stop.msgAtLocation(person2);
		assertEquals("The bus stop should now have two people. Bus instead it has "+ stop.waitingPassengers.size(),
				2, stop.waitingPassengers.size());
		assertEquals("There should be one event in the busStop's log saying the passenger is at location.", 
				2, stop.log.size());
		assertTrue("The bus stop should be active at this time",
				stop.isActive());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("Person2 log should be clean.", 
				0, person2.log.size());
		
		//When the bus comes
		stop.msgAtLocation(bus);
		assertEquals("There should be three events in the stop's log now", 
				3, stop.log.size());
		assertEquals("The stop should set the bus correctly", 
				bus, stop.getBus());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("The bus should now have one event in its log", 
				1, bus.log.size());
		assertEquals("The bus should be told the correct number of waiting people", 
				2, bus.waitingPeople);
		assertEquals("The person should have three events in his log now", 
				3, person.log.size());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("The person should now have four events in his log", 
				4, person.log.size());
		assertEquals("Person2 should now have three events in his log", 
				3, person2.log.size());
		assertTrue("BusStop's scheduler should have returned true, but didn't.", 
				stop.pickAndExecuteAnAction());
		assertEquals("The person should now have five events in his log", 
				5, person.log.size());
		assertEquals("Person2 should now have four events in his log", 
				4, person2.log.size());
		assertEquals("The bus should have two events in its log saying he can go", 
				2, bus.log.size());
		assertEquals("The bus pointer in the bus stop should be null now", null, stop.getBus());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());
		
		//Person leaves stop
		stop.msgLeavingStop(person);
		assertEquals("The bus stop should have one passenger waiting now",
				1, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());	
		stop.msgLeavingStop(person2);
		assertEquals("The bus should have no passenger waiting now", 
				0, stop.waitingPassengers.size());
		assertFalse("BusStop's scheduler should have returned false, but didn't.", 
				stop.pickAndExecuteAnAction());		
		
	}
	
	
}
