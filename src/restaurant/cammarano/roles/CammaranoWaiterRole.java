package restaurant.cammarano.roles;

// Java packages
import global.PersonAgent;
import java.util.*;
import java.util.concurrent.Semaphore;


// Role Packages
import global.roles.Role;

import restaurant.cammarano.roles.CammaranoHostRole.Table;
import gui.animation.role.restaurant.cammarano.CammaranoWaiterGUI;
import interfaces.Building;
import interfaces.Person;
import restaurant.cammarano.CammaranoRestaurantAgent;
import restaurant.cammarano.interfaces.*;
import restaurant.cammarano.roles.base.CammaranoBaseWaiter;

/**
 * Restaurant Waiter Role
 */

public class CammaranoWaiterRole extends CammaranoBaseWaiter implements CammaranoWaiter {

	private boolean isDoingTasks;
	//private Semaphore atDestination = new Semaphore(0, true);
	//private Timer timer = new Timer();

	private enum WaiterBreakState {
		Idle,
		Working,
		AskForBreak,
		BreakPending,
		//FinishingTasks,
		//OnBreakGuiCall,
		OnBreak,
	};

	private WaiterBreakState breakState;

	private enum CustomerState {
		Waiting,
		Seated,
		Ready,
		Asked,
		Ordered,
		WaitingForFood,
		MustReorder,
		HasFood,
		Eating,
		ReadyToPay,
		WaitingForCashier,
		ReadyToReceiveCheck,
		HasCheck,
		Paid,
		Leaving,
		PaidAndLeaving,
		LeftEarly,
		Gone
	};

	public List<CustomerHandler> customers = new ArrayList<CustomerHandler>();

	private CammaranoHost host;
	private CammaranoCook cook;
	private CammaranoCashier cashier;
	
	private CammaranoRestaurantAgent restaurant;

	public CammaranoWaiterGUI waiterGui = null;

	// Default constructor
	public CammaranoWaiterRole() {
		super();
		leaveTime = 40;

		isDoingTasks = true;
		breakState = WaiterBreakState.Idle;
	}

	@Override
	public String getName() {
		return person.getName();
	}

	@Override
	public void setPerson(Person p) {
		super.setPerson(p);
		for (PersonAgent.Job j : person.getJobs()) {
			if(j.getJob().equals("CammaranoWaiterRole")) {
				leaveTime = j.getEndTime();
			}
		}
	}
	
	public void setCook(CammaranoCook c) {
		cook = c;
	}

	public void setCashier(CammaranoCashier cashier) {
		this.cashier = cashier;
	}
	
	public void setBuilding(CammaranoRestaurantAgent r) {
		this.restaurant = r;
	}

	/************************************************************************************************/
	/** Messages **/
	/************************************************************************************************/
	// From Host
	@Override
	public void msgSitCustomer(CammaranoCustomer customer, Table table, HashMap<String, Float> menu) {
		customers.add(new CustomerHandler(table, customer, menu));
		isDoingTasks = true;
		stateChanged();
	}

	@Override
	public void msgBreakApproved() {
		print("I am now on break.");
		breakState = WaiterBreakState.OnBreak;
		//waiterGui.BreakApproved();
		stateChanged();
	}

	@Override
	public void msgBreakDenied() {
		print("I hate my boss");
		breakState = WaiterBreakState.Working;
		//waiterGui.SetOffBreak();
		stateChanged();
	}

	// From Customer
	public void msgICantPay(CammaranoCustomer customer) {
		print(customer.getName() + " can't afford anything here.");

		for (CustomerHandler c : customers) {
			if(c.customer == customer) {
				c.state = CustomerState.LeftEarly;
				stateChanged();
			}
		}
	}

	@Override
	public void msgReadyToOrder(CammaranoCustomer customer) {
		print(customer.getName() + "is  ready to order.");

		for (CustomerHandler c : customers) {
			if(c.customer == customer) {
				c.state = CustomerState.Ready;
				stateChanged();
			}
		}
	}
	
