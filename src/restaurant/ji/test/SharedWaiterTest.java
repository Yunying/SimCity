package restaurant.ji.test;

import global.test.mock.MockPerson;
import interfaces.Building;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import restaurant.ji.Menu;
import restaurant.ji.RevolvingStand;
import restaurant.ji.JiMyTable.TableState;
import restaurant.ji.roles.JiSharedWaiterRole;
import restaurant.ji.roles.JiSharedWaiterRole.*;
import restaurant.ji.roles.JiWaiterRole;
import restaurant.ji.test.mock.*;

public class SharedWaiterTest extends TestCase {
	MockJiRestaurant restaurant;
	MockCashier cashier; MockPerson cashierperson;
	MockCustomer customer1; MockPerson customer1person;
	MockCustomer customer2; MockPerson customer2person;
	MockCook cook; MockPerson cookperson;
	MockHost host; MockPerson hostperson;
	
	RevolvingStand stand;
	MockWaiter waiter; MockPerson waiterperson;
	JiSharedWaiterRole waiterShared; MockPerson waiterpersonShared;
	
	public void setUp() throws Exception{
		super.setUp();		
		
		restaurant = new MockJiRestaurant("MockJi");
		List<Building> b = new ArrayList<Building>(); b.add(restaurant); 
		
		cashier = new MockCashier(); cashierperson = new MockPerson("cashier", b); cashier.setPerson(cashierperson); 	
		customer1 = new MockCustomer(); customer1person = new MockPerson("customer1", b); customer1.setPerson(customer1person);
		customer2 = new MockCustomer(); customer2person = new MockPerson("customer2", b); customer2.setPerson(customer2person);
		cook = new MockCook(); cookperson = new MockPerson("cook", b); cook.setPerson(cookperson);
		host = new MockHost(); hostperson = new MockPerson("host", b); host.setPerson(hostperson);
		
		//waiter = new JiWaiterRole(); waiterperson = new MockPerson("waiter", b); waiter.setPerson(waiterperson);
		waiterShared = new JiSharedWaiterRole(); waiterpersonShared = new MockPerson("waiterShared", b); waiterShared.setPerson(waiterpersonShared);
		stand = new RevolvingStand(); waiterShared.setOrderStand(stand); cook.setOrderStand(stand);
		waiterShared.setCashier(cashier); waiterShared.setHost(host);
	}
	
