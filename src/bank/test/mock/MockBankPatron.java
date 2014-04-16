package bank.test.mock;

import interfaces.Building;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import bank.interfaces.Bank;
import bank.interfaces.BankPatron;
import bank.interfaces.BankTeller;

public class MockBankPatron extends MockRole implements BankPatron{

	public Bank bank;
	public Building company;
	public int companyAccount;
	public float balance;
	public float loans;
	public int accountNumber;
	
	public MockBankPatron() {
		super();
	}

	@Override
	public void msgBankIsClosed(){
		log.add(new LoggedEvent("Received msgBankIsClosed"));
	}
	
	
	@Override
	public void msgAtBuilding(Building bank) {
		log.add(new LoggedEvent("Received msgAtBuilding"));
		this.bank = (Bank)bank;
	}

	@Override
	public void msgWhatIsYourAccountNumber(BankTeller teller) {
		log.add(new LoggedEvent("Received msgWhatIsYourAccountNumber from teller"));
	}

	@Override
	public void msgHowMayIHelpYou() {
		log.add(new LoggedEvent("Received msgHowMayIHelpYou"));
	}

	@Override
	public void msgAccountClosed(float payout) {
		log.add(new LoggedEvent("Received msgAccountClosed"));
	}
	
	@Override
	public void msgWontCloseAccountYouStillOweUs(float diff) {
		log.add(new LoggedEvent("Received msgWontCloseAccountYouStillOweUs"));
	}

	@Override
	public void msgGrantedLoanFor(float loanAmount) {
		log.add(new LoggedEvent("Received msgGrantedLoanFor"));
	}

	@Override
	public void msgDeniedLoan() {
		log.add(new LoggedEvent("Received msgDeniedLoan"));
	}

	@Override
	public void msgLoanPaymentReceived(float paymentAccepted) {
		log.add(new LoggedEvent("Received msgLoanPaymentReceived"));
	}

	@Override
	public void msgDepositSuccessful(float transactionAmount) {
		log.add(new LoggedEvent("Received msgDepositSuccessful"));
	}

	@Override
	public void msgWithdrawalSuccessful(float transactionAmount) {
		log.add(new LoggedEvent("Received msgWithdrawalSuccessful"));		
	}

	@Override
	public void msgWithdrawalDenied() {
		log.add(new LoggedEvent("Received msgWithdrawalDenied"));
	}

	@Override
	public void msgAccountSummary(int accountNum, float balance, float loans) {
		log.add(new LoggedEvent("Received msgAccountSummary"));
		this.accountNumber = accountNum;
		this.balance = balance;
		this.loans = loans;
	}

	@Override
	public void msgTakeTheMoney(float bankBalance) {
		log.add(new LoggedEvent("Received msgTakeTheMoney"));
	}

	@Override
	public void msgYoureUnderArrest() {
		log.add(new LoggedEvent("Received msgYoureUnderArrest"));
	}

	@Override
	public boolean hasIncome() {
		return true;
	}
	@Override
	public void setCompany(Building b, int account) {
		 company = b;
		 companyAccount = account;
	}
	public Building getCompany() {	return company;}
	public int getCompanyAccountNum(){ return companyAccount;}
	public String getName(){ return getPerson().getName();}

	@Override
	public void setBank(Bank bank) {
		this.bank = bank;
	}

}