	@Override
	public void msgHereIsMyChoice(CammaranoCustomer customer, String choice) {
		print("Got order from: " + customer.getName());

		for (CustomerHandler c : customers) {
			if(c.customer == customer) {
				c.setChoice(choice);
				c.state = CustomerState.Ordered;
				stateChanged();
			}
		}
	}

	// Customer is ready to pay
	@Override
	public void msgReadyToPay(CammaranoCustomer customer) {
		print("Customer " + customer.getName() + " would like his or her bill.");

		for (CustomerHandler c : customers) {
			if(c.customer == customer) {
				c.state = CustomerState.ReadyToPay;
				stateChanged();
			}	
		}
	}
	
	@Override
	public void msgLeavingTable(CammaranoCustomer cust) {
		print("Leaving");
		
		for (CustomerHandler c : customers) {
			if (c.customer == cust) {
				c.state = CustomerState.Leaving;
				print(cust + " leaving " + c.table);
				stateChanged();
			}
		}
	}

	// From cook
	@Override
	public void msgOrderDone(String choice, Table table) {
		print("Received: " + choice);

		for (CustomerHandler c : customers) {
			if(c.table == table) {
				c.state = CustomerState.HasFood;
				stateChanged();
			}
		}
	}

	@Override
	public void msgOutOfFood(String choice, Table table) {
		print("We have no " + choice + ". I need to tell the customer at " + table);

		for (CustomerHandler c : customers) {
			if(c.table == table && c.choice.equals(choice)) {
				c.state = CustomerState.MustReorder;
				stateChanged();
			}
		}
	}

	// From the cashier
	@Override
	public void msgHereIsTheCheck(CammaranoCustomer customer) {
		for (CustomerHandler c : customers) {
			if(c.customer == customer) {
				c.state = CustomerState.ReadyToReceiveCheck;
				stateChanged();
			}
		}
	}

	// From animation
	public void msgAtDestination() {
		//atDestination.release();
		stateChanged();
	}

	// From Gui
	public void msgStartWorking() {
		print("Time to begin working");
		breakState = WaiterBreakState.Working;
		stateChanged();
	}

	public void msgGoOnBreak() {
		print("I would like to go on break");
		breakState = WaiterBreakState.AskForBreak;
		stateChanged();
	}

