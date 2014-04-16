package restaurant.cammarano.roles;

import global.PersonAgent;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import gui.animation.role.restaurant.cammarano.CammaranoCashierGUI;
import interfaces.Person;
import restaurant.cammarano.interfaces.*;

import java.util.*;
import market.TruckDriverRole;

import restaurant.cammarano.CammaranoRestaurantAgent;

/**
 * Restaurant Cook Role
 */

public class CammaranoCashierRole extends Role implements CammaranoCashier {

	private float money;
	private int leaveTime;
	//private Timer timer;
	
	public List<CheckHandler> checks = Collections.synchronizedList(new ArrayList<CheckHandler>());
	public List<MarketOrderHandler> marketOrders = Collections.synchronizedList(new ArrayList<MarketOrderHandler>());
	public HashMap<String, MenuItem> itemCosts = new HashMap<String, MenuItem>();
	
	private CammaranoRestaurantAgent restaurant;
	private CammaranoCashierGUI cashierGui = null;
	
	private enum CheckState {
		Received,
		Computing,
		Computed,
		SentToWaiter,
		CustomerWaiting,
		CustomerTold,
		Paid,
		NotPaid,
		OverPaid,
		GivingChange,
		Completed
	};
	
	private enum MarketOrderState {
		Idle,
		Received,
		ReadyToPay,
		CantPay,
		WaitingOnMoney,
		Finished
	};
	
	public CammaranoCashierRole() {
		super();
		leaveTime = 40;

		money = 50000.0f;
		
		itemCosts.put("steak", new MenuItem(15.99f, "steak"));
		itemCosts.put("chicken", new MenuItem(10.99f, "chicken"));
		itemCosts.put("pizza", new MenuItem(8.99f, "pizza"));
		itemCosts.put("salad", new MenuItem(5.99f, "salad"));
	}

	@Override
	public String getName() {
		return person.getName();
	}

	@Override
	public void setPerson(Person p) {
		super.setPerson(p);
		for (PersonAgent.Job j : person.getJobs()) {
			if(j.getJob().equals("CammaranoCashierRole")) {
				leaveTime = j.getEndTime();
			}
		}
	}

	public void setRestaurant(CammaranoRestaurantAgent restaurant) {
		this.restaurant = restaurant;
	}
	
	// Messages
	public void msgComputeBill(CammaranoWaiter w, CammaranoCustomer c, String choice) {
		checks.add(new CheckHandler(w, c, choice));
		stateChanged();
	}
	
	public void msgReadyToPay(CammaranoCustomer c) {
		//log.add(new LoggedEvent("Received ReadyToPay"));
		for (CheckHandler check : checks) {
			if(check.customer == c) {
				check.state = CheckState.CustomerWaiting;
				stateChanged();
			}
		}
	}
	
	public void msgCustomerPaying(CammaranoCustomer c, float check) {
		/*
		for (MarketOrderHandler o : marketOrders) {
			if(o.state == MarketOrderState.WaitingOnMoney) {
				if(check >= o.amount) {
					o.state = MarketOrderState.ReadyToPay;
					break;
				}
			}
		}
		*/
		for (CheckHandler ch : checks) {
			if(ch.customer == c) {
				ch.check = check;
				if(check == itemCosts.get(ch.choice).getCost()) {
					ch.state = CheckState.Paid;
					money += check;
					stateChanged();
				}
				
				else if(check < itemCosts.get(ch.choice).getCost()) {
					ch.state = CheckState.NotPaid;
					money += check;
					stateChanged();
				}
				
				else {
					ch.state = CheckState.OverPaid;
					money += itemCosts.get(ch.choice).cost;
					stateChanged();
				}
			}
		}
	}
	
	public void msgHereIsTheAmountWeOwe(TruckDriverRole truck, float cost) {
		log.add(new LoggedEvent("Received HereIsTheAmountYouOwe. The cost is: " + cost));
		print("Received bill from the market");
		marketOrders.add(new MarketOrderHandler(truck, cost));
		stateChanged();
	}

