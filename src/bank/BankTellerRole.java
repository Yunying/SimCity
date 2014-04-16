package bank;

import interfaces.Building;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import bank.interfaces.Bank;
import bank.interfaces.BankPatron;
import bank.interfaces.BankTeller;

public class BankTellerRole extends Role implements BankTeller {

	private Bank bank;
	private BankPatron patron;
	public enum Service {
		none, helpingNewCustomer, openAccount, closeAccount, createLoan, settleLoan, makeDeposit, makeWithdrawal, getAccountSummary, giveCustomerAccountInfo
	};
	private Service service;
	
	public enum State {
		none, arrivedAtBank, receivingNextCustomer, readyToHelp, lookingUpAccount, doneWithCustomer, readyToLeave
	};
	private State state;
	
	private int accountNumber;
	private Account account;
	private float transactionAmount;
	private boolean working;
	private boolean receivedPaycheck;
	
	public BankTellerRole(){
		super();
		state = State.none;
		service = Service.none;
		patron = null;
		working = false;
	}
	
	
	//**********Messages************//
	@Override
	public void msgAtBuilding(Building bank){
		log.add(new LoggedEvent("Received msgAtBuilding"));
		setState(State.arrivedAtBank);
		this.bank = (Bank) bank;
		stateChanged();
	}
	
	@Override
	public void msgHelpCustomer(BankPatron nextPatronInLine){
		log.add(new LoggedEvent("Received msgHelpCustomer"));
		state = State.receivingNextCustomer;
		service = Service.helpingNewCustomer;
		patron = nextPatronInLine;
		stateChanged();
	}
		
	@Override
	public void msgMyAccountNumberIs(int accountNum){
		log.add(new LoggedEvent("Received msgMyAccountNumberIs"));
		state = State.lookingUpAccount;
		service = Service.helpingNewCustomer;
		setAccountNumber(accountNum);
		stateChanged();
	}	
	
	@Override
	public void msgNeedToOpenAccount(){
		log.add(new LoggedEvent("Received msgNeedToOpenAccount"));
		state = State.readyToHelp;
		service = Service.openAccount;
		stateChanged();
	}	
	
	@Override
	public void msgHereIsAccountInfo(Account bankAccount){
		log.add(new LoggedEvent("Received msgHereIsAccountInfo"));
		state = State.readyToHelp;
		service = Service.helpingNewCustomer;
		setAccount(bankAccount);
		stateChanged();
	}	
	
	@Override
	public void msgAccountCreated(int accountNum, Account bankAccount){
		log.add(new LoggedEvent("Received msgAccountCreated"));
		state = State.readyToHelp;
		service = Service.getAccountSummary;
		setAccount(bankAccount);
		setAccountNumber(accountNum);
		stateChanged();
	}
	
	@Override
	public void msgClosingAccount(){
		log.add(new LoggedEvent("Received msgClosingAccount"));
		service = Service.closeAccount;
		stateChanged();
	}
	
	@Override
	public void msgAskingForLoan(float amount){
		log.add(new LoggedEvent("Received msgAskingForLoan"));
		service = Service.createLoan;
		setTransactionAmount(amount);
		stateChanged();
	}
	
	@Override
	public void msgRepayingLoan(float amount){
		log.add(new LoggedEvent("Received msgRepayingLoan"));
		service = Service.settleLoan;
		setTransactionAmount(amount);
		stateChanged();
	}
	
	@Override
	public void msgMakingDeposit(float amount){
		log.add(new LoggedEvent("Received msgMakingDeposit"));
		service = Service.makeDeposit;
		setTransactionAmount(amount);
		stateChanged();
	}
	
	@Override
	public void msgMakingWithdrawal(float amount){
		log.add(new LoggedEvent("Received msgMakingWithdrawal"));
		service = Service.makeWithdrawal;
		setTransactionAmount(amount);
		stateChanged();
	}
	
	@Override
	public void msgGetAccountSummary(){
		log.add(new LoggedEvent("Received msgGetAccountSummary"));
		service = Service.getAccountSummary;
		stateChanged();
	}
	
	@Override
	public void msgNoMoreServicesNeeded(){
		log.add(new LoggedEvent("Received msgNoMoreServicesNeeded"));
		state = State.doneWithCustomer;
		service = Service.none;
		stateChanged();
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		person.ChangeMoney(paycheck);
		receivedPaycheck = true;
		stateChanged();
	}
	
	@Override
	public void msgStopWorkingGoHome() {
		log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
		state = State.readyToLeave;
		working = false;
		stateChanged();
	}
	
	//**********Scheduler************//
	@Override
	public boolean pickAndExecuteAnAction(){
		if (state == State.arrivedAtBank){
			beginWork();
			return true;
		}
		else if (patron != null){
			if (state == State.receivingNextCustomer){
				getCustomerInfo();
				return true;
			}
			else if (state == State.lookingUpAccount){
				pullUpAccountInformation();
				return true;
			}
			else if (state == State.readyToHelp){
				 if (getService() == Service.helpingNewCustomer){
					helpCustomer();
					return true;
				 }
				// account services
				else if (getService() == Service.openAccount){
					openAccount();
					return true;
				}
				else if (getService() == Service.getAccountSummary){
					getAccountSummary();
					return true;
				}
				else if (getService() == Service.closeAccount){
					closeAccount();
					return true;
				}
				
				// loan services
				else if (getService() == Service.createLoan){
					makeLoanDecision();
					return true;
				}
				else if (getService() == Service.settleLoan){
					settleLoan();
					return true;
				}
				
				// balance services
				else if (getService() == Service.makeDeposit){
					depositFunds();
					return true;
				}
				else if (getService() == Service.makeWithdrawal){
					withdrawFunds();
					return true;
				}
				else{
					return false;
				}
			}
			else if (state == State.doneWithCustomer){
				getReadyForNewCustomer();
				return true;
			}
			else{
				return false;
			}
		}
		else if (!working && state == State.readyToLeave){
			leaveWork();
		}
		return false;
	}
	
	
	//**********Actions************//
	void beginWork(){
		state = State.none;
		Do("Working now at " + bank.getName() );
		working = true;
		bank.msgAtLocation(person, this, null); // change this message in Building so we're not always passing in unnecessary information
		receivedPaycheck = false;
	}
		
