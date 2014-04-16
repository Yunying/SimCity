package restaurant.mcneal.interfaces;

import global.roles.Role;
import interfaces.Building;

public interface McNealRestaurant extends Building {

	public abstract void setCurrentAssets(int cash);
	public abstract float getAssets();
	public void msgLeavingBuilding(Role r, float moneyToDeposit);

	

}
