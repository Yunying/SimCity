package transportation;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import bank.interfaces.BankPatron;
import restaurant.ji.interfaces.JiCustomer;
import transportation.Interfaces.Bus;
import transportation.Interfaces.Mediator;
import transportation.Interfaces.Passenger;
import transportation.Interfaces.Stop;
import agent.Agent;


public class TransportationRole  extends Role implements Passenger{

	String name;
	List<Stop> busStops = new ArrayList<Stop>();
	public boolean asked;
	public boolean car;
	Timer timer = new Timer();
	public Bus bus;
	Building start;
	Building destination;
	public Stop startStop;
	public Stop endStop;
	Stop currentStop;
	Stop nextStop;
	public enum PassengerState{medium, readyToWalk,goToBusStop, goingToStop, atStartStop, walking, arriveAtBuilding, seeABus, checkAvalability, getAboard, onBus, hesitating, arriveAtStop, stay, moving};
	public PassengerState state;
	//PassengerGui gui;
	Semaphore animation = new Semaphore(0,true);
	List<Action> actions = new ArrayList<Action>();
	Role nextRole;
	
	public TransportationRole(String s, Stop start, Stop end){
		startStop = start;
		endStop = end;
		name = s;
		asked = false;
	}
	
	public void setPerson(Person p){
		super.setPerson(p);
		this.name = p.getName();
	}
	
	public TransportationRole(List<Action> actions){
		super();
		asked = false;
		this.actions = actions;
	}
	
	public TransportationRole(String name){
		super();
		this.name = name;
	}
	
	public void addBusStop(Stop bs){
		busStops.add(bs);
	}
	
	public void setStartStop(Stop bs){
		startStop = bs;
	}
	
	public void setEndStop(Stop bs){
		endStop = bs;
	}
	
//	public void setGui(PassengerGui gui){
//		this.gui = gui;
//	}
	
	public void setActive(){
		System.out.println("setActive");

		//gui.DoGoToBusStop(startStop);
		
		
		startStop.msgAtLocation(this);

	}
	
	public void msgAnimation(){
		animation.release();
		print("release");
	}
	
	public void msgTransportationStopped(){
//		print("msg transportation stopped");
		log.add(new LoggedEvent("Transportation stopped"));
		state = PassengerState.readyToWalk;
	}
	
	public void msgGoToBuilding(Building start, Building end, boolean c){
//		print("msg go to building");
		asked = false;
		this.car = c;
		log.add(new LoggedEvent("Go To Building"));
		this.start = start;
		this.destination = end;
		if (car){
			//GUI work
			print("Going to the building by car");
			if (destination.getClass().toString().contains("Ji") || destination.getClass().toString().contains("Bank")){
				if (nextRole instanceof Employee){
					((Employee)nextRole).msgAtBuilding(destination);
				}
				else if (nextRole instanceof JiCustomer)
					((JiCustomer)nextRole).msgAtBuilding(destination);
				else if (nextRole instanceof BankPatron)
					((BankPatron)nextRole).msgAtBuilding(destination);
			}
			else
				destination.msgAtLocation(this.getPerson(), nextRole, actions);
			this.isActive = false;
		}
		else{
			for (Stop bs:busStops){
				for (Building b:bs.getBuildings()){
					if (b.equals(start)){
						startStop = bs;
					}
					if (b.equals(end)){
						endStop = bs;
					}
				}
			}
			if (endStop == null){
//				print("end stop is null");
			}
			if (startStop.equals(endStop)){
				//GUI work
				print("Walking to the building");
				if (destination.getClass().toString().contains("Ji") || destination.getClass().toString().contains("Bank")){
					if (nextRole instanceof Employee){
						((Employee)nextRole).msgAtBuilding(destination);
					}
					else if (nextRole instanceof JiCustomer)
						((JiCustomer)nextRole).msgAtBuilding(destination);
					else if (nextRole instanceof BankPatron)
						((BankPatron)nextRole).msgAtBuilding(destination);
				}
				else
					destination.msgAtLocation(this.getPerson(), nextRole, actions);
				this.isActive = false;
			}
			else{
				state = PassengerState.goToBusStop;
			}
		}
		/* if start Stop == end Stop, then walk there */
	}
	
	public void msgPleaseComeAboard(){
		print("msg please come aboard");
		log.add(new LoggedEvent("allowed to go aboard"));
		state = PassengerState.getAboard;
		stateChanged();
	}
	
	public void msgHereIsBus(Bus bus2){
		print("msg here is bus");
		log.add(new LoggedEvent("here is a bus"));
		this.bus = bus2;
		state = PassengerState.seeABus;
		stateChanged();
	}
	
	public void msgYouHaveToWait(){
		print("msg you have to wait");
		this.bus = null;
		state = PassengerState.hesitating;
		stateChanged();
	}
	
