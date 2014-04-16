package bank;

import global.BusinessAgent;
import bank.interfaces.BankPatron;

public class Account {

	BankPatron accountHolder;
	BusinessAgent businessAccountHolder;
	int accountNumber;
	float balance;
	float loans;
	
	public Account(BankPatron accountHolder, int accountNumber, float d, float e){
		this.businessAccountHolder = null;
		this.accountHolder = accountHolder;
		this.accountNumber = accountNumber;
		this.balance = d;
		this.loans = e;
	}

	public Account(BusinessAgent business, int accountNumber, float d, float e) {
		this.businessAccountHolder = business;
		this.accountHolder = null;
		this.accountNumber = accountNumber;
		this.balance = d;
		this.loans = e;
	}

	public Float getLoans() {
		return loans;
	}

	public float getBalance() {
		return balance;
	}
	
	public void setBalance(float balance) {
		this.balance = balance;
	}
	
	public int getAccountNumber() {
		return accountNumber;
	}

	public void setLoan(float loan) {
		this.loans = loan;
	}

	
}
