package transportation;
import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;

import java.util.*;

import restaurant.yunying.Order;
import transportation.Interfaces.*;

import java.util.concurrent.Semaphore;

import transportation.Interfaces.Bus;
import transportation.Interfaces.Mediator;
import transportation.Interfaces.Passenger;
import transportation.Interfaces.Stop;
import agent.Agent;
//import agent.BusGui;
//import agent.BusGui.BusGuiState;



public class BusAgent extends Agent implements Bus{

	String name;
	//BusGui gui;
	List<Passenger> passengers = Collections.synchronizedList(new ArrayList<Passenger>());
	public List<Stop> stops = new ArrayList<Stop>();
	Stop current;
	Stop next;
	public boolean readyToPark;
	boolean active;
	public boolean readyToGo;
	public boolean numberTold;
	int capacity;
	int currentCarry;
	int boardingPeople;
	Semaphore animation = new Semaphore(0,true);
	public enum BusState{atParkingLot, readyToRest, readyToOperate, goingToParking,
				  atStop, readyToGo, moving, informing, unloading};
	public BusState state;
	int stopCount;
	Mediator mediator;
	int time;
	public EventLog log = new EventLog();
	Semaphore passenger = new Semaphore(1, true);
	
	public BusAgent(Mediator m, String string){
		name = string;
		capacity = 10;
		currentCarry = 0;
		stopCount = 0;
		boardingPeople = 0;
		readyToGo = false;
		mediator = m;
		numberTold = false;
	}
	
	public BusAgent(TransportationMediator tm){
		//name = s;
		capacity = 30;
		currentCarry = 0;
		stopCount = 0;
		boardingPeople = 0;
		readyToGo = false;
		mediator = tm;
	}
	
	public void msgAnimation() {
		// TODO Auto-generated method stub
		//print("release");
		animation.release();
	}
	
	public void addBusStop(Stop bs){
		stops.add(bs);
	}
	
	public void msgUpdateTime(int time) {
		//print("msg update time "+time);
		this.time = time;
		if (time == 44){
			log.add(new LoggedEvent("ready to park"));
			print("ready to park");
			readyToPark = true;
			stateChanged();
		}		
		else if (time == 10){
			log.add(new LoggedEvent("ready to operate"));
			state = BusState.readyToOperate;
			stateChanged();
		}	
	}
	
	public void msgCanIGetOnToBus(Passenger tr){
		//print("msg can I get on bus");
		if (currentCarry < capacity){
			tr.msgPleaseComeAboard();
			//currentCarry++;
		}
		else {
			tr.msgYouHaveToWait();
			boardingPeople--;
		}
	}
	
	public void msgImAboard(Passenger tr){
		//print("msg I'm aboard");
		passengers.add(tr);
		boardingPeople--;
		//print("boarding People: "+ boardingPeople + " busState: "+ state.name());
		stateChanged();
		
	}
	
	public void msgYouCanGo(){
		//print("msg you can go from bus Stop");
		readyToGo = true;
		stateChanged();
	}
	
	public void msgLeavingBus(Passenger tr){
		//print("msg leaving bus");
		synchronized(passengers) {passengers.remove(tr);}
		stopCount++;
		stateChanged();
	}
	
	public void msgImStaying(){
		//print("msg staying on bus");
		log.add(new LoggedEvent("passenger stays on the bus"));
		stopCount++;
		stateChanged();
	}
	
	public void msgTotalWaitingPeople(int num){
		boardingPeople = num;
		print("Boarding People updated " + num);
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (state == BusState.readyToOperate){
			startOperating();
			return true;
		}
		
		if (state == BusState.readyToRest){
			goToParkingLot();
			return true;
		}
		
		
		if (state == BusState.atStop){
			informPassengers();
			return true;
		}
		
		if (state == BusState.unloading && stopCount == currentCarry && readyToGo && boardingPeople ==0){
			moveToNextStop();
			return true;
		}
		
		
		return false;
	}
	
	private void startOperating(){
		//print("startOperating");
		readyToPark = false;
		active = true;
		mediator.msgIBeginToWork(this);
		//gui.DoGoToBusStop(stops.get(0));
//		try {
//			print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		current = stops.get(0);
		next = stops.get(1);
		current.msgAtLocation(this);
		state = BusState.atStop;
	}
	
	private void goToParkingLot(){
		//print("goToParkingLot");
		log.add(new LoggedEvent("go to parking lot"));
		print("Go to parking lot");
		state = BusState.goingToParking;
		current = null;
		next = null;
		//gui.DoGoToParkingLot();
//		try {
//			print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		active = false;
		state = BusState.atParkingLot;
	}
	
	public void moveToNextStop(){
		//print("moveToNextStop");
		log.add(new LoggedEvent("move to next stop"));
		numberTold = false;
		state = BusState.moving;
		stopCount = 0;
		currentCarry = passengers.size();
		print("CurrentCarry: "+currentCarry);
		readyToGo = false;
		current.msgLeavingStop();
		class BusTimerTask extends TimerTask{
			public void run() {
				// TODO Auto-generated method stub
				//gui.DoGoToBusStop(next);
				msgAnimation();
				for (Passenger p: passengers){
					p.msgWeAreGoing(next);
				}
			}
    	}
    	
    	Timer timer = new Timer();
    	timer.schedule(new BusTimerTask(),1500);
		
		try {
			//print("acquire");
			animation.acquire();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		current = next;
		try {
			if (readyToPark==false || stops.indexOf(current) != 0){
				next = stops.get(stops.indexOf(current)+1);	
			}
			else{
				state = BusState.readyToRest;
				stateChanged();
				return;
			}
		} catch(java.lang.IndexOutOfBoundsException e){
			next = stops.get(0);
		}
		current.msgAtLocation(this);
		state = BusState.atStop;
		stateChanged();
	}
	
	private void informPassengers(){
		//print("informPassengers");
		log.add(new LoggedEvent("inform passengers"));
		state = BusState.informing;
		
		synchronized(passengers){
			Iterator<Passenger> i = passengers.iterator();
			while (i.hasNext()){
				i.next().msgAtStop(current,next);
				stateChanged();
			}
		}
		
		state = BusState.unloading;
	}

//	public void setGui(BusGui busGui) {
//		// TODO Auto-generated method stub
//		gui = busGui;
//	}
	
	public void print(String s){
		System.out.println("BusAgent: "+s);
	}

	@Override
	public String getBusName() {
		// TODO Auto-generated method stub
		return name;
	}


	@Override
	public Stop getCurrentStop() {
		// TODO Auto-generated method stub
		return current;
	}

	@Override
	public boolean getNumberTold() {
		// TODO Auto-generated method stub
		return numberTold;
	}

	@Override
	public void setNumberTold(boolean b) {
		// TODO Auto-generated method stub
		numberTold = b;
	}
	
	public int getCurrentCarry(){
		return currentCarry;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public Stop getNextStop(){
		return next;
	}
	
	/*********For Test Use************/
	public void setCurrentStop(Stop s){
		current = s;
	}
	
	public void setNextStop(Stop s){
		next = s;
	}

	public int getBoardingPeople() {
		// TODO Auto-generated method stub
		return boardingPeople;
	}
	
	public List<Passenger> getPassengers(){
		return passengers;
	}
	
	public int getStopCount(){
		return stopCount;
	}

}
