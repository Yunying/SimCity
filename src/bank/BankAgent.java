package bank;

import java.util.*;

import restaurant.ji.JiRestaurantAgent.RestaurantState;
import restaurant.ji.interfaces.JiCustomer;
import global.BusinessAgent;
import bank.interfaces.*;
import global.roles.Role;
import global.actions.Action;
import global.test.mock.LoggedEvent;
import gui.animation.base.GUI;
import gui.animation.building.BankGUI;
import interfaces.Employee;
import interfaces.Person;


public class BankAgent extends BusinessAgent implements Bank {

	//**********Data************//
	private Map<Integer, Account> bankAccounts; // map of account numbers and Accounts
	private Map<BankTeller, Integer> accountSearchRequests; // map of tellers and the accounts they need information for
	private List<BankTeller> accountsToCreate;
	
	private List<BankTeller> tellerList;
	private List<BankPatron> arrivingCustomers;
	
	private BankSecurity guard;
	private BankPatron robber;
	private int numBankAccounts;
	private float totalBankBalance;
	
	public enum BankState {
		closed, open, openNoEmployees, beingRobbed, robbed, payingEmployees, PayingAndClosing, 
		};
	BankState state;
	
	
	private BankGUI gui;
	private String name;

	public BankAgent(String name) {
		super();
		this.name = name;
		state = BankState.closed;
		tellerList = new ArrayList<BankTeller>();
		arrivingCustomers = new ArrayList<BankPatron>();
		bankAccounts = new HashMap<Integer, Account>();
		accountSearchRequests = new HashMap<BankTeller, Integer>();
		accountsToCreate = new ArrayList<BankTeller>();
		
		
		wages.put("BankTeller", 25f);
		wages.put("BankSecurity", 20f);
		
		numBankAccounts = 0;
		totalBankBalance = 5000f;
		
		startTime = 2;
		closeTime = 40;
	}
	
	
	//**********Messages************//
	
	@Override
	public void msgUpdateTime(int time, int day) {
		this.currentTime = time; this.currentDay = day;
		if (day == 5){
			state = BankState.closed; // closed on Sundays
		}
		if (currentTime == startTime){
			if (!tellerList.isEmpty()){
				state = BankState.open;
				print("Cathy's Bank is opening!");
			}
			else
				state = BankState.openNoEmployees;
		}
		else if (!tellerList.isEmpty() && state == BankState.openNoEmployees){
			state = BankState.open;
			Do("Cathy's Bank is opening (employees came late)");
		}
		else if (currentTime == closeTime){
			if (state == BankState.openNoEmployees){
				state = BankState.closed;
				Do("Cathy's Bank is closed... couldn't open today");
			}
			else if (state == BankState.open){
				state = BankState.PayingAndClosing;
				Do("Cathy's Bank is closing. Customers finishing up: " + customers.size());
			}
		}
		stateChanged();
		
	}
	
	// sent by the role
	@Override
	public void msgAtLocation(Person p, Role role, List<Action> actions){ // change later to remove actions
		log.add(new LoggedEvent("Received msgAtLocation from " + role.getPerson().getName()));
		if (role instanceof BankPatron){
//			Do("Hi customer" + role.getPerson().getName() + " we are " + state.toString());
			customers.add(role);
			arrivingCustomers.add((BankPatron) role);
			stateChanged();
			return;
		}
		else if (role instanceof Employee){
			employees.add(role);
			if (role instanceof BankTeller){
//				Do("welcome teller " + role.getPerson().getName() + " we are " + state.toString());
				tellerList.add((BankTeller) role);
				((BankTeller) role).setBank(this);
			}
			else if (role instanceof BankSecurity){
				guard = (BankSecurity) role;
			}
			stateChanged();
			return;
		}
	}
		
	
	// sent from both TellerAgents and SecurityGuardAgents when they leave work, which will be determined by either a global timer or the gui
	public void msgLeavingWork(Role employee){
		log.add(new LoggedEvent("Received msgLeavingWork from " + employee.getPerson().getName()));
		employees.remove(employee.getPerson());
		if (employee instanceof BankTeller)
			tellerList.remove(employee);
		else if (employee instanceof BankSecurity)
			guard = null;
		stateChanged();
	}
	
	// sent by robbers
	public void msgThisIsAHoldUp(BankPatron bankRobber){ // sent from a BankPatron
		state = BankState.beingRobbed;
		log.add(new LoggedEvent("Received msgThisIsAHoldUp"));
		robber = bankRobber;
		stateChanged();
	}
	
