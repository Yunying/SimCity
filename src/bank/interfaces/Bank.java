package bank.interfaces;

import global.BusinessAgent;
import global.roles.Role;
import interfaces.Building;

public interface Bank extends Building{
	// messages for normal bank-teller interaction
	public abstract void msgCreateAccount(BankTeller teller);
	public abstract void msgCloseAccount(int accountNumber);
	public abstract void msgGetAccountInformation(int accountNum, BankTeller teller);
	public abstract void msgGrantLoan(int accountNumber, float loanAmount);
	public abstract void msgPayBackLoan(int accountNumber, float payment);
	public abstract void msgWithdrawingMoney(int accountNumber, float amount);
	public abstract void msgDepositingMoney(int accountNum, float amount);
	
	// messages for non-normal bank-patron interactions
	public abstract void msgThisIsAHoldUp(BankPatron bankRobber);
	
	// accessors
	public abstract boolean willWithdrawOrLendMoney(float amount);
	public abstract void msgLeavingWork(Role employee);
	public abstract void msgRobberArrested();
	public abstract void msgokThanksBye();
	public abstract void createBankAccountForBusiness(BusinessAgent b, int accountNumber);
	public abstract void msgLeavingAsCustomer(BankPatron patron);

	/****inherited from Building****/
	/* 
	 * public abstract void msgAtLocation(Person p, Role r, ArrayList<Action> actions);
	 * public abstract void msgUpdateTime(int time);
	 * public abstract String getLocation();
	 * public abstract String getName();
	 * public abstract int getStartTime();
	 * public abstract void setStartTime(int t);
	 * public abstract int getCloseTime();
	 * public abstract void setCloseTime(int t);
	 * public abstract List<Role> getPeopleInTheBuilding();
	*/
}
