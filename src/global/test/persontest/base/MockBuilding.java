package global.test.persontest.base;

import java.util.ArrayList;

import interfaces.*;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.*;

import java.util.List;

public class MockBuilding extends Mock implements Building {
	
	protected String name;

	public void msgUpdateTime(int time, int day) {
		log.add(new LoggedEvent("Received UpdateTime. It is now " + time));
	}

	public String getLocation() {
		return "";
	}

	@Override
	public int getStartTime() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setStartTime(int t) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getCloseTime() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setCloseTime(int t) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
