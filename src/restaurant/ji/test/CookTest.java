package restaurant.ji.test;

import interfaces.Building;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.TestCase;
import global.test.mock.MockPerson;
import restaurant.ji.CustomerOrder.OrderStatus;
import restaurant.ji.Food;
import restaurant.ji.Food.FoodState;
import restaurant.ji.RevolvingStand;
import restaurant.ji.interfaces.JiCook;
import restaurant.ji.roles.JiCookRole;
import restaurant.ji.roles.JiSharedWaiterRole;
import restaurant.ji.roles.JiCookRole.State;
import restaurant.ji.test.mock.*;

public class CookTest extends TestCase {
	MockJiRestaurant restaurant;
	MockCashier cashier; MockPerson cashierperson;
	MockCustomer customer1; MockPerson customer1person;
	MockCustomer customer2; MockPerson customer2person;
	MockHost host; MockPerson hostperson;
	
	JiCookRole cook; MockPerson cookperson;
	RevolvingStand stand;
	MockWaiter waiter; MockPerson waiterperson;
	MockWaiter waiterShared; MockPerson waiterpersonShared;
	
	public void setUp() throws Exception{
		super.setUp();		
		
		restaurant = new MockJiRestaurant("MockJi");
		List<Building> b = new ArrayList<Building>(); b.add(restaurant); 
		
		cashier = new MockCashier(); cashierperson = new MockPerson("cashier", b); cashier.setPerson(cashierperson); 	
		customer1 = new MockCustomer(); customer1person = new MockPerson("customer1", b); customer1.setPerson(customer1person);
		customer2 = new MockCustomer(); customer2person = new MockPerson("customer2", b); customer2.setPerson(customer2person);
		host = new MockHost(); hostperson = new MockPerson("host", b); host.setPerson(hostperson);
		
		
		cook = new JiCookRole(); cookperson = new MockPerson("cook", b); cook.setPerson(cookperson);
		cook.UNITTESTING = true;// disable cooking timer

		waiter = new MockWaiter(); waiterperson = new MockPerson("waiter", b); waiter.setPerson(waiterperson);
		waiterShared = new MockWaiter(); waiterpersonShared = new MockPerson("waiterShared", b); waiterShared.setPerson(waiterpersonShared);
		stand = new RevolvingStand(); waiterShared.setOrderStand(stand); cook.setOrderStand(stand);
		waiterShared.setCashier(cashier); waiterShared.setHost(host);
	}
	
	
	public void testOneOrderFromWaiter(){
		//preconditions
		assertEquals("the person in the cook role should be cookperson, but it's actually " + cook.getPerson().getName(), cook.getPerson(), cookperson);
		assertFalse("cook shouldn't be working but is", cook.isWorking());
		assertEquals("cook's state should be none but is actually " + cook.getState(), cook.getState(), State.none);
		assertTrue("cook should have 0 bills in it. It doesn't.",cook.getIncomingOrders().isEmpty());
		assertEquals("cookAgent should have an empty event log before the cook's msgAtJiRestaurant is called. Instead, the cook's event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
		assertEquals("cook's order stand should be the stand", cook.getOrderStand(), stand);
		assertTrue("cook's order stand should be empty", cook.getOrderStand().isEmpty());
		assertTrue("cook should have nothing to do, but doesn't", cook.hasNothingToDo());
		
		// going to the restaurant to start work
		cook.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("cook should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cook.log.getLastLoggedEvent().toString(), 
				cook.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cook's state should be arrivedAtJiRestaurant but isn't", cook.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cook's restaurant is the mockrestaurant", cook.getRestaurant(), restaurant);
		assertTrue("cook's scheduler should have returned true to begin work but didn't", cook.pickAndExecuteAnAction());
		assertEquals("cook's state should be working but isn't", cook.getState(), State.working);
		assertTrue("cook should be working but isn't", cook.isWorking());
		assertFalse("cook should not have gotten paid but somehow he thinks he did", cook.hasReceivedPaycheck());
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		assertTrue("cook should have nothing to do, but doesn't", cook.hasNothingToDo());
		assertTrue("cook's scheduler should return true. nothing to do, so check the order stand", cook.pickAndExecuteAnAction());
		assertTrue("cook's incomingorders is still empty though, no orders in stand", cook.getIncomingOrders().isEmpty());
		assertFalse("cook's scheduler should have returned false, no orders in stand and really nothing to do", cook.pickAndExecuteAnAction());
		
		//order from the waiter
		cook.msgCookThisOrder("Steak", waiter, 1);
		// post-conditions of receiving an order
		assertEquals("cook's state should still be working but isn't", cook.getState(), State.working);
		assertTrue("cook should have logged \"Received msgCookThisOrder\" but didn't. His log reads instead: " + cook.log.getLastLoggedEvent().toString(), 
				cook.log.containsString("Received msgCookThisOrder"));
		assertEquals("cook should have 1 incoming order. It doesn't.", cook.getIncomingOrders().size(), 1);
		assertEquals("cook's order should have choice of steak, but instead has " + cook.getIncomingOrders().get(0).choice, cook.getIncomingOrders().get(0).choice, "Steak");
		assertEquals("cook's order should have the right waiter in it. It doesn't.", cook.getIncomingOrders().get(0).waiter, waiter);
		assertEquals("cook's order should have the right table number but doesn't, instead it's " + cook.getIncomingOrders().get(0).tableNum, cook.getIncomingOrders().get(0).tableNum, 1);
		assertEquals("cook's order should have status == uncooked but doesn't, instead it's " + cook.getIncomingOrders().get(0).status, 
				cook.getIncomingOrders().get(0).status, OrderStatus.uncooked);
		assertEquals("MockWaiter should have an empty event log before the cook's scheduler is called. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 
				waiter.log.size(), 0);
		assertTrue("cook's scheduler should have returned true to cook the order but didn't", cook.pickAndExecuteAnAction());
		assertFalse("cook SHOULDN'T have nothing to do, but he does", cook.hasNothingToDo());
		
//		assertEquals("cook's order should have status == cooking but doesn't, instead it's " + cook.getIncomingOrders().get(0).status, 
//				cook.getIncomingOrders().get(0).status, OrderStatus.cooking);
		Food f = JiCookRole.getPantry().getFoodFromInventory(cook.getIncomingOrders().get(0).getChoice());
		assertEquals("order's food should be in stock but isn't", f.state, FoodState.inStock);
		assertEquals("order's food is steak", f.getName(), "Steak");
		assertEquals("cook's order should have status == cooked but doesn't, instead it's " + cook.getIncomingOrders().get(0).status, 
				cook.getIncomingOrders().get(0).status, OrderStatus.cooked);
		// from the scheduler
		assertTrue("cook's scheduler should have returned true to notify waiter of order but didn't", cook.pickAndExecuteAnAction());
		assertTrue("cook's order should have removed incomingOrders but didn't", cook.getIncomingOrders().size() == 0);
		assertTrue("cook should have nothing to do now, but he doesn't", cook.hasNothingToDo());
		assertTrue("waiter should have logged \"Received msgOrderIsReady\" but didn't, instead logged " + waiter.log.getLastLoggedEvent().toString(),
				waiter.log.containsString("Received msgOrderIsReady"));

		// postconditions
		assertTrue("cook's scheduler returns true. he has nothing to do, so he checks the stand.", cook.pickAndExecuteAnAction());
		assertTrue("cook's incomingorders is still empty though, no orders in stand", cook.getIncomingOrders().isEmpty());
		assertFalse("cook's scheduler should have returned false, nothing to do", cook.pickAndExecuteAnAction());
		
	}
	
	public void testTwoOrderFromRevolvingStand(){
		stand.addOrder("Steak", waiterShared, 1);
		
		//preconditions
		assertEquals("the person in the cook role should be cookperson, but it's actually " + cook.getPerson().getName(), cook.getPerson(), cookperson);
		assertFalse("cook shouldn't be working but is", cook.isWorking());
		assertEquals("cook's state should be none but is actually " + cook.getState(), cook.getState(), State.none);
		assertTrue("cook should have 0 bills in it. It doesn't.",cook.getIncomingOrders().isEmpty());
		assertEquals("cookAgent should have an empty event log before the cook's msgAtJiRestaurant is called. Instead, the cook's event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
		assertEquals("cook's order stand should be the stand", cook.getOrderStand(), stand);
		assertTrue("cook should have nothing to do, but doesn't", cook.hasNothingToDo());
		
		// going to the restaurant to start work
		cook.msgAtBuilding(restaurant);
		// post-conditions of arriving at the restaurant
		assertTrue("cook should have logged \"Received msgAtJiRestaurant\" but didn't. His log reads instead: " + cook.log.getLastLoggedEvent().toString(), 
				cook.log.containsString("Received msgAtJiRestaurant"));
		assertEquals("cook's state should be arrivedAtJiRestaurant but isn't", cook.getState(), State.arrivedAtJiRestaurant);
		assertEquals("cook's restaurant is the mockrestaurant", cook.getRestaurant(), restaurant);
		assertTrue("cook's scheduler should have returned true to begin work but didn't", cook.pickAndExecuteAnAction());
		assertEquals("cook's state should be working but isn't", cook.getState(), State.working);
		assertTrue("cook should be working but isn't", cook.isWorking());
		assertFalse("cook should not have gotten paid but somehow he thinks he did", cook.hasReceivedPaycheck());
		assertTrue("restaurant should have logged \"Received msgAtLocation\" but didn't. Instead, it logged " + restaurant.log.getLastLoggedEvent().toString(),
				restaurant.log.containsString("Received msgAtLocation"));
		assertTrue("cook should have nothing to do, but doesn't", cook.hasNothingToDo());
		assertTrue("cook's scheduler should return true. nothing to do, so check the order stand", cook.pickAndExecuteAnAction());

		assertTrue("cook should add the order to his list of incomingorders, but didn't", cook.getIncomingOrders().size() == 1);
		//assertFalse("cook's scheduler should have returned false, no orders in stand and really nothing to do", cook.pickAndExecuteAnAction());
		
		assertEquals("cook should have 1 incoming order. It doesn't.", cook.getIncomingOrders().size(), 1);
		assertEquals("cook's order should have choice of steak, but instead has " + cook.getIncomingOrders().get(0).choice, cook.getIncomingOrders().get(0).choice, "Steak");
		assertEquals("cook's order should have the right waiter in it. It doesn't.", cook.getIncomingOrders().get(0).waiter, waiterShared);
		assertEquals("cook's order should have the right table number but doesn't, instead it's " + cook.getIncomingOrders().get(0).tableNum, cook.getIncomingOrders().get(0).tableNum, 1);
		assertEquals("cook's order should have status == uncooked but doesn't, instead it's " + cook.getIncomingOrders().get(0).status, 
				cook.getIncomingOrders().get(0).status, OrderStatus.uncooked);
		assertEquals("MockWaiter should have an empty event log before the cook's scheduler is called. Instead, the MockWaiter's event log reads: " + waiterShared.log.toString(), 
				waiterShared.log.size(), 0);
		assertTrue("cook's scheduler should have returned true to cook the order but didn't", cook.pickAndExecuteAnAction());
		assertFalse("cook SHOULDN'T have nothing to do, but he does", cook.hasNothingToDo());
		
//		assertEquals("cook's order should have status == cooking but doesn't, instead it's " + cook.getIncomingOrders().get(0).status, 
//				cook.getIncomingOrders().get(0).status, OrderStatus.cooking);
		Food f = JiCookRole.getPantry().getFoodFromInventory(cook.getIncomingOrders().get(0).getChoice());
		assertEquals("order's food should be in stock but isn't", f.state, FoodState.inStock);
		assertEquals("order's food is steak", f.getName(), "Steak");
		assertEquals("cook's order should have status == cooked but doesn't, instead it's " + cook.getIncomingOrders().get(0).status, 
				cook.getIncomingOrders().get(0).status, OrderStatus.cooked);
		// from the scheduler
		assertTrue("cook's scheduler should have returned true to notify waiter of order but didn't", cook.pickAndExecuteAnAction());
		assertTrue("cook's order should have removed incomingOrders but didn't", cook.getIncomingOrders().size() == 0);
		assertTrue("cook should have nothing to do now, but he doesn't", cook.hasNothingToDo());
		assertTrue("waiterShared should have logged \"Received msgOrderIsReady\" but didn't, instead logged " + waiterShared.log.getLastLoggedEvent().toString(),
				waiterShared.log.containsString("Received msgOrderIsReady"));

		// postconditions
		assertTrue("cook's scheduler returns true. he has nothing to do, so he checks the stand.", cook.pickAndExecuteAnAction());
		assertTrue("cook's incomingorders is still empty though, no orders in stand", cook.getIncomingOrders().isEmpty());
		assertFalse("cook's scheduler should have returned false, nothing to do", cook.pickAndExecuteAnAction());
		
	}
	
}
