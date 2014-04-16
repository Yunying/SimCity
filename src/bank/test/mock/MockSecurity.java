package bank.test.mock;

import interfaces.Building;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import bank.interfaces.Bank;
import bank.interfaces.BankPatron;
import bank.interfaces.BankSecurity;

public class MockSecurity extends MockRole implements BankSecurity{

	public Bank bank;
	boolean receivedPaycheck;
	
	public MockSecurity() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgAtBuilding(Building bank) {
		log.add(new LoggedEvent("Received msgAtBuilding"));
		// TODO Auto-generated method stub
		receivedPaycheck = false;
		this.bank = (Bank) bank;
	}

	@Override
	public void msgStopWorkingGoHome() {
		log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		log.add(new LoggedEvent("Received msgHeresYourPaycheck and got $" + paycheck));
		// TODO Auto-generated method stub
		receivedPaycheck = true;
		
	}

	@Override
	public void msgRobberyTakingPlace(BankPatron bankRobber) {
		log.add(new LoggedEvent("Received msgRobberyTakingPlace"));
		// TODO Auto-generated method stub
		bankRobber.msgYoureUnderArrest();
		bank.msgRobberArrested();
	}

	@Override
	public boolean hasReceivedPaycheck() {
		return receivedPaycheck;
	}

}
