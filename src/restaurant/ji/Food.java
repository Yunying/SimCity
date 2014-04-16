package restaurant.ji;

public class Food{
	public static enum FoodState {inStock, stockLow, beingOrdered, outOfStock};
	public FoodState state;
	
	private String name;
	private int inventory;
	private int expectingToBeDelivered;
	
	
	// Food for markets
//	public Food(String name, int initialStock){
//		this.name = name;
//		this.inventory = initialStock;
//		this.state = FoodState.inStock;
//		this.expectingToBeDelivered = 0;
//	}
//	
	// Food for cooks
	public Food(String name, int initialStock, int minStock){
		this.name = name;
		this.inventory = initialStock;
		this.state = (initialStock < minStock)? FoodState.stockLow : FoodState.inStock;
		this.expectingToBeDelivered = 0;
	}

	public String getName(){
		return name;
	}
	
	public boolean inStock(){
		return (inventory > 0);
	}
	
	public int getInventory() {
		return inventory;
	}
	
	public void reStock(int amountDelivered) {
		inventory += amountDelivered;
		expectingToBeDelivered -= amountDelivered;
	}
	
	public void depleteStock(int amountUsed) {
		inventory -= amountUsed;
		state = (inventory > 0)? state : FoodState.stockLow;
	}
	
	public int getExpectingToBeDelivered(){
		return expectingToBeDelivered;
	}
	public void expectingDelivered(int num){
		expectingToBeDelivered += num;
	}
	
}