	public void msgAtStop(Stop bs, Stop bs2){
		print("msg at Stop");
		log.add(new LoggedEvent("arrive at stop"));
		if (bs.equals(endStop)){
			state = PassengerState.arriveAtStop;
			stateChanged();
		}
		else{
			state = PassengerState.stay;
			nextStop = bs2;
			stateChanged();
		}
	}
	
	public void msgWeAreGoing(Stop bs){
		log.add(new LoggedEvent("going to next stop"));
		state = PassengerState.moving;
		nextStop = bs;
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (state == PassengerState.readyToWalk){
			walkToBuilding();
			return true;
		}
		
		if (state == PassengerState.goToBusStop){
			goToBusStop();
			return true;
		}
		
		if (state == PassengerState.seeABus){
			tryGetOnToBus();
			return true;
		}
		
		if (state == PassengerState.getAboard){
			getAboard();
			return true;
		}
		
		if (state == PassengerState.hesitating){
			makeAChoice();
			return true;
		}
		
		if (state == PassengerState.arriveAtStop){
			print("scheduler: arrive at Stop");
			leaveBus();
			return true;
		}
		
		if (state == PassengerState.stay){
			print("scheduler: stay on bus");
			stayOnBus();
			return true;
		}
		
		if (state == PassengerState.moving){
			movingWithBus();
			return true;
		}
		
		return false;
	}
	
	private void walkToBuilding(){
		print("Walking to " + destination.getName());
		
		state = PassengerState.walking;
		
		if (startStop != null){
			startStop.msgLeavingStop(this);
		}
		//gui.DoGoToBuilding(destination);
//		try {
//			print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		state = PassengerState.arriveAtBuilding;
		if (destination.getClass().toString().contains("Ji") || destination.getClass().toString().contains("Bank")){
			if (nextRole instanceof Employee){
				((Employee)nextRole).msgAtBuilding(destination);
			}
			else if (nextRole instanceof JiCustomer)
				((JiCustomer)nextRole).msgAtBuilding(destination);
			else if (nextRole instanceof BankPatron)
				((BankPatron)nextRole).msgAtBuilding(destination);
		}
		else
			destination.msgAtLocation(this.getPerson(), nextRole, actions);
		this.isActive = false;
	}
	
	private void goToBusStop(){
		print("Going to the bus stop");
		state = PassengerState.medium;
		//gui.DoGoToBusStop(startStop);
//		try {
//			print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		startStop.msgAtLocation(this);
		state = PassengerState.atStartStop;
	}
	
	private void tryGetOnToBus(){
		print("Can I get on the bus?");
		state = PassengerState.checkAvalability;
		bus.msgCanIGetOnToBus(this);
	}
	
	private void getAboard(){
		print("Getting on the bus whoo");
		state = PassengerState.medium;
		//gui.DoAboard();
//		try {
//			print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		bus.msgImAboard(this);
		state = PassengerState.onBus;
		startStop.msgLeavingStop(this);
		startStop = null;
	}
	
	private void makeAChoice(){
		print("Making a choice...");
		int tmp = endStop.getNumber() - startStop.getNumber();
		if (tmp == 1 || tmp == -1 || tmp == 3 || tmp == -3){
			state = PassengerState.readyToWalk;
			startStop.msgLeavingStop(this);
			stateChanged();
		}
		else{
			state = PassengerState.atStartStop;
		}
	}
	
	private void leaveBus(){
		state = PassengerState.medium;
		//gui.DoLeaveBus(endStop);
//		try {
//			print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		bus.msgLeavingBus(this);
		state = PassengerState.readyToWalk;
		stateChanged();
	}
	
	private void stayOnBus(){
		state = PassengerState.medium;
		bus.msgImStaying();
		state = PassengerState.onBus;
	}
	
	private void movingWithBus(){
		state = PassengerState.medium;
		//gui.DoMoveWithBus(nextStop);
//		try {
//			print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		state = PassengerState.onBus;
	}

//	public PassengerGui getGui() {
//		// TODO Auto-generated method stub
//		return gui;
//	}
	
	public void print(String s){
		System.out.println("Passenger "+name + ": " + s);
	}
	
	public String getPassengerName(){
		return name;
	}


	@Override
	public boolean isAsked() {
		// TODO Auto-generated method stub
		return asked;
	}
	
	public void setAsked(boolean a){
		asked = a;
	}

	@Override
	public Building getStartBuilding() {
		// TODO Auto-generated method stub
		return start;
	}

	@Override
	public Building getEndBuilding() {
		// TODO Auto-generated method stub
		return destination;
	}

	@Override
	public Stop getStartStop() {
		// TODO Auto-generated method stub
		return startStop;
	}

	@Override
	public Stop getEndStop() {
		// TODO Auto-generated method stub
		return endStop;
	}
	
	public void setNextRole(Role r){
		nextRole = r;
	}
	
	

}
