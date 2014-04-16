package bank;

import interfaces.Building;
import interfaces.Person;
import bank.interfaces.*;
import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;

public class BankPatronRole extends Role implements BankPatron {

	//**********Data************//
	private Bank bank;  // bank = buildings.get(bank). person agent should have this
	private BankTeller myTeller;
	public enum State {
		none, goingToBank, arrivedAtBank, givingAccountInfo, beingHelped, leaving, fleeing
	};
	private State state;
	
	// already in Role
	//private List<Action> actions;
	
	// float money; person agent has this
	private float myBalance;
	private float myDebt;
	private int accountNumber;
	
	private Building company;
	private int companyAccount;
	private float companyBalance;
	private float companyDebt;
	
	private Action currentService;
	
	// bank patrons here on behalf of a company
	public BankPatronRole(Building company){
		super();
		this.company = company;
		this.companyAccount = ((BusinessAgent)company).getBankAccount();
		bank = null;
		myBalance = 0f;
		myDebt = 0f;
		companyBalance = 0f;
		companyDebt = 0f;
		state = State.none;
	}
	
	// normal bank patrons
	public BankPatronRole(){
		super();
		bank = null;
		myBalance = 0f;
		myDebt = 0f;
		this.company = null;
		this.companyAccount = 0;
		companyBalance = 0f;
		companyDebt = 0f;
		state = State.none;
	}

	//**********Messages************//

	@Override
	public void msgAtBuilding(Building bank){
		log.add(new LoggedEvent("Received msgAtBuilding"));
		state = State.arrivedAtBank;
		this.bank = (Bank) bank;
		stateChanged();
	}

	@Override
	public void msgBankIsClosed(){
		log.add(new LoggedEvent("Received msgBankIsClosed"));
		state = State.leaving;
		stateChanged();
	}
	
	@Override
	public void msgWhatIsYourAccountNumber(BankTeller teller){
		log.add(new LoggedEvent("Received msgWhatIsYourAccountNumber"));
		state = State.givingAccountInfo;
		myTeller = teller;
		stateChanged();
	}
	
	@Override
	public void msgHowMayIHelpYou(){
		log.add(new LoggedEvent("Received msgHowMayIHelpYou"));
		state = State.beingHelped;
		stateChanged();
	}
		
	@Override
	public void msgAccountClosed(float payout){
		log.add(new LoggedEvent("Received msgAccountClosed"));
		state = State.beingHelped;
		person.ChangeMoney(payout);
		setAccountNumber(0);
		myBalance = 0;
		myDebt = 0;
		actions.remove(currentService);
		stateChanged();
	}
	
	@Override
	public void msgWontCloseAccountYouStillOweUs(float debt){
		log.add(new LoggedEvent("Received msgWontCloseAccountYouStillOweUs"));
		state = State.beingHelped;
		actions.remove(currentService);
		stateChanged();
	}
	
	@Override
	public void msgGrantedLoanFor(float loanAmount){
		log.add(new LoggedEvent("Received msgGrantedLoanFor"));
		state = State.beingHelped;
		person.ChangeMoney(loanAmount);
		actions.remove(currentService);
		stateChanged();
	}
	
	@Override
	public void msgDeniedLoan(){
		log.add(new LoggedEvent("Received msgDeniedLoan"));
		state = State.beingHelped;
		actions.remove(currentService);
		stateChanged();
	}
		
	@Override
	public void msgLoanPaymentReceived(float paymentAccepted){
		log.add(new LoggedEvent("Received msgLoanPaymentReceived"));
		state = State.beingHelped;
		person.ChangeMoney(-paymentAccepted);
		actions.remove(currentService);
		stateChanged();
	}
	
	@Override
	public void msgDepositSuccessful(float transactionAmount){
		log.add(new LoggedEvent("Received msgDepositSuccessful"));
		state = State.beingHelped;
		person.ChangeMoney(-transactionAmount);
		actions.remove(currentService);
		stateChanged();
	}
		
	@Override
	public void msgWithdrawalSuccessful(float transactionAmount){
		log.add(new LoggedEvent("Received msgWithdrawalSuccessful"));
		state = State.beingHelped;
		person.ChangeMoney(transactionAmount);
		actions.remove(currentService);
		stateChanged();
	}
		
	@Override
	public void msgWithdrawalDenied(){
		log.add(new LoggedEvent("Received msgWithdrawalDenied"));
		state = State.beingHelped;
		actions.remove(currentService);
		stateChanged();
	}
		
	@Override
	public void msgAccountSummary(int accountNum, float balance, float loans){
		log.add(new LoggedEvent("Received msgAccountSummary"));
		state = State.beingHelped;
		if (accountNum == companyAccount && company != null){
			companyBalance = balance;
			companyDebt = loans;
		}
		else{
			setAccountNumber(accountNum);
			setMyBalance(balance);
			setMyDebt(loans);
		}
		if (currentService.getTask() == "getsummary"){
			actions.remove(currentService);
		}
		stateChanged();
	}
		
	@Override
	public void msgTakeTheMoney(float bankBalance){ // from the bank, for a successful robber
		log.add(new LoggedEvent("Received msgTakeTheMoney"));
		state = State.fleeing;
		person.ChangeMoney(bankBalance);
		actions.remove(currentService);
		stateChanged();
	}
	
	@Override
	public void msgYoureUnderArrest(){ // from security, for a failed robber
		log.add(new LoggedEvent("Received msgYoureUnderArrest"));
		state = State.leaving;
		actions.remove(currentService);
		stateChanged();
	}


