package bank.interfaces;

import interfaces.Building;
import interfaces.Employee;
import bank.Account;

public interface BankTeller extends Employee{
	
	// message sent from the gui
	public abstract void msgAtBuilding(Building bank);
	
	// message for normal teller-bank interaction
	public abstract void msgHelpCustomer(BankPatron nextPatronInLine);
	//public abstract void msgStopWorkingGoHome();
	//public abstract void msgHeresYourPaycheck(float paycheck);
	
	// messages for normal teller-bank interaction
	public abstract void msgMyAccountNumberIs(int accountNum);
	public abstract void msgNeedToOpenAccount();
	public abstract void msgHereIsAccountInfo(Account bankAccount);
	public abstract void msgAccountCreated(int accountNum, Account bankAccount);
	public abstract void msgClosingAccount();
	public abstract void msgAskingForLoan(float amount);
	public abstract void msgRepayingLoan(float amount);
	public abstract void msgMakingDeposit(float amount);
	public abstract void msgMakingWithdrawal(float amount);
	public abstract void msgGetAccountSummary();
	public abstract void msgNoMoreServicesNeeded();
	
	// accessors
	public abstract BankPatron getPatron();
	public abstract boolean isAvailable();
	public abstract String getName();
	public abstract void setBank(Bank bank);
	
}
