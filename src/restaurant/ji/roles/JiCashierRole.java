package restaurant.ji.roles;

import global.roles.Role;
import global.test.mock.LoggedEvent;
import interfaces.Building;
import java.util.*;

import market.interfaces.TruckDriver;
import bank.interfaces.Bank;
import restaurant.ji.Menu;
import restaurant.ji.interfaces.*;

/**
 * Restaurant Cashier Agent
 * The agent that tells Waiters how much Customers should pay, and that receives payment from the customer
 */

public class JiCashierRole extends Role implements JiCashier{
	
	private JiRestaurant restaurant;
	private Bank companyBank;
	
	public enum State {arrivedAtJiRestaurant, working, leavingWork, none};
	private State state;
	
	private List<Check> checks	= Collections.synchronizedList(new ArrayList<Check>());
	public enum CheckState {beingCalculated, waiting, payReceived};
	private static final Menu menu = new Menu();
	
	private float restaurantMoney;
	private float moneyToDeposit;
	private Map<TruckDriver, Float> restaurantBills = new HashMap<TruckDriver, Float>();
	private boolean working;
	private boolean receivedPaycheck;
	
	public JiCashierRole() {
		super();
		moneyToDeposit = 0;
		working = false;
		receivedPaycheck = false;
		state = State.none;
	}

	
	//**********Messages************//
	@Override
	public void msgAtBuilding(Building rest){
		log.add(new LoggedEvent("Received msgAtJiRestaurant"));
		state = State.arrivedAtJiRestaurant;
		this.restaurant = (JiRestaurant)rest;
		stateChanged();
	}
	
	@Override
	public void msgPayMarketBill(float amount, TruckDriver driver){
		log.add(new LoggedEvent("Received msgPayMarketBill"));
		state = State.working;
		if (restaurantBills.containsKey(driver))
			restaurantBills.put(driver, restaurantBills.get(driver)+amount);
		else
			restaurantBills.put(driver, amount);
		stateChanged();
	}
	
	@Override
	public void msgComputeBill(String choice, JiCustomer customer, JiWaiter waiter) {
		state = State.working;
		log.add(new LoggedEvent("Received msgComputeBill"));
		checks.add(new Check(choice, customer, waiter));
		stateChanged();		
	}
	
	@Override
	public void msgPayingCheck(float money, JiCustomer customer) {
		log.add(new LoggedEvent("Received msgPayingCheck"));
		state = State.working;
		synchronized(checks){
			for (Check check : checks){
				if (check.customer == customer){
					check.pay(money);
					stateChanged();
					break;
				}
			}
		}
	}
	
	@Override
	public void msgICantPay(JiCustomer customer) {
		log.add(new LoggedEvent("Received msgICantPay"));
		state = State.working;
		synchronized(checks){
			Iterator<Check> i = checks.iterator();
			while (i.hasNext()){
				if (i.next().customer == customer){
					print("Just pay for it next time");
					log.add(new LoggedEvent("adding to tab"));
					i.remove();
					stateChanged();
				}
			}
		}
		
	}

	@Override
	public void msgDepositExcessFunds(float excess){
		moneyToDeposit = excess;
		stateChanged();
	}

