package restaurant.redland.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import market.MarketAgent;
import market.interfaces.Market;
import restaurant.redland.RedlandRestaurantAgent;
import restaurant.redland.interfaces.*;
import restaurant.redland.UtilityClasses.*;
import restaurant.redland.UtilityClasses.Order.CookingState;
import global.roles.Role;

public class RedlandCookRole extends Role implements RedlandCook {

	/********** Data **********/
	
	
	
	RedlandRestaurantAgent restaurant;
	Market market = null;
	private RedlandHost host;
	private RedlandCashier cashier;
	private String name;
	Timer timer = new Timer();
	
	private Map<String, Food> foods = new LinkedHashMap<String, Food>();
	private List<Order> orders = Collections.synchronizedList( new ArrayList<Order>() );//synchronized list of orders sent from waiters
	private Map<String, Integer> restaurantOrder = new HashMap<String, Integer>();
	
	public RedlandCookRole(){
		super();
		//this.name = name;
		
		//other stuff with initialization
		int cookingTime = 5000;
		int initialAmount = 5;
		int lowAmount = 2;
		//create menu
		addFood("Steak", cookingTime, (float)15.99, initialAmount, lowAmount);
		addFood("Chicken", cookingTime, (float)10.99, initialAmount, lowAmount);
		addFood("Salad", cookingTime, (float)5.99, initialAmount, lowAmount);
		addFood("Pizza", cookingTime, (float)8.99, initialAmount, lowAmount);
	}
	
	public void setHost( RedlandHost host){
		this.host = host;
	}
	
	/********** Messages **********/
	public void msgHereIsOrder( RedlandWaiter w, String choice, int table){
		//print( "Got new order " + choice + " for waiter " + w.getName() );
		synchronized( orders ){
			orders.add(new Order(w, choice, table));
		}
		stateChanged();
	}
	
	//message from waiter confirming that they got their order
	public void msgGotOrder( RedlandWaiter w, String choice, int table){
		synchronized( orders ){
			for ( Order order : orders ){
				//this assumes only one customer/order per table, change if necessary
				if ( (order.getWaiter() == w) 
					&& (order.getTable() == table))
				{
						order.setState(CookingState.recievedByWaiter);
				}
			}
		}
		stateChanged();
	}
	
	//message from timer telling cook food is done
	public void msgFoodIsDone(Order o){
		synchronized( orders ){
			for( Order order : orders){
				if( order == o ){
					//print( "Order " + o.getChoice() + " for " + o.getWaiter().getName() + " is done" );
					//order.setEvent(OrderEvent.doneCooking);
					order.setState(CookingState.doneCooking);
				}
			}
		}
		stateChanged();
	}
	
	//messages from market
	public void msgHereAreSupplies( String food, int amount, float price ){
		float bill = price;//TODO: send to cashier
		Food thisFood = foods.get(food);
		thisFood.setAmount( thisFood.getAmount() + amount );
		stateChanged();
	}
	
	public void msgOutOfOrder( String food ){
		//TODO: implement this
		stateChanged();
	}
	
	
	/********** Scheduler **********/
	public boolean pickAndExecuteAnAction() {
		synchronized( orders ){
			for ( Order order : orders ){
				//if low on order get supplies from cook
				if ( foods.get(order.getChoice()).isLow() ){
					NeedSupplies(order.getChoice());
					return true;
				}
			}
			//if food is done cooking PlateIt
			for ( Order order : orders ){
				if ( order.getState() == CookingState.doneCooking ){
					order.setState(CookingState.done);
					PlateIt(order);
					return true;
				}
			}
			//if an order is pending cook it
			for ( Order order : orders ){
				if ( order.getState() == CookingState.pending ){
					//check to make sure we have that order
					if ( foods.get(order.getChoice()).getAmount() == 0 ){
						OutOfOrder( order );//non-normative case
						return true;
					}
					order.setState(CookingState.cooking);
					CookIt(order);
					return true;
				}
			}
			for ( Order order : orders ){
			//if order is completed remove it
				if ( order.getState() == CookingState.recievedByWaiter){
					RemoveOrder(order);
				}
			}
		}
		return false;
	}
	
	
	
	/********** Actions **********/
	private void CookIt(final Order o){
		synchronized( orders ){
			for ( Order order : orders ){
				if ( order == o ){
					//print( "Cooking " + o.getChoice() + " for waiter " + o.getWaiter().getName() );
					DoCooking(o);//animation call
					timer.schedule(new TimerTask() {
						public void run() {
							msgFoodIsDone(o);
						}
					},
					foods.get( o.getChoice()).getCookingTime() );//how long to cook food
				}
			}
		}
	}
	
	private void PlateIt(Order o){
		synchronized( orders ){
			for ( Order order : orders ){
				if ( order == o ){
					//print( "Giving order " + o.getChoice() + " to waiter " + o.getWaiter().getName() );
					DoPlating(o);//animation call
					order.getWaiter().msgOrderIsReady( order.getChoice(),order.getTable() );
				}
			}
		}
	}
	
	private void RemoveOrder(Order o){
		synchronized( orders ){
			//print( "Removing order " + o.getChoice() + " for waiter " + o.getWaiter().getName() + " from list" );
			orders.remove( o );
		}
	}
	
	private void OutOfOrder( Order o ){
		synchronized( orders ){
			//print( "Out of order " + o.getChoice() + " for waiter " + o.getWaiter().getName() );
			o.getWaiter().msgOutOfOrder( o.getChoice(), o.getTable() );
			RemoveOrder( o );
		}
	}

	private void NeedSupplies( String food ){
		market.msgHereIsOrder( restaurantOrder, restaurant );
	}
	
	
	
	/********** GUI Calls **********/
	private void DoCooking( Order o ){
		
	}
	
	private void DoPlating( Order o ){
		
	}
	
	
	/********** Utilities **********/
	
	private void addFood(String type, int cookingTime, float price, int amount, int lowAmount){
		//creates a new food and adds it to foods map
		foods.put(type, new Food( type, cookingTime, price, amount, lowAmount) );	
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setCashier( RedlandCashier cashier ){
		this.cashier = cashier;
	}
	
	public void setMarket( Market market ){
		this.market = market;
	}
	
	public Market getMarket(){
		return this.market;
	}
	
	public void setRestaurant( RedlandRestaurantAgent rest ){
		this.restaurant = rest;
	}
	
	public RedlandRestaurantAgent getRestaurant(){
		return this.restaurant;
	}
	
	private class Food{
		String type;
		int cookingTime;
		float price;
		int amount;
		int lowAmount;
		
		public Food(String type, int cookingTime, float price, int amount, int lowAmount){
			this.type = type;
			this.cookingTime = cookingTime;
			this.price = price;
			this.amount = amount;
			this.lowAmount = lowAmount;
		}
		
		public float getPrice(){
			return price;
		}
		
		public String getType(){
			return this.type;
		}
		
		public int getCookingTime(){
			return cookingTime;
		}
		
		public int getAmount(){
			return amount;
		}

		public boolean isLow(){
			return (amount <= lowAmount);
		}
		
		public void setAmount( int amount ){
			this.amount = amount;
		}
	}
}
