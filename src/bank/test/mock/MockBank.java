package bank.test.mock;


import interfaces.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bank.Account;
import bank.interfaces.Bank;
import bank.interfaces.BankPatron;
import bank.interfaces.BankSecurity;
import bank.interfaces.BankTeller;
import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;

public class MockBank extends MockRole implements Bank{

	public Map<Integer, Account> bankAccounts; // map of account numbers and Accounts
	public Map<BankTeller, Integer> accountSearchRequests; // map of tellers and the accounts they need information for
	public List<BankTeller> accountsToCreate;
	
	public List<BankTeller> tellerList;
	public List<BankPatron> customersInLine;
	
	public BankSecurity guard;
	public BankPatron robber;
	public int numBankAccounts;
	public float totalBankBalance;
	public boolean handledRobbery;
	public boolean open;
	String name;
		
	public MockBank(String name) {
		super();
		this.name = name;
		// TODO Auto-generated constructor stub
		
		tellerList = new ArrayList<BankTeller>();
		customersInLine = new ArrayList<BankPatron>();
		/*
		ArrayList<Role> employees = new ArrayList<Role>();
		ArrayList<Role> customers = new ArrayList<Role>();
		bankAccounts = new HashMap<Integer, Account>();
		accountSearchRequests = new HashMap<BankTeller, Integer>();
		accountsToCreate = new ArrayList<BankTeller>();
		
		
		HashMap<String, float> wages = new HashMap<String, float>();
		wages.put("BankTeller", 25f);
		wages.put("BankSecurity", 20f);
		
		numBankAccounts = 0;
		totalBankBalance = 5000f;
		handledRobbery = false;
		*/
	}

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		log.add(new LoggedEvent("Received msgAtLocation from " + p.getName()));
		// TODO Auto-generated method stub
	}

	@Override
	public void msgCreateAccount(BankTeller teller) {
		log.add(new LoggedEvent("Received msgCreateAccount"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgCloseAccount(int accountNumber) {
		log.add(new LoggedEvent("Received msgCloseAccount"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGetAccountInformation(int accountNum, BankTeller teller) {
		log.add(new LoggedEvent("Received msgGetAccountInformation"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGrantLoan(int accountNumber, float loanAmount) {
		log.add(new LoggedEvent("Received msgGrantLoan"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPayBackLoan(int accountNumber, float payment) {
		log.add(new LoggedEvent("Received msgPayBackLoan"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWithdrawingMoney(int accountNumber, float amount) {
		log.add(new LoggedEvent("Received msgWithdrawingMoney"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDepositingMoney(int accountNum, float amount) {
		log.add(new LoggedEvent("Received msgDepositingMoney"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgThisIsAHoldUp(BankPatron bankRobber) {
		log.add(new LoggedEvent("Received msgThisIsAHoldUp"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeavingWork(Role r) {
		log.add(new LoggedEvent("Received msgLeavingWork"));
		// TODO Auto-generated method stub
		if (r instanceof BankTeller){
			tellerList.remove((BankTeller) r);
		}
		else if (r instanceof BankSecurity){
			guard = null;
		}
		
	}

	@Override
	public void msgRobberArrested() {
		log.add(new LoggedEvent("Received msgRobberArrested"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgokThanksBye() {
		log.add(new LoggedEvent("Received msgokThanksBye"));
		// TODO Auto-generated method stub
		
	}
	

	/** Accessors **/
	
	public void msgUpdateTime(int time) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getName() {
		return name;
	}
	

	@Override
	public boolean willWithdrawOrLendMoney(float amount) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStartTime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCloseTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCloseTime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createBankAccountForBusiness(BusinessAgent b, int accountNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeavingAsCustomer(BankPatron patron) {
		// TODO Auto-generated method stub
		
	}

}
