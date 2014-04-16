package restaurant.ji.roles;

import java.util.*;
import java.util.concurrent.Semaphore;

import interfaces.Building;
import interfaces.Person;
import market.interfaces.Market;
import restaurant.ji.*;
import restaurant.ji.CustomerOrder.OrderStatus;
import restaurant.ji.interfaces.*;
import restaurant.ji.Food.FoodState;
import global.roles.Role;
import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;


/**
 * Restaurant Cook Agent
 * The agent that takes cooks orders
 */
public class JiCookRole extends Role implements JiCook{
        
	public EventLog log = new EventLog();
	
	private JiRestaurant restaurant;
	
	public enum State {
		none, arrivedAtJiRestaurant, working, leavingWork
	};
	private State state;
	
	private List<CustomerOrder> incomingOrders = Collections.synchronizedList(new ArrayList<CustomerOrder>());
    private RevolvingStand orderStand;
    
    // semaphores for animation
    private Semaphore atPlating = new Semaphore(0,true);
    private Semaphore atFridge = new Semaphore(0,true);
    private Semaphore atStove = new Semaphore(0,true);
    
    private final Timer timer = new Timer();
    
    public static final int minStock = 5;
    private static final int initialStock = 10;
    private static CooksPantry pantry = new CooksPantry();

    private boolean working;
	private boolean receivedPaycheck;
	public boolean UNITTESTING = false; // to disable timer when unit testing 
	private boolean nothingToDo = true;
	
	//CookGui gui;
    
    public JiCookRole() {
    	super();
    	state = State.none;
    	working = false;
    	receivedPaycheck = false;
    }
	
    // semaphore release from the guis
    @Override public void msgAtPlating() {  atPlating.release(); }
	@Override public void msgAtFridge() { atFridge.release(); }
	@Override public void msgAtStove() { atStove.release(); }
    
	//**********Messages************//
	@Override
	public void msgAtBuilding(Building rest){
		log.add(new LoggedEvent("Received msgAtJiRestaurant"));
		state = State.arrivedAtJiRestaurant;
		this.restaurant = (JiRestaurant)rest;
		stateChanged();
	}
	
    @Override
	public void msgCookThisOrder(String choice, JiWaiter w, int tableNum){
    	synchronized(incomingOrders){
    		log.add(new LoggedEvent("Received msgCookThisOrder"));
        	state = State.working;
            incomingOrders.add(new CustomerOrder(choice, w, tableNum));
            stateChanged();
        }
    }       
    
    @Override
	public void msgNextOrderFromStand(CustomerOrder order){
    	synchronized(incomingOrders){
    		log.add(new LoggedEvent("Received msgNextOrderFromStand"));
    		state = State.working;
            incomingOrders.add(order);
            stateChanged();
        }
    }
    
	@Override
	public void msgOrderCameIn(String f, int amount){
		log.add(new LoggedEvent("Received msgOrderCameIn"));
		state = State.working;
		Food food = getPantry().getFoodFromInventory(f);
		food.reStock(amount);
		Do("Restocked! Now have " + food.getInventory() + " " + food.getName() + "s");
		food.state = (food.getInventory() < minStock)? food.state : FoodState.inStock;
		food.expectingDelivered(0);
		stateChanged();
	}
	
    @Override
	public void msgCanOnlyDeliverPartial(Market market, Food food, int amountDeliverable) {
    	log.add(new LoggedEvent("Received msgCanOnlyDeliverPartial"));
    	state = State.working;
    	food.state = FoodState.stockLow;
        getPantry().removeMarket(market, food);
        food.expectingDelivered(amountDeliverable);
        stateChanged();
    }
    
    @Override
	public void msgMarketOutOfOrderedItem(Food food, Market market) {
    	log.add(new LoggedEvent("Received msgMarketOutOfOrderedItem"));
    	state = State.working;
        getPantry().removeMarket(market, food);
        food.state = FoodState.stockLow;
        food.expectingDelivered(0);
        stateChanged();
    }
    
    @Override
	public void msgStopWorkingGoHome() {
    	log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
		state = State.leavingWork;
		setWorking(false);
		stateChanged();
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		log.add(new LoggedEvent("Received msgHeresYourPaycheck"));
		person.ChangeMoney(paycheck);
		receivedPaycheck = true;
		stateChanged();
	}

