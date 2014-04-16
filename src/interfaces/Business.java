package interfaces;

import global.roles.Role;

public interface Business extends Building {
		
	// messages sent from employees
	public abstract void msgLeavingWork(Role employee);
	
	// messages sent from customers
	public abstract void msgLeavingAsCustomer(Role role);

	/****inherited from Building****/
	/* 
	 * String location = null;
	 * public abstract void msgAtLocation(Person p, Role r, ArrayList<Action> actions);
	 * public abstract void msgUpdateTime(int time);
	 * public abstract String getLocation();
	 * public abstract void setLocation(String l);
	*/
	
}
