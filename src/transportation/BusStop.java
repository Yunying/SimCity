package transportation;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import gui.animation.base.GUI;
import interfaces.Building;
import interfaces.Person;

import java.util.*;

import restaurant.ji.roles.JiCashierRole.Check;
import transportation.Interfaces.Bus;
import transportation.Interfaces.Mediator;
import transportation.Interfaces.Passenger;
import transportation.Interfaces.Stop;
import transportation.tests.mock.MockMediator;
import agent.Agent;



public class BusStop extends Agent implements Building, Stop{

	String name;
	boolean active = true;
	//buildings that are nearest to the busStop(2 for each)
	List<Building> buildings;
	public List<Passenger> waitingPassengers = Collections.synchronizedList(new ArrayList<Passenger>());
	int number;
	public int xPos;
	public int yPos;
	Bus bus;
	String location;
	Mediator mediator;
	int startTime;
	int closeTime;
	
	public BusStop(Mediator m, String s){
		super();
		this.name = s;
		buildings = new ArrayList<Building>();
		mediator = m;
		startTime = 8;
		closeTime = 44;
	}
	
	public BusStop(String s, int n, int x, int y){
		//TODO:initialize location xPos yPos
		name = s;
		number = n;
		xPos = x;
		yPos = y;
	}
	
	public void setMediator(Mediator m){
		mediator = m;
	}
	
	public void addBuilding(Building b){
		buildings.add(b);
	}
	
	public Bus getBus(){
		return bus;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String s){
		this.name = s;
	}

	@Override
	public void msgUpdateTime(int time) {
		if (time < startTime || time > closeTime){
			active = false;
			stateChanged();
		}		
		else{
			active = true;
			stateChanged();
		}	
	}
	
	@Override
	public void msgUpdateTime(int time, int day) {
		if (time < startTime || time > closeTime){
			active = false;
			stateChanged();
		}		
		else{
			active = true;
			stateChanged();
		}	
	}
	
	public boolean isActive(){
		return active;
	}
	

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public void setLocation(String l) {
		this.location = l;
	}
	
	public void msgAtLocation(Passenger tr){
		print(tr.getPassengerName() + " is here");
		log.add(new LoggedEvent("Passenger at bus stop"));
		synchronized(waitingPassengers) {waitingPassengers.add(tr);}
		stateChanged();
	}
	
	public void msgAtLocation(Bus bus){
		print(bus.getBusName() + " is here");
		log.add(new LoggedEvent("bus is here"));
		this.bus = bus;
		stateChanged();
	}
	
	public void msgLeavingStop(Passenger tr){
		//print("msg person leaving Stop");
		//waitingPassengers.remove(tr);
		synchronized(waitingPassengers){
			for (Passenger p:waitingPassengers){
				if (p.equals(tr)){
					waitingPassengers.remove(tr);
				}
			}
		}
		stateChanged();
	}
	
	public void msgLeavingStop(){
		//print("msg bus leaving Stop");
		bus = null;
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (bus != null){
			releasePassengers();
			//print("Here");
			return true;
		}
		
		if (!waitingPassengers.isEmpty()){
			if (isActive() == false){
				
				tellPassengersToLeave();
				//print("Here2");
				return true;
			}
		}
		
		
		return false;
	}
	
	private void tellPassengersToLeave(){
		print("tell passengers to leave");
		boolean stay = mediator.msgAPassengerIsHere(this);
		if (stay){
		}
		else{
			try{
				for (Passenger tr:waitingPassengers){
					 tr.msgTransportationStopped();
					 waitingPassengers.remove(tr);
		          }
	        }catch(java.util.ConcurrentModificationException e){
		        tellPassengersToLeave();
		    }
			
		}
	}
	
	private void releasePassengers(){
		if (!bus.getNumberTold()){
			bus.msgTotalWaitingPeople(waitingPassengers.size());
			bus.setNumberTold(true);
		}
		//print("release passengers");
		for (Passenger tr:waitingPassengers){
			 if (!tr.isAsked()){
			 //print("asked");
				 tr.msgHereIsBus(bus);
				 tr.setAsked(true);  
				 return;
			 }
		 }
			bus.msgYouCanGo();
			bus = null;
	}

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Building> getBuildings() {
		// TODO Auto-generated method stub
		return buildings;
	}

	@Override
	public int getNumber() {
		// TODO Auto-generated method stub
		return number;
	}

	@Override
	public int getStartTime() {
		// TODO Auto-generated method stub
		return startTime;
	}

	@Override
	public void setStartTime(int t) {
		// TODO Auto-generated method stub
		startTime = t;
	}

	@Override
	public int getCloseTime() {
		// TODO Auto-generated method stub
		return closeTime;
	}

	@Override
	public void setCloseTime(int t) {
		// TODO Auto-generated method stub
		closeTime = t;
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		// TODO Auto-generated method stub
		return null;
	}

	public Mediator getMediator(){
		return mediator;
	}

	@Override
	public void setGui(GUI gui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNumber(int size) {
		// TODO Auto-generated method stub
		number = size;
	}
	

}