	//**********Scheduler************//
    @Override
	public boolean pickAndExecuteAnAction() {
    	if (state == State.arrivedAtJiRestaurant){
    		beginWork();
    		return true;
    	}
    	else if (state == State.working){
	        synchronized(incomingOrders){
	        	for (Food f : CooksPantry.inventory){
	                if (f.state == FoodState.stockLow){
	                    orderMoreFood(f);
	                    return true;
	                }
	            }
	            for (CustomerOrder o : getIncomingOrders()){
	                if (o.getStatus() == OrderStatus.uncooked){
	                	Do("Cooking");
	                	cookOrder(o);
	                	return true;
	                }
	                else if (o.getStatus() == OrderStatus.cooked){
	                    notifyWaiterOrderIsDone(o);
	                    return true;
	                }
	                else if (o.getStatus() == OrderStatus.cooking){
	                	//gui.DoCook();
	                	setNothingToDo(false);
	                }
	            }
	            if (hasNothingToDo()){
	            	checkOrderStand();
	            	return true;
	            }
	        }
    	}
    	
    	else if (state == State.leavingWork){
    		leaveWork();
    	}
    	return false;
    }

    
    //**********Actions************//
    
    // from the animation. add back in later
    void cookOrderAnimation(String food){
//    	gui.DoGoToPlating(false);
//		try { 
//			atPlating.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		gui.setChoiceText(food, false);
//    	gui.DoGoToFridge();
//    	try { 
//			atFridge.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//    	gui.DoGoToStove(true);
//    	try { 
//			atStove.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
    }
    void finishOrderAnimation(String food){
//    	gui.DoGoToStove(false);
//    	try { 
//			atStove.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//    	gui.setChoiceText(food, true);
//    	gui.DoGoToPlating(true);
//		try { 
//			atPlating.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
    }
    
    private void beginWork(){
    	state = State.working;
    	Do("Working as cook now!");
    	working = true;
    	receivedPaycheck = false;
    	restaurant.msgAtLocation(person, this, null);
    }
    
    private void checkOrderStand(){
		orderStand.getOrder(this);
		setNothingToDo(false);
    }
    
    private void cookOrder(final CustomerOrder o){
    	o.setStatus(OrderStatus.cooking);
    	Food f = getPantry().getFoodFromInventory(o.getChoice());
    	if (f == null){
			return;
    	}
    	if (f.inStock()){
    		cookOrderAnimation(o.getChoice().substring(0,2));
    		print("Cooking " + o.choice + "...");
        	f.depleteStock(1);
            f.state = (f.state != FoodState.beingOrdered && f.getInventory() < minStock)? FoodState.stockLow : FoodState.inStock;
            
            if (UNITTESTING){
            	o.setStatus(OrderStatus.cooked);
            }
            else{
	            timer.schedule(new TimerTask() {
	                @Override
					public void run() {
	                    o.setStatus(OrderStatus.cooked);
	                }
	            }, getPantry().getCookTime(f));
	            setNothingToDo(true);
            }
        }
        else {
            print(o.getWaiter().getName() + ", we're out of " + o.getChoice());
            o.getWaiter().msgOutOfThis(o.getChoice());
            incomingOrders.remove(o);
        }
    }

    private void notifyWaiterOrderIsDone(CustomerOrder o){
        o.setStatus(OrderStatus.plated);
    	finishOrderAnimation(o.getChoice().substring(0,2));
		Do(o.getChoice() + " OUT! ");
        o.getWaiter().msgOrderIsReady(o.getTableNum());
        print(o.waiter.getName() + ", " + o.choice + " is ready");
        incomingOrders.remove(o);
        setNothingToDo(true);
        //gui.DoGoIdle();
    }
    
    private void orderMoreFood(Food f){
        f.state = FoodState.beingOrdered; // the food has been ordered, no need to order again
        if (!getPantry().getMarketsSupplying(f.getName()).isEmpty()){
            Market market = getPantry().getMarketsSupplying(f.getName()).get(0); // get first market with this item in stock
            Do("      Ordering " + (minStock - (f.getInventory() + f.getExpectingToBeDelivered())) + " " + f.getName() + " from " + market.getName());
            
            Map<String, Integer> orders = new HashMap<String, Integer>();
            orders.put(f.getName().toLowerCase(), minStock - (f.getInventory() + f.getExpectingToBeDelivered()));
            market.msgHereIsOrder(orders, restaurant);
            
            //market.msgOrderingFood(f, minStock - (f.getInventory() + f.getExpectingToBeDelivered()), (JiCook)this, cashier);
            // my market code
        }
        else{
            f.state = FoodState.outOfStock;
            print("No markets are supplying " + f.getName());
        }
    }

