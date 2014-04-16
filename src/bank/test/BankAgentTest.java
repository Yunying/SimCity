package bank.test;

import java.util.ArrayList;

import global.roles.Role;
import interfaces.Building;
import global.test.mock.MockPerson;
import junit.framework.TestCase;
import bank.BankAgent;
import bank.test.mock.MockBankPatron;
import bank.test.mock.MockSecurity;
import bank.test.mock.MockTeller;

public class BankAgentTest extends TestCase{

	BankAgent bank;
	MockTeller teller1; //MockTeller teller2;
	MockSecurity guard;
	MockBankPatron patron1; //MockBankPatron patron2;
	
	MockPerson personteller1;
	//MockPerson personteller2;
	MockPerson personpatron1;
	//MockPerson personpatron2;
	MockPerson personguard;
	
	public void setUp() throws Exception{
		super.setUp();
		bank = new BankAgent("Bank");
		ArrayList<Building> buildings = new ArrayList<Building>(); buildings.add(bank);
		
		personteller1 = new MockPerson("teller1", buildings);
		//personteller2 = new PersonAgent("teller2", buildings);
		personpatron1 = new MockPerson("patron1", buildings);
		//personpatron2 = new PersonAgent("patron2", buildings);
		personguard = new MockPerson("guard", buildings);
		
		teller1 = new MockTeller(); teller1.setPerson(personteller1);
		//teller2 = new MockTeller(); teller2.setPerson(personteller2);
		guard = new MockSecurity(); guard.setPerson(personguard);
		
		patron1 = new MockBankPatron(); patron1.setPerson(personpatron1);
		//patron2 = new MockBankPatron(); patron2.setPerson(personpatron2);
		
		teller1.bank = bank;
		//teller2.bank = bank;
		guard.bank = bank;
		patron1.bank = bank;
		//patron2.bank = bank;
		
	}
	
	/*
	 * A new customer opens an account, goes through every possible bank service, and closes his account.
	 */
	public void testOneCustomerOneTellerNormalInteraction(){
		
		// Pre-conditions
		assertTrue("bank shouldn't have any accounts. But it DOES. In fact it has " + bank.getNumBankAccounts(), bank.getBankAccounts().isEmpty() && bank.getNumBankAccounts() == 0);
		assertTrue("bank shouldn't have any employees on site. But it DOES.", bank.getEmployees().isEmpty());
		assertTrue("bank shouldn't have any tellers. But it DOES.", bank.getTellerList().isEmpty());
		assertTrue("bank shouldn't have any customers. But it DOES.", bank.getArrivingCustomers().isEmpty());
		assertNull("bank shouldn't have a guard. But it DOES.", bank.getGuard());
		assertTrue("bank shouldn't have any accounts to look up. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertTrue("bank shouldn't have any accounts to create. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertEquals("bank should have a starting balance of $5000.0. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5000f);

		// make bank open
		bank.msgUpdateTime(5, 1); // start time is 5 right now
		// post-conditions of opening bank through timer
		assertFalse("Bank is closed because no tellers", bank.isOpen());
		assertEquals("current time is 5", bank.getCurrentTime(), 5);
		assertTrue("payday", bank.isPayDay());
		
		// teller arrives at bank
		bank.msgAtLocation(teller1.getPerson(), teller1, null );
		// post-conditions of teller arriving at bank
		assertTrue("bank should have logged \"Received msgAtLocation from teller1\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from teller1"));
		assertEquals("bank employee list should be size 1. it's not", bank.getEmployees().size(), 1);
		assertFalse("bank scheduler should return false. nothing to do", bank.pickAndExecuteAnAction());

		// update time again to open the bank now that teller is here
		bank.msgUpdateTime(6, 1);
		// post-conditions of opening bank through timer
		assertTrue("Bank is open", bank.isOpen());
		assertEquals("current time is 6", bank.getCurrentTime(), 6);
		assertTrue("same as before: payday", bank.isPayDay());
		
		// patron1 arrives at bank
		bank.msgAtLocation(personpatron1, patron1, null);
		// post-conditions of patron1 arriving at bank
		assertTrue("bank should have logged \"Received msgAtLocation from patron1\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from patron1"));
		assertEquals("bank customersInLine list should be size 1. it's not", bank.getArrivingCustomers().size(), 1);
		assertTrue("bank scheduler should return true. Needs to assign customer to teller.", bank.pickAndExecuteAnAction());
		assertTrue("teller1 should have logged \"Received msgHelpCustomer\" but didn't. Instead, says " + teller1.log.getLastLoggedEvent().toString(),
				teller1.log.containsString("Received msgHelpCustomer"));
		assertTrue("bank should have removed patron1 and customersInLine list should be empty. it's not", bank.getArrivingCustomers().isEmpty());
		
