package bank.interfaces;

import interfaces.Building;
import interfaces.Employee;

public interface BankSecurity extends Employee{

	// message from the gui
	public abstract void msgAtBuilding(Building bank);
	
	// message for normal security-bank interaction
	//public abstract void msgStopWorkingGoHome();
	//public abstract void msgHeresYourPaycheck(float paycheck);
	
	// message for non-normal bank-security interactions
	public abstract void msgRobberyTakingPlace(BankPatron bankRobber);
	
}
