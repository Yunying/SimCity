package restaurant.mcneal.roles;

import global.roles.Role;
import interfaces.Building;
import interfaces.Employee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;













import market.interfaces.Market;
import restaurant.mcneal.McNealFood;
import restaurant.mcneal.McNealRestaurantAgent;
import restaurant.mcneal.interfaces.McNealCook;
import restaurant.mcneal.interfaces.McNealRestaurant;
import restaurant.mcneal.interfaces.McNealWaiter;
import restaurant.mcneal.roles.McNealHostRole.Table;


public class McNealCookRole extends Role implements McNealCook, Employee{

	private McNealWaiter waiter;
	
	private boolean inventoryout = false;
	private boolean start = false;
	private boolean paycheckhere = false;
	private List<McNealFood> foods = Collections.synchronizedList(new ArrayList<McNealFood>());
	public List<Grill> grills;
	Order order;
	McNealFood f;
	public McNealRestaurant building;
	
	public List<Market> marketList = Collections.synchronizedList(new ArrayList<Market>()); 
	//public Market m;
	Timer timer = new Timer();
	public McNealCookRole() {
		super();

		foods.add(new McNealFood("Steak"));
		foods.add(new McNealFood("Soup"));
		foods.add(new McNealFood("Pizza"));
		foods.add(new McNealFood("Salad"));
		grills = Collections.synchronizedList(new ArrayList<Grill>(3));
		synchronized(grills) {
			for (int ix = 1; ix <= 3; ix++) {
				grills.add(new Grill(ix));//how you add to a collections

			}
		}

		grills.get(0).setPos(50, 300);
		grills.get(1).setPos(100, 300);
		grills.get(2).setPos(150,300);
	}

	public void setRestaurantAgent(McNealRestaurant building) {
		this.building = building;
	}

	public McNealFood getFood() {
		return f;
	}
	/*	public void setMarket(McNealMarket m){
		this.m = m;
	}
	public void setMarketList(List<McNealMarket> markets) {
		this.marketList = markets;
	}
	public McNealMarket getMarket() {
		return m;
	}
	 */
	public enum orderStates {doingNothing, pending, cooking, cooked};
	public class Grill {
		Order occupiedBy;
		int grillNum; 
		int pos;
		Grill(int grillNum) {
			this.grillNum =	grillNum;
			//System.out.println("table number " + tableNumber);
		}
		public int getNumber() {
			return grillNum;
		}
		public int getPos() {
			return pos;
		}
		public void setPos(int grillx, int grilly) {
			pos = grillx;
		}
		void setOccupant(Order order) {
			occupiedBy = order;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Order getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "order " + grillNum;
		}
	}

	public List<Order> pendingOrders = Collections.synchronizedList(new ArrayList <Order>());