		// testing open account service
		teller1.patron = patron1;
		bank.msgCreateAccount(teller1); // sent from teller1
		assertTrue("bank's log should say \"Received msgCreateAccount\" but didn't. It says: " + bank.log.getLastLoggedEvent(),
				bank.log.containsString("Received msgCreateAccount"));
		assertTrue("bank should have added teller1 to accountsToCreate so it has size 1 and contains teller1. But it didn't.",
				bank.getAccountsToCreate().size() == 1 && bank.getAccountsToCreate().contains(teller1));
		
		// Post-conditions of opening account
		assertTrue("bank scheduler should return true. needs to open account", bank.pickAndExecuteAnAction());
		assertEquals("Bank should now have 1 account. but doesn't", bank.getNumBankAccounts(), 1);
		assertTrue("bank should have created a new account for patron1 with account number 1. But it didn't. Instead it has " + bank.getBankAccounts().keySet(),
				bank.getBankAccounts().containsKey(1));
		assertTrue("bank should have created account but didn't",
				bank.getBankAccounts().get(1) != null);
		assertTrue("bank should have removed teller1 from accountsToCreate so it is empty. But it didn't.",
				bank.getAccountsToCreate().isEmpty());
		assertFalse("bank scheduler should return false. No new customers in line so nothing to do", bank.pickAndExecuteAnAction());
		assertTrue("teller1's log should say \"Received msgAccountCreated\" but didn't. It says: " + teller1.log.getLastLoggedEvent(),
				teller1.log.containsString("Received msgAccountCreated"));
		
