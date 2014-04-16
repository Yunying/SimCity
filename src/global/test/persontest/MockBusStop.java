package global.test.persontest;

import java.util.ArrayList;

import global.actions.Action;
import global.roles.Role;
import global.test.persontest.base.MockBuilding;
import interfaces.Building;
import interfaces.Person;
import java.util.List;

public class MockBusStop extends MockBuilding implements Building {

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
