package bank.test;

import java.util.ArrayList;
import global.test.mock.MockPerson;
import interfaces.Building;
import junit.framework.TestCase;
import bank.BankSecurityRole;
import bank.BankSecurityRole.State;
import bank.test.mock.MockBank;
import bank.test.mock.MockBankPatron;
public class BankSecurityRoleTest extends TestCase{

	MockBank bank;
	BankSecurityRole guard;
	MockBankPatron robber;
	
	MockPerson personrobber;
	MockPerson personguard;
	
	public void setUp() throws Exception{
		super.setUp();
		bank = new MockBank("mockbank");
		ArrayList<Building> buildings = new ArrayList<Building>(); buildings.add(bank);
		
		personguard = new MockPerson("guard", buildings); personguard.setMoney(0f);	
		guard = new BankSecurityRole(); guard.setPerson(personguard);
		
		personrobber = new MockPerson("robber", buildings);		
		robber = new MockBankPatron(); robber.setPerson(personrobber);
		
		robber.bank = bank;
	}
	
	public void testnoRobbery(){
		// Pre-conditions
		assertNull("guard doesn't have a bank to report to", guard.getBank());
		assertTrue("guard's state is none", guard.getState() == State.none);
		assertFalse("guard is not working yet", guard.isWorking());
		assertFalse("guard has not been paid", guard.hasReceivedPaycheck());
		assertNull("guard is not aware of any robber", guard.getRobber());
		assertFalse("guard's scheduler returns false. nothing to do", guard.pickAndExecuteAnAction());
		assertEquals("the person in the guard role has no money", personguard.getMoney(), 0f);
		
		//going to work
		guard.msgAtBuilding(bank);
		assertTrue("guard should have logged \"Received msgAtBuilding\" but didn't. Instead, says " + guard.log.getLastLoggedEvent().toString(),
				guard.log.containsString("Received msgAtBuilding"));
		assertTrue("guard's state is arrivedAtBank", guard.getState() == State.arrivedAtBank);
		assertTrue("guard's scheduler returns true. needs to start working", guard.pickAndExecuteAnAction());
		assertTrue("guard's state is working", guard.getState() == State.working);
		assertTrue("guard is working", guard.isWorking());
		assertTrue("bank should have logged \"Received msgAtLocation from guard \" but didn't. Instead: " + bank.log.toString(),
				bank.log.size()==1);
		assertFalse("guard's scheduler returns false. nothing to do", guard.pickAndExecuteAnAction());
	
		// robber arrives and tries to rob the bank
		bank.msgThisIsAHoldUp(robber);
		// post-conditions of robber demanding money
		assertTrue("bank should have logged \"Received msgThisIsAHoldUp\" but didn't, instead says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgThisIsAHoldUp"));
		guard.msgRobberyTakingPlace(robber); // from the bank
		assertTrue("guard's state is working", guard.getState() == State.working);
		assertEquals("guard should have set robber to robber but didn't", guard.getRobber(), robber);
		assertTrue("guard scheduler should return true. Needs to respond to robbery.", guard.pickAndExecuteAnAction());
		assertTrue("guard's state is working", guard.getState() == State.working);
		assertTrue("robber should have logged \"Received msgYoureUnderArrest\" but didn't, instead says: " + robber.log.getLastLoggedEvent().toString(),
				robber.log.containsString("Received msgYoureUnderArrest"));
		assertTrue("bank should have logged \"Received msgRobberArrested\" but didn't, instead says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgRobberArrested"));
		assertNull("guard does not have a robber to deal with anymore", guard.getRobber());
		assertFalse("guard's scheduler returns false. nothing to do", guard.pickAndExecuteAnAction());
		
		guard.msgHeresYourPaycheck(25f);
		//post conditions of getting paid
		assertTrue("guard should have logged \"Received msgHeresYourPaycheck\" but didn't. Instead, says " + guard.log.getLastLoggedEvent().toString(),
				guard.log.containsString("Received msgHeresYourPaycheck"));
		assertEquals("the person in the guard role has $25", personguard.getMoney(), 25f);
		assertTrue("guard has gotten paid", guard.hasReceivedPaycheck());
		assertEquals("the person in the guard role got paid $25, now has $25", personguard.getMoney(), 25f);
		
		guard.msgStopWorkingGoHome();
		//post-conditions of the business day ending
		assertTrue("guard should have logged \"Received msgStopWorkingGoHome\" but didn't. Instead, says " + guard.log.getLastLoggedEvent().toString(),
				guard.log.containsString("Received msgStopWorkingGoHome"));
		assertFalse("guard is no longer working", guard.isWorking());
		assertFalse("guard's scheduler returns false. leaving building.", guard.pickAndExecuteAnAction());
		assertTrue("guard's state is still none", guard.getState() == State.none);
		assertTrue("bank should have logged \"Received msgLeavingWork\" but didn't. Instead, says " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgLeavingWork"));
		assertTrue("the person within the guard role should have logged \"Received msgLeavingLocation\" but didn't. Instead, says " + personguard.log.getLastLoggedEvent().toString(),
				personguard.log.containsString("Received msgLeavingLocation"));
		
		//Post-conditions for everything
		assertEquals("guard reports to this bank", guard.getBank(), bank);
		assertTrue("guard's state is none", guard.getState() == State.none);
		assertFalse("guard is not working anymore", guard.isWorking());
		assertTrue("guard has been paid", guard.hasReceivedPaycheck());
		assertNull("guard is not aware of any robber", guard.getRobber());
		assertFalse("guard's scheduler returns false. nothing to do", guard.pickAndExecuteAnAction());
		assertEquals("the person in the guard role has $25", personguard.getMoney(), 25f);
	}
	
	// TODO: interleaving robber and bank closing
}