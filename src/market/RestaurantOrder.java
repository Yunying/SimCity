package market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.Building;

public class RestaurantOrder {
	private Building restaurant;
	private Map<String, Integer> order;
	private float bill;
	private float moneyGiven;
	private orderState state;
	
	enum orderState {pending, processing, givenToDriver, enroute, delivered};
	
	public RestaurantOrder( Building restaurant, Map<String, Integer> order ){
		this.setRestaurant(restaurant);
		this.order = new HashMap<String, Integer>();
		this.order = order;
		this.setState(orderState.pending);
		bill = 0.0f;
		moneyGiven = 0.0f;
	}
	
	/** Utilities */
	
	public void setBill( float bill ){
		this.bill = bill;
	}
	
	public float getBill(){
		return this.bill;
	}

	public Building getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Building restaurant) {
		this.restaurant = restaurant;
	}

	public Map<String, Integer> getOrder() {
		return order;
	}

	public void setOrder(Map<String, Integer> order) {
		this.order = order;
	}

	public orderState getState() {
		return state;
	}

	public void setState(orderState state) {
		this.state = state;
	}

	public float getMoneyGiven() {
		return moneyGiven;
	}

	public void setMoneyGiven(float moneyGiven) {
		this.moneyGiven = moneyGiven;
	}
}
