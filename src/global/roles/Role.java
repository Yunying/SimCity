package global.roles;

import agent.StringUtil;
import global.PersonAgent;
import global.actions.Action;
import global.test.mock.EventLog;
import interfaces.Building;
import interfaces.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
	
/**
 * Base class for simple agents
 */
public abstract class Role {
	
	public boolean isActive = false;
	
	protected Person person;
	protected Semaphore stateChange = new Semaphore(1, true); //binary semaphore, fair

	public EventLog log = new EventLog();

	public List<Action> actions;
	
	protected Role() {
		actions = new ArrayList<Action>();
		
	}

	/**
	 * This should be called whenever state has changed that might cause
	 * the agent to do something.
	 */
	protected void stateChanged() {
		person.stateChanged();
	}

	/**
	 * Agents must implement this scheduler to perform any actions appropriate for the
	 * current state.  Will be called whenever a state change has occurred,
	 * and will be called repeated as long as it returns true.
	 *
	 * @return true if some action was executed that might have changed the
	 *		 state.
	 */
	public abstract boolean pickAndExecuteAnAction();

	/**
	 * Return agent name for messages.  Default is to return java instance
	 * name.
	 */
	
	protected String getName() {
		return StringUtil.shortName(this);
	}
	
	/**
	 * The simulated action code
	 */
	protected void Do(String msg) {
		print(msg, null);
	}

	/**
	 * Print message
	 */
	protected void print(String msg) {
		print(msg, null);
	}

	/**
	 * Print message with exception stack trace
	 */
	protected void print(String msg, Throwable e) {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append(": ");
		sb.append(msg);
		sb.append("\n");
		if (e != null) {
			sb.append(StringUtil.stackTraceString(e));
		}
		System.out.print(sb.toString());
	}
	
	// Accessors and mutators for roles
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public void activate(List<Action> actions){
		this.actions = actions;
		isActive = true;
		for (Action a : actions){
			System.out.println(a.task);
		}
	}
	public void deactivate(){
		isActive = false;
	}

	public void msgAtBuilding(Building rest) {
		// TODO Auto-generated method stub
		
	}
	
	//for testing and debug
	public boolean hasAction(String name){
		for (Action a : actions){
			if (a.task.equals(name)){
				return true;
			}
		}
		return false;
	}

}

