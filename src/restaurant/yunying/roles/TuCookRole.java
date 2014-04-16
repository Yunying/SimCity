package restaurant.yunying.roles;
import agent.Agent;
import restaurant.yunying.Order;
import restaurant.yunying.Order.OrderState;
import restaurant.yunying.TuRestaurantAgent;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import interfaces.Person;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.interfaces.Market;

public class TuCookRole extends Role implements Cook{
	List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	Timer timer = new Timer();
	CookGui cookGui= null;
	OrderStand orderStand;
	boolean os;
	TuRestaurantAgent tr;
	
	enum FoodState{none, ordering, ordered, failOrder}
	private Semaphore animation = new Semaphore(0,true);
	private enum CookState {none, cooking};
	CookState state = CookState.none;
	Cashier cashier;
	class Food {
		String choice;
		Integer cookTime;
		int amount;
		int threshold;
		int capacity;
		int orderedAmt;
		int tempAmt;
		double marketPrice;
		Market currentMkt = null;
		
		public Food(int time, String name, int amt){
			choice = name;
			cookTime = time;
			amount = amt;
			threshold = 3;
			capacity = 10;
			tempAmt = 0;
			orderedAmt = 0;
		}
		
		public void reduceAmount(){
			amount--;
		}
		FoodState state = FoodState.none;
	}
	List<Food> foods = Collections.synchronizedList(new ArrayList<Food>());
	List<Market> markets = Collections.synchronizedList(new ArrayList<Market>());
	String name;
	private float salary;
	boolean readyToLeave;
	
	public void setPerson(Person p){
		super.setPerson(p);
		this.name = p.getName();
	}
	
	public float getSalary(){
		return 10;
	}
	
	public void setSalary(float a){
		salary = a;
	}
	
	public void msgAnimation() {
		//print("release");
		animation.release();
	}
	
	public TuCookRole() {
		super();
		foods.add(new Food(300,"Brownie",200));
		foods.add(new Food(250,"Cheesecake",200));
		foods.add(new Food(200,"Waffle",200));
		foods.add(new Food(150,"Crepe",200));
		foods.add(new Food(100,"Gelato",200));
		//markets.add(new Market("market"));
	}
	
	public void setCashier(Cashier c){
		cashier = c;
	}
	
	public void msgHereIsAnOrder(Waiter w, String s, int table){
		print("receive here is a customer order");
		Order o = new Order(s,w,table);
		synchronized (orders) {orders.add(o);}
		o.orderState=OrderState.pending;
		stateChanged();
	}
	
	public void msgHereIsAnOrder(){
		log.add(new LoggedEvent("here is an order from order stand"));
		os = true;
		stateChanged();
	}
	
	public void setGui(CookGui gui){
		cookGui = gui;
	}
	
	public void addMarket(Market m){
		markets.add(m);
	}
	
	public String getName() {
		return name;
	}

	public void msgMarketOutOfStock(Market m, String choice, int amt){
		print("Received message Market " + m.getName() + " has not enough storage");
		for (Food f:foods){
			if (f.choice.equals(choice)){
				f.tempAmt += amt;
				f.currentMkt = m;
				f.state = FoodState.failOrder;
				stateChanged();
			}
		}
		//Call the next market
//		for (int i=0; i<markets.size();i++){
//			if (markets.get(i).equals(m)){
//				if (markets.size()>i+1){
//				  print ("Now ordering from market " + (i+1));
//				  markets.get(i+1).msgIWantToOrder(this, choice, food.capacity-food.amount);
//				  return;
//				}
//				else{
//					print("This is the last one. No market has enough storage");
//				}
//			}
//		}
	}
	
	public void msgHereIsOrderedStorage(String choice, int amt){
		print("Receive food " + choice + "from Market");
		for (Food f:foods){
			if (f.choice.equals(choice)){
				//f.amount += amt;
				f.orderedAmt += amt;
				f.state = FoodState.ordering;
				stateChanged();
			}
		}
		//print("Food "+choice + " Now have " + amt);
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		if (os){
			takeOrderFromStand();
			return true;
		}
		
		if (!orders.isEmpty() && state != CookState.cooking){
			for (Order o:orders){
				if (o.orderState == OrderState.cooked){
					print("Scheduler Call waiter");
					callWaiter(o);
					return true;
				}
			}
			
			for (Order o:orders){
				if (o.orderState == OrderState.pending){
					print("Scheduler Cook Order");
					cookOrder(o);
					return true;
				}
				
			}
		}
		
		for (Food f: foods){
			if (f.state == FoodState.ordering){
				f.state = FoodState.none;
				increaseStorage(f);
				return true;
			}
		}
		
		for (Food f: foods){
			if (f.state == FoodState.failOrder){
				f.state = FoodState.none;
				callNextMarket(f);
				return true;
			}
		}
		
		if (readyToLeave){
			leaveWork();
		}
		
		
		return false;
	}
	
