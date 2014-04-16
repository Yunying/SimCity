package restaurant.cammarano.roles;

import global.PersonAgent;
import restaurant.cammarano.roles.base.CammaranoRevolvingStand;
import global.actions.Action;
import global.roles.Role;
import restaurant.cammarano.roles.*;
import restaurant.cammarano.interfaces.*;
import gui.animation.role.restaurant.cammarano.CammaranoCookGUI;
import interfaces.Person;
import restaurant.cammarano.roles.CammaranoHostRole.Table;

import java.util.*;
import java.util.concurrent.*;
import market.MarketAgent;
import restaurant.cammarano.CammaranoRestaurantAgent;
import restaurant.cammarano.misc.CammaranoOrder;
import restaurant.cammarano.misc.CammaranoOrderEnum;

/**
 * Restaurant Cook Role
 */

public class CammaranoCookRole extends Role implements CammaranoCook {

	private boolean onOpening;
	private int leaveTime;
	
	public CammaranoCashier cashier;
	public CammaranoRevolvingStand stand;
	
	private Timer timer;
	private CammaranoRestaurantAgent restaurant;
	private CammaranoOrderEnum CammaranoOrderState;
	
	private MarketAgent market;

	private enum OrderState { Pending, Cooking, Done, Finished };
	//private Semaphore ordering = new Semaphore(0, true);
	//private Semaphore atDestination = new Semaphore(0, true);
	
	public List<Order> orders;
	//public List<MarketHandler> markets;
	public HashMap<String, Food> foods;
	
	public CammaranoCookGUI cookGui = null;
	
	public CammaranoCookRole() {
		super();
		leaveTime = 40;
		
		timer = new Timer();
		
		orders = Collections.synchronizedList(new ArrayList<Order>());
		foods = new HashMap<String, Food>();
		//markets = Collections.synchronizedList(new ArrayList<MarketHandler>());
		
		foods.put("steak", new Food(2500, 10, 20, 3, "steak"));
		foods.put("chicken", new Food(2000, 10, 20, 3, "chicken"));
		foods.put("pizza", new Food(5000, 10, 20, 3, "pizza"));
		foods.put("salad", new Food(1500, 10, 20, 3, "salad"));
	}

	@Override
	public String getName() {
		return person.getName();
	}

	@Override
	public void setPerson(Person p) {
		super.setPerson(p);
		for (PersonAgent.Job j : person.getJobs()) {
			if(j.getJob().equals("CammaranoCookRole")) {
				leaveTime = j.getEndTime();
			}
		}
	}
	
	/*
	public void setCashier(CammaranoCashier cashier) {
	this.cashier = cashier;
	}
	 */
	public void setRestaurant(CammaranoRestaurantAgent restaurant) {
		this.restaurant = restaurant;
	}
	
	// Messages
	@Override
	public void msgHereIsTheOrder(CammaranoWaiter w, Table t, String choice) {
		print(w.getName() + " needs " + choice);
		orders.add(new Order(w, t, choice));
		stateChanged();
	}
	
	public void msgTimerDone(Order o) {
		print(o.getChoice() + " is done.");
		o.state = OrderState.Done;
		stateChanged();
	}
	
	@Override
	public void msgOrderOnTheStand() {
		stateChanged();
	}
	
	/*
	public void msgHereIsTheFoodYouNeed(String f, int a) {
		print("Oi! I've gotten everything I need!");
		for (String s : foods.keySet()) {
			if(s == f) {
				foods.get(f).amount += a;
				ordering.release();
				stateChanged();
			}
		}
	}
	
	public void msgHereIsTheFoodYouNeedAndWeAreOut(String f, int a, Market m) {
		print("Oi! I've gotten almost everything I need!");
		for (MarketHandler mh : markets) {
			if(mh.market == m) {
				for (String s : foods.keySet()) {
					if(s == f) {
						foods.get(f).amount += a;
						mh.missingFood.add(f);
						ordering.release();
						stateChanged();
					}
				}
			}
		}
	}
	
	public void msgWeCanNotFullfillTheOrder(String f, Market m) {
		print("Oi! These bloody idiots ain't got nothin'!");
		for (MarketHandler mh : markets) {
			if(mh.market == m) {
				mh.missingFood.add(f);
				ordering.release();
				stateChanged();
			}
		}
	}
	*/
	
