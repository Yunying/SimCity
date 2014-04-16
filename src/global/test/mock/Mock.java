/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package global.test.mock;

import interfaces.Person;

/**
 *
 * @author Colin
 */
public class Mock {
	public EventLog log = new EventLog();

	Person person;

	public Mock() {}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Person getPerson() {
		return person;
	}

	public String toString() {
		return this.getClass().getName() + ": " + person.getName();
	}

}
