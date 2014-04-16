/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package restaurant.cammarano.test;

import bank.BankAgent;
import global.test.mock.*;
import junit.framework.TestCase;
import market.MarketAgent;
import restaurant.cammarano.*;
import restaurant.cammarano.roles.*;

/**
 *
 * @author CMCammarano
 */
public class CashierBankTest extends TestCase {
	
	public CammaranoRestaurantAgent restaurant;
	public BankAgent bank;
	public MarketAgent market;
	
	public CammaranoCashierRole cashier;
	public MockPerson person;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		bank = new BankAgent("bank");
		market = new MarketAgent("market", "loc");
		
		restaurant = new CammaranoRestaurantAgent("restaurant", market, bank);
		
		person = new MockPerson("mock", null);
	
		cashier = new CammaranoCashierRole();
		cashier.setPerson(person);
		cashier.setRestaurant(restaurant);
	}
	
	public void testCashierBankInteraction() throws Exception {
		setUp();
		
		// Checking preconditions before tests are run
		assertEquals("The restaurant has no people inside of it. It doesn't.", restaurant.people.size(), 0);		
		assertEquals("The restaurant should have an empty event log before someone is added. Instead, the restaurant's event log reads: " + restaurant.log.toString(), 0, restaurant.log.size());
		assertEquals("MockPerson should have nothing logged when the interaction begins. It does: " + person.log.toString(), 0, person.log.size());
		assertEquals("The cashier should have no logged events. It does: " + cashier.log.toString(), 0, cashier.log.size());
		
		// Step one--adding our cashier
		restaurant.msgAtLocation(person, cashier, null);
		assertEquals("The restaurant should have 1 person in it. It doesn't.", restaurant.people.size(), 1);
		assertFalse("The cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("The restaurant should have a log with no entries. Instead, the restaurant's event log reads: " + restaurant.log.toString(), 0, restaurant.log.size());
		assertTrue("The restaurant's scheduler should have returned true, but didn't.", restaurant.pickAndExecuteAnAction());
		assertTrue("The Restaurant should have logged \"AssignCashier called\"  but didn't. Its log reads instead: " + restaurant.log.getLastLoggedEvent().toString(), restaurant.log.containsString("AssignCashier called"));
		assertFalse("The cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		
		// Step one--sending our cashier out
		cashier.LeaveWork();
		assertEquals("The restaurant should have 1 person in it. It doesn't.", restaurant.people.size(), 1);
		assertFalse("The cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("The cashier should have logged \"LeaveWork called\". Instead, the cashier's event log reads: " + cashier.log.toString(), 1, cashier.log.size());
		assertFalse("The restaurant's scheduler should have returned false, but didn't.", restaurant.pickAndExecuteAnAction());
		assertFalse("The cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());		
		assertTrue("MockPerson should have. \"AddTaskDepositEarnings called\" but it didn't It does: " + person.log.getLastLoggedEvent().toString(), person.log.containsString("AddTaskDepositEarnings called"));
	}
}
