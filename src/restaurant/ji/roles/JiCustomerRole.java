package restaurant.ji.roles;

import restaurant.ji.Menu;
import restaurant.ji.interfaces.*;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import interfaces.Building;
import interfaces.Person;

import java.util.*;

/**
 * Restaurant customer agent.
 */
public class JiCustomerRole extends Role implements JiCustomer {
	
	private int hungerLevel = 5;        // determines length of meal
	private int sitX; private int sitY;
	private Timer timer = new Timer();
	//private CustomerGui customerGui;
	
	private Menu menu;
	private String choice;
	private Float money;
	private Float bill;
	private int timeToDecide;
	private boolean intendToPay; // whether the customer will order based on whether he can afford it or not
	
	// agent correspondents
	private JiHost host;
	private JiWaiter waiter;
	private JiCashier cashier;
	private JiRestaurant restaurant;
	
	private List<String> unavailableEntrees = new ArrayList<String>();

	private enum State {
		arrivedAtJiRestaurant, WAITING, DECIDINGTOWAITORLEAVE, SITTING, ORDERING, 
		SERVED, EATING, GOTCHECK, LEAVINGFULL, LEAVINGHUNGRY, approachingHost, none
		};
	private State state;

	public enum AgentEvent {
		None, GotHungry, FollowHost, LeftRestaurant, DoneEating, WentToRestaurant
		};
	AgentEvent event;

	/**
	 * Constructor for CustomerAgent class
	 */

	public JiCustomerRole() {
		super();
		// naming hacks
		//this.intendToPay = name.endsWith("!")? false : true;

//		this.money = name.startsWith("$")? 
//				(intendToPay? 
//						Float.valueOf(name.substring(1, name.length())) 
//						: Float.valueOf(name.substring(1, name.length()-1))
//				) : Float.valueOf(new DecimalFormat("#.##").format(new Random().nextFloat() * 25));


		timeToDecide = new Random().nextInt(3000) + 2000;
		bill = null;
		state = State.WAITING;
		event = AgentEvent.None;
	}
	
	//**********Messages************//
	@Override
	public void msgAtBuilding(Building rest){
		log.add(new LoggedEvent("Received msgAtBuilding"));
		this.restaurant = (JiRestaurant) rest;
		state = State.WAITING;
		event = AgentEvent.GotHungry;
		stateChanged();
	}
	
	@Override
	public void msgWeAreClosed(){
		state = State.LEAVINGHUNGRY;
		stateChanged();
	}
	
	@Override
	public void msgDirectingYouToHost(JiHost host){
		this.host = host;
		state = State.approachingHost;
		stateChanged();
	}
	
	@Override
	public void gotHungry() {
		//print("I'm hungry");
		event = AgentEvent.GotHungry;
		stateChanged();
	}
	
	@Override
	public void msgTablesAreFull() {
		state = State.DECIDINGTOWAITORLEAVE;
		stateChanged();
	}

	@Override
	public void msgFollowMe(JiWaiter w, Menu m, int x, int y) {
		this.waiter = w;
		this.menu = m;
		state = State.SITTING;
		sitX = x; sitY = y;
		stateChanged();
	}
	
	@Override
	public void msgWhatWouldYouLike(){
		state = State.ORDERING;
		stateChanged();
	}
	
	@Override
	public void msgWeAreOut(String choice){
		unavailableEntrees.add(choice);
		state = State.SITTING;
		stateChanged();
	}
	
	@Override
	public void msgHeresYourFood(String c, JiCashier cashier){
		state = State.SERVED;
		this.cashier = cashier;
		stateChanged();
	}

	@Override
	public void msgHereIsCheck(Float amount){
		state = State.GOTCHECK;
		bill  = (bill == null)? amount : bill+amount;
		stateChanged();
	}
	
	@Override
	public void msgHereIsChange(Float change){
		state = State.LEAVINGFULL;
		person.ChangeMoney(change);
		this.money += change;
		stateChanged();
	}
	
	@Override
	public void msgAnimationFinishedGoToSeat() { //from animation
		state = State.WAITING;
		stateChanged();
	}
	@Override
	public void msgAnimationFinishedLeaveRestaurant() { //from animation
		event = AgentEvent.LeftRestaurant;
		stateChanged();
	}