	@Override
	public void msgokThanksBye(){
		state = BankState.robbed;
		log.add(new LoggedEvent("Received msgokThanksBye"));
		robber = null;
		stateChanged();
	}
	
	// sent by the guard
	@Override
	public void msgRobberArrested(){
		state = BankState.open;
		log.add(new LoggedEvent("Received msgRobberArrested"));
		robber = null;
		stateChanged();
	}
	
	// the rest of these messages are sent from a BankTeller
	
	@Override
	public void msgCreateAccount(BankTeller teller){
		state = BankState.open;
		log.add(new LoggedEvent("Received msgCreateAccount"));
		accountsToCreate.add(teller);
		stateChanged();
	}
	
	@Override
	public void msgCloseAccount(int accountNumber){
		state = BankState.open;
		log.add(new LoggedEvent("Received msgCloseAccount"));
		bankAccounts.remove(accountNumber);
		stateChanged();
	}
	
	@Override
	public void msgGetAccountInformation(int accountNumber, BankTeller teller){
		state = BankState.open;
		log.add(new LoggedEvent("Received msgGetAccountInformation"));
		accountSearchRequests.put(teller, accountNumber);
		stateChanged();
	}
	
	@Override
	public void msgGrantLoan(int accountNumber, float loanAmount){
		state = BankState.open;
		log.add(new LoggedEvent("Received msgGrantLoan"));
		bankAccounts.get(accountNumber).loans += loanAmount;
		setTotalBankBalance(totalBankBalance - loanAmount);
		stateChanged();
	}
	
	@Override
	public void msgPayBackLoan(int accountNumber, float payment){
		state = BankState.open;
		log.add(new LoggedEvent("Received msgPayBackLoan"));
		bankAccounts.get(accountNumber).loans -= payment;
		setTotalBankBalance(totalBankBalance + payment);
		stateChanged();
	}
		
	@Override
	public void msgWithdrawingMoney(int accountNumber, float amount){
		state = BankState.open;
		log.add(new LoggedEvent("Received msgWithdrawingMoney"));
		bankAccounts.get(accountNumber).balance -= amount;
		setTotalBankBalance(totalBankBalance - amount);
		stateChanged();
	}
	
	@Override
	public void msgDepositingMoney(int accountNumber, float amount){
		state = BankState.open;
		log.add(new LoggedEvent("Received msgDepositingMoney"));
		bankAccounts.get(accountNumber).balance += amount;
		setTotalBankBalance(totalBankBalance + amount);
		stateChanged();
	}
	
	@Override
	public void msgLeavingAsCustomer(BankPatron patron){
		customers.remove(patron);
		stateChanged();
	}
	
	//**********Scheduler************//	
	@Override
	public boolean pickAndExecuteAnAction() {
		if (state == BankState.beingRobbed){
			respondToRobbery();
			return true;
		}
		else if (!customers.isEmpty()){
			if (state == BankState.openNoEmployees){
				informBankIsClosed( customers.get(0) );
				return true;
			}
			else if (state == BankState.open){
				if (!accountsToCreate.isEmpty()){
					openAccount(accountsToCreate.get(0), accountsToCreate.get(0).getPatron());
					return true;
				}
				else if (!accountSearchRequests.isEmpty()){
					for (BankTeller teller : accountSearchRequests.keySet()){
						int accountNum = accountSearchRequests.get(teller);
						retrieveAccount(teller, accountNum);
					}
					accountSearchRequests.clear();
					return true;
				}
				if (!arrivingCustomers.isEmpty()){
					for (BankTeller teller : getTellerList()){
						if (teller.isAvailable()){
							helpNextCustomer(teller, arrivingCustomers.get(0));
							return true;
						}
					}
				}
			}
		}
		else if (state == BankState.PayingAndClosing){
			for (Role employee : getEmployees()){
				if (!((Employee) employee).hasReceivedPaycheck()){
					payEmployees(employee);
					return true;
				}
			}
			closeBank();
		}
		return false;
	}

	//**********Actions************//
	private void informBankIsClosed(Role r) {
		BankPatronRole p = (BankPatronRole) r;
		Do("Go home " + p.getName() + " we are closed (" + state.toString() + ")");
		p.msgBankIsClosed();
		customers.remove(r);
	}
	
