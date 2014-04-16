package restaurant.cammarano.roles;

import gui.animation.role.restaurant.cammarano.CammaranoCustomerGUI;
//import restaurant.gui.RestaurantGui;

import restaurant.cammarano.interfaces.*;
import global.roles.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import restaurant.cammarano.*;
import restaurant.cammarano.roles.CammaranoHostRole.Table;

/**
 * Restaurant customer agent.
 */
public class CammaranoCustomerRole extends Role implements CammaranoCustomer {
	private String name;
	private String choice = "steak";
	
	public boolean canAffordFood;
	public boolean restaurantIsFull;
	
	private Table table;
	private ArrayList<String> menuFoods = new ArrayList<String>();
	private ArrayList<String> prohibitedFoods = new ArrayList<String>();
	private HashMap<String, Float> menu;
	private Timer timer = new Timer();
	private CammaranoCustomerGUI customerGUI;

	// agent correspondents
	private CammaranoRestaurantAgent restaurant;
	private CammaranoHostRole host;
	private CammaranoCashierRole cashier;
	private CammaranoWaiter waiter;

	public enum AgentState {
		DoingNothing,
		WaitingInRestaurant,
		BeingSeated,
		Seated,
		Choosing,
		Ordering,
		Eating,
		DoneEating,
		AskForCheck,
		AboutToPay,
		Paying,
		Leaving,
		FullRestaurant //For full restaurant only, this can easily be overwritten
	};
	
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent {
		none,
		gotHungry,
		followWaiter,
		seated,
		choosingFood,
		reorderingFood,
		reorderedFood,
		choseFood,
		gotFood,
		doneEating,
		gotCheck,
		readyToPay,
		readyToLeave,
		doneLeaving
	};
	
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CammaranoCustomerRole(){
		super();

		canAffordFood = true;
		restaurantIsFull = false;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(CammaranoHostRole host) {
		this.host = host;
	}
	
	public void setCashier(CammaranoCashierRole cashier) {
		this.cashier = cashier;
	}

	public String getCustomerName() {
		return person.getName();
	}
	
	// Messages
	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		restaurantIsFull = false;
		stateChanged();
	}
	
	public void msgRestaurantClosed() {
		print("Restaurant is closed. I'm leaving");
		state = AgentState.FullRestaurant;
		event = AgentEvent.doneLeaving;
		person.msgLeavingLocation(this);
		stateChanged();
	}
	
	@Override
	public void msgRestaurantIsFull() {
		print("Oh no, the restaurant is full");
		Random rand = new Random();
		
		int r = rand.nextInt(7);
		
		switch(r) {
			case 0:
				print("I'm leaving");
				state = AgentState.FullRestaurant;
				event = AgentEvent.doneLeaving;
				restaurantIsFull = true;
				person.msgLeavingLocation(this);
				stateChanged();
				break;
			default:
				print("I'm staying");
				stateChanged();
				break;
		}
	}
	
	
	// From waiter
	@Override
	public void msgSitAtTable(CammaranoWaiter w, Table t, HashMap<String, Float> m) {
		print("Received msgSitAtTable");
		waiter = w;
		table = t;
		menu = m;
		
		for (String s : menu.keySet()) {
			menuFoods.add(s);
		}

		event = AgentEvent.followWaiter;
		stateChanged();
	}
	
	@Override
	public void msgWhatDoYouWant() {
		print("I want to pick something.");
		event = AgentEvent.choosingFood;
		stateChanged();
	}
	
	@Override
	public void msgYouHaveToReorder(String choice) {
		print("I have to reorder!");
		event = AgentEvent.reorderingFood;
		prohibitedFoods.add(choice);
		stateChanged();
	}
	
	@Override
	public void msgHereIsYourFood(String choice) {
		print("I have gotten " + choice);
		event = AgentEvent.gotFood;
		stateChanged();
	}
	
	@Override
	public void msgHereIsYourCheck() {
		print("I got the check for my " + choice);
		event = AgentEvent.gotCheck;
		stateChanged();
	}
	
	@Override
	public void msgIAmReadyForYouToPay() {
		print("I am ready to pay!");
		event = AgentEvent.readyToPay;
		stateChanged();
	}
	
	// From the cashier
	@Override
	public void msgPaymentReceived() {
		event = AgentEvent.readyToLeave;
		stateChanged();
	}
	
	@Override
	public void msgHereIsYourChange(float change) {
		person.ChangeMoney(change);
		event = AgentEvent.readyToLeave;
		stateChanged();
	}
	
	@Override
	public void msgYouDidNotPayEnough() {
		print("OH CRAP! I NEED TO GET THE HELL OUT OF HERE!");
		event = AgentEvent.readyToLeave;
		stateChanged();
	}

	// From animation
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		
		if(!canAffordFood) {
			//customerGUI.DoExitRestaurant();
			return false;
		}
		
