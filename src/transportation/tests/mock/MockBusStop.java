package transportation.tests.mock;

import interfaces.Building;
import interfaces.Person;

import java.util.ArrayList;
import java.util.List;

import global.actions.Action;
import global.roles.Role;
import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;
import global.test.mock.Mock;
import gui.animation.base.GUI;
import transportation.BusAgent;
import transportation.TransportationRole;
import transportation.Interfaces.Bus;
import transportation.Interfaces.Mediator;
import transportation.Interfaces.Passenger;
import transportation.Interfaces.Stop;

public class MockBusStop extends Mock implements Stop, Building {
	String name;
	int number;
	int xPos;
	int yPos;
	List<Building> buildings = new ArrayList<Building>();
	Bus bus;
	int start;
	int close;

	public MockBusStop(String name) {
		super();
		this.name = name;
	}
	
	public void setBus(Bus bus){
		this.bus=bus;
	}
	
	public MockBusStop(String s, int n, int x, int y){
		//TODO:initialize location xPos yPos
		name = s;
		number = n;
		xPos = x;
		yPos = y;
	}
	
	public MockBusStop(String s, int n, int x, int y, Bus bus){
		//TODO:initialize location xPos yPos
		name = s;
		number = n;
		xPos = x;
		yPos = y;
		this.bus = bus;
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgUpdateTime(int time) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocation(String l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgAtLocation(Passenger tr) {
		// TODO Auto-generated method stub
		
		log.add(new LoggedEvent("There is one person at the bus stop"));

	}


	@Override
	public void msgLeavingStop(Passenger tr) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Passenger is leaving the stop"));

	}

	@Override
	public void msgLeavingStop() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("bus leaving stop"));

	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
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
	public void msgAtLocation(Bus bus) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("bus is here"));
	}

	@Override
	public void addBuilding(Building b) {
		// TODO Auto-generated method stub
		buildings.add(b);
	}
	
	public Bus getBus(){
		return bus;
	}

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public int getStartTime() {
		// TODO Auto-generated method stub
		return start;
	}

	@Override
	public void setStartTime(int t) {
		// TODO Auto-generated method stub
		start = t;
	}

	@Override
	public int getCloseTime() {
		// TODO Auto-generated method stub
		return close;
	}

	@Override
	public void setCloseTime(int t) {
		// TODO Auto-generated method stub
		close = t;
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mediator getMediator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGui(GUI gui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNumber(int size) {
		// TODO Auto-generated method stub
		
	}



}