	void respondToRobbery(){
		if (guard != null){
			Do("arrest him yo");
			guard.msgRobberyTakingPlace(robber);
		}
		else{
			Do("Take our money :o");
			robber.msgTakeTheMoney(totalBankBalance);
			totalBankBalance = 0;
			open = false;
		}
	}

	void helpNextCustomer(BankTeller teller, BankPatron bankPatron){
		Do("Directing " + bankPatron.getName() + "  to teller " + teller.getName());
		teller.msgHelpCustomer(bankPatron);
		arrivingCustomers.remove(bankPatron);
	}	
		
	void retrieveAccount(BankTeller teller, int accountNumber){
		Do("Here's the account info, " +  teller.getName());
		//if (accountNumber == -1){
		//	openAccount(teller, teller.getPatron());
		//}
		//else{
			teller.msgHereIsAccountInfo(bankAccounts.get(accountNumber));
		//}
		//accountSearchRequests.remove(teller);
	}
	
	void openAccount(BankTeller teller, BankPatron patron){
		Do("Hey " + teller.getName() + ", I opened an account for " + patron.getName());
		setNumBankAccounts(numBankAccounts + 1);
		Account newAccount = new Account(teller.getPatron(), numBankAccounts, 0, 0);
		bankAccounts.put(numBankAccounts, newAccount);
		teller.msgAccountCreated(numBankAccounts, newAccount);
		accountsToCreate.remove(teller);
	}

	void closeBank(){
		state = BankState.closed;
		Do("Everyone go home");
		for (Role employee : getEmployees()){
			Employee e = (Employee) employee;
			e.msgStopWorkingGoHome();
			if (employee instanceof BankTeller)
				tellerList.remove(employee);
			else if (employee instanceof BankSecurity)
				setGuard(null);
		}
		employees.clear();
	}
	
	void payEmployees(Role employee){
		float pay = 0;
		if (employee instanceof BankTeller){
			pay = wages.get("BankTeller");
		}
		else if (employee instanceof BankSecurity){
			pay = wages.get("BankSecurity");
		}
		if (totalBankBalance < pay)
			payDay = false;
		else{
			Do("Paying " + employee.getPerson().getName() + " $" + pay);
			Employee e = (Employee) employee;
			e.msgHeresYourPaycheck(pay);
			totalBankBalance -= pay;
		}
	}

		
		
	//**********Accessors************//
		
	@Override
	public boolean willWithdrawOrLendMoney(float amount){ // will allow withdrawals or loans if the bank can afford to do so
		return ( (totalBankBalance > 500) && (amount < totalBankBalance) ); }

	@Override public String getName() {return name;}
	public BankGUI getGUI(){return gui;}
	public void setGUI(GUI gui){ this.gui = (BankGUI) gui; }


	// for unit testing...
	public int getNumBankAccounts() {return numBankAccounts;}
	public void setNumBankAccounts(int numBankAccounts) {this.numBankAccounts = numBankAccounts;}
	public Map<Integer, Account> getBankAccounts() {return bankAccounts;}
	public void setBankAccounts(Map<Integer, Account> bankAccounts) {this.bankAccounts = bankAccounts;}
	public List<BankTeller> getAccountsToCreate() {	return accountsToCreate;}
	public void setAccountsToCreate(List<BankTeller> accountsToCreate) {this.accountsToCreate = accountsToCreate;}
	public BankSecurity getGuard() {return guard;}
	public void setGuard(BankSecurity guard) {	this.guard = guard;}
	public List<Role> getAllCustomers() {	return customers; }
	public void setAllCustomers(List<Role> customers) { this.customers = customers; }
	public List<BankPatron> getArrivingCustomers() {	return arrivingCustomers; }
	public void setArrivingCustomers(List<BankPatron> customers) { this.arrivingCustomers = customers; }
	public List<BankTeller> getTellerList() {return tellerList;}
	public void setTellerList(List<BankTeller> tellerList) {this.tellerList = tellerList;}
	public float getTotalBankBalance() {return totalBankBalance;}
	public void setTotalBankBalance(float totalBankBalance) {this.totalBankBalance = totalBankBalance;}
	public BankPatron getRobber() {	return robber;}
	public void setRobber(BankPatron robber) {this.robber = robber;}
	public void createBankAccountForBusiness(BusinessAgent business, int accountNumber){
		bankAccounts.put(accountNumber, new Account(business, accountNumber, 0f, 0f));
	}
	
}
