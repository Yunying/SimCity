package restaurant.redland.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import restaurant.redland.interfaces.*;
import global.roles.Role;

public class RedlandCustomerRole extends Role implements RedlandCustomer{
	
	/********** Data **********/
	
	private String name;
	//private CustomerGui customerGui;
	Timer timer = new Timer();
	Random generator = new Random();
	int myTable;
	float myCash;
	private int hungerLevel;

	private List<String> myMenu = new ArrayList<String>();
	private RedlandWaiter waiter;
	private RedlandHost host;
	private RedlandCashier cashier;

	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, ReadyToOrder, Ordered, Eating, DoneEating, Leaving, PayingBill};
	private AgentState state = AgentState.DoingNothing;
	
	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, finishedLookingAtMenu, askedToOrder, gotFood, doneEating, doneLeaving,
		reOrder, paidBill};
	AgentEvent event = AgentEvent.none;

	
	public RedlandCustomerRole(){
		super();
		
		//this.name = name;
		this.hungerLevel = 8 - generator.nextInt( 5 );
		this.myCash = 15;//how should we determine how much cash they have?
		this.state = AgentState.WaitingInRestaurant;
	}

	public void setWaiter( RedlandWaiter waiter) {
		this.waiter = waiter;
	}
	
	public void setHost( RedlandHost host){
		this.host = host;
	}
	
	
	
	/********** Messages **********/

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMeToTable( RedlandWaiter waiter, int table, ArrayList<String> menu) {//message 3
		this.waiter = waiter;
		myTable = table;	//TODO: pass table coordinates
		myMenu = menu;
		//print("Received msgSitAtTable");
		event = AgentEvent.followWaiter;
		stateChanged();
	}

	public void msgWhatWouldYouLike(){//message 5
		event = AgentEvent.askedToOrder;
		stateChanged();
	}
	
	public void msgHereIsYourFood(){//message 9
		//print( "Got my food" );
		event = AgentEvent.gotFood;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from CustomerGui
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		//from CustomerGui
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	//message from waiter with bill, informs customer of cashier
	public void msgHereIsYourBill( RedlandCashier cashier ){
		//what does customer need???
		//event = AgentEvent.gotBill;
		this.cashier = cashier;
		stateChanged();
	}
	
	//message from cashier with change from bill
	public void msgHereIsYourChange( float change ){
		this.myCash = change;
		this.person.ChangeMoney( change );//update persons money
		event = AgentEvent.paidBill;
		stateChanged();
	}
	
	//Non-normative scenario
		//need to reorder, either out of this order or... (can't afford?)
	public void msgReOrder( String lastOrder ){
		event = AgentEvent.reOrder;
		//Make sure the customer does not order this same item
		myMenu.remove(lastOrder);
		stateChanged();
	}



	/********** Scheduler **********/
	
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			GoToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Seated;
			TellWaiterSeated();
			return true;
		}
		if (state == AgentState.Seated && event == AgentEvent.finishedLookingAtMenu){
			state = AgentState.ReadyToOrder;
			ReadyToOrder();
			return true;
		}
		if (state == AgentState.ReadyToOrder && event == AgentEvent.askedToOrder){
			state = AgentState.Ordered;
			Order();
			return true;
		}
		if (state == AgentState.Ordered && event == AgentEvent.gotFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.PayingBill;
			PayBill();
			return true;
		}
		if (state == AgentState.PayingBill && event == AgentEvent.paidBill){
			state = AgentState.Leaving;
			LeaveTable();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		//Non-normative
		if (state == AgentState.Ordered && event == AgentEvent.reOrder){
			Order();
			return true;
		}
		
		return false;
	}

	// Actions

	private void GoToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		DoGoToSeat();
	}

	private void TellWaiterSeated(){
		//print("Seated, looking at menu");
		this.waiter.msgImAtTable(this);
		timer.schedule(new TimerTask() {
			public void run() {
				//print("Finished looking at menu");
				event = AgentEvent.finishedLookingAtMenu;
				stateChanged();
			}
		},
		5000);//this is a magic number, how long does he look at menu?
	}
	
	private void ReadyToOrder(){
		//release semaphore holding waiter at table
		//print("I'm ready to order");
		waiter.msgImReadyToOrder(this);
	}
	
	private void Order(){
		//do something to pick food choice
		String myChoice = "No Choice";
		int randNum = generator.nextInt(myMenu.size());//generate number between 0 and menuSize-1
		myChoice = myMenu.get( randNum );
		//print( "My choice is " + myChoice );
		waiter.msgHereIsMyChoice(this, myChoice);
	}
	
	private void EatFood() {
		Do("Eating Food");
		timer.schedule(new TimerTask() {
			public void run() {
				//print("Done eating");
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		getHungerLevel() * 1000);//how long to wait before running task
	}

	private void LeaveTable() {
		Do("Leaving");
		waiter.msgDoneEating(this);
		DoLeaveRestaurant();
	}

	private void PayBill(){
		DoPayBill( cashier );
		cashier.msgPayBill( this, this.myCash );
	}
	
	
	/********** GUI Calls **********/
	private void DoLeaveRestaurant(){
		//customerGui.DoExitRestaurant();
		this.msgAnimationFinishedLeaveRestaurant();
	}

	private void DoGoToSeat(){
		//customerGui.DoGoToSeat( myTable );
		this.msgAnimationFinishedGoToSeat();//to trigger state event
	}
	
	private void DoPayBill( RedlandCashier cashier ){
		//customerGui.DoGoToCashier( ... );
	}

	/********** Utilities **********/

	public String getName() {
		return name;
	}
	
	public AgentState getState(){
		return state;
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

	/*GUI STUFF
	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	*/
}
