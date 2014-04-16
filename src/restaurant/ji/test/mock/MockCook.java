package restaurant.ji.test.mock;

import java.util.List;

import global.test.mock.*;
import restaurant.ji.CustomerOrder;
import restaurant.ji.Food;
import restaurant.ji.RevolvingStand;
import restaurant.ji.interfaces.JiCook;
import restaurant.ji.interfaces.JiWaiter;
import restaurant.ji.roles.JiCookRole.State;
import market.interfaces.Market;

public class MockCook extends MockRole implements JiCook{
		
	public EventLog log = new EventLog();
	public MockCook() {
		super();
		// TODO Auto-generated constructor stub
	}	

	@Override
	public void msgNextOrderFromStand(CustomerOrder customerOrder) {
		log.add(new LoggedEvent("Received msgNextOrderFromStand"));
		
	}
	
	@Override
	public void msgCookThisOrder(String choice, JiWaiter w, int tableNum){
		log.add(new LoggedEvent("Received msgCookThisOrder"));
	}

	@Override
	public void msgOrderCameIn(String f, int amount) {
		log.add(new LoggedEvent("Received msgOrderCameIn"));
	}
	
//	@Override
//	public void msgOrderFulfilled(Food food, int amountOrdered) {
//		log.add(new LoggedEvent("Received msgOrderFulfilled"));
//	}	
//	
	@Override
	public void msgCanOnlyDeliverPartial(Market market, Food food, int amountDeliverable) {
		log.add(new LoggedEvent("Received msgCanOnlyDeliverPartialOrder"));
	}
	
	@Override
	public void msgMarketOutOfOrderedItem(Food food, Market market) {
		log.add(new LoggedEvent("Received msgMarketOutOfOrderedItem"));
	}

	public void msgHeresYourPaycheck(float pay) {
		log.add(new LoggedEvent("Received msgHeresYourPaycheck"));
		
	}

	public void msgStopWorkingGoHome() {
		log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
		
	}

	@Override
	public boolean hasReceivedPaycheck() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getName(){
		return null;
	}

	@Override
	public void msgAtFridge() {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void msgAtPlating() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAtStove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOrderStand(RevolvingStand orderstand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWorking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomerOrder> getIncomingOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RevolvingStand getOrderStand() {
		// TODO Auto-generated method stub
		return null;
	}


}
