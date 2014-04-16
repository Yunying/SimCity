package restaurant.yunying.roles;

import agent.Agent;
import bank.BankPatronRole;
import bank.interfaces.Bank;
import restaurant.yunying.TuRestaurantAgent;
import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;
import interfaces.Building;
import interfaces.Person;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.interfaces.Market;
import market.interfaces.TruckDriver;

public class TuCashierRole extends Role implements Cashier{
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	public Map<Market, Float>debts = new HashMap<Market, Float>();
	public List<Bill> bills = 
		    Collections.synchronizedList(new ArrayList<Bill>());
	public enum CheckState {none, pending, processing, paying, processeded};
	private Map<String, Float> price = new HashMap<String, Float>();
	CashierGui cashierGui;
	public EventLog log = new EventLog();
	public float savings;
	private float salary;
	Bank bank;
	TuRestaurantAgent tr;
	boolean readyToLeave;

	String name;
	public void setCapital(float c){
		savings = c;
	}
	
	public void reduceCapital(float c){
		savings -= c;
	}
	
	public float getCapital(){
		return savings;
	}
	
	public void setRest(TuRestaurantAgent t){
		tr = t;
	}
	
	public class Check {
		Check(String c, Waiter waiter, int table){
			choice = c;
			w = waiter;
			tableNum=table;
		}

		public Waiter w;
		String choice;
		public CheckState orderState = CheckState.none;
		int tableNum;
		 public double price;
		public double payment;
		public Customer c;
		public double debt = 0;
		public double change = 0;
	}
	
	public float getSalary(){
		return 10;
	}
	
	public void setPerson(Person p){
		super.setPerson(p);
		name = p.getName();
	}
	
	public class Bill{
		Bill(Market m2, TruckDriver d, double p){
			this.m = m2;
			this.driver = d;
			price = p;
		}
		public Market m;
		public TruckDriver driver;
		double price;
	}
	
	
	public TuCashierRole() {
		super();
		price.put("Brownie",(float) 4);
		price.put("Cheesecake",(float) 6);
		price.put("Gelato",(float) 2);
		price.put("Crepe",(float) 7);
		price.put("Waffle",(float) 5);
		savings = 100;
	}
	
//	public void msgHereIsBill(Market m, double price){
//		print("receive market bill");
//		synchronized (bills) {bills.add(new Bill(m,price));}
//		stateChanged();
//	}
	
	public void msgHereIsBill(Market m, TruckDriver d, double price){
		synchronized (bills) {bills.add(new Bill(m, d, price));}
		stateChanged();
	}

	
	public void msgComputeBill(Waiter w, String s, int table){
		log.add(new LoggedEvent("Received msgComputeBill"));
		Check check = new Check(s,w,table);
		synchronized (checks) {checks.add(check);}
		check.orderState=CheckState.pending;
		stateChanged();
	}
	
	public void setGui(CashierGui gui){
		cashierGui = gui;
	}
	
	public String getName() {
		return name;
	}
	
	public void msgHereIsPayment(Customer c, int table, double amount, double owe){
		log.add(new LoggedEvent("Received msgHereIsPayment"));
		savings += amount;
		for (Check check:checks){
			if (check.tableNum == table){
				check.orderState = CheckState.paying;
				check.payment = amount;
				check.c = c;
				check.debt = owe;
				stateChanged();
			}
		}
	}
	
	
	@Override
	public boolean pickAndExecuteAnAction() {
		
		if (!checks.isEmpty()){
			for (Check o:checks){
				if (o.orderState == CheckState.pending){
					receiveCheck(o);
					return true;
				}
				if (o.orderState == CheckState.paying){
					receivePayment(o);
					return true;
				}
			}
		}
		
		if (!bills.isEmpty()){
			for (Bill b:bills){
				payBill(b);
				return true;
			}
		}
		
		if (readyToLeave){
			leaveWork();
			return true;
		}
		return false;
	}
	
	private void leaveWork(){
		tr.msgLeavingWork(this);
	}
	
	private void payBill(Bill b){
		print("Pay Market the bill $" + b.price);
		savings -= b.price;
		if (savings >= 0){
			log.add(new LoggedEvent("I have enough money"));
			cashierGui.clearDebt();
			//b.m.msgHereIsMoney(b.price);
			float billAmt = (float) b.price;
			b.driver.msgHereIsBill(billAmt, tr);
			synchronized(bills) {bills.remove(b);}
		}
		
		else {
			//What to do?
			log.add(new LoggedEvent("I don't have enough money"));
			print("The restaurant does not have enough money to pay the bill. Price is "+b.price + ". But we only have " + (savings+b.price) + " in savings. So we have to owe "+ (0-savings));
			b.driver.msgHereIsBill((float)(b.price),tr);
			debts.put(b.m, 0-savings);
			cashierGui.haveDebt(0-savings);
			savings=0;
			synchronized(bills) {bills.remove(b);}
		}
	}
	
	private void receiveCheck(Check o){
		o.orderState = CheckState.processing;
		double money = price.get(o.choice);
		o.price = money;
		o.w.msgHereIsCheck(money, o.tableNum);
	}
	
	private void receivePayment(Check o){
		o.orderState = CheckState.processing;
		double change = o.payment-o.price - o.debt;
		o.change = change;
		print("Food price is $" + o.price);
		print("The payment is $" + o.payment + ". The debt from last time is $" + o.debt);
		if (change>0){
			o.c.msgHereIsChange(change);
			savings -= change;
			synchronized (checks) {checks.remove(o);}
			print("The payment is OK. Here is your change $" + change);
		}
		else{
			o.c.msgYouNeedMoreMoney(0-change);
			print("You need more money $" + (0-change));
			synchronized (checks) {checks.remove(o);}
		}
	}
	
	public void setBank(Bank b){
		bank = b;
	}
	
	public void depositMoney(){
		if (savings > 0){
			print("deposit $"+savings/2 +" to the bank");
			this.getPerson().AddTaskDepositEarnings(tr, (float)(savings / 2));
		}
	}

	public void msgLeaveWork() {
		// TODO Auto-generated method stub
		readyToLeave = true;
	}


	
}