		// testing deposit service
		bank.msgDepositingMoney(1, 20f); // from teller1
		assertTrue("bank's log should say \"Received msgDepositingMoney\" but didn't. It says: " + bank.log.getLastLoggedEvent(),
				bank.log.containsString("Received msgDepositingMoney"));
		// Post-conditions of deposit
		assertEquals("bank should have added $20 to patron1's account for a new balance of $20. It didn't.",
				bank.getBankAccounts().get(1).getBalance(), 20f);
		assertEquals("bank should have added $20 to the bank balance for a total of $5020. But it DIDN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5020f);
		assertFalse("bank scheduler should return false. No new customers in line so nothing to do", bank.pickAndExecuteAnAction());
		assertEquals("teller1 should know the account now has $20. He doesn't. He thinks it has $" + teller1.account.getBalance(), 
				teller1.account.getBalance(), 20f);

		//testing withdrawal
		bank.msgWithdrawingMoney(1, 15f);
		assertTrue("bank's log should say \"Received msgWithdrawingMoney\" but didn't. It says: " + bank.log.getLastLoggedEvent(),
				bank.log.containsString("Received msgWithdrawingMoney"));
		// Post-conditions of withdrawal
		assertEquals("bank should have subtracted $15 from patron1's account for a new balance of $5. It didn't. The balance is actually $" + bank.getBankAccounts().get(1).getBalance(),
				bank.getBankAccounts().get(1).getBalance(), 5f);
		assertEquals("bank should have removed $15 from the bank balance for a total of $5005. But it DIDN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5005f);
		assertFalse("bank scheduler should return false. No new customers in line so nothing to do", bank.pickAndExecuteAnAction());
		assertEquals("teller1 should know the account now has $5. He doesn't. He thinks it has $" + teller1.account.getBalance(), 
				teller1.account.getBalance(), 5f);
		
		// testing getting a loan
		bank.msgGrantLoan(1, 100f); // from teller1
		assertTrue("bank's log should say \"Received msgGrantLoan\" but didn't. It says: " + bank.log.getLastLoggedEvent(),
				bank.log.containsString("Received msgGrantLoan"));
		// Post-conditions of getting a loan
		assertEquals("bank should have added $105 to patron1's account for a new loan total of $105. It didn't. The loan total is actually $" + bank.getBankAccounts().get(1).getLoans(),
				bank.getBankAccounts().get(1).getLoans(), 100f);
		assertEquals("bank should have removed $100 from the bank balance for a total of $4905. But it DIDN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 4905f);
		assertFalse("bank scheduler should return false. No new customers in line so nothing to do", bank.pickAndExecuteAnAction());
		assertEquals("teller1 should know the account now has $105 in loans. He doesn't. He thinks he has $" + teller1.account.getLoans(), 
				teller1.account.getLoans(), 100f);
		
		//testing repaying a loan
		bank.msgPayBackLoan(1, 25f);
		assertTrue("bank's log should say \"Received msgPayBackLoan\" but didn't. It says: " + bank.log.getLastLoggedEvent(),
				bank.log.containsString("Received msgPayBackLoan"));
		// Post-conditions of repaying a loan
		assertEquals("bank should have subtracted $25 from patron1's account for a new loan total of $75. It didn't. The loan total is actually $" + bank.getBankAccounts().get(1).getLoans(),
				bank.getBankAccounts().get(1).getLoans(), 75f);
		assertEquals("bank should have added $25 to the bank balance for a total of $4930. But it DIDN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 4930f);
		assertFalse("bank scheduler should return false. No new customers in line so nothing to do", bank.pickAndExecuteAnAction());
		assertEquals("teller1 should know the account now has $75 in loans. He doesn't. He thinks he has $" + teller1.account.getLoans(), 
				teller1.account.getLoans(), 75f);
		
		// testing close account service
		bank.msgCloseAccount(1); // from teller1
		assertTrue("bank's log should say \"Received msgCloseAccount\" but didn't. It says: " + bank.log.getLastLoggedEvent(),
				bank.log.containsString("Received msgCloseAccount"));
		assertTrue("bank should have removed account 1 from its records in bankAccounts. But it didn't.",
				bank.getBankAccounts().isEmpty());
		assertFalse("bank scheduler should return false. No new customers in line so nothing to do", bank.pickAndExecuteAnAction());
		
		//close bank
		bank.msgUpdateTime(30, 1); // closetime is 30 right now
		//test post-conditions of closing bank through the timer
		assertEquals("current time is closing time", bank.getCurrentTime(), 30);
		assertTrue("it's payday", bank.isPayDay());
		assertFalse("bank is closed", bank.isOpen());
		
		assertTrue("bank scheduler should return true. Bank needs to pay its employees", bank.pickAndExecuteAnAction());
		assertTrue("teller1 should have logged \"msgHeresYourPaycheck\" but didn't. instead, says: " + teller1.log.getLastLoggedEvent().toString(),
				teller1.log.containsString("msgHeresYourPaycheck"));
		assertEquals("bank should have an ending balance of $4905 after paying the teller $25. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 4905f);
		assertTrue("teller1 should have been paid. but he wasn't", teller1.hasReceivedPaycheck());
		
		// test bank closing
		assertFalse("bank scheduler should return false. Bank needs to close and go to sleep", bank.pickAndExecuteAnAction());
		assertTrue("teller1 should have logged \"msgStopWorkingGoHome\" but didn't. instead, says: " + teller1.log.getLastLoggedEvent().toString(),
				teller1.log.containsString("msgStopWorkingGoHome"));
		assertTrue("bank shouldn't have any employees on site. But it DOES", bank.getEmployees().isEmpty());
		
		//post-conditions for everything
		assertEquals("bank should have only one account. But it DOESN'T. It has " + bank.getNumBankAccounts(), bank.getNumBankAccounts(), 1);
		assertTrue("bank shouldn't have any employees on site. But it DOES.", bank.getEmployees().isEmpty());
		assertTrue("bank shouldn't have any tellers. But it DOES.", bank.getTellerList().isEmpty());
		assertTrue("bank shouldn't have any customers. But it DOES.", bank.getArrivingCustomers().isEmpty());
		assertNull("bank shouldn't have a guard. But it DOES.", bank.getGuard());
		assertTrue("bank shouldn't have any accounts to look up. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertTrue("bank shouldn't have any accounts to create. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertEquals("bank should have an end-of-day balance of $4905. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 4905f);

	}
	
	public void testBankRobberyNoGuard(){
		// Pre-conditions
		assertTrue("bank shouldn't have any accounts. But it DOES. In fact it has " + bank.getNumBankAccounts(), bank.getBankAccounts().isEmpty() && bank.getNumBankAccounts() == 0);
		assertTrue("bank shouldn't have any employees on site. But it DOES.", bank.getEmployees().isEmpty());
		assertTrue("bank shouldn't have any tellers. But it DOES.", bank.getTellerList().isEmpty());
		assertTrue("bank shouldn't have any customers. But it DOES.", bank.getArrivingCustomers().isEmpty());
		assertNull("bank shouldn't have a guard. But it DOES.", bank.getGuard());
		assertTrue("bank shouldn't have any accounts to look up. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertTrue("bank shouldn't have any accounts to create. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertEquals("bank should have a starting balance of $5000.0. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5000f);

		// make bank open
		bank.msgUpdateTime(5, 1); // start time is 5 right now
		// post-conditions of opening bank through timer
		assertFalse("Bank is closed because no tellers", bank.isOpen());
		assertEquals("current time is 5", bank.getCurrentTime(), 5);
		assertTrue("payday", bank.isPayDay());
		
		// teller arrives at bank
		bank.msgAtLocation(teller1.getPerson(), teller1, null );
		// post-conditions of teller arriving at bank
		assertTrue("bank should have logged \"Received msgAtLocation from teller1\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from teller1"));
		assertEquals("bank employee list should be size 1. it's not", bank.getEmployees().size(), 1);
		assertFalse("bank scheduler should return false. nothing to do", bank.pickAndExecuteAnAction());

		// update time again to open the bank now that teller is here
		bank.msgUpdateTime(6, 1);
		// post-conditions of opening bank through timer
		assertTrue("Bank is open", bank.isOpen());
		assertEquals("current time is 6", bank.getCurrentTime(), 6);
		assertTrue("same as before: payday", bank.isPayDay());
	
		// patron1 arrives and tries to rob the bank
		bank.msgThisIsAHoldUp(patron1);
		// post-conditions of patron1 demanding money
		assertTrue("bank should have logged \"Received msgThisIsAHoldUp\" but didn't, instead says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgThisIsAHoldUp"));
		assertEquals("bank should have set robber to patron1 but didn't", bank.getRobber(), patron1);
		assertTrue("bank scheduler should return true. Needs to respond to robber.", bank.pickAndExecuteAnAction());
		assertEquals("bank should have given all its money to the robber for a new balance of $0. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 0f);
		assertTrue("patron1 should have logged \"Received msgTakeTheMoney\" but didn't, instead says: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgTakeTheMoney"));
		assertFalse("bank should have closed after being robbed but didn't", bank.isOpen());
		assertFalse("bank scheduler should return false. responded to robber, needs to wait to see what happens next.", bank.pickAndExecuteAnAction());
		
		bank.msgokThanksBye();
		// robber responding to successful robbery
		assertTrue("bank should have logged \"Received msgokThanksBye\" but didn't, instead says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgokThanksBye"));
		assertNull("bank should have set robber back to null but didn't", bank.getRobber());		
		
		//check post-conditions
		assertFalse("bank scheduler should return false. Bank needs to close and go to sleep", bank.pickAndExecuteAnAction());
		assertTrue("teller1 should have logged \"msgStopWorkingGoHome\" but didn't. instead, says: " + teller1.log.getLastLoggedEvent().toString(),
				teller1.log.containsString("msgStopWorkingGoHome"));
		assertTrue("bank shouldn't have any employees on site. But it DOES", bank.getEmployees().isEmpty());
		// Post-conditions
		assertTrue("bank shouldn't have any accounts. But it DOES. In fact it has " + bank.getNumBankAccounts(), bank.getBankAccounts().isEmpty() && bank.getNumBankAccounts() == 0);
		assertTrue("bank shouldn't have any employees on site. But it DOES.", bank.getEmployees().isEmpty());
		assertTrue("bank shouldn't have any tellers. But it DOES.", bank.getTellerList().isEmpty());
		assertTrue("bank shouldn't have any customers. But it DOES.", bank.getArrivingCustomers().isEmpty());
		assertNull("bank shouldn't have a guard. But it DOES.", bank.getGuard());
		assertTrue("bank shouldn't have any accounts to look up. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertTrue("bank shouldn't have any accounts to create. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertEquals("bank should have an end of day balance of $0. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 0f);

	}
	
	public void testBankRobberyGuardPresent(){
		// Pre-conditions
		assertTrue("bank shouldn't have any accounts. But it DOES. In fact it has " + bank.getNumBankAccounts(), bank.getBankAccounts().isEmpty() && bank.getNumBankAccounts() == 0);
		assertTrue("bank shouldn't have any employees on site. But it DOES.", bank.getEmployees().isEmpty());
		assertTrue("bank shouldn't have any tellers. But it DOES.", bank.getTellerList().isEmpty());
		assertTrue("bank shouldn't have any customers. But it DOES.", bank.getArrivingCustomers().isEmpty());
		assertNull("bank shouldn't have a guard. But it DOES.", bank.getGuard());
		assertTrue("bank shouldn't have any accounts to look up. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertTrue("bank shouldn't have any accounts to create. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertEquals("bank should have a starting balance of $5000.0. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5000f);

		// make bank open
		bank.msgUpdateTime(5, 1); // start time is 5 right now
		// post-conditions of opening bank through timer
		assertFalse("Bank is closed because no tellers", bank.isOpen());
		assertEquals("current time is 5", bank.getCurrentTime(), 5);
		assertTrue("payday", bank.isPayDay());
		
		// teller arrives at bank
		bank.msgAtLocation(teller1.getPerson(), teller1, null );
		// post-conditions of teller arriving at bank
		assertTrue("bank should have logged \"Received msgAtLocation from teller1\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from teller1"));
		assertEquals("bank employee list should be size 1. it's not", bank.getEmployees().size(), 1);
		assertFalse("bank scheduler should return false. nothing to do", bank.pickAndExecuteAnAction());

		// update time again to open the bank now that teller is here
		bank.msgUpdateTime(6, 1);
		// post-conditions of opening bank through timer
		assertTrue("Bank is open", bank.isOpen());
		assertEquals("current time is 6", bank.getCurrentTime(), 6);
		assertTrue("same as before: payday", bank.isPayDay());
		
		// guard arrives at bank
		bank.msgAtLocation(guard.getPerson(), (Role) guard, null );
		// post-conditions of guard arriving at bank
		assertTrue("bank should have logged \"Received msgAtLocation from guard\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from guard"));
		assertTrue("bank should know the guard is here but doesn't", guard != null);
		assertEquals("bank employee list should be size 2. but it's " + bank.getEmployees().size(), bank.getEmployees().size(), 2);
		assertFalse("bank scheduler should return false. nothing to do", bank.pickAndExecuteAnAction());
	
		// patron1 arrives and tries to rob the bank
		bank.msgThisIsAHoldUp(patron1);
		// post-conditions of patron1 demanding money
		assertTrue("bank should have logged \"Received msgThisIsAHoldUp\" but didn't, instead says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgThisIsAHoldUp"));
		assertEquals("bank should have set robber to patron1 but didn't", bank.getRobber(), patron1);
//		assertFalse("didn't handle robbery yet. but somehow it thinks it did?", bank.hasHandledRobbery());
		assertTrue("bank scheduler should return true. Needs to respond to robber.", bank.pickAndExecuteAnAction());
		
		assertTrue("guard should have logged \"Received msgRobberyTakingPlace\" but didn't, instead says: " + guard.log.getLastLoggedEvent().toString(),
				guard.log.containsString("Received msgRobberyTakingPlace"));
		assertTrue("patron1 should have logged \"Received msgYoureUnderArrest\" but didn't, instead says: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgYoureUnderArrest"));
		bank.msgRobberArrested();// from the guard
		assertTrue("bank should have logged \"Received msgRobberArrested\" but didn't, instead says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgRobberArrested"));
		assertNull("bank should have set robber back to null but didn't", bank.getRobber());
		
		//close bank
		bank.msgUpdateTime(31, 1); // closetime is 30 right now. payday is at 30
		//test post-conditions of closing bank through the timer
		assertEquals("current time is past closing time", bank.getCurrentTime(), 31);
		assertFalse("bank is closed", bank.isOpen());
		assertTrue("bank has money", bank.getTotalBankBalance() > 0);
		assertTrue("it's payday", bank.isPayDay());
		assertTrue("bank scheduler should return true. Bank needs to pay its employees", bank.pickAndExecuteAnAction());
		assertTrue("teller1 should have logged \"msgHeresYourPaycheck\" but didn't. instead, says: " + teller1.log.getLastLoggedEvent().toString(),
				teller1.log.containsString("msgHeresYourPaycheck"));
		assertEquals("bank should have an ending balance of $5000-25 after paying the teller $25. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5000-25f);
		assertTrue("teller1 should have been paid. but he wasn't", teller1.hasReceivedPaycheck());
		assertTrue("bank scheduler should return true. Bank needs to continue to pay its employees", bank.pickAndExecuteAnAction());
		assertTrue("guard should have logged \"msgHeresYourPaycheck\" but didn't. instead, says: " + guard.log.getLastLoggedEvent().toString(),
				guard.log.containsString("msgHeresYourPaycheck"));
		assertEquals("bank should have an ending balance of $5000-25-20 after paying the guard $20. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5000-20-25f);
		assertTrue("guard should have been paid. but he wasn't", guard.hasReceivedPaycheck());
		
		assertFalse("bank scheduler should return false. Bank needs to close and go to sleep", bank.pickAndExecuteAnAction());
		assertTrue("guard should have logged \"msgStopWorkingGoHome\" but didn't. instead, says: " + guard.log.getLastLoggedEvent().toString(),
				guard.log.containsString("msgStopWorkingGoHome"));
		assertTrue("bank shouldn't have any employees on site. But it DOES", bank.getEmployees().isEmpty());
		// Post-conditions
		assertTrue("bank shouldn't have any accounts. But it DOES. In fact it has " + bank.getNumBankAccounts(), bank.getBankAccounts().isEmpty() && bank.getNumBankAccounts() == 0);
		assertTrue("bank shouldn't have any employees on site. But it DOES.", bank.getEmployees().isEmpty());
		assertTrue("bank shouldn't have any tellers. But it DOES.", bank.getTellerList().isEmpty());
		assertTrue("bank shouldn't have any customers. But it DOES.", bank.getArrivingCustomers().isEmpty());
		assertNull("bank shouldn't have a guard. But it DOES.", bank.getGuard());
		assertTrue("bank shouldn't have any accounts to look up. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertTrue("bank shouldn't have any accounts to create. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertEquals("bank should have an end of day balance of $5000-25-20. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5000-25-20f);

	}
	
	public void patronsNoTellers(){
		// Pre-conditions
		assertTrue("bank shouldn't have any accounts. But it DOES. In fact it has " + bank.getNumBankAccounts(), bank.getBankAccounts().isEmpty() && bank.getNumBankAccounts() == 0);
		assertTrue("bank shouldn't have any employees on site. But it DOES.", bank.getEmployees().isEmpty());
		assertTrue("bank shouldn't have any tellers. But it DOES.", bank.getTellerList().isEmpty());
		assertTrue("bank shouldn't have any customers. But it DOES.", bank.getArrivingCustomers().isEmpty());
		assertNull("bank shouldn't have a guard. But it DOES.", bank.getGuard());
		assertTrue("bank shouldn't have any accounts to look up. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertTrue("bank shouldn't have any accounts to create. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertEquals("bank should have a starting balance of $5000.0. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 5000f);
	
		// make bank open
		bank.msgUpdateTime(5, 1); // start time is 5 right now
		// post-conditions of opening bank through timer
		assertFalse("Bank is closed because no tellers", bank.isOpen());
		assertEquals("current time is 5", bank.getCurrentTime(), 5);
		assertTrue("payday", bank.isPayDay());
		
//		// update time again to open the bank now that teller is here
//		bank.msgUpdateTime(6);
//		// post-conditions of opening bank through timer
//		assertTrue("Bank is open", bank.isOpen());
//		assertEquals("current time is 6", bank.getCurrentTime(), 6);
//		assertFalse("same as before: not all employees paid", bank.allEmployeesPaid);
//		assertFalse("same as before: not payday", bank.isPayDay());
		
		// patron1 arrives at bank
		bank.msgAtLocation(personpatron1, patron1, null);
		// post-conditions of patron1 arriving at bank
		assertTrue("bank should have logged \"Received msgAtLocation from patron1\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from patron1"));
		assertEquals("bank customersInLine list should be size 1. it's not", bank.getArrivingCustomers().size(), 1);
		assertTrue("bank scheduler should return true. Needs to tell customer to leave.", bank.pickAndExecuteAnAction());
		assertTrue("teller1 should have logged \"Received msgBankIsClosed\" but didn't. Instead, says " + teller1.log.getLastLoggedEvent().toString(),
				teller1.log.containsString("Received msgBankIsClosed"));
		assertTrue("bank should have removed patron1 and customersInLine list should be empty. it's not", bank.getArrivingCustomers().isEmpty());
		
		assertFalse("bank scheduler should return false. No tellers and no customers, so go to sleep", bank.pickAndExecuteAnAction());
		
		//close bank and check post-conditions
		bank.setOpen(false);
		assertFalse("bank scheduler should return false. Bank needs to close and go to sleep", bank.pickAndExecuteAnAction());
		assertTrue("teller1 should have logged \"msgStopWorkingGoHome\" but didn't. instead, says: " + teller1.log.getLastLoggedEvent().toString(),
				teller1.log.containsString("msgStopWorkingGoHome"));
		assertTrue("bank shouldn't have any employees on site. But it DOES", bank.getEmployees().isEmpty());
		// Post-conditions
		assertTrue("bank shouldn't have any accounts. But it DOES. In fact it has " + bank.getNumBankAccounts(), bank.getBankAccounts().isEmpty() && bank.getNumBankAccounts() == 0);
		assertTrue("bank shouldn't have any employees on site. But it DOES.", bank.getEmployees().isEmpty());
		assertTrue("bank shouldn't have any tellers. But it DOES.", bank.getTellerList().isEmpty());
		assertTrue("bank shouldn't have any customers. But it DOES.", bank.getArrivingCustomers().isEmpty());
		assertNull("bank shouldn't have a guard. But it DOES.", bank.getGuard());
		assertTrue("bank shouldn't have any accounts to look up. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertTrue("bank shouldn't have any accounts to create. But it DOES.", bank.getAccountsToCreate().isEmpty());
		assertEquals("bank should have an end of day balance of $0. But it DOESN'T. It has $" + bank.getTotalBankBalance(), bank.getTotalBankBalance(), 0f);

	}
}
