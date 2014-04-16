package interfaces;

import global.actions.Action;
import global.roles.Role;
import java.util.*;

/**
 * @author CMCammarano
 *
 */
public interface Building {
	// Messages
	public abstract void msgAtLocation(Person p, Role r, List<Action> actions);
	public abstract void msgUpdateTime(int time, int day);
	
	public abstract String getLocation();
	public abstract String getName();
	public abstract int getStartTime();
	public abstract void setStartTime(int t);
	public abstract int getCloseTime();
	public abstract void setCloseTime(int t);
	public abstract List<Role> getPeopleInTheBuilding();
}