	//**********Scheduler************//

	@Override
	public boolean pickAndExecuteAnAction(){
		if (state == State.fleeing){
			runAway();
			return true;
		}
		else if (!actions.isEmpty()){
			setCurrentService(actions.get(0));
			if (state == State.arrivedAtBank){
				if (currentService.getTask() == "robBank"){
					robBank();
				}
				else {
					waitInLine();
				}
				return true;
			}
			else if (state == State.givingAccountInfo){
				giveTellerAccount();
				return true;
			}
			else if (state == State.beingHelped){
				String[] action = currentService.getTask().split("\\$");
				String service = action[0];
				float amount = 0;
				if (action.length > 1){
					amount = (Float.valueOf(action[1]));
				}
				
				if (service.equals("deposit")){
					depositMoney(amount);					
				}
				else if (service.equals("withdraw")){
					withdrawMoney(amount);
				}
				else if (service.equals("borrow")){
					askForLoan(amount);
				}
				else if (service.equals("pay")){
					makeLoanPayment(amount);
				}
				else if (service.equals("getsummary")){
					getAccountSummary();
				}
				else if (service.equals("closeaccount")){
					closeAccount();
				}
				return true;
			}
			return false;
		}
		else {
			leaveBank();
		}
		return false;
	}

	//**********Actions************//
	
	
	void robBank(){
		Do("robbing you");
		state = State.none;
		bank.msgThisIsAHoldUp(this);
	}
		
	void waitInLine(){
		Do("Waiting in line yay");
		state = State.none;
		bank.msgAtLocation(person, this, null); // person, role, List of actions
	}
	
	void giveTellerAccount(){
		state = State.none;
		if (company != null){
			Do("Here on behalf of " + company.getName() + ", account number is " + companyAccount);
			myTeller.msgMyAccountNumberIs(companyAccount);
		}
		else if (accountNumber != 0){
			Do("my account is " + accountNumber);
			myTeller.msgMyAccountNumberIs(accountNumber);
		}
		else {
			Do("Need to open account");
			myTeller.msgNeedToOpenAccount();
		}
	}
	
	void getAccountSummary(){
		Do("Give me account summary");
		state = State.none;
		myTeller.msgGetAccountSummary();
	}
	
	void askForLoan(Float amountOfLoan){
		Do("Give me a loan for $" + amountOfLoan);
		state = State.none;
		myTeller.msgAskingForLoan(amountOfLoan);
	}
		
	void makeLoanPayment(Float payment){
		Do("Paying back $" + payment);
		state = State.none;
		myTeller.msgRepayingLoan(payment);
	}
	
	void depositMoney(Float amountToDeposit){
		Do("Depositing $" + amountToDeposit);
		state = State.none;
		myTeller.msgMakingDeposit(amountToDeposit);
	}
	
	void withdrawMoney(Float amountToWithdraw){
		Do("Withdrawing $" + amountToWithdraw);
		state = State.none;
		myTeller.msgMakingWithdrawal(amountToWithdraw);
	}
	
	void closeAccount(){
		Do("Closing account");
		state = State.none;
		myTeller.msgClosingAccount();
	}
	
	void leaveBank(){
		Do("bye now I have $" + person.getMoney());
		state = State.none;
		if (getMyTeller() != null){ // exists for normal bank patrons but is null for robbers
			myTeller.msgNoMoreServicesNeeded();
			myTeller = null;
		}
		if (company != null){
			company = null;
			companyAccount = 0;
		}
		bank.msgLeavingAsCustomer(this);
		//gui.DoLeaveBuilding();
		person.msgLeavingLocation(this); // inactivate BankPatronRole
	}

	private void runAway() {
		Do("just robbed u o yea now I have $" + person.getMoney());
		state = State.none;
		bank.msgokThanksBye();
	}
		
	//**********Accessors************//
		
	@Override
	public void setPerson(Person person){
		this.person = person;
		this.bank = (Bank) person.getBuilding("bank");
	}
	public String getName(){ return person.getName(); }
	@Override public boolean hasIncome(){ return !(person.getJobs().isEmpty()); }
	// for unit testing...
	public Bank getBank() {	return bank;}
	public void setBank(Bank bank) {this.bank = bank;}
	public BankTeller getMyTeller() {return myTeller;}
	public void setMyTeller(BankTeller myTeller) {this.myTeller = myTeller;}
	
	public Building getCompany() {	return company;}
	public int getCompanyAccountNum(){ return companyAccount;}
	public void setCompany(Building company, int acc) {this.company = company; this.companyAccount = acc;}
	public float getCompanyBalance() {return companyBalance;}
	public void setCompanyBalance(float companyBalance) { this.companyBalance = companyBalance;}
	public float getCompanyDebt() {return companyDebt;}
	public void setCompanyDebt(float companyDebt) {this.companyDebt = companyDebt;}
	
	public int getAccountNumber() {	return accountNumber;}
	public void setAccountNumber(int accountNumber) {this.accountNumber = accountNumber;}
	public float getMyBalance() {return myBalance;}
	public void setMyBalance(float myBalance) {	this.myBalance = myBalance;}
	public float getMyDebt() {return myDebt;}
	public void setMyDebt(float myDebt) {this.myDebt = myDebt;}
	
	public Action getCurrentService() {return currentService;}
	public void setCurrentService(Action currentService) {this.currentService = currentService;}
	public State getState() {return state;}
	public void setState(State state) {this.state = state;}

}
