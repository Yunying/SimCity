package restaurant.ji.test.mock;

import interfaces.Building;
import interfaces.Person;

import java.util.List;
import java.util.Map;

import market.MarketAgent.MarketState;
import market.RestaurantOrder;
import market.interfaces.Market;
import market.interfaces.TruckDriver;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.*;

public class MockMarket extends Mock implements Market{
		
		public EventLog log = new EventLog();
		int stock;
		String name;
		
		public MockMarket(String name) {
			super();
			this.name = name;
//			price.put("Steak", new Float(10.00));
//			price.put("Chicken", new Float(8.00));
//			price.put("Salad", new Float(4.00));
//			price.put("Pizza", new Float(5.00));
//			
//			this.stock = stock;
//			inventory.addAll(Arrays.asList(
//					new Food("Steak", stock), 
//					new Food("Chicken", stock),
//					new Food("Salad", stock),
//					new Food("Pizza", stock)));
		}
		
		@Override
		public void msgHereIsOrder(Map<String, Integer> order, Building restaurant) {
			log.add(new LoggedEvent("Received msgHereIsOrder"));
//			//cook.msgOrderFulfilled(food, number);
//			if (number > stock){
//				log.add(new LoggedEvent("Charging cashier for partial order"));
//				//cook.msgCanOnlyDeliverPartial(this, food, stock);
//				cashier.msgPayMarket(price.get(food.getName()) * stock, this);
//			}
//			else{
//				log.add(new LoggedEvent("Charging cashier for entire order"));
//				//cook.msgOrderFulfilled(food, number);
//				cashier.msgPayMarket(price.get(food.getName())*number, this);
//			}
		}
		
		public void msgHereIsBill(float payment){
			log.add(new LoggedEvent("Received msgHereIsBill"));
		}
		
		@Override
		public void msgAtLocation(Person p, Role r, List<Action> actions) {
			log.add(new LoggedEvent("Received msgAtLocation"));
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void msgOrderDelivered(TruckDriver driver, RestaurantOrder o) {
			log.add(new LoggedEvent("Received msgOrderDelivered"));
			// TODO Auto-generated method stub
			
		}

		@Override
		public void msgHereIsMarketState(MarketState state) {
			log.add(new LoggedEvent("Received msgHereIsMarketState"));
			// TODO Auto-generated method stub
			
		}

		@Override
		public void msgMarketOpen() {
			log.add(new LoggedEvent("Received msgMarketOpen"));
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
		public String getName() {
			return name;
		}

		@Override
		public void msgPersonHasLeft(Person person, Role r) {
			// TODO Auto-generated method stub
			
		}

		

}
