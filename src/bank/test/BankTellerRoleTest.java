package bank.test;

import java.util.ArrayList;

import global.test.mock.MockPerson;
import interfaces.Building;
import junit.framework.TestCase;
import bank.Account;
import bank.BankTellerRole;
import bank.BankTellerRole.*;
import bank.test.mock.MockBank;
import bank.test.mock.MockBankPatron;

public class BankTellerRoleTest extends TestCase{

	MockBank bank;
	BankTellerRole teller;
	MockBankPatron patron1; MockBankPatron patron2;
	
	MockPerson personteller;
	MockPerson personpatron1;
	MockPerson personpatron2;
	
	public void setUp() throws Exception{
		super.setUp();
		bank = new MockBank("mockbank");
		ArrayList<Building> buildings = new ArrayList<Building>(); buildings.add(bank);
		
		personteller = new MockPerson("teller", buildings); personteller.setMoney(0);
		personpatron1 = new MockPerson("patron1", buildings);
		//personpatron2 = new PersonAgent("patron2", buildings);
		
		teller = new BankTellerRole(); teller.setPerson(personteller);
		
		patron1 = new MockBankPatron(); patron1.setPerson(personpatron1);
		//patron2 = new MockBankPatron(); patron2.setPerson(personpatron2);
		
		patron1.bank = bank;
		//patron2.bank = bank;
		
	}
	
