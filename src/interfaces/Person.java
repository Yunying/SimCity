package interfaces;

import global.PersonAgent.Job;
import global.actions.Action;
import global.roles.*;

import java.util.List;

public interface Person {
	// Messages
	public abstract void msgLeavingLocation(Role r);
	public abstract void msgUpdateTime(int time, int day);

	public abstract Building getBuilding(String building);
	public abstract Role getRole(String role);
	public abstract String getName();
	public abstract float getMoney();
	public abstract void setMoney(float f);
	public abstract void setHasCar(boolean hasCar);
	public abstract void ChangeMoney(float m);
	
	// Adding tasks
	public abstract void AddTaskWakeUpAndGetReady();
	public abstract void AddTaskGetFood();
	public abstract void AddTaskBuyFoodAtMarket(List<Action> a);
	public abstract void AddTaskBuyCarAtMarket();
	public abstract void AddTaskGoHome();
	public abstract void AddTaskGoToBankForLoan();
	public abstract void AddTaskDepositEarnings(Building building, float f);

	public void setWorking(boolean t);	
	public abstract void stateChanged();
	public abstract int getCurrentTime();
	public List<Job> getJobs();
}
