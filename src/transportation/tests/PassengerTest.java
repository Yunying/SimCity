package transportation.tests;

import global.PersonAgent;

import java.util.ArrayList;
import java.util.List;

import transportation.TransportationRole;
import transportation.tests.mock.MockBuilding;
import transportation.tests.mock.MockBus;
import transportation.tests.mock.MockBusStop;
import junit.framework.*;

public class PassengerTest extends TestCase{
	TransportationRole person;
	PersonAgent p;
	MockBusStop bs1;
	MockBusStop bs2;
	MockBusStop bs3;
	MockBusStop bs4;
	MockBus bus;
	List<MockBuilding> buildings;
	
	
	public void setUp() throws Exception{
		bus = new MockBus("bus");
		p = new PersonAgent("p", null, null);
		person = new TransportationRole("person");
		person.setPerson(p);
		buildings = new ArrayList<MockBuilding>();
		
		bs1 = new MockBusStop("bs1", 0, 100, 100, bus);
		bs2 = new MockBusStop("bs2", 0, 200, 200, bus);
		bs3 = new MockBusStop("bs3", 0, 300, 300, bus);
		bs4 = new MockBusStop("bs4", 0, 400, 400, bus);
		for (int i=0; i<4; i++){
			for (int j=0; j<3; j++){
				buildings.add(new MockBuilding("Building"+i));
			}
		}
		for (int i=0; i<3; i++){
			bs1.addBuilding(buildings.get(i));
			bs2.addBuilding(buildings.get(i+3));
			bs3.addBuilding(buildings.get(i+6));
			bs4.addBuilding(buildings.get(i+9));
		}
		
		person.addBusStop(bs1);
		person.addBusStop(bs2);
		person.addBusStop(bs3);
		person.addBusStop(bs4);
	}
	
	
	public void testWithOutBuildingOnePassengerNoGui(){
		System.out.println();
		System.out.println("One Passenger; One Bus; No Gui");
		person.setStartStop(bs1);
		person.setEndStop(bs2);
		
		//Check pre conditions
		assertEquals("There should be eight buildings in the building list.",12,buildings.size());
		assertEquals("The person should have bs1 as his startStop. But it doesn't", person.startStop, bs1);
		assertEquals("The person should have bs2 as his endStop. But it doesn't", person.endStop, bs2);
		assertEquals("There should be no event in the bs1's log.", 0, bs1.log.size());
		assertEquals("There should be no event in the bs2's log.", 0, bs2.log.size());
		assertEquals("There should be no event in the bs3's log.", 0, bs3.log.size());
		assertEquals("There should be no event in the bs4's log.", 0, bs4.log.size());
		
		//Begin with going to the bus Stop
		person.setActive();
		
		assertEquals("There should be one event in the busStop's log saying the passenger is at location.", 
					1, bs1.log.size());
		person.msgHereIsBus(bus);
		assertEquals("Passenger should now have a bus named bus", "bus", 
					person.bus.getBusName());
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
					person.pickAndExecuteAnAction());
		assertEquals("There should be one event in bus's log saying can I get onto bus. Bus instead it has " +
					bus.log.size(), 1, bus.log.size());
		assertEquals("There should be one event in passenger's log saying please come aboard. But instead it has "+
					person.log.size(), 2, person.log.size());
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
				person.pickAndExecuteAnAction());
		assertEquals("There should be two events in bus's log saying the passenger is aboard. Bus instead it has " +
				bus.log.size(), 2, bus.log.size());
		assertEquals("There should be two events in the busStop's log saying the passenger is leaving location.", 
				2, bs1.log.size());
		
		//On bus. Receive message that next stop is passenger's destination
		person.msgAtStop(bs2, bs3);
		
		assertEquals("There should be two events in passenger's log saying please come aboard. But instead it has "+
				person.log.size(), 3, person.log.size());
		assertEquals("PassengerState should be arriveAtStop. Instead it is "+person.state.name(),
				person.state.name(), "arriveAtStop");
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
				person.pickAndExecuteAnAction());
		assertEquals("There should be three events in bus's log saying the passenger is leaving. Bus instead it has " +
				bus.log.size(), 3, bus.log.size());
		assertTrue("There last event in bus's log should say the passenger is leaving. Bus instead it says" +
				bus.log.getLastLoggedEvent().toString(), bus.log.getLastLoggedEvent().toString().contains("leaving bus"));
		
		//On bus. Receive message the next stop is not passenger's destination
		person.msgAtStop(bs3, bs4);
		
		assertEquals("There should be two events in passenger's log saying please come aboard. But instead it has "+
				person.log.size(), 4, person.log.size());
		assertEquals("PassengerState should be arriveAtStop. Instead it is "+person.state.name(),
				person.state.name(), "stay");
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
				person.pickAndExecuteAnAction());
		assertEquals("There should be three events in bus's log saying the passenger is leaving. Bus instead it has " +
				bus.log.size(), 4, bus.log.size());
		assertTrue("There last event in bus's log should say the passenger is leaving. Bus instead it says" +
				bus.log.getLastLoggedEvent().toString(), bus.log.getLastLoggedEvent().toString().contains("staying on bus"));
	}
	
	public void testWithAssignedBuilding(){
		System.out.println();
		System.out.println("One Passenger; One Bus; With Building; No Gui");
		person.log.clear();
		bs1.log.clear();
		bs2.log.clear();
		MockBuilding start = buildings.get(0);
		MockBuilding end = buildings.get(11);
		
		//Check pre conditions
		assertEquals("There should be twelve buildings in the building list.",12,buildings.size());
		assertEquals("The person should have no bus as his startStop. But it doesn't", person.startStop, null);
		assertEquals("The person should have no bus as his endStop. But it doesn't", person.endStop, null);
		assertEquals("There should be no event in the bs1's log.", 0, bs1.log.size());
		assertEquals("There should be no event in the bs2's log.", 0, bs2.log.size());
		assertEquals("There should be no event in the bs3's log.", 0, bs3.log.size());
		assertEquals("There should be no event in the bs4's log.", 0, bs4.log.size());
		assertEquals("There should be no event in the person's log.", 0, person.log.size());
		
		//Go To Building
		person.msgGoToBuilding(start, end, false);
		assertEquals("There should be one event in the person's log.", 
					1, person.log.size());
		assertEquals("The person should correctly store the information about start building", 
					person.getStartBuilding(), start);
		assertEquals("The person should correctly store the information about end building", 
					person.getEndBuilding(), end);
		assertEquals("The person should correctly choose the start bus stop", 
					person.getStartStop(), bs1);
		assertEquals("The person should correctly choose the end bus stop"+person.getEndStop().getNumber(), 
					person.getEndStop(), bs4);
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
					person.pickAndExecuteAnAction());
		assertTrue("There should be one event in bs1's log saying the person is at the bus stop. but instead it is saying" + bs1.log.getLastLoggedEvent().toString(), 
					bs1.log.getLastLoggedEvent().toString().contains("at the bus stop"));
		person.msgHereIsBus(bus);
		assertEquals("There should be two events in the person's log.", 
					2, person.log.size());
		assertTrue("The second event should say here is a bus",
					person.log.getLastLoggedEvent().toString().contains("here is a bus"));
		assertEquals("The person should now have a bus in its data", person.bus, bus);
		assertEquals("The person's state should be seeABus. But instead it is "+person.state.name(),
					person.state.name(), "seeABus");
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
					person.pickAndExecuteAnAction());
		assertEquals("There should be one event in bus's log saying can I get onto bus. Bus instead it has " +
				bus.log.size(), 1, bus.log.size());
		assertEquals("There should be one event in passenger's log saying please come aboard. But instead it has "+
					person.log.size(), 3, person.log.size());
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
				person.pickAndExecuteAnAction());
		assertEquals("There should be two events in bus's log saying the passenger is aboard. Bus instead it has " +
				bus.log.size(), 2, bus.log.size());
		assertEquals("There should be two events in the busStop's log saying the passenger is leaving location.", 
				2, bs1.log.size());
		assertEquals("The person should have now null as his start stop", null, person.getStartStop());
		
		
		//The middle part is the same as the first test
		//See if the person knows to get off at correct bus stop
		//If next stop is the correct stop...
		person.msgAtStop(bs4, bs1);
		
		assertEquals("There should be three events in passenger's log saying please come aboard. But instead it has "+
				person.log.size(), 4, person.log.size());
		assertEquals("PassengerState should be arriveAtStop. Instead it is "+person.state.name(),
				person.state.name(), "arriveAtStop");
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
				person.pickAndExecuteAnAction());
		assertEquals("There should be one event in bus's log saying the passenger is leaving. Bus instead it has " +
				bus.log.size(), 3, bus.log.size());
		assertTrue("There last event in bus's log should say the passenger is leaving. Bus instead it says" +
				bus.log.getLastLoggedEvent().toString(), bus.log.getLastLoggedEvent().toString().contains("leaving bus"));
		assertEquals("Now the passenger state should be readyToWalk", person.state.name(), "readyToWalk");
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
				person.pickAndExecuteAnAction());
		assertEquals("The start stop should still have two logged events", 2, bs1.log.size());
		assertEquals("The destination building should now receive the person's message that he's at building",
					end.log.size(),1);
	}
	
	public void testWhenTransportationStopped(){
		System.out.println();
		System.out.println("One Passenger; One Bus; With Building; No Gui");
		person.log.clear();
		bs1.log.clear();
		bs2.log.clear();
		MockBuilding start = buildings.get(0);
		MockBuilding end = buildings.get(11);
		
		//Check pre conditions
		assertEquals("There should be twelve buildings in the building list.",
				12,buildings.size());
		assertEquals("The person should have no bus as his startStop. But it doesn't", 
				person.startStop, null);
		assertEquals("The person should have no bus as his endStop. But it doesn't", 
				person.endStop, null);
		assertEquals("There should be no event in the bs1's log.", 
				0, bs1.log.size());
		assertEquals("There should be no event in the bs2's log.", 
				0, bs2.log.size());
		assertEquals("There should be no event in the bs3's log.", 
				0, bs3.log.size());
		assertEquals("There should be no event in the bs4's log.", 
				0, bs4.log.size());
		assertEquals("There should be no event in the person's log.", 
				0, person.log.size());
		
		//Person wants to go from building 0 to building 11
		person.msgGoToBuilding(start, end, false);
		assertEquals("There should be one event in person's log", 
				1, person.log.size());
		assertEquals("Person should set the building correctly", 
				person.getStartBuilding(), start);
		assertEquals("Person should set the end building correctly", 
				person.getEndBuilding(), end);
		assertEquals("Person should choose the start bus stop correctly", 
				person.getStartStop(), bs1);
		assertEquals("Person should choose the end bus stop correctly", 
				person.getEndStop(), bs4);
		assertEquals("Person state should be goToBusStop", "goToBusStop", 
				person.state.name());
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
				person.pickAndExecuteAnAction());
		//Person arrives at the bus stop
		assertEquals("There should be one event in bs1's log",
				1, bs1.log.size());
		//The bus stop tells the passenger that the transportation has closed
		person.msgTransportationStopped();
		assertEquals("There should be two events in person's log now", 
				2, person.log.size());
		assertEquals("Person's state should be readyToWald", "readyToWalk", 
				person.state.name());
		assertTrue("Passenger's scheduler should have returned true, but didn't.", 
				person.pickAndExecuteAnAction());
		assertEquals("bs1 should have two events in its log saying the person is leaving",
				2, bs1.log.size());
		assertEquals("The building should have one event in its log saying the person is at location",
				1, end.log.size());
		assertEquals("The person should never arrive at end bus stop so there should be no event in end stop log",
				0, bs4.log.size());
	}
}