	/************************************************************************************************/
	/** Scheduler **/
	/************************************************************************************************/
	@Override
	public boolean pickAndExecuteAnAction() {
		// If the waiter is idle and wants to go on break, ask the host before being given any tasks.
		if(!isDoingTasks) {
			if(breakState == WaiterBreakState.AskForBreak) {
				AskHostToGoOnBreak();
				return true;
			}
		}

		// If we are not on break and we are not idling, continue with the scheduler as normal
		if(breakState != WaiterBreakState.OnBreak || isDoingTasks) {

			// Ask for break after ushering customer away from the table.
			try {
				for (CustomerHandler c : customers) {
					if(c.getState() == CustomerState.Leaving) {
						if(breakState == WaiterBreakState.AskForBreak) {
							ReturnToHost();
							AskHostToGoOnBreak();
						}

						TellHostTableIsEmpty(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}

			// Tell host the table is empty if a customer leaves early
			try {
				for (CustomerHandler c : customers) {
					if(c.state == CustomerState.LeftEarly) {
						ReturnToHost();
						TellHostTableIsEmpty(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}

			// Bring food to the customer
			try {
				for (CustomerHandler c : customers) {
					if(c.getState() == CustomerState.HasFood) {
						BringFoodToCustomer(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}
						
			// Go to the cook and order food
			try {
				for (CustomerHandler c : customers) {
					if(c.getState() == CustomerState.Ordered) {
						GoToCook(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}

			// Get check from cashier
			try {
				for (CustomerHandler c : customers) {
					if(c.state == CustomerState.ReadyToPay) {
						AskCashierToComputeCheck(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}

			// Bring the check to the customer
			try {
				for (CustomerHandler c : customers) {
					if(c.state == CustomerState.ReadyToReceiveCheck) {
						BringCheckToCustomer(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}

			// Ask customer to reorder food
			try {
				for (CustomerHandler c : customers) {
					if(c.state == CustomerState.MustReorder) {
						ReturnToReorder(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}

			// Seat customer
			try {
				for (CustomerHandler c : customers) {
					if(c.getState() == CustomerState.Waiting) {
						seatCustomer(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}

			// Take order from customer
			try {
				for (CustomerHandler c : customers) {
					if(c.state == CustomerState.Ready) {
						TakeOrder(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}


			/*
			// Go to the cook and order food
			try {
				for (CustomerHandler c : customers) {
					if(c.getState() == CustomerState.Leaving) {
						PickupCheck(c);
						return true;
					}
				}
			} catch (ConcurrentModificationException c) {
				c.printStackTrace();
			}
			 */

			// If we have no customers, then return to the host and maybe ask for a break.
			if(customers.size() == 0) {
				GoToTheWaitArea();
				//ReturnToHost();
				if(breakState == WaiterBreakState.AskForBreak) {
					AskHostToGoOnBreak();
				}
				isDoingTasks = false;
				return true;
			}
		}
		
		if(person.getCurrentTime() >= leaveTime) {
			LeaveWork();
			return true;
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	/************************************************************************************************/
	/** Actions **/
	/************************************************************************************************/
	// Seat customer
	private void seatCustomer(CustomerHandler c) {
		print("Seating customer: " + c.customer.getName());
		c.state = CustomerState.Seated;
		/*
		DoGoToCustomerArea();
		try {
			atDestination.acquire();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		c.customer.msgSitAtTable(this, c.table, c.menu);
		
		/*
		DoSeatCustomer(c.customer, c.table);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DoGoToTheWaitArea();
		print("Going to the wait area.");
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	// Take Order
	private void TakeOrder(CustomerHandler c) {
		print("Taking food order from: " + c.customer.getName());
		c.state = CustomerState.Asked;
		/*
		DoGoToTable(c.table);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		c.customer.msgWhatDoYouWant();
	}

	// Return to ask customer to reorder food
	private void ReturnToReorder(CustomerHandler c) {
		print("Taking food order from: " + c.customer.getName() + " again because we are out of " + c.choice);
		c.state = CustomerState.Asked;
		/*
		DoGoToTable(c.table);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		c.customer.msgYouHaveToReorder(c.choice);
	}

	// Go to the cook
	private void GoToCook(CustomerHandler c) {
		print("Going to the cook");
		c.state = CustomerState.WaitingForFood;
		/*
		DoGoToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		cook.msgHereIsTheOrder(this, c.table, c.choice);
	}

	// Bring food to customer
	private void BringFoodToCustomer(CustomerHandler c) {
		print("Going back to the customer with food");
		c.state = CustomerState.Eating;
		/*
		DoGoToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		DoBringFoodToCustomer(c.choice, c.table);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		c.getCustomer().msgHereIsYourFood(c.choice);
		/*
		waiterGui.GiveFood();
		DoGoToTheWaitArea();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*
		// This checks to see if we have other customers waiting for food. If we do, go to the cook instead of the host
		int numCustomers = 0;
		try {
			for (CustomerHandler cc : customers) {
				if(cc.state == CustomerState.HasFood) {
					numCustomers++;
				}
			}
		} catch (ConcurrentModificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(numCustomers > 0) {
			DoGoToCook();
			print("Returning to the cook.");
			try {
				atDestination.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			DoReturnToHost();
			print("Returning to the host.");
			try {
				atDestination.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}

	// Ask cashier to compute check
	private void AskCashierToComputeCheck(CustomerHandler c) {
		print("Bringing " + c.customer.getName() + "'s order to the cashier.");
		c.state = CustomerState.WaitingForCashier;
		/*
		DoGoToCashier();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		cashier.msgComputeBill(this, c.customer, c.choice);

	}

	// Bring check to customer
	private void BringCheckToCustomer(CustomerHandler c) {
		print("Bringing " + c.customer.getName() + " his or her check.");
		c.state = CustomerState.HasCheck;

		/*
		DoGoToCustomerWithCheck(c.table);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		c.customer.msgHereIsYourCheck();
		
		/*
		DoGoToTheWaitArea();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	// Return to the host
	private void ReturnToHost() {
		/*
		DoReturnToHost();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DoGoToTheWaitArea();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	// Return to the host
	private void GoToTheWaitArea() {
		/*
		DoGoToTheWaitArea();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	public void PickupCheck(CustomerHandler c) {
		print("Picking up " + c.customer.getName() + "'s check and cleaning table.");
		c.state = CustomerState.PaidAndLeaving;

		/*
		DoGoToCustomerWithCheck(c.table);
		try {
			atDestination.acquire();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DoGoToCashier();

		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	// Return to the host to specifically ask to go on break
	private void AskHostToGoOnBreak() {
		breakState = WaiterBreakState.BreakPending;
		/*
		DoReturnToHost();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		host.msgAskToGoOnBreak(this);
	}

	// Tell the host that the table is empty
	private void TellHostTableIsEmpty(CustomerHandler c) {
		print("Table " + c.table.getTableNumber() + " is empty!");
		c.state = CustomerState.Gone;
		host.msgTableEmpty(c.table);
		customers.remove(c);
	}

	private void LeaveWork() {
		isDoingTasks = false;
		restaurant.msgLeavingBuilding(this);
	}
	/************************************************************************************************/
	/** Animation Subroutines **/
	/************************************************************************************************/
	// Animation call to seat customer
	private void DoSeatCustomer(CammaranoCustomer customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		waiterGui.DoBringToTable(table); 
	}

	// Animation call to head to the table (generic)
	private void DoGoToCustomerArea() {
		waiterGui.DoGoToCustomerArea();
	}
		
	// Animation call to head to the table (generic)
	private void DoGoToTable(Table table) {
		waiterGui.DoBringToTable(table);
	}

	// Animation call to go to the cook
	private void DoGoToCook() {
		waiterGui.DoGoToCook();
	}

	// Animation call to bring food to the customer
	private void DoBringFoodToCustomer(String choice, Table table) {
		waiterGui.DoBringFoodToCustomer(choice, table);
	}

	// Animation call to go to the cashier
	private void DoGoToCashier() {
		waiterGui.DoGoToCashier();
	}

	// Animation call to go the customer with the check
	private void DoGoToCustomerWithCheck(Table table) {
		waiterGui.DoBringToTable(table);
	}

	// Animation call to return to the host
	private void DoReturnToHost() {
		waiterGui.DoLeaveCustomer();
	}
	
	// Animation call to go to the wait area
	private void DoGoToTheWaitArea() {
		waiterGui.DoGoToWaitPos();
	}

	/************************************************************************************************/
	/** Utilities **/
	/************************************************************************************************/
	public void setGui(CammaranoWaiterGUI gui) {
		waiterGui = gui;
	}

	public void setRestaurant(CammaranoRestaurantAgent restaurant) {
		this.restaurant = restaurant;
	}
	
	public CammaranoWaiterGUI getGui() {
		return waiterGui;
	}

	// Host setup
	public CammaranoHost getHost() {
		return host;
	}

	public void setHost(CammaranoHostRole host) {
		this.host = host;
	}

	public class CustomerHandler {
		private Table table;
		private HashMap<String, Float> menu;
		private String choice;
		private CammaranoCustomer customer;
		private CustomerState state;

		CustomerHandler(CammaranoCustomer c) {
			customer = c;
			state = CustomerState.Waiting;
		}

		CustomerHandler(Table t, CammaranoCustomer c) {
			table = t;
			customer = c;
			state = CustomerState.Waiting;
		}

		CustomerHandler(Table t, CammaranoCustomer c, HashMap<String, Float> m) {
			table = t;
			customer = c;
			state = CustomerState.Waiting;
			menu = m;
		}

		// Accessors and mutators
		public Table getTable() {
			return table;
		}

		public void setTable(Table table) {
			this.table = table;
		}

		public String getChoice() {
			return choice;
		}

		public void setChoice(String choice) {
			this.choice = choice;
		}

		public CammaranoCustomer getCustomer() {
			return customer;
		}

		public void setCustomer(CammaranoCustomer customer) {
			this.customer = customer;
		}

		public CustomerState getState() {
			return state;
		}

		public void setState(CustomerState state) {
			this.state = state;
		}
	}
}