		if(state == AgentState.FullRestaurant) {
			if(restaurantIsFull) {
				LeaveFullRestaurant();
				return false;
			}
		}


		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Choosing;
			ChooseFood();
			return true;
		}

		if (state == AgentState.Choosing && event == AgentEvent.choosingFood){
			state = AgentState.Ordering;
			OrderFood();
			return true;
		}
		
		if (state == AgentState.Ordering && event == AgentEvent.reorderingFood){
			ReorderFood();
			return true;
		}
		
		if (state == AgentState.Ordering && event == AgentEvent.gotFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		/*
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		*/
		
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.AskForCheck;
			AskForCheck();
			return true;
		}
		
		if (state == AgentState.AskForCheck && event == AgentEvent.gotCheck){
			state = AgentState.AboutToPay;
			TellCashierIAmReady();
			return true;
		}
		
		if (state == AgentState.AboutToPay && event == AgentEvent.readyToPay){
			state = AgentState.Paying;
			PayForFood();
			return true;
		}
		
		if (state == AgentState.Paying && event == AgentEvent.readyToLeave){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions
	private void goToRestaurant() {
		Do("Going to restaurant");
		Do("I have $" + person.getMoney());
		
		if(person.getMoney() < 5.99f) {
			if(!person.getName().equalsIgnoreCase("Kristoff")) {
				print("I can not afford the food here.");
				state = AgentState.Leaving;
				event = AgentEvent.doneLeaving;
				canAffordFood = false;
				return;
			}
		}
		//customerGUI.DoGoToHost();
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		event = AgentEvent.seated; // remove when animation is called
		//customerGUI.DoGoToSeat(table);//hack; only one table
	}

	private void ChooseFood() {
		Do("Choosing food from menu.");
		timer.schedule(new TimerTask() {
			public void run() {
				do {
					Random rand = new Random();
					int random = rand.nextInt(4);
					
					choice = menuFoods.get(random);
					
					if(name == "steak") {
						choice = "steak";
					}
						
					if(name == "chicken") {
						choice = "chicken";
					}
						
					if(name == "pizza") {
						choice = "pizza";
					}
						
					if(name == "salad") {
						choice = "salad";
					}
						
				} while(menu.get(choice) > person.getMoney());
					
				OrderingFood();
			}
		},
		500);
	}
	
	private void OrderingFood() {
		print("Calling waiter!");
		waiter.msgReadyToOrder(this);
	}
	
	private void OrderFood() {
		Do("I would like " + choice);
		//customerGUI.MadeOrder(choice);
		waiter.msgHereIsMyChoice(this, choice);
	}
	
	private void ReorderFood() {
		Do("Reordering my food");
		choice = null;
		
		ArrayList<String> tempMenu = new ArrayList<String>();
		tempMenu.add("steak");
		tempMenu.add("chicken");
		tempMenu.add("pizza");
		tempMenu.add("salad");
		
		int randomUpperRange = 0;
		
		for(int cnt = 0; cnt < tempMenu.size(); cnt++) {
			for(int i = 0; i < prohibitedFoods.size(); i++) {
				if(tempMenu.get(cnt) == prohibitedFoods.get(i)) {
					tempMenu.remove(cnt);
					randomUpperRange++;
				}
			}
		}
		
		Random rand = new Random();
		int random = rand.nextInt(randomUpperRange);
		
		choice = tempMenu.get(random);
		
		if(menu.get(choice) > person.getMoney() && !person.getName().equalsIgnoreCase("Kristoff")) {
			state = AgentState.Paying;
			event = AgentEvent.readyToLeave;
			print("I can not afford the food here.");
			return;
		}
		
		//customerGUI.MadeOrder(choice);
		event = AgentEvent.reorderedFood;
		waiter.msgHereIsMyChoice(this, choice);
	}
	
	private void EatFood() {
		Do("Eating Food");
		//customerGUI.GotFood();
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
				print("Done eating, cookie=" + cookie);
				
				event = AgentEvent.doneEating;
				//customerGUI.FinishedFood();
				stateChanged();
			}
		},
		2000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void AskForCheck() {
		Do("Ask for check");
		waiter.msgReadyToPay(this);
	}
	
	private void TellCashierIAmReady() {
		Do("Tell cashier I am ready.");
		cashier.msgReadyToPay(this);
	}
	
	private void PayForFood() {
		Do("Paying for food");
		float paid;
		
		if(person.getMoney() >= menu.get(choice)) {
			paid = menu.get(choice);
		}
		
		else {
			paid = person.getMoney();
		}
		
		person.ChangeMoney(-menu.get(choice));
		
		print("I have $" + person.getMoney() + " left.");
		cashier.msgCustomerPaying(this, paid);
	}
	
	private void LeaveFullRestaurant() {
		host.msgIAmLeavingFullRestaurant(this);
		event = AgentEvent.doneLeaving;
		state = AgentState.DoingNothing;
		//customerGUI.DoExitRestaurant();
	}
	
	private void leaveTable() {
		Do("Leaving.");
		waiter.msgLeavingTable(this);
		//customerGUI.DoExitRestaurant();
		//restaurant.msgLeavingBuilding(this);
		person.msgLeavingLocation(this);
	}

	// Accessors, etc.
	public String getName() {
		return person.getName();
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGUI(CammaranoCustomerGUI g) {
		customerGUI = g;
	}

	public CammaranoCustomerGUI getGUI() {
		return customerGUI;
	}
	
	/*
	public ArrayList<Table> openTables() {
		return host.tables;
	}
	*/
}

