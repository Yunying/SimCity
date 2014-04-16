package restaurant.cammarano.interfaces;

import restaurant.cammarano.roles.CammaranoHostRole.Table;

public interface CammaranoHost {

	public abstract String getName();

	public abstract void msgAskToGoOnBreak(CammaranoWaiter waiterAgent);
	public abstract void msgTableEmpty(Table table);
}