	public void testOneSharedWaiterOrder(){
		//check preconditions
		assertEquals("the person in the waiterShared role should be waiterSharedperson, but it's actually " + waiterShared.getPerson().getName(), waiterShared.getPerson(), waiterpersonShared);
		assertEquals("waiterShared's state should be none but is actually " + waiterShared.getState(), waiterShared.getState(), State.none);
		assertTrue("waiterShared should have 0 tables. It doesn't.",waiterShared.getMyTables().isEmpty());
		assertTrue("waiterShared should have 0 unavailable entrees. It doesn't.",waiterShared.getUnavailableEntrees().isEmpty());
		assertFalse("waiterShared shouldn't have received paycheck but he did", waiterShared.hasReceivedPaycheck());
		assertEquals("waiterSharedAgent should have an empty event log before the waiterShared's msgAtJiRestaurant is called. Instead, the waiterShared's event log reads: "
						+ waiterShared.log.toString(), 0, waiterShared.log.size());
		assertFalse("waiterShared's scheduler should have returned false but didn't", waiterShared.pickAndExecuteAnAction());
		
		// going to the restaurant to start work
		waiterShared.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("waiterShared should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + waiterShared.log.getLastLoggedEvent().toString(), 
				waiterShared.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("waiterShared's state should be arrivedAtJiRestaurant but isn't", waiterShared.getState(), State.arrivedAtJiRestaurant);
		assertEquals("waiterShared's restaurant is the mockrestaurant", waiterShared.getRestaurant(), restaurant);
		assertTrue("waiterShared's scheduler should have returned true to begin work but didn't", waiterShared.pickAndExecuteAnAction());
		assertEquals("waiterShared's state should be working but isn't", waiterShared.getState(), State.working);
		assertFalse("waiterShared should not have gotten paid but somehow he thinks he did", waiterShared.hasReceivedPaycheck());
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		assertFalse("waiterShared's scheduler should have returned false but didn't", waiterShared.pickAndExecuteAnAction());
		
		waiterShared.msgHeresANewCustomer(customer1, 1, 5, 5);
		//post-conditions of getting a customer
		assertTrue("waiterShared should have logged \"Received msgHeresANewCustomer\" but didn't. His log reads instead: " + waiterShared.log.getLastLoggedEvent().toString(), 
				waiterShared.log.containsString("Received msgHeresANewCustomer"));
		assertEquals("waiterShared's state should be working but isn't", waiterShared.getState(), State.working);
		assertEquals("waiterShared created a new MyTable. Should have the right customer but doens't", waiterShared.getMyTables().get(0).getCustomer(), customer1);
		assertEquals("the MyTable created should have the right table number but doens't", waiterShared.getMyTables().get(0).getTableNum(), 1);
		assertNull("the MyTable created shouldn't have a choice yet but it does", waiterShared.getMyTables().get(0).getChoice());
		assertNull("the MyTable created shouldn't have a bill yet but it does", waiterShared.getMyTables().get(0).getBill());
		assertEquals("the MyTable created should have a state of waitingToBeSeated but doesnt", waiterShared.getMyTables().get(0).getState(), TableState.waitingToBeSeated);
		assertEquals("waiterShared should now have 1 MyTable but doesn't", waiterShared.getMyTables().size(), 1);
		assertEquals("customer1's log should have been empty before waiterShared's scheduler was called. instead, it had " + customer1.log.toString(), customer1.log.size(), 0);
		
		assertTrue("waiterShared's scheduler should have returned true to seat the customer", waiterShared.pickAndExecuteAnAction());
		// post-conditions of scheduler after getting a new customer
		assertEquals("the MyTable created should have a state of idle but doesnt", waiterShared.getMyTables().get(0).getState(), TableState.IDLE);
		assertTrue("customer1 should have logged \" Received msgFollowMe\" but instead logged " + customer1.log.getLastLoggedEvent().toString(),
				customer1.log.containsString("Received msgFollowMe"));
		assertTrue("waiterShared's scheduler should have returned true, table is idle", waiterShared.pickAndExecuteAnAction());
		
		waiterShared.msgImReadyToOrder(customer1);
		// post-conditions of being called over
		assertTrue("waiterShared should have logged \"Received msgImReadyToOrder\" but didn't. His log reads instead: " + waiterShared.log.getLastLoggedEvent().toString(), 
				waiterShared.log.containsString("Received msgImReadyToOrder"));
		assertEquals("waiterShared's state should be working but isn't", waiterShared.getState(), State.working);
		assertEquals("waiterShared should have found the right table and set its state to goingtotakeorder but doesnt", 
				waiterShared.getMyTables().get(0).getState(), TableState.GOINGTOTAKEORDER);
		assertTrue("waiterShared's scheduler should have returned true to take the order", waiterShared.pickAndExecuteAnAction());
		// post-scheduler
		assertEquals("the MyTable created should have a state of idle but doesnt", waiterShared.getMyTables().get(0).getState(), TableState.IDLE);
		assertTrue("customer1 should have logged \" Received msgWhatWouldYouLike\" but instead logged " + customer1.log.getLastLoggedEvent().toString(),
				customer1.log.containsString("Received msgWhatWouldYouLike"));
		assertTrue("waiterShared's scheduler should have returned true, table is idle", waiterShared.pickAndExecuteAnAction());
		
		waiterShared.msgHeresMyOrder(customer1, "Steak");
		// post-conditions of being called over
		assertTrue("waiterShared should have logged \"Received msgHeresMyOrder\" but didn't. His log reads instead: " + waiterShared.log.getLastLoggedEvent().toString(), 
				waiterShared.log.containsString("Received msgHeresMyOrder"));
		assertEquals("waiterShared's state should be working but isn't", waiterShared.getState(), State.working);
		assertEquals("waiterShared should have found the right table and set its state to TAKINGORDER but doesnt", 
				waiterShared.getMyTables().get(0).getState(), TableState.TAKINGORDER);
		assertTrue("waiterShared's scheduler should have returned true to deliver the order to the stand", waiterShared.pickAndExecuteAnAction());
		// post-scheduler
		assertEquals("the MyTable created should have a state of WAITINGFORFOOD but doesnt", waiterShared.getMyTables().get(0).getState(), TableState.WAITINGFORFOOD);
		assertTrue("stand should have one order but has ", stand.getOrders().size() == 1);
		assertTrue("stand's order should be for steak and customer1's table num but isn't", stand.get(0).choice == "Steak" && stand.get(0).tableNum == 1);
		assertFalse("waiterShared's scheduler should have return false, nothing to do", waiterShared.pickAndExecuteAnAction());
		
		stand.getOrder(cook);
		waiterShared.msgOrderIsReady(1); // from cook
		// post-conditions of being called over
		assertTrue("waiterShared should have logged \"Received msgOrderIsReady\" but didn't. His log reads instead: " + waiterShared.log.getLastLoggedEvent().toString(), 
				waiterShared.log.containsString("Received msgOrderIsReady"));
		assertEquals("waiterShared's state should be working but isn't", waiterShared.getState(), State.working);
		assertEquals("waiterShared should have found the right table and set its state to PICKINGUPORDER but doesnt", 
				waiterShared.getMyTables().get(0).getState(), TableState.PICKINGUPORDER);
		assertTrue("waiterShared's scheduler should have returned true to pick up the order from the cook", waiterShared.pickAndExecuteAnAction());
		// post-scheduler
		assertEquals("the MyTable created should have a state of IDLE but doesnt", waiterShared.getMyTables().get(0).getState(), TableState.IDLE);
		assertTrue("stand should have no orders but has ", stand.getOrders().isEmpty());
		assertTrue("customer1 should have logged \" Received msgHeresYourFood\" but instead logged " + customer1.log.getLastLoggedEvent().toString(),
				customer1.log.containsString("Received msgHeresYourFood"));
		assertTrue("waiterShared's scheduler should have returned true, table is idle", waiterShared.pickAndExecuteAnAction());
		
		waiterShared.msgIWantMyCheck(customer1);
		// post-conditions of being called over
		assertTrue("waiterShared should have logged \"Received msgIWantMyCheck\" but didn't. His log reads instead: " + waiterShared.log.getLastLoggedEvent().toString(), 
				waiterShared.log.containsString("Received msgIWantMyCheck"));
		assertEquals("waiterShared's state should be working but isn't", waiterShared.getState(), State.working);
		assertEquals("waiterShared should have found the right table and set its state to GETTINGCHECK but doesnt", 
				waiterShared.getMyTables().get(0).getState(), TableState.GETTINGCHECK);
		assertTrue("waiterShared's scheduler should have returned true to get check from cashier", waiterShared.pickAndExecuteAnAction());
		// post-scheduler
		assertTrue("cashier should have logged \" Received msgComputeBill\" but instead logged " + cashier.log.getLastLoggedEvent().toString(),
				cashier.log.containsString("Received msgComputeBill"));
		assertEquals("the MyTable created should have a state of IDLE but doesnt", waiterShared.getMyTables().get(0).getState(), TableState.IDLE);
		assertTrue("waiterShared's scheduler should have returned true, table is idle", waiterShared.pickAndExecuteAnAction());

		waiterShared.msgHereIsCheck(Menu.getPrice("Steak"), customer1);
		// post-conditions of being called over
		assertTrue("waiterShared should have logged \"Received msgHereIsCheck\" but didn't. His log reads instead: " + waiterShared.log.getLastLoggedEvent().toString(), 
				waiterShared.log.containsString("Received msgHereIsCheck"));
		assertEquals("waiterShared's state should be working but isn't", waiterShared.getState(), State.working);
		assertEquals("waiterShared should have found the right table and set its state to DELIVERINGCHECK but doesnt", 
				waiterShared.getMyTables().get(0).getState(), TableState.DELIVERINGCHECK);
		assertTrue("waiterShared's scheduler should have returned true to deliver check", waiterShared.pickAndExecuteAnAction());
		//post-scheduler
		assertTrue("customer1 should have logged \" Received msgHereIsCheck\" but instead logged " + customer1.log.getLastLoggedEvent().toString(),
				customer1.log.containsString("Received msgHereIsCheck"));
		assertEquals("the MyTable created should have a state of IDLE but doesnt", waiterShared.getMyTables().get(0).getState(), TableState.IDLE);
		assertTrue("waiterShared's scheduler should have returned true, table is idle", waiterShared.pickAndExecuteAnAction());

		waiterShared.msgDoneEatingAndLeaving(customer1);
		// post-conditions of being done
		assertTrue("waiterShared should have logged \"Received msgDoneEatingAndLeaving\" but didn't. His log reads instead: " + waiterShared.log.getLastLoggedEvent().toString(), 
				waiterShared.log.containsString("Received msgDoneEatingAndLeaving"));
		assertEquals("waiterShared's state should be working but isn't", waiterShared.getState(), State.working);
		assertEquals("waiterShared should have found the right table and set its state to CUSTOMERLEFT but doesnt", 
				waiterShared.getMyTables().get(0).getState(), TableState.CUSTOMERLEFT);
		assertTrue("waiterShared's scheduler should have returned true to finish customer", waiterShared.pickAndExecuteAnAction());
		//post-scheduler
		assertTrue("host should have logged \"Received msgTableIsFree\" but instead logged " + host.log.getLastLoggedEvent().toString(),
				host.log.containsString("Received msgTableIsFree"));
		assertEquals("the MyTable created should have been removed, myTables is size 0", waiterShared.getMyTables().size(), 0);
		assertFalse("waiterShared's scheduler should have returned false, nothing to do", waiterShared.pickAndExecuteAnAction());
		
		waiterShared.msgStopWorkingGoHome();
		// post-conditions
		assertTrue("waiterShared should have logged \"Received msgStopWorkingGoHome\" but didn't. Instead, says " + waiterShared.log.getLastLoggedEvent().toString(),
				waiterShared.log.containsString("Received msgStopWorkingGoHome"));
		assertTrue("waiterShared's state is leavingWork", waiterShared.getState() == State.leavingWork);
		assertFalse("waiterShared's scheduler returns false. leaving building.", waiterShared.pickAndExecuteAnAction());
		assertTrue("waiterShared's state is none", waiterShared.getState() == State.none);
		assertTrue("restaurant should have logged \"Received msgLeavingWork\" but didn't. Instead, says " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgLeavingWork"));
		assertTrue("waiterpersonShared should have logged \"Received msgLeavingLocation\" but didn't. Instead, says " + waiterpersonShared.log.getLastLoggedEvent().toString(),
				waiterpersonShared.log.containsString("Received msgLeavingLocation")); 
		
		//post-conditions
		assertEquals("the person in the waiterShared role should be waiterSharedperson, but it's actually " + waiterShared.getPerson().getName(), waiterShared.getPerson(), waiterpersonShared);
		assertEquals("waiterShared's state should be none but is actually " + waiterShared.getState(), waiterShared.getState(), State.none);
		assertTrue("waiterShared should have 0 tables. It doesn't.",waiterShared.getMyTables().isEmpty());
		assertTrue("waiterShared should have 0 unavailable entrees. It doesn't.",waiterShared.getUnavailableEntrees().isEmpty());
		assertFalse("waiterShared shouldn't have received paycheck but he did", waiterShared.hasReceivedPaycheck());
		assertFalse("waiterShared's scheduler should have returned false but didn't", waiterShared.pickAndExecuteAnAction());
		
	}
}