	private void leaveWork(){
		tr.msgLeavingWork(this);
	}
	
	private void takeOrderFromStand(){
		os = false;
		while (orderStand.getSize() != 0){
			Order o = orderStand.getOrder();
			o.orderState = OrderState.pending;
			orders.add(o);
			orderStand.remove(o);
		}
		stateChanged();
	}
	
	public void increaseStorage(Food f){
		f.amount += f.orderedAmt;
		f.orderedAmt = 0;
	}
	
	public void callNextMarket(Food f){
		//increaseStorage(f);
		for (int i=0; i<markets.size();i++){
			if (markets.get(i).equals(f.currentMkt)){
				if (markets.size()>i+1){
				  //print ("Now ordering from market " + (i+1));
					Map<String, Integer> myOrder = new HashMap<String, Integer>();
					myOrder.put(f.choice, f.capacity-f.amount-f.tempAmt);
					markets.get(i+1).msgHereIsOrder(myOrder, tr);
					return;
				}
				else{
					print("This is the last one. No market has enough storage to fill the capacity");
				}
			}
		}
	}
	
	synchronized public void cookOrder(Order o){
		o.orderState = OrderState.cooking;
		state = CookState.cooking;
//		cookGui.DoGetIngredients();
//		try {
//			//print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		
		int time = 0;
		Food f = null;
		for (Food fo:foods){
			if (fo.choice == o.choice){
				f = fo;
			}
		}
		
		if (f.amount == 0){
			print("Out of food "+f.choice);
			o.w.msgOutOfFood(o.tableNum);
			try {
				Map<String, Integer> myOrder = new HashMap<String, Integer>();
				myOrder.put(f.choice, f.capacity-f.amount-f.tempAmt);
				markets.get(0).msgHereIsOrder(myOrder, tr);
			}catch(java.lang.IndexOutOfBoundsException c){
				print("There is no market");
			}
			synchronized (orders) {orders.remove(o);}
			state = CookState.none;
//			cookGui.DoWaiting();
//			try {
//				//print("acquire");
//				animation.acquire();
//
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
		else {
			f.amount--;
			if (f.amount < f.threshold){
				try {
					Map<String, Integer> myOrder = new HashMap<String, Integer>();
					myOrder.put(f.choice, f.capacity-f.amount-f.tempAmt);
					markets.get(0).msgHereIsOrder(myOrder, tr);
				}catch(java.lang.IndexOutOfBoundsException c){
					print("There is no market");
				}
			}
			
//			cookGui.DoCooking(o.choice);
//			try {
//				//print("acquire");
//				animation.acquire();
//
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
			class cookTimerTask extends TimerTask{
				Order order = null;
				cookTimerTask(Order o){
					order=o;
				}
			
				public void run(){
					order.orderState=OrderState.cooked;
					state = CookState.none;
					//cookGui.cooking = false;
					stateChanged();
				}
			}
			time = f.cookTime;
			timer.schedule(new cookTimerTask(o) ,time);
		}
	}
	
	synchronized private void callWaiter(Order o){
		print("Waiter please come to pick up the order");
//		cookGui.DoPlating();
//		try {
//			//print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		o.w.msgOrderIsReady(o.choice, o.tableNum);
		synchronized (orders) {orders.remove(o);}
//		cookGui.DoWaiting();
//		try {
//			//print("acquire");
//			animation.acquire();
//
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public Cashier getCashier() {
		// TODO Auto-generated method stub
		return cashier;
	}

	public void setOrderStand(OrderStand orderStand) {
		// TODO Auto-generated method stub
		this.orderStand = orderStand;
	}
	
	public void setRest(TuRestaurantAgent t){
		tr = t;
	}

	public OrderStand getOrderStand(){
		return orderStand;
	}
	
	public boolean getOs(){
		return os;
	}
	
	public List<Order> getOrders(){
		return orders;
	}

	public void msgLeaveWork() {
		// TODO Auto-generated method stub
		readyToLeave = true;
	}


	
}

