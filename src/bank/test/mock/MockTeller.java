package bank.test.mock;

import interfaces.Building;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import bank.Account;
import bank.interfaces.Bank;
import bank.interfaces.BankPatron;
import bank.interfaces.BankTeller;

public class MockTeller extends MockRole implements BankTeller{

	public Bank bank;
	public BankPatron patron;
	public int accountNum;
	public Account account;
	boolean working;
	boolean receivedPaycheck;
	
	public MockTeller() {
		super();
	}

	@Override
	public void msgAtBuilding(Building bank) {
		log.add(new LoggedEvent("Received msgAtBuilding"));
		// TODO Auto-generated method stub
		receivedPaycheck = false;
		this.bank = (Bank) bank;
	}

	@Override
	public void msgHelpCustomer(BankPatron nextPatronInLine) {
		log.add(new LoggedEvent("Received msgHelpCustomer"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgStopWorkingGoHome() {
		log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
		// TODO Auto-generated method stub
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		log.add(new LoggedEvent("Received msgHeresYourPaycheck and got $" + paycheck));
		// TODO Auto-generated method stub
		receivedPaycheck = true;
	}

	@Override
	public void msgMyAccountNumberIs(int accountNum) {
		log.add(new LoggedEvent("Received msgMyAccountNumberIs"));
		// TODO Auto-generated method stub
		this.accountNum = accountNum;
		
	}

	@Override
	public void msgNeedToOpenAccount() {
		log.add(new LoggedEvent("Received msgNeedToOpenAccount"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsAccountInfo(Account bankAccount) {
		log.add(new LoggedEvent("Received msgHereIsAccountInfo"));
		// TODO Auto-generated method stub
		account = bankAccount;
		accountNum = bankAccount.getAccountNumber();
		
	}

	@Override
	public void msgAccountCreated(int accountNum, Account bankAccount) {
		log.add(new LoggedEvent("Received msgAccountCreated"));
		// TODO Auto-generated method stub
		account = bankAccount;
		this.accountNum = accountNum;
		
	}

	@Override
	public void msgClosingAccount() {
		log.add(new LoggedEvent("Received msgClosingAccount"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAskingForLoan(float amount) {
		log.add(new LoggedEvent("Received msgAskingForLoan"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRepayingLoan(float amount) {
		log.add(new LoggedEvent("Received msgRepayingLoan"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgMakingDeposit(float amount) {
		log.add(new LoggedEvent("Received msgMakingDeposit"));
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void msgMakingWithdrawal(float amount) {
		log.add(new LoggedEvent("Received msgMakingWithdrawal"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGetAccountSummary() {
		log.add(new LoggedEvent("Received msgGetAccountSummary"));
		// TODO Auto-generated method stub
		//patron.msgAccountSummary(accountNum, account.getBalance(), account.getLoans());
		
	}

	@Override
	public void msgNoMoreServicesNeeded() {
		log.add(new LoggedEvent("Received msgNoMoreServicesNeeded"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public BankPatron getPatron() {
		return patron;
	}

	@Override
	public boolean isAvailable() {
		return (patron == null);
	}
	
	public boolean hasReceivedPaycheck() {
		return receivedPaycheck;
	}
	public String getName(){ return getPerson().getName(); }

	@Override
	public void setBank(Bank bank) {
		this.bank = bank;
	}
}
