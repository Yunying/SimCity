package transportation.tests.mock;

import java.util.ArrayList;
import java.util.List;

import interfaces.Building;
import interfaces.Person;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import global.test.mock.Mock;

public class MockBuilding extends Mock implements Building{
	String name;
	
	public MockBuilding(String s){
		super();
		this.name = s;
	}

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("person at the building"));
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
	
	public String getName(){
		return name;

	}

}
