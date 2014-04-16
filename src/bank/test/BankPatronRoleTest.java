package bank.test;

import global.actions.Action;
import global.test.mock.MockPerson;
import interfaces.Building;

import java.util.ArrayList;
import java.util.List;

import restaurant.ji.test.mock.MockJiRestaurant;
import junit.framework.TestCase;
import bank.BankPatronRole;
import bank.BankPatronRole.State;
import bank.test.mock.MockBank;
import bank.test.mock.MockSecurity;
import bank.test.mock.MockTeller;

public class BankPatronRoleTest extends TestCase {
	MockBank bank;
	MockJiRestaurant restaurant;
	MockTeller teller;
	MockSecurity guard;
	BankPatronRole patron;
	
	MockPerson personpatron;
	MockPerson personteller;
	MockPerson personguard;
	
	public void setUp() throws Exception{
		super.setUp();
		bank = new MockBank("MockBank"); restaurant = new MockJiRestaurant("Cathy's restaurant"); 
		ArrayList<Building> buildings = new ArrayList<Building>(); buildings.add(bank); buildings.add(restaurant);
		
		restaurant.bank = bank; restaurant.companyAccount = 500;
		
		personteller = new MockPerson("teller", buildings);
		personpatron = new MockPerson("patron", buildings);
		personguard = new MockPerson("guard", buildings);
		
		teller = new MockTeller(); teller.setPerson(personteller);
		guard = new MockSecurity(); guard.setPerson(personguard);
		
		patron = new BankPatronRole(); patron.setPerson(personpatron);
		
		teller.bank = bank;
		guard.bank = bank;
		
	}
	