	void getCustomerInfo(){
		Do(patron.getName() + ", what's your account number?");
		state = State.none;
		service = Service.none;
		patron.msgWhatIsYourAccountNumber(this);
	}
	
	void pullUpAccountInformation(){
		Do(patron.getName() + ", please wait while I pull up your account");
		state = State.none;
		service = Service.none;
		bank.msgGetAccountInformation(getAccountNumber(), this);
	}
	
	void openAccount(){
		Do("ok opening account for " + patron.getName());
		state = State.none;
		service = Service.none;
		bank.msgCreateAccount(this);
	}
	
	void closeAccount(){
		service = Service.none;
		float diff = account.getBalance() - account.getLoans();
		if (diff > 0){
			Do(patron.getName() + "'s account closed");
			bank.msgCloseAccount(accountNumber);
			patron.msgAccountClosed(diff);
		}
		else{
			Do("No won't close, you owe us $$");
			patron.msgWontCloseAccountYouStillOweUs(diff);
		}
	}
		
	void helpCustomer(){
		Do("How may I help you, " + patron.getName() + "?");
		service = Service.none;
		patron.msgHowMayIHelpYou();
	}
	
	void makeLoanDecision(){
		service = Service.none;
		// approve the loan if the bank has enough money and the customer either has a source of income or is debt-free
		if ( bank.willWithdrawOrLendMoney(transactionAmount) && ( patron.hasIncome() || account.getLoans() == 0) ){
			Do("Granting loan for $" + transactionAmount);
			bank.msgGrantLoan(getAccountNumber(), transactionAmount);
			patron.msgGrantedLoanFor(transactionAmount);
		}
		else{
			Do("No loan for you");
			patron.msgDeniedLoan();
		}
	}
	
	void settleLoan(){
		service = Service.none;
		if (transactionAmount > account.getLoans()){
			setTransactionAmount(account.getLoans());
		}
		Do("$" + transactionAmount + " payment received");
		patron.msgLoanPaymentReceived(transactionAmount);
		bank.msgPayBackLoan(getAccountNumber(), transactionAmount);
	}
	
	void depositFunds(){
		service = Service.none;
		Do("Mo money in the bank $" + transactionAmount);
		bank.msgDepositingMoney(accountNumber, transactionAmount);
		patron.msgDepositSuccessful(transactionAmount);
	}
	
	void withdrawFunds(){
		service = Service.none;
		if (account.getBalance() < transactionAmount){
			Do("You only have this much: $" + transactionAmount + " and I'm withdrawing it all");
			setTransactionAmount(account.getBalance());
		}
		if (! bank.willWithdrawOrLendMoney(transactionAmount)){
			Do("We're not withdrawing");
			patron.msgWithdrawalDenied();
		}
		else{
			Do("Ok here's your withdrawal: $" + transactionAmount);
			bank.msgWithdrawingMoney(getAccountNumber(), transactionAmount);
			patron.msgWithdrawalSuccessful(transactionAmount);
		}
	}
	
	void getAccountSummary(){
		Do("Here's your summary. account#: " + account.getAccountNumber() + ", balance: $" + account.getBalance() + ", debt: $" + account.getLoans() );
		service = Service.none;
		patron.msgAccountSummary(account.getAccountNumber(), account.getBalance(), account.getLoans());
	}
	
	void getReadyForNewCustomer(){
		Do("Done with " + patron.getName() + " and available to help next person in line");
		state = State.none;
		service = Service.none;
		patron = null;
		setTransactionAmount(0);
		setAccountNumber(0);
		setAccount(null);
	}
	
	void leaveWork(){
		Do("Off work now");
		state = State.none;
		service = Service.none;
		bank.msgLeavingWork(this);
		//gui.DoLeaveBuilding(); // Role should have a gui
		person.msgLeavingLocation(this); // inactivate BankTellerRole
	}
		
	//**********Accessors************//
	@Override public BankPatron getPatron(){ return patron; }		
	@Override public boolean isAvailable(){ return (patron == null && working); }	
	@Override public boolean hasReceivedPaycheck() { return receivedPaycheck; }
	public String getName(){ return person.getName(); }
	// for unit testing...
	public Bank getBank() {	return bank;}
	public void setBank(Bank bank) {this.bank = bank; }
	public int getAccountNumber() { return accountNumber;	}
	public void setAccountNumber(int accountNumber) {this.accountNumber = accountNumber;}
	public Account getAccount() {return account;}
	public void setAccount(Account account) {this.account = account;}
	public float getTransactionAmount() {return transactionAmount;}
	public void setTransactionAmount(float transactionAmount) {	this.transactionAmount = transactionAmount;}
	public boolean isWorking() {return working;}
	public void setWorking(boolean working) {this.working = working;}
	public State getState() {return state;}
	public void setState(State state) {	this.state = state;}
	public Service getService() {return service;}
	public void setService(Service service) {this.service = service;}

}
