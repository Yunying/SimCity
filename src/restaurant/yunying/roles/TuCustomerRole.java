package restaurant.yunying.roles;

import restaurant.yunying.Food;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.PersonAgent;
import global.roles.Role;
import agent.Agent;
import interfaces.Person;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant customer agent.
 */
public class TuCustomerRole extends Role implements Customer{
	//String noMoney = "NoMoney";
	private String name;
	private int hungerLevel = 5;
	Timer timer = new Timer();
	private CustomerGui customerGui;
	int customerTableNum = 0;
	private Waiter w;
	private List<Food> myMenu = new ArrayList<Food>();
	String myChoice;
	double price;
	double cash;
	double owe = 0;
	Cashier cashier = null;
	public int waitingNumber;

	private Host host;

	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, waitingToOrder, 
		readyToOrder, makingOrder, ordered, waitingForFood, Eating, DoneEating, 
		readyForCheck,Leaving, goingToPay, Paying, workToPay, WaitingInPosition, InPosition};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, getReadyToOrder, makeDecision, ordered, 
		served, doneEating, doneLeaving, checkReceived, doneGoToCashier, finishedPayment, paymentFailed, working, WaitingInLine};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public TuCustomerRole(){
		super();
		Random rand =new Random();
		cash = 0;
	}
	
	@Override
	public void setPerson(Person p){
		super.setPerson(p);
		name = person.getName();
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setWaiter(Waiter waiter) {
		this.w = waiter;
	}
	
	public void setCashier(Cashier cashier){
		this.cashier = cashier;
	}
	
	public void setHost(Host h){
		this.host=h;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages
	
	public void msgMeetTheHost(TuHostRole h, TuCashierRole c){
		this.host = h;
		this.cashier = c;
		gotHungry();
	}
	
	public void msgPleaseLeave(){
		state = AgentState.Paying;
		event = AgentEvent.finishedPayment;
		stateChanged();
	}

	public void gotHungry() {
		//print("I'm hungry");
		myMenu = new ArrayList<Food>();
		Random rand = new Random();
		if (name.equals("Cheap")){
			cash = 6;
		}
		else if (name.equals("Zero")){
			cash = 0;
		}
		else {
			cash = cash + rand.nextInt(20) +20;
		}
		print("I'm hungry. I have $" + cash + ". I owe $" + owe);
		
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	
	public void msgTablesAreFull(){
		Random rand = new Random();
		int wait = rand.nextInt(2);
		if(wait<1){
			print("Tables are full. Do not want to wait");
			host.msgImLeaving(this);
			state = AgentState.Paying;
			event = AgentEvent.finishedPayment;
			stateChanged();
		}
	}
	
	public void msgWhatWouldYouLike(){
		//print ("I would like to eat...");
		event = AgentEvent.makeDecision;
		stateChanged();
	}
	
	public void msgHereIsYourFood(String choice){
		//print("Thank you I got my food");
		event = AgentEvent.served;
		stateChanged();
	}
	
	public void msgPleaseReorder(String choice){
		for (Food f:myMenu){
			if (f.getName().equals(choice)){
				int index = myMenu.indexOf(f);
				myMenu.remove(index);
				break;
			}
		}
		//System.out.println(myMenu.size());
		state = AgentState.readyToOrder;
		event = AgentEvent.makeDecision;

		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToCashier(){
		event = AgentEvent.doneGoToCashier;
		stateChanged();
	}
	
	public void msgHereIsCheck(double amount){
		print("check received");
		event = AgentEvent.checkReceived;
		price = amount;
		stateChanged();
	}
	
	public void msgHereIsChange(double change){
		print("receive change");
		event = AgentEvent.finishedPayment;
		cash += change;
		stateChanged();
	}
	
	public void msgYouNeedMoreMoney(double owe){
		event = AgentEvent.paymentFailed;
		this.owe = owe;
		stateChanged();
	}
	
	public void msgHereIsYourNumber(int num){
		waitingNumber = num;
		event = AgentEvent.WaitingInLine;
		print("receive number "+num+" Go to position");
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//print("Customer Scheduler");
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			print("1");
			goToRestaurant();
			return true;
		}
		
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.WaitingInLine){
			state = AgentState.WaitingInPosition;
			print("2");
			adjustPosition();
			return true;
		}
		
		if (state == AgentState.InPosition && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			print("3");
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated ){
			state = AgentState.waitingToOrder;
			//print("waitingToOrder");
			waitingToOrder();
			return true;
		}
		if (state == AgentState.waitingToOrder && event == AgentEvent.getReadyToOrder){
			state = AgentState.readyToOrder;
			//print("readyToOrder");
			print("3.5");
			makeOrder();
			return true;
		}
		
		if (state == AgentState.readyToOrder && event == AgentEvent.makeDecision){
			state = AgentState.makingOrder;
			//print("Tell waiter the order");
			print("4");
			tellWaiterTheOrder();
			return true;
		}

		if (state == AgentState.makingOrder && event == AgentEvent.served){
			state = AgentState.Eating;
			//print("begin eating");
			print("5");
			EatFood();
			return true;
		}
		
		//START TO PAY ACTION
		
		if (state == AgentState.DoneEating && event == AgentEvent.checkReceived){
			state = AgentState.goingToPay;
			leaveTable();
			return true;
		}
		
		if (state == AgentState.goingToPay && event == AgentEvent.doneGoToCashier){
			state = AgentState.Paying;
			payCheck();
			return true;
		}
		
		if (state == AgentState.Paying && event == AgentEvent.finishedPayment){
			state = AgentState.Leaving;
			leaveRestaurant();
			return true;
		}
		
		if (state == AgentState.Paying && event == AgentEvent.paymentFailed){
			state = AgentState.workToPay;
			workToPay();
			return true;
		}
		
		
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			print("DoingNothing");
			//no action
			this.getPerson().AddTaskGoHome();
			return true;
		}
		
		//print("return false");
		return false;
	}

	// Actions
	private void adjustPosition(){
		print("Do adjustPosition");
		//customerGui.DoAdjustPosition(waitingNumber);
		state = AgentState.InPosition;
		stateChanged();
	}
	
	public void msgAnimationFinishedAdjusting(){
		state = AgentState.InPosition;
		print("Animation finished");
		stateChanged();
	}
	
	private void workToPay(){
		event = AgentEvent.finishedPayment;
		state = AgentState.Paying;
		stateChanged();
		//customerGui.DoGoToCook(owe);	
	}
	
	public void doneWorking(){
		state = AgentState.Paying;
		
		cash = 0;
		stateChanged();
	}
	
	private void payCheck(){
		print("Pay check");
		cashier.msgHereIsPayment(this, customerTableNum, cash, owe);
		cash = 0;
	}

	private void goToRestaurant() {
		//customerGui.DoGoToRestaurant();
		host.msgIWantFood(this);
	}
	
	public void msgAnimationFinishedGoToRestaurant(){
		host.msgIWantFood(this);
	}

	private void SitDown() {
		print("being seated");
		//customerGui.DoGoToSeat(customerTableNum);//hack; only one table
		event = AgentEvent.seated;
		stateChanged();
	}
	
	private void waitingToOrder(){
//		timer.schedule(new TimerTask() {
//			public void run() {
//				//print("Waiting to order");
//				event = AgentEvent.getReadyToOrder;
//				//isHungry = false;
//				stateChanged();
//			}
//		},
//				100);
		event = AgentEvent.getReadyToOrder;
		stateChanged();
	}
	
	private void makeOrder(){
		//print("Ready to order");
		w.msgImReadyToOrder(this);
	}
	
	Semaphore order = new Semaphore(0,true);
	private void tellWaiterTheOrder(){
		//print("Tell waiter the order");
		myChoice = orderFood(myMenu);
		if (myChoice == "Nothing"){
			w.msgLeavingTable(this);
			state = AgentState.Paying;
			event = AgentEvent.finishedPayment;
			stateChanged();
			return;
		}
		else{
			//customerGui.showChoice(myChoice);
			w.msgHereIsMyChoice(this, myChoice);
		}
	}
	
	private String orderFood(List<Food> m){
		if (!name.equals("NoMoney")){
			int i=0;
			//System.out.println(cash);
			while (cash<m.get(i).getPrice() && i<m.size()){
				//System.out.println(m.get(i).price);
				i++;
				if (i==m.size()){
					break;
				}
			}
			if (i==m.size()){
				print("Cannot afford any food");
				return "Nothing";
			}
			else {
				Random rand = new Random();
				int index;
				if (m.size()-i-1 == 0){
					index = 0;
				}
				else {
					index = rand.nextInt(m.size()-i);
				}
				//print("index: " + index + " i: "+ i);
				//print("return here");
				return m.get(index+i).getName();
			}
		}
		else {
			print("No Money");
			int i=m.size();
			Random rand = new Random();
			int index = rand.nextInt(i);
			return m.get(index).getName();
		}
		
	}

	private void EatFood() {
		print("Eating Food");
		//customerGui.getFood(myChoice);
		w.msgImReadyForCheck(this);
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				state = AgentState.DoneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		1000);
	}

	private void leaveTable() {
		Do("Going to pay.");
		w.msgLeavingTable(this);
		//customerGui.DoGoToCashier();
		event = AgentEvent.doneGoToCashier;
		stateChanged();
		
		
	}
	
	private void leaveRestaurant(){
		print("leave restaurant");
		//customerGui.DoExitRestaurant();
		this.getPerson().ChangeMoney((float)(0-cash));
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;

	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}


	public void msgFollowMeToTable(Waiter waiter, int table,
			List<Food> menu) {
		w=waiter;
		event = AgentEvent.followHost;
		customerTableNum = table;
		for (int i=0; i<menu.size();i++){
			myMenu.add(new Food(menu.get(i).getName(),menu.get(i).getPrice()));
		}
		print("Follow waiter " + w.getName() + " to table");
		stateChanged();	
	}


	@Override
	public int getWaitingNumber() {
		// TODO Auto-generated method stub
		return waitingNumber;
	}
}