	@Override
	public void msgStopWorkingGoHome() {
		state = State.leavingWork;
		working = false;
		stateChanged();
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
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
			synchronized(checks){
				for (Check check : checks) {
					if (check.state == CheckState.beingCalculated){
						computeBill(check);
						return true;
					}
					else if (check.state == CheckState.payReceived){
						giveChange(check);
						return true;
					}
				}
			}
			if (restaurantMoney > 0 && !restaurantBills.isEmpty()){
				for (TruckDriver d : restaurantBills.keySet()){
					payForMarketDelivery(d);
					return true;
				}
			}
			return false;
		}
		else if (state == State.leavingWork){
			leaveWork();
			return true;
		}
		return false;
	}

	//**********Actions************//
	private void beginWork(){
		state = State.working;
		Do("Working as cashier now!");
		working = true;
		receivedPaycheck = false;
		this.restaurantMoney = restaurant.getCurrentAssets();
		this.companyBank = restaurant.getBank();
		restaurant.msgAtLocation(person, this, null); // change this message in Building so we're not always passing in unnecessary information
	}
	
	private void payForMarketDelivery(TruckDriver d){
		float amountPaying = 0f;
		if (restaurantMoney < restaurantBills.get(d)){
			amountPaying = restaurantMoney;
			restaurantBills.put(d, restaurantBills.get(d) - restaurantMoney);
			restaurantMoney = 0;
			Do("Paid " + d.getClass() + " $" + amountPaying + ". Still owe $" + restaurantBills.get(d));
		}
		else{
			amountPaying = restaurantBills.get(d);
			restaurantMoney = restaurantMoney - restaurantBills.get(d);
			restaurantBills.remove(d);
			Do("Paid " + d.getClass() + " $" + amountPaying + " bill in full");
		}
		d.msgHereIsBill(amountPaying, this.restaurant);
		restaurant.msgPaidBillToMarket(amountPaying);
	}
	
	private void computeBill(Check check){
		Do("Computing bill: $" + check.due);
		check.waiter.msgHereIsCheck(check.due, check.customer);
		check.state = CheckState.waiting;
	}
	private void giveChange(Check check){
		Do("Giving change: $" + check.change + ". Restaurant now has $" + restaurant.getCurrentAssets());
		check.customer.msgHereIsChange(check.change);
		checks.remove(check);
	}
	
	void leaveWork(){
		Do("Done working bye");
		state = State.none;
		if (moneyToDeposit > 0){
			Do("Stopping by the bank though to deposit $" + moneyToDeposit);
			person.AddTaskDepositEarnings(restaurant, moneyToDeposit); // add task to go to the bank and make a deposit
			person.ChangeMoney(moneyToDeposit); // add to person so he can go to bank and deposit it
		}
		restaurant.msgLeavingWork(this);
		//gui.DoLeaveBuilding(); // Role should have a gui
		person.msgLeavingLocation(this); // inactivate CashierRole
	}
	
	public class Check {			
		public float due;
		public float change;
		public JiCustomer customer;
		public JiWaiter waiter;
		public CheckState state;
		
		public Check(String choice, JiCustomer customer, JiWaiter waiter){ 
			this.due = menu.getPrice(choice);
			this.change = 0;
			this.customer = customer;
			this.waiter = waiter;
			this.state = CheckState.beingCalculated;
		}
		
		public void pay(float money){
			state = CheckState.payReceived;
			change = money - due;
			restaurantMoney = (change > 0)? restaurantMoney + due : restaurantMoney + money;
			restaurant.msgReceivedPaymentFromCustomer(due);
		}
	}
	
	//**********Accessors************//
	
	@Override public String getName() { return person.getName(); }
	@Override public boolean hasReceivedPaycheck() { return receivedPaycheck; }
	public float getRestaurantMoney() { return restaurantMoney; }
	public void setRestaurantMoney(float restaurantMoney) { this.restaurantMoney = restaurantMoney; }
	public boolean isWorking() { return working; }
	public void setWorking(boolean working) { this.working = working;}
	public List<Check> getChecks() { return checks; }
	public void setChecks(List<Check> checks) { this.checks = checks; }
	public Map<TruckDriver, Float> getRestaurantBills() { return restaurantBills; }
	public void setRestaurantBills(Map<TruckDriver, Float> restaurantBills) { this.restaurantBills = restaurantBills; }
	public JiRestaurant getRestaurant() { return restaurant; }
	public void setRestaurant(JiRestaurant restaurant) { this.restaurant = restaurant; }
	public State getState() { return state; }
	public void setState(State state) { this.state = state; }
	public Bank getCompanyBank() { return companyBank; }
	public void setCompanyBank(Bank companyBank) { this.companyBank = companyBank; }
	public float getMoneyToDeposit() { return moneyToDeposit;}
	public void setMoneyToDeposit(float moneyToDeposit) { this.moneyToDeposit = moneyToDeposit; }
	
}

