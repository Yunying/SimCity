package restaurant.yunying.interfaces;

import java.util.List;

import restaurant.yunying.Food;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import restaurant.yunying.roles.*;
import global.roles.Role;
import global.test.mock.EventLog;


public interface Customer {

	public enum AgentEvent {}

	Cashier cashier = null;
	
	public void msgHereIsYourNumber(int num);
	
	public void msgTablesAreFull();
	
	public void msgWhatWouldYouLike();
	
	public void msgHereIsYourFood(String choice);
	
	public void msgPleaseReorder(String choice);
	
	public void msgHereIsCheck(double amount);
	
	public void msgHereIsChange(double change);
	
	public void msgYouNeedMoreMoney(double owe);

	public int getWaitingNumber();

	public void msgFollowMeToTable(Waiter tuWaiterRole, int table,
			List<Food> menu);

	public void msgAnimationFinishedGoToSeat();

	public void doneWorking();

	public void gotHungry();

	public String getName();

	public CustomerGui getGui();

	public void msgAnimationFinishedAdjusting();

	public void msgAnimationFinishedGoToRestaurant();

	public void msgAnimationFinishedGoToCashier();

	public void msgAnimationFinishedLeaveRestaurant();

	

}