package bank.interfaces;

import interfaces.Building;

public interface BankPatron {
	
	// message sent from the gui
	public abstract void msgAtBuilding(Building bank);
	
	// messages for patron-teller interaction
	public abstract void msgBankIsClosed();
	public abstract void msgWhatIsYourAccountNumber(BankTeller teller);
	public abstract void msgHowMayIHelpYou();	
	public abstract void msgAccountClosed(float payout);	
	public abstract void msgGrantedLoanFor(float loanAmount);
	public abstract void msgDeniedLoan();
	public abstract void msgLoanPaymentReceived(float paymentAccepted);
	public abstract void msgDepositSuccessful(float transactionAmount);
	public abstract void msgWithdrawalSuccessful(float transactionAmount);
	public abstract void msgWithdrawalDenied();
	public abstract void msgAccountSummary(int accountNum, float balance, float loans);
	public abstract void msgWontCloseAccountYouStillOweUs(float diff);
	
	// messages for non-normal bank-patron and guard-patron interactions
	public abstract void msgTakeTheMoney(float totalBankBalance);
	public abstract void msgYoureUnderArrest();

	// accessors
	public abstract String getName();
	public abstract boolean hasIncome();
	public abstract void setCompany(Building business, int account);
	public abstract Building getCompany();
	public abstract int getCompanyAccountNum();
	public abstract void setBank(Bank bank);
}
