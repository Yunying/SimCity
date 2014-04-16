package market;

import java.util.HashMap;
import java.util.Map;

/**
 * Inventory
 * @author Jeff Redland
 * This class is to keep track of the items, their quantities, and prices
 * It is shared data between all members of the market because that is natural to a market
 * Only one instance of this exists at a time and is used by all employees (manager, employee, driver)
 */
public class Inventory {
	
	Map< String, FoodItem > stock;
	
	public Inventory( int startingAmounts ){
		//create inventory
		stock = new HashMap< String, FoodItem >();
		
		/*do this in MarketAgent
		stock.put( "Pizza", new FoodItem( "Pizza", startingAmounts, (float) 6.00 ) );
		stock.put( "Steak", new FoodItem( "Steak", startingAmounts, (float) 7.00 ) );
		stock.put( "Salad", new FoodItem( "Salad", startingAmounts, (float) 5.00 ) );
		stock.put( "Soup", new FoodItem( "Soup", startingAmounts, (float) 5.00 ) );
		*/
	}
	
	public void AddFoodItem( String type, int amount, float price){
		stock.put( type, new FoodItem( type, amount, price) );
	}

	public int getAmount( String type ) {
		return stock.get( type ).amount;
	}

	public void setAmount( String type, int amount ) {
		stock.get( type ).amount = amount;
	}

	public float sell( String type ){
		//reduces amount by 1 and returns price of item
		if( stock.get( type ).amount > 0 ){
			setAmount( type, stock.get( type ).amount-1 );
			return stock.get( type ).price;
		}
		else return 0.0f;//should this throw an error message?
	}
	
	public float getPrice( String type ) {
		return stock.get( type ).price;
	}

	public void setPrice( String type, float price ) {
		stock.get( type ).price = price;
	}
	
	public boolean isAvailable( String type ){
		try {return ( stock.get( type ).amount > 0 );
		
		}catch(java.lang.NullPointerException e){
			return false;
		}
	}
	
	private class FoodItem{
		public String type;
		public int amount;
		public float price;
		
		public FoodItem( String type, int amount, float price){
			this.type = type;
			this.amount = amount;
			this.price = price;
		}
	}
}