    private void leaveWork(){
    	state = State.none;
    	Do("Done working bye");
    	restaurant.msgLeavingWork(this);
    	//gui.DoLeaveBuilding();
    	person.msgLeavingLocation(this); // inactivate JiCookRole
    	setNothingToDo(false);
    }
    
    //**********Cook's classes************//
    public static class CooksPantry{
        private static Map<String, Integer> entreeCookingTime = new HashMap<String, Integer>();
        private static Map<String, List<Market>> availableMarkets = new HashMap<String, List<Market>>();
        private static List<Food> inventory = new ArrayList<Food>(Arrays.asList(
                new Food("Steak", initialStock, minStock),
                new Food("Chicken", initialStock, minStock), 
                new Food("Salad", initialStock, minStock), 
                new Food("Pizza", initialStock, minStock)
                ));
        
       private CooksPantry(){        	
        	entreeCookingTime.put("Steak", 30);
        	entreeCookingTime.put("Chicken", 20);
        	entreeCookingTime.put("Pizza", 15);
        	entreeCookingTime.put("Salad", 10);
        	
	        for (String f : entreeCookingTime.keySet()){
               availableMarkets.put(f, new ArrayList<Market>());
            }
        }
        
        public int getCookTime(Food food){
            return entreeCookingTime.get(food.getName());
        }
        
        public Food getFoodFromInventory(String name){
        	Food food = null;
        	for (Food f : inventory){
        		if (name.equals(f.getName())){
        			food =  f;
        			break;
        		}
        	}
        	return food;
        }
        
        public void deplete(String foodName, int byHowMuch){
        	Food f = getFoodFromInventory(foodName);
        	if (f != null)
        		f.depleteStock(byHowMuch);
        }
        public int getStockOf(String foodName){
    		Food f = getFoodFromInventory(foodName);
        	return (f == null)? -1 : f.getInventory();
    	}

        private List<Market> getMarketsSupplying(String food){
            return availableMarkets.get(food);
        }
        
        public static void addMarket(Market market){
            for (String f : availableMarkets.keySet()){
                if (availableMarkets.containsKey(f) && !availableMarkets.get(f).contains(market)){
                    availableMarkets.get(f).add(market);
                }
            }
        }
        
        private void removeMarket(Market market, Food food){
            List<Market> marketsWithFood = availableMarkets.get(food.getName());
            if (marketsWithFood.contains(market)){
        	   marketsWithFood.remove(market);
            }
        }

		public static void addMarkets(List<Market> markets) {
			for (Market m : markets){
				addMarket(m);
			}
		}
    }
    
    //**********Accessors************//
    
    //public void setGui(CookGui g){ gui = g;};
    //CookGui getGui(){return gui;}

    @Override public boolean hasReceivedPaycheck() { return receivedPaycheck; }
    @Override public void setPerson(Person person){ this.person = person; }
    @Override public Person getPerson(){ return person; }
    @Override public String getName() { return person.getName(); }
  	@Override public void setOrderStand(RevolvingStand stand) { this.orderStand = stand; }	
  	public List<Food> getStock() { return CooksPantry.inventory; }
  	public void addMarket(Market market) { getPantry().addMarket(market);  }
  	public JiRestaurant getRestaurant() { return restaurant; }
	public void setRestaurant(JiRestaurant restaurant) { this.restaurant = restaurant;}
	public RevolvingStand getOrderStand() { return orderStand;}
	public static CooksPantry getPantry() { return pantry; }
	public static void setPantry(CooksPantry pantry) { JiCookRole.pantry = pantry; }
	
	// for testing
	public boolean isWorking() { return working;}
	public void setWorking(boolean working) { this.working = working; }
	public State getState() { return state; }
	public void setState(State state) { this.state = state; }
	public List<CustomerOrder> getIncomingOrders() { return incomingOrders;}
	public void setIncomingOrders(List<CustomerOrder> incomingOrders) { this.incomingOrders = incomingOrders;}

	public boolean hasNothingToDo() {
		return nothingToDo;
	}

	public void setNothingToDo(boolean nothingToDo) {
		this.nothingToDo = nothingToDo;
	}


}