package restaurant.ji.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import restaurant.ji.Food;

public interface JiMarket {
	
		static Map<String, Float> price = new HashMap<String, Float>();
		List<Food> inventory = new ArrayList<Food>();
	
		public abstract void msgOrderingFood(Food food, int number, JiCook cook, JiCashier cashier);
		public abstract void fulfillOrder(final restockOrder o);

		public static class restockOrder{
			public Food food;
			public int amountNeeded;
			public JiCook cook;
			public JiCashier cashier;
			
			public restockOrder(Food food, int amountOrdered, JiCook cook, JiCashier cashier){
				this.food = food;
				this.amountNeeded = amountOrdered;
				this.cook = cook;
				this.cashier = cashier;
			}
			
		}

		public abstract void msgPayingForFood(Float amountPaying, JiCashier cashierAgent);
		public abstract String getName();
		public abstract List<Food> getStock();
		public abstract void msgOrderingFood(Food f, int i, JiRestaurant restaurant);
		

}