	// From animation
	@Override
	public void msgAtDestination() {
		//atDestination.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	@Override
	public boolean pickAndExecuteAnAction() {

		synchronized(orders) {
			for (Order o : orders) {
				if(o.state == OrderState.Done) {
					FinishAndCallWaiter(o);
					return true;
				}
			}
		}
		
		synchronized(orders) {
			for (Order o : orders) {
				if(o.state == OrderState.Pending) {
					CookIt(o);
					return true;
				}
			}
		}
		
		try {
			for (CammaranoOrder o : stand.orders) {
				if(o.state == CammaranoOrderState.Pending) {
					AddOrder(o);
					return true;
				}
			}
		} catch (ConcurrentModificationException | NullPointerException e) {}
		
		if(person.getCurrentTime() >= leaveTime) {
			LeaveWork();
			return true;
		}
		
		return false;
	}
	
	private void AddOrder(CammaranoOrder o) {
		print(o.getWaiter().getName() + " needs " + o.getChoice());
		o.state = CammaranoOrderState.Added;
		orders.add(new Order(o.getWaiter(), o.getTable(), o.getChoice()));
		stateChanged();
	}
	
	// Actions
	public void CookIt(final Order o) {
		/*
		DoGoToRefrigerator();
		try {
			atDestination.acquire();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		Food f = foods.get(o.choice);
		
		if(f.amount < 1) {
			o.waiter.msgOutOfFood(o.choice, o.table);
			orders.remove(o);
			return;
		}
		
		f.amount--;
		print("We have " + f.amount + " " + f.name + "s left.");
		
		if(f.amount <= f.low) {
			OrderLowFood();
		}
		
		/*
		DoGrabFood(o);
		DoCooking();
		try {
			atDestination.acquire();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		o.state = OrderState.Cooking;
		timer.schedule(new TimerTask() {
			public void run() {
				print("Cooking finished");
				msgTimerDone(o);
			}
		}, foods.get(o.choice).cookTime);
	}

	public void FinishAndCallWaiter(Order o) {
		/*
		DoPlating(o);
		try {
			atDestination.acquire();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		print("Plating");
		timer.schedule(new TimerTask() {
			public void run() {
				print("Finished plating");
			}
		}, 2000);
		/*
		DoGoToFoodArea(o);
		try {
			atDestination.acquire();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		o.waiter.msgOrderDone(o.choice, o.table);
		o.state = OrderState.Finished;
		//cookGui.FinishedOrder();
	}
	
	public void OrderLowFood() {
		print("Oi! We need more food");
		HashMap<String, Integer> marketOrders = new HashMap<String, Integer>();
		for (String key : foods.keySet()) {
			if(foods.get(key).amount <= foods.get(key).low) {
				marketOrders.put(key, (foods.get(key).capacity - foods.get(key).amount));
			}
		}
		
		market.msgHereIsOrder(marketOrders, restaurant);
	}

	public void LeaveWork() {
		restaurant.msgLeavingBuilding(this);
	}
	
	//Animations
	private void DoGoToRefrigerator() {
		cookGui.DoGoToRefrigerator();
	}
	
	private void DoGrabFood(Order o) {
		cookGui.DoGrabFood(o.choice);
	}
	
	private void DoCooking() {
		cookGui.DoCooking();
	}

	private void DoPlating(Order o) {
		cookGui.DoGoToPlatingArea(o.choice);
	}
	
	private void DoGoToFoodArea(Order o) {
		cookGui.DoGoToFoodArea(o.choice);
	}
	
	// Utilities
	public void setCookGui(CammaranoCookGUI cookGui) {
		this.cookGui = cookGui;
	}

	public CammaranoRevolvingStand getStand() {
		return stand;
	}

	public void setStand(CammaranoRevolvingStand stand) {
		this.stand = stand;
	}
	
	/*
	public class MarketHandler {
		private Market market;
		public ArrayList<String> missingFood = new ArrayList<String>();
		
		public MarketHandler(Market m) {
			market = m;
		}
		
		public Market getMarket() {
			return market;
		}
		
		public void setMarket(Market market) {
			this.market = market;
		}
	}
	*/
	
	public class Order {
		private CammaranoWaiter waiter;
		private Table table;
		private String choice;
		private OrderState state;
		
		public Order(CammaranoWaiter w, Table t, String c) {
			waiter = w;
			table = t;
			choice = c;
			state = OrderState.Pending;
		}
		
		// Accessors and mutators
		public CammaranoWaiter getWaiter() {
			return waiter;
		}
		
		public void setWaiter(CammaranoWaiter waiter) {
			this.waiter = waiter;
		}
		
		public Table getTable() {
			return table;
		}
		
		public void setTable(Table table) {
			this.table = table;
		}
		
		public String getChoice() {
			return choice;
		}
		
		public void setChoice(String choice) {
			this.choice = choice;
		}
		
		public OrderState getState() {
			return state;
		}
		
		public void setState(OrderState state) {
			this.state = state;
		}
	}
	
	// Food class for the restaurant. The host stores all of the food in a map
	public class Food {
		int cookTime;
		int amount;
		int capacity;
		int low;
		String name;
		
		Food(int ct, int a, int c, int l, String n) {
			cookTime = ct;
			amount = a;
			capacity = c;
			low = l;
			name = n;
		}
		
		// Accessors and mutators
		public int getCookTime() {
			return cookTime;
		}
		
		public void setCookTime(int cookTime) {
			this.cookTime = cookTime;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
}

