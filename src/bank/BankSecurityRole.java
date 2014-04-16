package bank;

import interfaces.Building;
import bank.interfaces.Bank;
import bank.interfaces.BankPatron;
import bank.interfaces.BankSecurity;
import global.roles.Role;
import global.test.mock.LoggedEvent;

public class BankSecurityRole extends Role implements BankSecurity {

	//**********Data************//
	private Bank bank;
	private BankPatron robber;
	public enum State{
		arrivedAtBank, working, none
	};
	private State state;
	private boolean working;
	private boolean receivedPaycheck;
	
	public BankSecurityRole(){
		super();
		working = false;
		robber = null;
		state = State.none;
		receivedPaycheck = false;
	}

	//**********Messages************//
	
	@Override
	public void msgAtBuilding(Building bank){ // from the gui after the PersonAgent calls DoGoToBank()
		log.add(new LoggedEvent("Received msgAtBuilding"));
		setState(State.arrivedAtBank);
		this.bank = (Bank) bank;
	}
	
	@Override
	public void msgRobberyTakingPlace(BankPatron bankRobber){ // from bank
		log.add(new LoggedEvent("Received msgRobberyTakingPlace"));
		setState(State.working);
		robber = bankRobber;
	}
	
	@Override
	public void msgStopWorkingGoHome() {
		log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
		working = false;
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		log.add(new LoggedEvent("Received msgHeresYourPaycheck"));
		person.ChangeMoney(paycheck); // money += paycheck;
		receivedPaycheck = true;
	}

	//**********Scheduler************//
		
	@Override
	public boolean pickAndExecuteAnAction(){
		if (state == State.arrivedAtBank){
			beginWork();
			return true;
		}
		else if (robber!=null){
			catchTheRobber();
			return true;
		}
		else if (state == State.working && !isWorking()){
			leaveWork();
		}
		return false;
	}

	//**********Actions************//
	
	void beginWork(){
		setState(State.working);
		working = true;
		bank.msgAtLocation(person, this, null);
	}
	
	void catchTheRobber(){
		setState(State.working);
		robber.msgYoureUnderArrest();
		bank.msgRobberArrested();
		robber = null;
	}
		
	void leaveWork(){
		state = State.none;
		bank.msgLeavingWork(this);
		//gui.DoLeaveBuilding();
		person.msgLeavingLocation(this);
	}

	//**********Accessors************//
	
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean hasReceivedPaycheck() {
		return receivedPaycheck;
	}
	
	// for unit testing...
	public Bank getBank() {	return bank;}
	public void setBank(Bank bank) {this.bank = bank;}
	public BankPatron getRobber() {	return robber;}
	public State getState() {return state;}
	public void setState(State state) {this.state = state;}
}