	/* a new customer opens an account, goes through every possible transaction, and closes his account */
	public void testOneNormalBankPatron(){
	
		List<Action> actions = new ArrayList<Action>();
		patron.actions = actions;
		personpatron.setMoney(100);
		
		// Pre-conditions
		assertEquals ("patron's bank is the person's bank", patron.getBank(), bank);
		assertEquals ("the person within patron is personpatron", patron.getPerson(), personpatron);
		assertEquals("patron's person should have $100", patron.getPerson().getMoney(), 100f);
		assertNull("patron doesn't have a teller", patron.getMyTeller());
		assertNull("patron doesn't have a company", patron.getCompany());
		assertEquals("patron doesn't have an account number" + patron.getAccountNumber(), patron.getAccountNumber(), 0);
		assertEquals("patron doesn't have a balance" + patron.getMyBalance(), patron.getMyBalance(), 0f);
		assertEquals("patron doesn't have debt" + patron.getMyDebt(), patron.getMyDebt(), 0f);
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("patron has no Actions", patron.actions.isEmpty());
		
		actions.add(new Action("deposit$25"));
		actions.add(new Action("withdraw$15"));
		actions.add(new Action("borrow$100"));
		actions.add(new Action("pay$25"));
		actions.add(new Action("getsummary"));
		
		assertTrue("patron has 5 Actions", patron.actions.size() == 5);
		
		bank.tellerList.add(teller);
		
		//patron arrives at bank
		patron.msgAtBuilding(bank);
		assertTrue("patron should have logged \"Received msgAtBuilding\" but didn't. Instead, says " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgAtBuilding"));
		assertTrue("patron's state is arrivedAtBank", patron.getState() == State.arrivedAtBank);
		assertEquals("patron's bank is the mockbank", patron.getBank(), bank);
		assertTrue("patron's scheduler returns true. needs to wait in line and tell bank he's here", patron.pickAndExecuteAnAction());
		assertTrue("patron should have an action list of size 5 but didn't", patron.actions.size() == 5);
		assertEquals("patron's currentService should be deposit$25 but isn't. Instead: " + patron.getCurrentService().task, patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("bank should have logged \"Received msgAtLocation from patron\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from patron"));
		teller.msgHelpCustomer(patron);
		assertTrue("teller should have logged \"Received msgHelpCustomer\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgHelpCustomer"));
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgWhatIsYourAccountNumber(teller);
		// post-conditions of teller asking for account information
		assertTrue("patron should have logged \"Received msgWhatIsYourAccountNumber\" but didn't. Instead, says " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgWhatIsYourAccountNumber"));
		assertTrue("patron's state is givingAccountInfo", patron.getState() == State.givingAccountInfo);
		assertEquals("patron's teller is the mock teller", patron.getMyTeller(), teller);
		assertTrue("patron's scheduler returns true. needs to respond to teller", patron.pickAndExecuteAnAction());
		assertTrue("patron should have an action list of size 5 but didn't", patron.actions.size() == 5);
		assertEquals("patron's currentService should be deposit$25 but isn't. Instead: " + patron.getCurrentService().task, patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgNeedToOpenAccount\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgNeedToOpenAccount"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgAccountSummary(1, 0, 0);
		//post-conditions of getting new account
		assertTrue("patron should have logged \"Received msgAccountSummary\" but didn't. Instead, says " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgAccountSummary"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's accountNumber is 1", patron.getAccountNumber(), 1);
		assertEquals("patron's balance is 0", patron.getMyBalance(), 0f);
		assertEquals("patron's debt is 0", patron.getMyDebt(), 0f);
		// scheduler automatically makes patron ask for his first service
		assertTrue("patron's scheduler returns true. needs to ask teller for service", patron.pickAndExecuteAnAction());
		assertEquals("patron's currentService should be deposit$25 but isn't. Instead: " + patron.getCurrentService().task, patron.getCurrentService(), actions.get(0));
		assertTrue("patron should have an action list of size 5 but didn't", patron.actions.size() == 5);
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgMakingDeposit\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgMakingDeposit"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgDepositSuccessful(25f); // from the teller
		//post-conditions of successful deposit
		assertTrue("patron should have logged \"Received msgDepositSuccessful\" but didn't. Instead: " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgDepositSuccessful"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's person should have $75 now, instead has $" + patron.getPerson().getMoney(), patron.getPerson().getMoney(), 75f);
		assertTrue("patron should have removed deposit$25 from his action list but didn't. size should be 4, but is " + patron.actions.size(), patron.actions.size() == 4);
		// scheduler automatically tells patron to ask for his next service
		assertTrue("patron's scheduler returns true. asks for next service in his list", patron.pickAndExecuteAnAction());
		assertEquals("patron's currentService should be withdraw$15 but isn't", patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgMakingWithdrawal\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgMakingWithdrawal"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgWithdrawalSuccessful(15f); // from the teller
		//post-conditions of successful withdraw
		assertTrue("patron should have logged \"Received msgWithdrawalSuccessful\" but didn't. Instead: " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgWithdrawalSuccessful"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's person should have $90 now, instead has $" + patron.getPerson().getMoney(), patron.getPerson().getMoney(), 90f);
		assertTrue("patron should have removed withdraw$15 from his action list but didn't. size should be 3, but is " + patron.actions.size(), patron.actions.size() == 3);
		// scheduler automatically tells patron to ask for his next service
		assertTrue("patron's scheduler returns true. asks for next service in his list", patron.pickAndExecuteAnAction());
		assertEquals("patron's currentService should be borrow$100 but isn't", patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgAskingForLoan\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgAskingForLoan"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgGrantedLoanFor(100f); // from the teller
		//post-conditions of loan granted
		assertTrue("patron should have logged \"Received msgGrantedLoanFor\" but didn't. Instead: " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgGrantedLoanFor"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's person should have $190 now, instead has $" + patron.getPerson().getMoney(), patron.getPerson().getMoney(), 190f);
		assertTrue("patron should have removed borrow$100 from his action list but didn't. size should be 2, but is " + patron.actions.size(), patron.actions.size() == 2);
		// scheduler automatically tells patron to ask for his next service
		assertTrue("patron's scheduler returns true. asks for next service in his list", patron.pickAndExecuteAnAction());
		assertEquals("patron's currentService should be pay$25 but isn't", patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgRepayingLoan\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgRepayingLoan"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgLoanPaymentReceived(75f); // from the teller
		//post-conditions of payment received
		assertTrue("patron should have logged \"Received msgLoanPaymentReceived\" but didn't. Instead: " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgLoanPaymentReceived"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's person should have $115 now, instead has $" + patron.getPerson().getMoney(), patron.getPerson().getMoney(), 115f);
		assertTrue("patron should have removed pay$25 from his action list but didn't. size should be 1, but is " + patron.actions.size(), patron.actions.size() == 1);
		// scheduler automatically tells patron to ask for his next service
		assertTrue("patron's scheduler returns true. asks for next service in his list", patron.pickAndExecuteAnAction());
		assertEquals("patron's currentService should be getsummary but isn't", patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgGetAccountSummary\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgGetAccountSummary"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgAccountSummary(1, 85, 75); // from the teller
		//post-conditions of getting account summary
		assertTrue("patron should have logged \"Received msgAccountSummary\" but didn't. Instead: " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgAccountSummary"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's person should have $115, instead has $" + patron.getPerson().getMoney(), patron.getPerson().getMoney(), 115f);
		assertEquals("patron's account number should be 1, but he thinks its $" + patron.getAccountNumber(), patron.getAccountNumber(), 1);
		assertEquals("patron's balance should be $85, but he thinks its $" + patron.getMyBalance(), patron.getMyBalance(), 85f);
		assertEquals("patron's debt should be $75, but he thinks its $" + patron.getMyDebt(), patron.getMyDebt(), 75f);
		assertTrue("patron should have removed getsummary from his action list but didn't. size should be 0, but is " + patron.actions.size(), patron.actions.isEmpty());
		// scheduler automatically tells patron to ask for his next service
		
		patron.msgAccountClosed(10); // from the teller
		//post-conditions of closing account
		assertTrue("patron should have logged \"Received msgAccountClosed\" but didn't. Instead: " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgAccountClosed"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's person should have added $10 for a total of $125, but instead has $" + patron.getPerson().getMoney(), patron.getPerson().getMoney(), 125f);
		assertEquals("patron should have reset his knowledge of an account number to 0 but didn't", patron.getAccountNumber(), 0);
		assertTrue("patron should have removed closeaccount from his action list but didn't. should be empty, but has size " + patron.actions.size(), patron.actions.isEmpty());
		// scheduler automatically tells patron to ask for his next service
		assertFalse("patron's scheduler returns false. done with services and leaving bank", patron.pickAndExecuteAnAction());
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgNoMoreServicesNeeded\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgNoMoreServicesNeeded"));		
		assertTrue("patron's person should have logged \"Received msgLeavingLocation\" but didn't. Instead, says " + personpatron.log.getLastLoggedEvent().toString(),
				personpatron.log.containsString("Received msgLeavingLocation"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		//post-conditions for everything
		assertEquals ("patron's bank is the person's bank", patron.getBank(), bank);
		assertEquals ("the person within patron is personpatron", patron.getPerson(), personpatron);
		assertEquals("patron's person should have $125", patron.getPerson().getMoney(), 125f);
		assertNull("patron doesn't have a teller", patron.getMyTeller());
		assertNull("patron doesn't have a company", patron.getCompany());
		assertEquals("patron doesn't have an account number" + patron.getAccountNumber(), patron.getAccountNumber(), 0);
		assertEquals("patron doesn't have a balance" + patron.getMyBalance(), patron.getMyBalance(), 0f);
		assertEquals("patron doesn't have debt" + patron.getMyDebt(), patron.getMyDebt(), 0f);
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("patron has no Actions", patron.actions.isEmpty());
		
	}
	
	public void testTwoDepositingForBusiness(){
		List<Action> actions = new ArrayList<Action>();
		patron.actions = actions;
		personpatron.setMoney(300);
		
		patron.setCompany((Building) restaurant, restaurant.getBankAccount()); // account num 500
		
		// Pre-conditions
		assertEquals ("patron's bank is the person's bank", patron.getBank(), bank);
		assertEquals ("the person within patron is personpatron", patron.getPerson(), personpatron);
		assertEquals("patron's person should have $300", patron.getPerson().getMoney(), 300f);
		assertNull("patron doesn't have a teller", patron.getMyTeller());
		assertEquals("patron has a company", patron.getCompany(), restaurant);
		assertEquals("patron's company account num is company's account num" + patron.getCompanyAccountNum(), patron.getCompanyAccountNum(), 500);
		assertTrue("patron has a different personal account num: "+patron.getAccountNumber(), patron.getAccountNumber() != patron.getCompanyAccountNum());
		assertEquals("patron doesn't have a balance" + patron.getMyBalance(), patron.getMyBalance(), 0f);
		assertEquals("patron doesn't have debt" + patron.getMyDebt(), patron.getMyDebt(), 0f);
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("patron has no Actions", patron.actions.isEmpty());
		
		actions.add(new Action("deposit$200.0"));
		actions.add(new Action("getsummary"));
		
		assertTrue("patron has 2 Action", patron.actions.size() == 2);
		
		bank.tellerList.add(teller);
		
		//patron arrives at bank
		patron.msgAtBuilding(bank);
		assertTrue("patron should have logged \"Received msgAtBuilding\" but didn't. Instead, says " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgAtBuilding"));
		assertTrue("patron's state is arrivedAtBank", patron.getState() == State.arrivedAtBank);
		assertEquals("patron's bank is the mockbank", patron.getBank(), bank);
		assertTrue("patron's scheduler returns true. needs to wait in line and tell bank he's here", patron.pickAndExecuteAnAction());
		assertTrue("patron should have an action list of size 2 but didn't", patron.actions.size() == 2);
		assertEquals("patron's currentService should be deposit$200.0 but isn't. Instead: " + patron.getCurrentService().task, 
				patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("bank should have logged \"Received msgAtLocation from patron\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from patron"));
		teller.msgHelpCustomer(patron);
		assertTrue("teller should have logged \"Received msgHelpCustomer\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgHelpCustomer"));
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgWhatIsYourAccountNumber(teller);
		// post-conditions of teller asking for account information
		// patron gives company account info, not his own
		assertTrue("patron should have logged \"Received msgWhatIsYourAccountNumber\" but didn't. Instead, says " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgWhatIsYourAccountNumber"));
		assertTrue("patron's state is givingAccountInfo", patron.getState() == State.givingAccountInfo);
		assertEquals("patron's teller is the mock teller", patron.getMyTeller(), teller);
		assertTrue("patron's scheduler returns true. needs to respond to teller", patron.pickAndExecuteAnAction());
		assertTrue("patron should have an action list of size 2 but didn't", patron.actions.size() == 2);
		assertEquals("patron's currentService should be deposit$200.0 but isn't. Instead: " + patron.getCurrentService().task,
				patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgMyAccountNumberIs\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgMyAccountNumberIs"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgHowMayIHelpYou();
		//post-conditions of successful look up
		assertTrue("patron should have logged \"Received msgHowMayIHelpYou\" but didn't. Instead, says " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgHowMayIHelpYou"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		
		// scheduler automatically makes patron ask for his first service
		assertTrue("patron's scheduler returns true. needs to ask teller for service", patron.pickAndExecuteAnAction());
		assertEquals("patron's currentService should be deposit$200.0 but isn't. Instead: " + patron.getCurrentService().task, 
				patron.getCurrentService(), actions.get(0));
		assertTrue("patron should have an action list of size 2 but doesn't", patron.actions.size() == 2);
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgMakingDeposit\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgMakingDeposit"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		patron.msgDepositSuccessful(200.0f); // from the teller
		//post-conditions of successful deposit
		assertTrue("patron should have logged \"Received msgDepositSuccessful\" but didn't. Instead: " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgDepositSuccessful"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's person should have $100 now, instead has $" + patron.getPerson().getMoney(), patron.getPerson().getMoney(), 100f);
		assertTrue("patron should have removed deposit$200.0 from his action list but didn't. size should be 1, but is " + patron.actions.size(), 
				patron.actions.size() == 1);
		// scheduler automatically tells patron to ask for his next service
		assertTrue("patron's scheduler returns true. asks for next service in his list", patron.pickAndExecuteAnAction());
		assertEquals("patron's currentService should be getsummary but isn't", patron.getCurrentService(), actions.get(0));
		assertTrue("patron's state is none", patron.getState() == State.none);
		
		patron.msgAccountSummary(500, 200, 0); // from the teller
		//post-conditions of getting account summary
		assertTrue("patron should have logged \"Received msgAccountSummary\" but didn't. Instead: " + patron.log.getLastLoggedEvent().toString(),
				patron.log.containsString("Received msgAccountSummary"));
		assertTrue("patron's state is beingHelped", patron.getState() == State.beingHelped);
		assertEquals("patron's person should have $100, instead has $" + patron.getPerson().getMoney(), patron.getPerson().getMoney(), 100f);
		assertEquals("patron's account number should be 500 (company's account num), but he thinks its $" + patron.getCompanyAccountNum(), 
				patron.getCompanyAccountNum(), 500);
		assertEquals("patron's company's balance should be $200, but he thinks its $" + patron.getCompanyBalance(), patron.getCompanyBalance(), 200f);
		assertEquals("patron's debt should be 0, but he thinks its $" + patron.getCompanyDebt(), patron.getCompanyDebt(), 0f);
		assertTrue("patron should have removed getsummary from his action list but didn't. size should be 0, but is " + patron.actions.size(), patron.actions.isEmpty());
		
		// scheduler automatically tells patron to ask for his next service
		assertFalse("patron's scheduler returns false. done with services and leaving bank", patron.pickAndExecuteAnAction());
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("teller should have logged \"Received msgNoMoreServicesNeeded\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgNoMoreServicesNeeded"));		
		assertTrue("patron's person should have logged \"Received msgLeavingLocation\" but didn't. Instead, says " + personpatron.log.getLastLoggedEvent().toString(),
				personpatron.log.containsString("Received msgLeavingLocation"));		
		assertFalse("patron's scheduler returns false. nothing to do", patron.pickAndExecuteAnAction());
		
		//post-conditions for everything
		assertEquals ("patron's bank is the person's bank", patron.getBank(), bank);
		assertEquals ("the person within patron is personpatron", patron.getPerson(), personpatron);
		assertEquals("patron's person should have $10", patron.getPerson().getMoney(), 100f);
		assertNull("patron doesn't have a teller", patron.getMyTeller());
		assertNull("patron no longer associated with a company", patron.getCompany());
		assertEquals("patron's company's balance should be $200, but he thinks its $" + patron.getCompanyBalance(), patron.getCompanyBalance(), 200f);
		assertEquals("patron's debt should be 0, but he thinks its $" + patron.getCompanyDebt(), patron.getCompanyDebt(), 0f);
		assertTrue("patron's state is none", patron.getState() == State.none);
		assertTrue("patron has no Actions", patron.actions.isEmpty());
		
	}
}
