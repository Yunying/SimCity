package restaurant.mcneal.roles;

import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import interfaces.Building;
import interfaces.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bank.BankPatronRole;
import market.Inventory;
import market.interfaces.Market;
import restaurant.ji.roles.JiCashierRole.State;
import restaurant.mcneal.McNealCheck;
import restaurant.mcneal.interfaces.McNealCashier;
import restaurant.mcneal.interfaces.McNealCustomer;
//import restaurant.test.mock.LoggedEvent;
import restaurant.mcneal.interfaces.McNealRestaurant;
import restaurant.mcneal.interfaces.McNealWaiter;

public class McNealCashierRole extends Role implements McNealCashier, Employee{

	
	String name;
	public int chew = 0;
	boolean paycheckhere = false;
	public double costt = 0;
	private int change;
	private McNealCustomer customer;
	private McNealWaiter w;
	private Market market;
	public double cost;
	private int cashgiven;
	private int cashiercash;
	private String choice;
	private McNealRestaurant r;
	//private Market market;
	private boolean computerequest = false;
	private boolean customerpaying = false;
	private int moneyToDeposit;
	public List<McNealCheck> checks = Collections.synchronizedList(new ArrayList<McNealCheck>());
	public List<McNealCheck> marketChecks = Collections.synchronizedList(new ArrayList<McNealCheck>());
	public enum CheckStates { nothing, computerequest, computed, payingfor};
	public enum MarketCheckStates { nothing, needtobepaid, payingfor, paid};
	public MarketCheckStates st = MarketCheckStates.nothing;
	public CheckStates state = CheckStates.nothing;
	
	public McNealCashierRole() {
		//startThread();
		cashiercash = 100;
		
	}
	public McNealCustomer getCashierCustomer() {
		return customer;
	}
	
	
	public void setRestaurant(McNealRestaurant r){
		this.r = r;
	}
	public McNealRestaurant getRestaurant() {
		return r;
	}

	
	public String getName() {
		return getPerson().getName();
	}
	//msg 
	public void msgBillForCustomer(String choice, McNealCustomer c){
		log.add(new LoggedEvent("Received request to calculate bill"));
		print("Received request to calculate bill");
		this.choice = choice;
		this.customer = c;
		
		state = CheckStates.computerequest;
		checks.add(new McNealCheck(cost,c));
		
		
		stateChanged();
	}
	public void msgHeresmyPayment(McNealCheck check, int cash){
		//call a state that checks to see if the waiter has the check 
		print("Recieved Payment from Customer");
		log.add(new LoggedEvent("Recieved Payment from Customer"));
		synchronized(checks) {
		for(int i = 0; i < checks.size(); i++ ) {
			if(check.getCheckCustomer().getName().equals(checks.get(i).getCheckCustomer().getName())){
				cashgiven = cash;
				r.setCurrentAssets(cash);
				cashiercash += cashgiven;
				state = CheckStates.payingfor;
			}
		
		}
		}
		stateChanged();
	}
	/*public void msgOrderDelivered(Map<String, Integer> order, Building market,
			float bill) {
		marketChecks.add(new McNealCheck((double) bill, market));
		Food f = new Food(orde)
		
		
		
		
		
	}*/
	public void msgHereIsChange(double change, McNealCheck c) {
		log.add(new LoggedEvent("Recieved Change From Market"));
		print("Change Recieved from Market " + change);
		
		for(int i = 0; i < marketChecks.size(); i++) {
			 //if(c.getMarket().getName().equals(marketChecks.get(i).getMarket().getName())){// == marketChecks.get(i).getMarketAmount()) {
				 st = MarketCheckStates.paid;
				 stateChanged();
				 
			 }
		}
		//}
	/*public void msgDepositFunds() {
	moneyToDeposit -= 34; //this is the total amount of the waiters to be paid
		if (moneyToDeposit > 0){
			// go to bank
			Action depositForRestaurant = new Action("deposit$" + moneyToDeposit);
			List<Action> deposit = new ArrayList<Action>(); actions.add(depositForRestaurant);
			person.getRole("BankPatron").activate(deposit);
			((BankPatronRole) person.getRole("BankPatron")).setCompany((BusinessAgent)getRestaurant(), moneyToDeposit);
		}
		
		//r.msgLeavingWork(this);
		//gui.DoLeaveBuilding(); // Role should have a gui
		person.msgLeavingLocation(this); // inactivate Role
	
	
         stateChanged();	
	}
	*/
	@Override
	public boolean pickAndExecuteAnAction() { 
		synchronized(marketChecks) {
		for(int i = 0; i < marketChecks.size(); i++) {
			 if(st == MarketCheckStates.needtobepaid) {
				 st = MarketCheckStates.payingfor;
				// payMarket(marketChecks.get(i));
				 return true;
			 }
			}
		}
		synchronized(marketChecks) {
		for(int i = 0; i < marketChecks.size(); i++) {
			if(st == MarketCheckStates.paid) {
				st = MarketCheckStates.nothing;
				marketChecks.remove(marketChecks.get(i));
				return true;
			}
		}
		
		
		}
		synchronized(checks) {
		for(int i = 0; i < checks.size(); i++ ) {
		if(state == CheckStates.computerequest) {System.err.println(" checking for paying for but state is " + state);
			state = CheckStates.computed;
		
		//	System.out.println(checks.get(i).getCheckCustomer().getName());
		Compute(checks.get(i) ,checks.get(i).getCheckCustomer());
		return true;
		}
		}
			// TODO Auto-generated method stub
		}
		synchronized(checks) {
		for(int i = 0; i < checks.size(); i++ ){ 
		if(state == CheckStates.payingfor ) {
			GiveChange(checks.get(i),cashgiven);
			state = CheckStates.nothing;
			return true;
		}
		}
		
		}
		if(paycheckhere == true && checks.isEmpty() && marketChecks.isEmpty()) {
			leave();
			paycheckhere = false;
			return true;
			
		}
		return false;
	}
	
