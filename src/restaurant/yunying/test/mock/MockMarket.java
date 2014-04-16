package restaurant.yunying.test.mock;

import interfaces.Building;
import interfaces.Person;

import java.util.List;
import java.util.Map;

import market.MarketAgent.MarketState;
import market.RestaurantOrder;
import market.interfaces.Market;
import market.interfaces.TruckDriver;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;

public class MockMarket extends MockRole implements Market{
	String name;
	
	public MockMarket(String name){
		super();
		this.name = name;
	}
	
	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStartTime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCloseTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCloseTime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgHereIsOrder(Map<String, Integer> order, Building restaurant) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderDelivered(TruckDriver driver, RestaurantOrder o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsMarketState(MarketState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgMarketOpen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgMarketClosed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgUpdateBalance(float bill) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPersonHasLeft(Person person, Role r) {
		// TODO Auto-generated method stub
		
	}
	
	public String getName(){
		return getName();
	}
}