	//Messages
	public void msgHereisAnOrder(McNealWaiter wa, String choice, Table t) { System.out.println("new order for table "
			+ t.getNumber());

	waiter = wa;
	pendingOrders.add(new Order(wa, choice, t));
	synchronized(pendingOrders) {
		for(int i = 0; i < pendingOrders.size();i++) {
			if(pendingOrders.get(i).getTable().getNumber() == t.getNumber()){
				//System.err.print("NEW order with " + pendingOrders.get(i).getChoice() + " " + pendingOrders.get(i).getStringChoice());
				pendingOrders.get(i).state = orderStates.pending;
				stateChanged();
			}
		}
	}
	}
	public void msgFoodisDone(Order o) {//sends a message to himself
		//pendingOrders.remove(o);

		o.state = orderStates.cooked;
		stateChanged();
	}
	public void msgHeresMoreFood() {
		print("Recieved more food from market");
		synchronized(foods) {
			for(int i = 0; i < foods.size(); i++) {
				foods.get(i).increaseQuant(foods.get(i).getStringChoice());
				System.out.println("yay now food size is " + foods.get(i).getQuant(foods.get(i).getStringChoice()));
				stateChanged();
			}
		}
	}
	public void msgInventoryOut(String food){
		print("No more Market food. Restaurant will self destruct in T Minus 10 hours. Everyone will stay where they are");
		inventoryout = true;
		stateChanged();
		/*synchronized(foods) {
			for(int i = 0; i < foods.size(); i++) {
				if(foods.get(i).getStringChoice().equals(food)) {
					inventoryout = true;
					stateChanged();
				}
			}
		}*/

	}
	@Override
	public void msgStopWorkingGoHome() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		person.ChangeMoney(paycheck);
		paycheckhere = true;
		
	}

	@Override
	public boolean hasReceivedPaycheck() {
		// TODO Auto-generated method stub
		return false;
	}
	// Actions
	public void CookFood(final Order o) {
		//if(!paused) {

		final McNealFood f = foods.get(o.getChoice() - 1);System.out.println("get choice " + o.getChoice());
		System.out.println("the food is " + f.getStringChoice() + " with " + f.getQuant(f.getStringChoice()));
		if(f.getQuant(f.getStringChoice()) == 0) { System.out.println("out of that young food");
		pendingOrders.remove(o);
		o.getWaiter().msgOutOFFood(f, o.getTable());	
		/*
			print("Out of food "+f.getStringChoice());
			pendingOrders.remove(o);
			o.getWaiter().msgOutOFFood(f, o.getTable());
			try {
				HashMap<String, Integer> ordermap = new HashMap<String, Integer>();
				ordermap.put(f.getStringChoice(), 2);
				marketList.get(0).msgHereIsOrder(ordermap, building );
			}catch(java.lang.IndexOutOfBoundsException c){
				print("There is no market");
			}/*
			synchronized (orders) {orders.remove(o);}
			state = CookState.none;
			cookGui.DoWaiting();
			try {
				//print("acquire");
				animation.acquire();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		else {
			f.amount--;
			if (f.amount < f.threshold){
				try {
					markets.get(0).msgNeedMoreFood(this, f.getStringChoice());
				}catch(java.lang.IndexOutOfBoundsException c){
					print("There is no market");
				}
			}*/

		/*
			boolean fullfilledorder = false;
			synchronized(foods) {
				for(int i = 0; i < foods.size(); i++){
					if(foods.get(i).getQuant(foods.get(i).getStringChoice()) <= 2) {
						synchronized(marketList) {for(int j = 0; j < marketList.size(); i++) {
							if(marketList.get(j).getInv().get(0).getAmt() > 0 ||  marketList.get(j).getInv().get(1).getAmt() > 0 
									|| marketList.get(j).getInv().get(2).getAmt() > 0 || marketList.get(j).getInv().get(3).getAmt() > 0  ) {

								if(!fullfilledorder){
									marketList.get(j).msgNeedMoreFood(this, foods.get(i).getStringChoice());

									fullfilledorder = true;
									break;
								}
							}
						}
						if(!fullfilledorder){
							Do("Market Cant fulfill order");
						}
						}
					}
				}
			}*/
		//msgmarket
		/*
			for(int i = 0; i < marketList.size(); i++) {	Do("Asking Market for more food " + (i+1) + "of 3");

			for(int j = 0; j < marketList.get(i).getInv().size(); j++ ) {
					if(marketList.get(i).getInv().get(j).getFood().equals(f.getStringChoice())) {
						if( marketList.get(i).getInv().get(0).getAmt() >= 2){
							Do("OH JOY IM OUT BUT LUCKILY MARKET " + (i +1) + " HAS MORE FOOD");
							marketList.get(i).msgNeedMoreFood(this, f.getStringChoice());

							break;
						}
						else {
							Do("WE ARE OUT OF THIS FOOD MAN :(");
							break;
						}
					}

				}

			}

		 */

		}

		else {
			System.err.println("ffood quatn : " + f.getQuant(f.getStringChoice()));
			if(f.getQuant(f.getStringChoice()) < 3) { 
				boolean ful = false;
				boolean hasit = false;
				/*for(int j = 0; j < marketList.size(); j++) {

						if(marketList.get(j).getInv().get(0).getAmt() > 0 &&  marketList.get(j).getInv().get(1).getAmt() > 0 
								&& marketList.get(j).getInv().get(2).getAmt() > 0 && marketList.get(j).getInv().get(3).getAmt() > 0  ) {
							hasit =true;

							if(!ful){
								marketList.get(j).msgNeedMoreFood(this, foods.get(j).getStringChoice());

								ful = true;
								break;
							}
						}
					}
					if(!hasit) {

						boolean gg = false;
						/*for(int j = 0; j < marketList.size(); j++) {
							if(marketList.get(j).getInv().get(0).getAmt() > 0 || marketList.get(j).getInv().get(1).getAmt() > 0 
									|| marketList.get(j).getInv().get(2).getAmt() > 0 || marketList.get(j).getInv().get(3).getAmt() > 0  ) {
								if(!gg) {
									for(int i = 0; i < marketList.get(j).getInv().size(); i++ ){

										//System.out.println("COUNT for " + marketList.get(j).getInv().get(i).getFood() + " " + 
										//	marketList.get(j).getInv().get(i).getAmt());
										if(marketList.get(j).getInv().get(i).getAmt() != 0) {
											if(marketList.get(j).getInv().get(i).getFood().equals(f.getStringChoice())) {
												marketList.get(j).msgNeedMoreFood(this, foods.get(j).getStringChoice());
												gg = true;
												break;
											}
											else {
												marketList.get(j).msgNeedMoreFood(this, foods.get(j).getStringChoice());
											}
										}


									}



								}

							}
						}

					}*/

				if(!ful){
					Do("Market Cant fulfill order");
				}
			}


			/*if(f.getQuant(f.getStringChoice()) < 3) { 
					boolean mofood = false;
					//System.out.println(f.getQuant(f.getStringChoice()));
				for(int i = 0; i < marketList.size(); i++) {	Do("Asking Market " + (i+1) + " of 3 for more food");
					for(int j = 0; j < marketList.get(i).getInv().size(); j++ ) { 
						if(marketList.get(i).getInv().get(j).getFood().equals(f.getStringChoice())) {
							if( marketList.get(i).getInv().get(j).getAmt() >= 2){
								Do("OH MARKET " + (i+1) + "HAS MORE OF THIS FOOD");
								marketList.get(i).msgNeedMoreFood(this, f.getStringChoice());
								mofood = true;
								break;
							}
							else {
								Do("LAST BATCH CUZ MARKET DOESNT HAVE FOOD");
								break;
							}




						}
					}

				}

				}*/
			f.decreaseQuant(f.getStringChoice());
			System.out.println(f.getStringChoice() + " " + f.getQuant(f.getStringChoice()) + " left");
			System.out.println("Cooking Food choice "+ o.getStringChoice());
			for(int i = 0; i < grills.size(); i++) {
				if(!grills.get(i).isOccupied()) {
					grills.get(i).setOccupant(o);
					f.setFoodLocation(grills.get(i).getPos());
					f.onDaGrill(true);
				}
			}
			//This next complicated line creates and starts a timer thread.
			//We schedule a deadline of getHungerLevel()*1000 milliseconds.
			//When that time elapses, it will call back to the run routine
			//located in the anonymous class created right there inline:
			//TimerTask is an interface that we implement right there inline.
			//Since Java does not all us to pass functions, only objects.
			//So, we use Java syntactic mechanism to create an
			//anonymous inner class that has the public method run() in it.
			timer.schedule(new TimerTask() {
				Object cookie = 1;
				public void run() {
					print(o.getTable() + " Done cooking, cookie=" + cookie);
					//event = AgentEvent.doneEating;
					//isHungry = false;

					o.state = orderStates.cooked;
					f.onDaGrill(false);
					stateChanged();


				}
			},
			f.getCookTime() );
		}
		//}
	}
	public void FoodDone(Order o) { 

		Do("please take food " + o.getStringChoice() + ", waiter " + o.getWaiter().getName() + " and take it to table " + o.getTable().getNumber());


		o.getWaiter().msgOrderIsReady(o.getWaiter(), o.getStringChoice(),o.getTable());
		pendingOrders.remove(o);
	}

	public void NoMoreItem(McNealFood f, McNealWaiterRole w) {

		Do("We are out of food " + f.getFood());
		//w.msgOutOfFood(f);
	}


	public void checkInventory() {
		/*
			boolean fullfilledorder = false;
			for(int i = 0; i < foods.size(); i++){
				if(foods.get(i).getQuant(foods.get(i).getStringChoice()) <= 2) {
					for(int j = 0; j < marketList.size(); i++) {
						if(marketList.get(i).getInv().get(0).getAmt() > 0 ||  marketList.get(i).getInv().get(1).getAmt() > 0 
								|| marketList.get(i).getInv().get(2).getAmt() > 0 || marketList.get(i).getInv().get(3).getAmt() > 0  ) {

							if(!fullfilledorder){
								marketList.get(j).msgNeedMoreFood(this, foods.get(i).getStringChoice());
								Do("Market Can fulfill the order");
								fullfilledorder = true;
								break;
							}
						}
					}
					if(!fullfilledorder){
						Do("Market Cant fulfill order");
					}
				}
			}
		 */

	}
	public void tellWaiterBadNews() {
		Do("Aww we are out of food. So sorry");


	}
	public void leave() {
		Do("Chuckin up the deuces. Leaving");
		building.msgLeavingBuilding(this, building.getAssets());
		person.msgLeavingLocation(this);
		
	}
	//Scheduler
	public boolean pickAndExecuteAnAction() {
		if(start) {
			checkInventory();
			start = false;
			return true;
		}
		if(inventoryout) {
			inventoryout = false;
			tellWaiterBadNews();

			return true;
		}

		for(Order o : pendingOrders) {
			if( o.state == orderStates.pending ) { 
				System.out.println("order pending to b cookd " + o.getStringChoice());
				//if(o.getChoice() == not in map foodmap)
				//noMoreItem( food, o.getWaiter());
				o.state = orderStates.cooking;


				CookFood(o); 
				//pendingOrders.remove(pendingOrders.get(0));

				return true;
			}
		}
		for(Order o : pendingOrders) {
			if(o.state == orderStates.cooked) { 
				o.state = orderStates.doingNothing;
				FoodDone(o);
				//pendingOrders.remove(o);
				return true;
			}


		}
		if(paycheckhere == true) {
			paycheckhere = false;
			leave();
			return true;
		}

		return false;
	}

	@Override
	public void msgAtBuilding(Building building) {
		// TODO Auto-generated method stub
		
	}



}