	//actions
	public void Compute(McNealCheck check, McNealCustomer c){
		Do("Calculating Bill");
		if(choice == "ST") {
			 cost = 15.99;
			}
			if(choice == "CH") {
				 cost = 10.99;
			}
			if(choice == "PI") {
				 cost = 8.99;
			}
			if(choice == "SAL") {
				 cost = 5.99;
			}
			check.setCheckCustAmount(cost);
		
		System.out.println(c.getWaiter().getName() + " is the waiter");
		c.getWaiter().msgComputedBill(check.getCustAmount(), c.getTableNum());
	}
	/*public void payMarket(McNealCheck check) {
		
		
			
		check.getMarket().msgCashierPaysMarket(check, cashiercash);
			

		
		
	}
	*/
	public void leave() {
		Do("Recieved Paycheck now I can leave. See ya");
		person.msgLeavingLocation(this);
	}
	public void GiveChange(McNealCheck check, int cash) {
		Do("Giving customer change who has " + cash + " and the cost is  " + cost);
		//restaurant.currentMoney += cash;
		double change =  cash - cost;
		if(cash < cost) {
			customer.msgNotEnoughMoney();
			checks.remove(check);
		
			
		}
		else {
			
		if(cash >= cost) {
		customer.msgHereIsChange(change);
		checks.remove(check);
		}
	}
}
	@Override
	public void msgOrderDelivered(Map<String, Integer> order, Building market,
			float bill) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgStopWorkingGoHome() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		person.ChangeMoney(paycheck);
		paycheckhere = true;
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean hasReceivedPaycheck() {
		// TODO Auto-generated method stub
		return paycheckhere;
	}
	@Override
	public void msgAtBuilding(Building building) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgDepositFunds() {
		// TODO Auto-generated method stub
		
	}


}
