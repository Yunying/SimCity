package restaurant.mcneal.roles;


import restaurant.mcneal.McNealCheck;
import restaurant.mcneal.McNealFood;
import gui.animation.role.restaurant.mcneal.McNealCustomerGUI;
import restaurant.mcneal.interfaces.McNealCashier;
import restaurant.mcneal.interfaces.McNealCook;
import restaurant.mcneal.interfaces.McNealCustomer;
import restaurant.mcneal.interfaces.McNealHost;
import restaurant.mcneal.interfaces.McNealRestaurant;
import restaurant.mcneal.interfaces.McNealWaiter;
import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.mcneal.roles.WaiterAgent.Menu;
import agent.Agent;
import global.roles.Role;







import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class McNealCustomerRole extends Role implements McNealCustomer {
	private String name;
	private McNealFood f;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	//private McNealCustomerGui customerGui;
	private Table tableNumber;
	private int choice = 1;
	private String foodchoice = "?";
	private Table table;
	private restaurant.mcneal.roles.WaiterAgent.Menu m;
	private boolean receivedBill = false;
	// agent correspondents
	private McNealHost host;
	private McNealWaiter waiter;
	private McNealCook cook;
	private McNealCheck check;
	private McNealCashier cashier;
	private McNealCustomerGUI customerGui;
	private McNealRestaurant mc;
	private int cash;
	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, LookingAtMenu, ReadyToOrder, WaitingForFood,WaitingToo, Eating, DoneEating, AskingForBill, Paying, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, ordering, reordering, beingserved, receivedBill, doneEating, paying, changegiven, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public McNealCustomerRole(){
		super();
		//cash =(int) (5 + Math.random() * 20);
		//cash = 6;
		
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	/*public void setHost(McNealHostRole host) {
		this.host = host;
	}*/
	public int getCash() {
		return cash;
	}
	public McNealHost getHost() {
		return host; 
	}
	public void setHost(McNealHost D) {
		host = D;
	}
	public McNealWaiter getWaiter() {
		return waiter;
	}
	public void setWaiter(McNealWaiter waiter) {
		this.waiter = waiter;
	}
	public void setCashier(McNealCashierRole cashier) {
		this.cashier = cashier;
	}
	public void setCook(McNealCookRole cook){
		this.cook = cook;
	}
	public String getFoodChoice() {
		return m.gtChoice();
	}

	public String getName() {
		return getPerson().getName();
	}
	// Messages

	public void gotHungry() {//from animation
		
		print("I'm hungry");
		cash = (int) (5 + Math.random() * 20);
		event = AgentEvent.gotHungry;
		state = AgentState.DoingNothing;
		print("Customer equipped with $ " + cash);
		stateChanged();
	}
	public void msgMeetHost(McNealHost host){
		
		print("meeting the host though");
		this.host = (McNealHostRole) host;
		
		gotHungry();
		
		
		
	}
	public void msgTableFull() {
		int shouldIWait = (int)( 0 + Math.random() * 1);
		if(shouldIWait == 0) {
			event = AgentEvent.changegiven;
			state = AgentState.Paying;
			//these states change to the last state and event before the customer leaves the restaurant
			print("I'm not about this life of waiting in line. I'm leaving");
			stateChanged();
		}
		else {
			print("Ill just waait it out");
		}
		
	}
	public void msgFollowMe(Table t, McNealWaiter wa, restaurant.mcneal.roles.WaiterAgent.Menu menu) {
		table = t;
		m = menu;
		print("Received msgFollowMe ");
		setWaiter(wa);
		
		print("My waiters name is " + wa.getName());
		event = AgentEvent.followWaiter;
		print("state "  + state);
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}



	public void msgWhatWouldYouLike() {

		event = AgentEvent.ordering;
		stateChanged();
	}
	public void msgWhatWouldYouLikeToo(restaurant.mcneal.roles.WaiterAgent.Menu g) {
		print("about to order again");
		event = AgentEvent.reordering;
		stateChanged();
	}
	public void msgHereIsYourFood() {
		event = AgentEvent.beingserved;
		stateChanged();

	}
	public void msgHereIsBill(McNealCheck c) {
		print("Got my bill now finna pay it");
		this.check = c;
		event = AgentEvent.receivedBill;
		stateChanged();
	}
	public void msgAtCashier() {
		event = AgentEvent.paying;
		stateChanged();
	}
	public void msgHereIsChange(double change) {
		cash = (int) change;
		print("Received Change $" + change);
		event = AgentEvent.changegiven;
		cash = (int) (5 + Math.random() * 20);
		stateChanged();
	}
	public void msgNotEnoughMoney() {
		print("Awkward I dont have enough money. I'll pay next time");
		event = AgentEvent.changegiven;
		cash = (int) (5 + Math.random() * 20);
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
	public boolean pickAndExecuteAnAction() {
		//if(!paused) {
			//	CustomerAgent is a finite state machine
			//System.out.println(state + " state & " + event + " event");
			if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
				state = AgentState.WaitingInRestaurant;
				goToRestaurant();
				return true;
			}
			if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){
				print("REEEECHED THIS STSE");
				state = AgentState.BeingSeated;
				SitDown();
				return true;
			}
			if (state == AgentState.BeingSeated && event == AgentEvent.seated){
				state = AgentState.LookingAtMenu;
				LookAtMenu();
				/*state = AgentState.Eating;
			EatFood();*/
				return true;
			}
			if( state == AgentState.LookingAtMenu && event == AgentEvent.ordering){
				state = AgentState.WaitingForFood;
				Order(m.choose((int) (choice + Math.random() * m.getNumChoices())));

				return true;
			}
			if( event == AgentEvent.reordering){ System.out.println("reorder");
			event = AgentEvent.ordering;
			Order(m.choose((int) (choice + Math.random() * m.getNumChoices())));
			return true;
			}
			if(state == AgentState.WaitingForFood && event == AgentEvent.beingserved){
				state = AgentState.Eating;
				EatFood();

				return true;
			}
			if (state == AgentState.Eating && event == AgentEvent.doneEating){
				state = AgentState.AskingForBill;
				callWaiterforBill();
				return true;

			}
			if(state == AgentState.AskingForBill && event == AgentEvent.receivedBill) {
				state = AgentState.Paying;
				leaveTableandPay(cash);
				return true;
			}

			if(state == AgentState.Paying && event == AgentEvent.changegiven) {
				state = AgentState.Leaving;
				leaveRest();
				return true;
			}
			if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
				state = AgentState.DoingNothing;
				//no action
				return true;
			}
		//}

		return false;

	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");

		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		Do("Being seated. Going to table with waiter " + getWaiter() );
		this.msgAnimationFinishedGoToSeat();
		//member to take this out
		//customerGui.DoGoToSeat(1, this.getWaiter().getTable());
		//hack; only one table]

	}


	private void LookAtMenu() {
		
		boolean canafford = false;
		for(int i = 0; i < m.getPriceList().size(); i++) {
			if(m.getPriceList().get(i) <= this.getCash()){
				canafford = true;

				Do("Looking at Menu. Now Ready to Order " + getWaiter() + " !");
				this.getWaiter().msgReadyToOrder(this.getTableNum());
				break;
			}
		
		}
		if(!canafford) {
			
			
			this.getWaiter().msgCantAffordAndLeave(this.getTableNum());
			//customerGui.DoExitRestaurant();
			
		}
			

		}
	



	private void Order(String choice){

		//customerGui.setOrdered(true);

		f = new McNealFood(choice);

		
		Do("I would like choice # " + choice);
		getWaiter().msgHereIsmyChoice(choice, this.getTableNum());
		//customerGui.setOrdered(false);


	}	

	private void EatFood() { //customerGui.setOrdered(false);
	Do("Eating Food");
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
			//isHungry = false;
			stateChanged();
		}
	},
	5000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void leaveTableandPay(int cash) {
		Do("Paying");
		getWaiter().msgDoneEatingandPaying(this.getTableNum());
		customerGui.DoPay();
		cashier.msgHeresmyPayment(check, cash);
		//customerGui.DoExitRestaurant();
	}
	private void callWaiterforBill() {
		Do("Hey Waiter, Im Ready for Bill!!!");
		getWaiter().msgreadyforbill(this.getFoodChoice(), this.getTableNum());
	}
	private void leaveRest() {
		Do("Leaving.");
		//customerGui.DoExitRestaurant();
	}

	// Accessors, etc.

	

	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(McNealCustomerGUI g) { System.out.println("gui added");
	    customerGui = g;
	}

	public McNealCustomerGUI getGui() {
		return customerGui;
	}

	public Table getTableNum() {
		return table;
	}
	public void setTable(Table t) {
		table = t;
	}


	
}