	/*
	 * A new customer opens an account, goes through every possible bank service, and closes his account.
	 */
	public void testOneCustomerOneTellerNormalInteraction(){
		
		// for testing
		float depositAmount = 100f;
		float withdrawalAmount = 20f;
		float loanAmount = 50f;
		float paymentAmount = 25f;
		
		// Pre-conditions
		assertNull("teller doesn't have a bank", teller.getBank());
		assertNull("teller doesn't have a patron", teller.getPatron());
		assertEquals("teller doesn't have an account number" + teller.getAccountNumber(), teller.getAccountNumber(), 0);
		assertNull("teller doesn't have an account", teller.getAccount());
		assertEquals("teller's transaction amount is $0", teller.getTransactionAmount(), 0f);
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertEquals("teller's person shouldn't have money. Actually has $" + teller.getPerson().getMoney(), teller.getPerson().getMoney(), 0f);
				
		//going to work
		teller.msgAtBuilding(bank);
		assertEquals("teller's bank is bank", teller.getBank(), bank);
		assertTrue("teller should have logged \"Received msgAtBuilding\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgAtBuilding"));
		assertTrue("teller's state is arrivedAtBank", teller.getState() == State.arrivedAtBank);
		assertTrue("teller's scheduler returns true. needs to start working", teller.pickAndExecuteAnAction());
		//post-conditions
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller should be working. But he's not.", teller.isWorking());
		assertTrue("bank should have logged \"Received msgAtLocation from teller \" but didn't. Instead: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from teller"));
		assertFalse("teller has not received his paycheck", teller.hasReceivedPaycheck());
		assertFalse("teller's scheduler returns false. no one to help", teller.pickAndExecuteAnAction());
	
		// patron1 arrives at bank
		bank.msgAtLocation(personpatron1, patron1, null);
		// post-conditions of patron1 arriving at bank
		assertTrue("bank should have logged \"Received msgAtLocation from patron1\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from patron1"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgHelpCustomer(patron1); // from the bank
		//post-conditions of being assigned a customer to help
		assertTrue("teller should have logged \"Received msgHelpCustomer\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgHelpCustomer"));
		assertTrue("teller's state is receivingNextCustomer", teller.getState() == State.receivingNextCustomer);
		assertTrue("teller's service is helpingNewCustomer", teller.getService() == Service.helpingNewCustomer);
		assertEquals("teller is helping patron1", teller.getPatron(), patron1);
		assertTrue("teller scheduler should return true. Needs to help next customer.", teller.pickAndExecuteAnAction());
		//post-condition of receiving patron to help
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("patron1 should have logged \"Received msgWhatIsYourAccountNumber\" but didn't. Instead: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgWhatIsYourAccountNumber"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgNeedToOpenAccount();
		//post conditions of getting patron's account number
		assertTrue("teller should have logged \"Received msgNeedToOpenAccount\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgNeedToOpenAccount"));
		assertTrue("teller's state is readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is openAccount", teller.getService() == Service.openAccount);
		assertTrue("teller's scheduler returns true. needs to open new account.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("bank should have logged \"Received msgCreateAccount\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgCreateAccount"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		Account newAccount = new Account(patron1, 1, 0, 0);
		teller.msgAccountCreated(1, newAccount);
		//post conditions of new account created
		assertTrue("teller should have logged \"Received msgAccountCreated\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgAccountCreated"));
		assertTrue("teller's state is readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is getAccountSummary", teller.getService() == Service.getAccountSummary);
		assertEquals("teller's account is the right account", teller.getAccount(), newAccount);
		assertEquals("teller's accountnumber is the right accountnumber", teller.getAccountNumber(), 1);
		assertTrue("teller's scheduler returns true. needs to get account summary.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("patron1 should have logged \"Received msgAccountSummary\" but didn't. Instead: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgAccountSummary"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgMakingDeposit(depositAmount);
		//post conditions of a deposit
		assertTrue("teller's log should say \"Received msgMakingDeposit\" but didn't. It says: " + teller.log.getLastLoggedEvent(),
				teller.log.containsString("Received msgMakingDeposit"));
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is makeDeposit", teller.getService() == Service.makeDeposit);
		assertEquals("teller's transaction amount is $20", teller.getTransactionAmount(), depositAmount);
		assertTrue("teller's scheduler returns true. needs to make deposit.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("bank should have logged \"Received msgDepositingMoney\" but didn't. Instead: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgDepositingMoney"));
		assertTrue("patron1 should have logged \"Received msgDepositSuccessful\" but didn't. Instead: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgDepositSuccessful"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgMakingWithdrawal(withdrawalAmount);
		//post-conditions of making a withdrawal
		assertTrue("teller's log should say \"Received msgMakingWithdrawal\" but didn't. It says: " + teller.log.getLastLoggedEvent(),
				teller.log.containsString("Received msgMakingWithdrawal"));
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is makeWithdrawal", teller.getService() == Service.makeWithdrawal);
		assertEquals("teller's transaction amount is $15", teller.getTransactionAmount(), withdrawalAmount);
		assertTrue("teller's scheduler returns true. needs to make withdrawal.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("bank should have logged \"Received msgWithdrawingMoney\" but didn't. Instead: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgWithdrawingMoney"));
		assertTrue("patron1 should have logged \"Received msgWithdrawalSuccessful\" but didn't. Instead: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgWithdrawalSuccessful"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgAskingForLoan(loanAmount);
		// Post-conditions of getting a loan
		assertTrue("teller's log should say \"Received msgAskingForLoan\" but didn't. It says: " + teller.log.getLastLoggedEvent(),
				teller.log.containsString("Received msgAskingForLoan"));
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is createLoan", teller.getService() == Service.createLoan);
		assertEquals("teller's transaction amount is $50", teller.getTransactionAmount(), loanAmount);
		assertTrue("teller's scheduler returns true. needs to grant loan.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("bank should have logged \"Received msgGrantLoan\" but didn't. Instead: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgGrantLoan"));
		assertTrue("patron1 should have logged \"Received msgGrantedLoanFor\" but didn't. Instead: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgGrantedLoanFor"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgRepayingLoan(paymentAmount);
		// Post-conditions of repaying a loan
		assertTrue("teller's log should say \"Received msgRepayingLoan\" but didn't. It says: " + teller.log.getLastLoggedEvent(),
				teller.log.containsString("Received msgRepayingLoan"));
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is settleLoan", teller.getService() == Service.settleLoan);
		assertEquals("teller's transaction amount is $25", teller.getTransactionAmount(), paymentAmount);
		assertTrue("teller's scheduler returns true. needs to submit payment.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("bank should have logged \"Received msgPayBackLoan\" but didn't. Instead: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgPayBackLoan"));
		assertTrue("patron1 should have logged \"Received msgLoanPaymentReceived\" but didn't. Instead: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgLoanPaymentReceived"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		// update testing copy of Account
		newAccount.setBalance(depositAmount-withdrawalAmount);
		newAccount.setLoan(loanAmount-paymentAmount);
		
		teller.msgClosingAccount();
		//post conditions of closing an account
		assertTrue("teller should have logged \"Received msgClosingAccount\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgClosingAccount"));
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is closeAccount", teller.getService() == Service.closeAccount);
		assertTrue("teller's scheduler returns true. needs to close account.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		
		
		
		assertTrue("bank should have logged \"Received msgCloseAccount\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgCloseAccount"));
		assertTrue("patron1 should have logged \"Received msgAccountClosed\" but didn't. Instead: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgAccountClosed"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgGetAccountSummary();
		//post conditions of asking for account summary
		assertTrue("teller's log should say \"Received msgGetAccountSummary\" but didn't. It says: " + teller.log.getLastLoggedEvent(),
				teller.log.containsString("Received msgGetAccountSummary"));
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is getAccountSummary", teller.getService() == Service.getAccountSummary);
		assertTrue("teller's scheduler returns true. needs to get summary.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("patron1's log should say \"Received msgAccountSummary\" but didn't. It says: " + patron1.log.getLastLoggedEvent(),
				patron1.log.containsString("Received msgAccountSummary"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgNoMoreServicesNeeded();
		//post-conditions of telling teller customer is done
		assertTrue("teller should have logged \"Received msgNoMoreServicesNeeded\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgNoMoreServicesNeeded"));
		assertTrue("teller's state is doneWithCustomer", teller.getState() == State.doneWithCustomer);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("teller's scheduler returns true. needs to finish up with customer.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is still none", teller.getService() == Service.none);
		assertTrue("Teller1 should now be free to help another patron. but he isn't", teller.isAvailable());
		
		teller.msgStopWorkingGoHome();
		//post-conditions of the business day ending, teller told to stop working
		assertFalse("teller is no longer working", teller.isWorking());
		assertTrue("teller should have logged \"Received msgStopWorkingGoHome\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgStopWorkingGoHome"));
		assertFalse("teller's scheduler returns false. leaving building.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still none", teller.getState() == State.none);
		assertTrue("teller's service is still none", teller.getService() == Service.none);
		assertTrue("bank should have logged \"Received msgLeavingWork\" but didn't. Instead, says " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgLeavingWork"));
		//assertTrue("the person within the teller role should have logged \"Received msgLeavingLocation\" but didn't. Instead, says " + personteller.log.getLastLoggedEvent().toString(),
				//personteller.log.containsString("Received msgLeavingLocation"));
		
		//post-conditions for everything
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertNull("teller doesn't have a patron", teller.getPatron());
		assertEquals("teller doesn't have an account number" + teller.getAccountNumber(), teller.getAccountNumber(), 0);
		assertNull("teller doesn't have an account", teller.getAccount());
		assertEquals("teller's transaction amount is $0", teller.getTransactionAmount(), 0f);
	
	}
	
	/* A customer with an account already in the bank verifies his information */
	public void testSearchExistingAccount(){
		// Pre-conditions
		assertNull("teller doesn't have a bank", teller.getBank());
		assertNull("teller doesn't have a patron", teller.getPatron());
		assertEquals("teller doesn't have an account number" + teller.getAccountNumber(), teller.getAccountNumber(), 0);
		assertNull("teller doesn't have an account", teller.getAccount());
		assertEquals("teller's transaction amount is $0", teller.getTransactionAmount(), 0f);
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
				
		//going to work
		teller.msgAtBuilding(bank);
		assertEquals("teller's bank is bank", teller.getBank(), bank);
		assertTrue("teller should have logged \"Received msgAtBuilding\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgAtBuilding"));
		assertTrue("teller's state is arrivedAtBank", teller.getState() == State.arrivedAtBank);
		assertTrue("teller's scheduler returns true. needs to start working", teller.pickAndExecuteAnAction());
		//post-conditions
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller should be working. But he's not.", teller.isWorking());
		assertTrue("bank should have logged \"Received msgAtLocation from teller \" but didn't. Instead: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from teller"));
		assertFalse("teller has not received his paycheck", teller.hasReceivedPaycheck());
		assertFalse("teller's scheduler returns false. no one to help", teller.pickAndExecuteAnAction());
	
		// patron1 arrives at bank
		bank.msgAtLocation(personpatron1, patron1, null);
		// post-conditions of patron1 arriving at bank
		assertTrue("bank should have logged \"Received msgAtLocation from patron1\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgAtLocation from patron1"));
		
		teller.msgHelpCustomer(patron1);
		assertTrue("teller should have logged \"Received msgHelpCustomer\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgHelpCustomer"));
		assertTrue("teller's state is receivingNextCustomer", teller.getState() == State.receivingNextCustomer);
		assertTrue("teller's service is helpingNewCustomer", teller.getService() == Service.helpingNewCustomer);
		assertEquals("teller is helping patron1", teller.getPatron(), patron1);
		assertTrue("teller scheduler should return true. Needs to help next customer.", teller.pickAndExecuteAnAction());
		//post-condition of receiving patron to help
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("patron1 should have logged \"Received msgWhatIsYourAccountNumber\" but didn't. Instead: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgWhatIsYourAccountNumber"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());

		teller.msgMyAccountNumberIs(1);
		//post conditions of getting patron's account number
		assertTrue("teller should have logged \"Received msgMyAccountNumberIs\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgMyAccountNumberIs"));
		assertTrue("teller's state is lookingUpAccount", teller.getState() == State.lookingUpAccount);
		assertTrue("teller's service is helpingNewCustomer", teller.getService() == Service.helpingNewCustomer);
		assertTrue("teller's scheduler returns true. needs to pull up account.", teller.pickAndExecuteAnAction());
		assertEquals("teller has the right account number to look up", teller.getAccountNumber(), 1);
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("bank should have logged \"Received msgGetAccountInformation\" but didn't. Instead, says: " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgGetAccountInformation"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		Account existingAccount = new Account(patron1, 1, 0, 0);
		teller.msgHereIsAccountInfo(existingAccount);
		//post-conditions of getting account info from the bank
		assertTrue("teller should have logged \"Received msgHereIsAccountInfo\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgHereIsAccountInfo"));
		assertTrue("teller's state is readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is helpingNewCustomer", teller.getService() == Service.helpingNewCustomer);
		assertEquals("teller has the right account number to look up", teller.getAccount(), existingAccount);
		assertTrue("teller's scheduler returns true. needs to help customer.", teller.pickAndExecuteAnAction());
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("patron1 should have logged \"Received msgHowMayIHelpYou\" but didn't. Instead, says: " + patron1.log.getLastLoggedEvent().toString(),
				patron1.log.containsString("Received msgHowMayIHelpYou"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgGetAccountSummary();
		//post conditions of asking for account summary
		assertTrue("teller's log should say \"Received msgGetAccountSummary\" but didn't. It says: " + teller.log.getLastLoggedEvent(),
				teller.log.containsString("Received msgGetAccountSummary"));
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is getAccountSummary", teller.getService() == Service.getAccountSummary);
		assertTrue("teller's scheduler returns true. needs to get summary.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still readyToHelp", teller.getState() == State.readyToHelp);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("patron1's log should say \"Received msgAccountSummary\" but didn't. It says: " + patron1.log.getLastLoggedEvent(),
				patron1.log.containsString("Received msgAccountSummary"));
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		
		teller.msgNoMoreServicesNeeded();
		//post-conditions of telling teller customer is done
		assertTrue("teller should have logged \"Received msgNoMoreServicesNeeded\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgNoMoreServicesNeeded"));
		assertTrue("teller's state is doneWithCustomer", teller.getState() == State.doneWithCustomer);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("teller's scheduler returns true. needs to finish up with customer.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertTrue("Teller1 should now be free to help another patron. but he isn't", teller.isAvailable());
		
		teller.msgStopWorkingGoHome();
		//post-conditions of the business day ending, teller told to stop working
		assertFalse("teller is no longer working", teller.isWorking());
		assertTrue("teller should have logged \"Received msgStopWorkingGoHome\" but didn't. Instead, says " + teller.log.getLastLoggedEvent().toString(),
				teller.log.containsString("Received msgStopWorkingGoHome"));
		assertFalse("teller's scheduler returns false. leaving building.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is still none", teller.getState() == State.none);
		assertTrue("teller's service is still none", teller.getService() == Service.none);
		assertTrue("bank should have logged \"Received msgLeavingWork\" but didn't. Instead, says " + bank.log.getLastLoggedEvent().toString(),
				bank.log.containsString("Received msgLeavingWork"));
		assertTrue("the person within the teller role should have logged \"Received msgLeavingLocation\" but didn't. Instead, says " + personteller.log.getLastLoggedEvent().toString(),
				personteller.log.containsString("Received msgLeavingLocation"));
		
		//post-conditions for everything
		assertFalse("teller's scheduler returns false. nothing to do.", teller.pickAndExecuteAnAction());
		assertTrue("teller's state is none", teller.getState() == State.none);
		assertTrue("teller's service is none", teller.getService() == Service.none);
		assertNull("teller doesn't have a patron", teller.getPatron());
		assertEquals("teller doesn't have an account number" + teller.getAccountNumber(), teller.getAccountNumber(), 0);
		assertNull("teller doesn't have an account", teller.getAccount());
		assertEquals("teller's transaction amount is $0", teller.getTransactionAmount(), 0f);
	}
	
	// TODO: Non-normative tests. 
	// 1) loan is denied
	// 2) withdrawal denied
}