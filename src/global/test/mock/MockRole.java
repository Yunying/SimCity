package global.test.mock;

import java.util.ArrayList;

import interfaces.Person;
import global.actions.Action;
import global.roles.Role;

public class MockRole extends Role {
	public EventLog log = new EventLog();
	Person person;
	
	public MockRole() {
		super();
		
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Person getPerson() {
		return person;
	}

	public String toString() {
		return this.getClass().getName() + ": " + person.getName();
	}
	public String getName(){ return person.getName();}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}
}
