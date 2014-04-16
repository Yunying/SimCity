//package restaurant.ji.roles;
//
//import restaurant.ji.Food;
//import restaurant.ji.interfaces.JiCashier;
//import restaurant.ji.interfaces.JiCook;
//import restaurant.ji.interfaces.JiMarket;
//import global.roles.Role;
//
//import java.util.*;
//
///**
// * Restaurant Cook Agent
// * The agent that takes cooks orders
// */
//public class JiMarketRole extends Role implements JiMarket {
//	
//	private Vector<restockOrder> incomingRestockOrder = new Vector<restockOrder>();
//	private Timer timer = new Timer();
//	private String name;
//	private final int startingStock = 5;
//	private List<Food> inventory = new ArrayList<Food>(Arrays.asList(
//			new Food("Steak", startingStock), 
//			new Food("Chicken", startingStock),
//			new Food("Salad", startingStock),
//			new Food("Pizza", startingStock)));
//	private static Map<String, Float> price = new HashMap<String, Float>();
//	
//	private Float owed;
//	
//	public JiMarketRole(String name) {
//		super();
//		this.name = name;
//		price.put("Steak", new Float(10.00));
//		price.put("Chicken", new Float(8.00));
//		price.put("Salad", new Float(4.00));
//		price.put("Pizza", new Float(5.00));
//		owed = 0f;
//	}
//	
//	public String getName() { return name; }
//	public List<Food> getStock() { return inventory; }
//	
//	/*
//	 * Messages
//	 */
//	public void msgOrderingFood(Food food, int number, JiCook cook, JiCashier cashier){
//		incomingRestockOrder.add(new restockOrder(food, number, cook, cashier));
//		stateChanged();
//	}
//	public void msgPayingForFood(Float amount, JiCashier cashier){
//		if (amount < owed){
//			Do("Adding a fee for not paying your bill in full!!!");
//			owed += 5f;
//		}
//		else{
//			Do("Thanks for your business!");
//		}
//		owed -= amount;
//	}
//
//	/**
//	 * Scheduler.  Determine what action is called for, and do it.
//	 */
//	public boolean pickAndExecuteAnAction() {
//		if (!incomingRestockOrder.isEmpty()){
//			fulfillOrder(incomingRestockOrder.get(0));
//			return true;
//		}
//		return false;
//	}
//
//	
//	/*
//	 *  Actions	
//	 */
//	public void fulfillOrder(final restockOrder o){
//		for (final Food f : inventory){
//			if (f.getName() == o.food.getName()){
//				if (f.inStock()){
//					final int amountToBeDelivered = (f.getInventory() < o.amountNeeded)? f.getInventory() : o.amountNeeded; 
//					
//					if ((f.getInventory() < o.amountNeeded)) { // if this market cannot fulfill the entire order
//						print("     We can only give you " + amountToBeDelivered + " " + o.food.getName() + "(s)");
//						o.cook.msgCanOnlyDeliverPartial(this, o.food, amountToBeDelivered);
//					}
//					timer.schedule(new TimerTask() {
//						public void run() {
//							deliverOrder(f, amountToBeDelivered, o);
//							stateChanged();
//						}
//					}, amountToBeDelivered * 500); // time to deliver to cook
//				}
//				else{ // if out of stock completely
//					o.cook.msgMarketOutOfOrderedItem(o.food, this);
//					print("     Sorry we don't have any " + o.food.getName());
//				}
//				incomingRestockOrder.remove(o);
//			}
//		}
//	}
//	public void deliverOrder(Food f, int amountToBeDelivered, restockOrder o){
//		print("      Delivering " + amountToBeDelivered + " " + o.food.getName() + "(s). You owe $" + price.get(o.food.getName()) * amountToBeDelivered + " for this order ($" + (owed+price.get(o.food.getName()) * amountToBeDelivered) + " total)");
//		owed += price.get(o.food.getName()) * amountToBeDelivered;
//		o.cashier.msgPayMarket(owed, this);
//		o.cook.msgOrderFulfilled(o.food, amountToBeDelivered);
//		f.depleteStock(amountToBeDelivered);
//	}
//	
//	public void depleteStock(String foodName, int byHowMuch){
//		for (Food f : inventory){
//			if (f.getName() == foodName){
//				f.depleteStock(byHowMuch);
//			}
//		}
//	}
//	
//	public int getStockOf(String foodName){
//		for (Food f : inventory){
//			if (f.getName() == foodName){
//				return f.getInventory();
//			}
//		}
//		return -1;
//	}
//
//
//}