	//**********Scheduler************//
	@Override
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == State.WAITING && event == AgentEvent.GotHungry ){
			goToRestaurant();
			return true;
		}
		if (state == State.approachingHost){
			approachHost();
			return true;
		}
		if (state == State.DECIDINGTOWAITORLEAVE){
			DecideToWaitOrLeave();
			return true;
		}
		if (state == State.SITTING){
			SitDownAndDecideOnFood();
			return true;
		}
		if (state == State.ORDERING){
			Order();
			return true;
		}
		if (state == State.SERVED){
			EatFood();
			return true;
		}
		if (state == State.EATING && event == AgentEvent.DoneEating){			
			AskForCheck();
			return true;
		}
		if (state == State.GOTCHECK){
			PayCheck();
			return true;
		}
		if (state == State.LEAVINGFULL){
			leave();
			return true;
		}
		if (state == State.LEAVINGHUNGRY){
			findSomewhereElseToEat();
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		state = State.none;
		Do("Hungry! Going to restaurant");
		restaurant.msgAtLocation(person, this, null);
		event = AgentEvent.WentToRestaurant;
	}
	
	private void approachHost(){
		state = State.none;
		host.msgTableForOne(this);
	}
	
	private void DecideToWaitOrLeave(){
		state = State.WAITING;
		if (new Random().nextBoolean()){ // if 1, wait
			Do("Ok, I'll wait");
			host.msgIWillWait(this);
		}
		else{
			Do("I don't want to wait");
			host.msgIDontWantToWait(this);
			event = AgentEvent.None;
			restaurant.msgLeavingAsCustomer(this);
			//customerGui.DoExitRestaurant();
		}
	}

	private void SitDownAndDecideOnFood() {
		state = State.WAITING;
		//customerGui.DoGoToSeat(sitX, sitY);
		//customerGui.setChoiceText(" ... ", true);
		
		//if (menu.contains(name)){  choice = name; } // naming hack -- order your name
		//else 
		if (intendToPay){
			for (String entree : menu.keySet()){
				if (menu.getPrice(entree) > getMoney())
					unavailableEntrees.add(entree);
			}
			for (String entree : unavailableEntrees){
				menu.removeOption(entree);
			}
			//print("Options for my $" + getMoney() + ": " + menu.keySet());
		}
		
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (menu.keySet().isEmpty()){
					state = State.LEAVINGHUNGRY;
					print("Can't afford anything with $" + getMoney());
				}
				else{ 
					state = State.WAITING;
					choice = choice != null? choice : new ArrayList<String>(menu.keySet()).get( new Random().nextInt(menu.keySet().size()) ); // get random choice from the map
					//choice = new ArrayList<String>(menu.keySet()).get(0) // uncomment for hack to get customer to order salad
					FlagWaiter();
				}
				stateChanged();
			}
		}, timeToDecide);
	}
	
	
	private void FlagWaiter(){
		//customerGui.setChoiceText(" ! " , true);
		print(waiter.getName() + ", I'm ready to order!");
		waiter.msgImReadyToOrder(this);
	}
	
	private void Order(){
		state = State.WAITING;
		print("I want " + choice);
		waiter.msgHeresMyOrder(this, choice);
		//customerGui.setChoiceText(choice.substring(0, 2), false);
	}

	private void EatFood() {
		//customerGui.setChoiceText(choice.substring(0,2), true);
		state = State.EATING;
		print("Eating");
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
			@Override
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.DoneEating;
				stateChanged();
			}
		},
		getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void AskForCheck() {
		Do("Can I have my check?");
		waiter.msgIWantMyCheck(this);
		state = State.WAITING;
	}
	
	private void PayCheck() {
		state = State.LEAVINGFULL;
		if (getMoney() >= bill){
			Do("Paying check... $" + bill + " with my available $" + getMoney());
			cashier.msgPayingCheck(money, this);
			setMoney(getMoney() - bill);
			bill = 0f;
			Do("Now have $" + (getMoney() - bill) + ". Unpaid bill: $" + bill);
		}
		else{
			print("I can't afford this -- only have $" + getMoney());
			cashier.msgICantPay(this);
		}
	}

	private void leave() {
		Do("Done eating thx bye");
		state = State.WAITING;
		//customerGui.clearChoicetext();
		if (waiter!= null)
			waiter.msgDoneEatingAndLeaving(this);
		restaurant.msgLeavingAsCustomer(this);
		//customerGui.DoExitRestaurant();
		unavailableEntrees.removeAll(unavailableEntrees);
		person.msgLeavingLocation(this);
	}
	
	private void findSomewhereElseToEat(){
		state = State.WAITING;
		restaurant.msgLeavingAsCustomer(this);
		person.msgLeavingLocation(this);
		person.AddTaskGetFood();
	}

	//**********Accessors************//
	@Override public Person getPerson() { return person; }
	@Override public void setPerson(Person p){ person = p; money = p.getMoney();}
	@Override public String getName() {return person.getName();}
	public float getMoney() {return person.getMoney();}
	public void setMoney(float money) { 
		this.money = money; 
		person.setMoney(money);
	}
	public boolean hasUnsettledBill() {return (bill != null && bill > 0.0);}
	public JiWaiter getWaiter() {	return waiter;}	
	public int getHungerLevel() {return hungerLevel;}
	public void setHungerLevel(int hungerLevel) {this.hungerLevel = hungerLevel;}

//	public void setGui(CustomerGui g) { customerGui = g; }
//	public CustomerGui getGui() { return customerGui; }
	
	public void setCashier(JiCashier cashier) { this.cashier = cashier; }
	public void setHost(JiHost host) { this.host = host; }


}