	public void msgTimerDone(CheckHandler order) {
		for (CheckHandler ch : checks) {
			if(ch == order) {
				ch.state = CheckState.Computed;
				stateChanged();
			}
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		synchronized(checks) {
			for (CheckHandler ch : checks) {
				if(ch.state == CheckState.Received) {
					ComputeBill(ch);
					return true;
				}
			}
		}
		
		synchronized(checks) {
			for (CheckHandler ch : checks) {
				if(ch.state == CheckState.Computed) {
					SendBillToWaiter(ch);
					return true;
				}
			}
		}
		
		synchronized(checks) {
			for (CheckHandler ch : checks) {
				if(ch.state == CheckState.CustomerWaiting) {
					TellCustomerHeCanPay(ch);
					return true;
				}
			}
		}
		
		synchronized(checks) {
			for (CheckHandler ch : checks) {
				if(ch.state == CheckState.Paid) {
					CustomerHasPaid(ch);
					return true;
				}
			}
		}
		
		synchronized(checks) {
			for (CheckHandler ch : checks) {
				if(ch.state == CheckState.OverPaid) {
					GiveChange(ch);
					return true;
				}
			}
		}
		
		synchronized(checks) {
			for (CheckHandler ch : checks) {
				if(ch.state == CheckState.NotPaid) {
					YellAtCustomer(ch);
					return true;
				}
			}
		}
		
		synchronized(marketOrders) {
			for (MarketOrderHandler o : marketOrders) {
				if(o.state == MarketOrderState.Received) {
					PayMarket(o);
					return true;
				}
			}
		}
		/*
		synchronized(marketOrders) {
			for (MarketOrderHandler o : marketOrders) {
				if(o.state == MarketOrderState.ReadyToPay) {
					PayMarket(o);
					return true;
				}
			}
		}
		
		synchronized(marketOrders) {
			for (MarketOrderHandler o : marketOrders) {
				if(o.state == MarketOrderState.CantPay) {
					TellMarketCashierCantPay(o);
					return true;
				}
			}
		}
		*/
		
		if(person.getCurrentTime() >= leaveTime) {
			LeaveWork();
			return true;
		}
		
		return false;
	}
	
	// Actions
	private void ComputeBill(CheckHandler ch) {
		//DoComputeBill();
		ch.state = CheckState.Computing;
		print("Computing " + ch.waiter.getName() + "'s bill");
		ch.state = CheckState.Computed;
	}
	
	private void SendBillToWaiter(CheckHandler ch) {
		//DoSendBillToWaiter();
		print("Giving the bill to " + ch.waiter.getName());
		ch.state = CheckState.SentToWaiter;
		ch.waiter.msgHereIsTheCheck(ch.customer);
	}
	
	private void TellCustomerHeCanPay(CheckHandler ch) {
		print("I am ready for " + ch.customer.getName() + " to pay.");
		ch.state = CheckState.CustomerTold;
		ch.customer.msgIAmReadyForYouToPay();
	}
	
	private void GiveChange(CheckHandler ch) {
		print("Oi! Here is your change!");
		ch.state = CheckState.Completed;
		ch.customer.msgHereIsYourChange(ch.check - itemCosts.get(ch.choice).cost);
	}
	
	private void YellAtCustomer(CheckHandler ch) {
		print("Oi! " + ch.customer.getName() + ", you didn't pay you bloody idiot!");
		ch.state = CheckState.Completed;
		ch.customer.msgYouDidNotPayEnough();
	}
	
	private void CustomerHasPaid(CheckHandler ch) {
		print(ch.customer.getName() + " has paid.");
		ch.state = CheckState.Completed;
		ch.customer.msgPaymentReceived();
	}
	/*
	private void PrepareToPay(MarketOrderHandler order) {
		print("I am getting ready to pay " + order.market.getName() + ", but I need to see how much I have.");
		if(order.amount <= money) {
			order.state = MarketOrderState.ReadyToPay;
		}
		
		else {
			order.state = MarketOrderState.CantPay;
		}
	}
	*/
	
	private void PayMarket(MarketOrderHandler order) {
		log.add(new LoggedEvent("Payed market"));
		print("I am paying the market what I owe. That comes out to be $" + order.amount);
		order.state = MarketOrderState.Finished;
		money -= order.amount;
		print("I have $" + money + " left.");
		order.truck.msgHereIsBill(order.amount, restaurant);
	}
	/*
	private void TellMarketCashierCantPay(MarketOrderHandler order) {
		log.add(new LoggedEvent("Could not pay market. What I owe is: " + (order.amount - money)));
		print("I don't have enough to pay " + order.market.getName() + ". What I can pay now is $" + money);
		order.state = MarketOrderState.WaitingOnMoney;
		order.amount -= money;
		order.market.msgWeCanNotAffordTheFood(order.amount);
		money = 0;
	}
	*/
	
	public void LeaveWork() {
		log.add(new LoggedEvent("LeaveWork called"));
		restaurant.msgLeavingBuilding(this);
	}
	
	// Animations

	// Utilities
	public void setCashierGui(CammaranoCashierGUI cashierGui) {
		this.cashierGui = cashierGui;
	}
	
	public class CheckHandler {
		public CammaranoWaiter waiter;
		public CammaranoCustomer customer;
		public String choice;
		public CheckState state;
		public float check;
		
		public CheckHandler(CammaranoWaiter w, CammaranoCustomer c, String ch) {
			waiter = w;
			customer = c;
			choice = ch;
			state = CheckState.Received;
		}

		public CammaranoWaiter getWaiter() {
			return waiter;
		}
		
		public void setWaiter(CammaranoWaiter waiter) {
			this.waiter = waiter;
		}
		
		public CammaranoCustomer getCustomer() {
			return customer;
		}
		
		public void setCustomer(CammaranoCustomer customer) {
			this.customer = customer;
		}
		
		public String getChoice() {
			return choice;
		}
		
		public void setChoice(String choice) {
			this.choice = choice;
		}
	}
	
	public class MarketOrderHandler {
		public TruckDriverRole truck;
		public float amount;
		public MarketOrderState state;
		
		public MarketOrderHandler(TruckDriverRole t, float a) {
			truck = t;
			amount = a;
			state = MarketOrderState.Received;
		}
	}
	
	public class MenuItem {
		float cost;
		String name;
		
		MenuItem(float c, String n) {
			cost = c;
			name = n;
		}
		
		// Accessors and mutators
		public float getCost() {
			return cost;
		}

		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}
}

