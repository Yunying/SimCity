package gui.ui.building.mock;

import java.util.ArrayList;
import java.util.List;

import interfaces.Building;
import interfaces.Person;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;

public class MockBuilding extends MockRole implements Building{
	String name;
	int start;
	int close;
	
	public MockBuilding(String name){
		super();
		this.name = name;
		start = 8;
		close = 20;
	}
	

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartTime() {
		// TODO Auto-generated method stub
		return start;
	}

	@Override
	public void setStartTime(int t) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("set start time"));
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
		log.add(new LoggedEvent("set close time" + t));
		close = t;
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getName(){
		return name;
	}

}
