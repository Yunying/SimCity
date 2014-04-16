package global.test.persontest;

import java.util.ArrayList;

import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import global.test.persontest.base.MockBuilding;
import interfaces.Building;
import interfaces.Person;

import java.util.List;

public class MockBank extends MockBuilding implements Building {

	public MockBank(String name) {
		this.name = name;
	}

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		log.add(new LoggedEvent("msgAtLocation Received"));
	}

}
