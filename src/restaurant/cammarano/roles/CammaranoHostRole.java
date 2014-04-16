package restaurant.cammarano.roles;

import global.PersonAgent;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import restaurant.cammarano.interfaces.*;
import gui.animation.role.restaurant.cammarano.CammaranoHostGUI;
import interfaces.Person;

import java.util.*;

import restaurant.cammarano.CammaranoRestaurantAgent;
//import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Role
 */
public class CammaranoHostRole extends Role implements CammaranoHost {
	static int NTABLES = 6;//a global for the number of tables.
	private String name;
	private int leaveTime;
	
	private CammaranoRestaurantAgent restaurant;
	
	public List<CustomerManager> waitingCustomers = Collections.synchronizedList(new ArrayList<CustomerManager>());
	public List<WaiterHandler> waiters = Collections.synchronizedList(new ArrayList<WaiterHandler>());
	public List<Table> tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
	public HashMap<String, Float> menu = new HashMap<String, Float>();
	
	private enum WaiterState {
		Working,
		Pending,
		Denied,
		OnBreak
	};
	
	private enum CustomerState {
		Idle,
		Waiting,
		InRestaurant,
		Gone
	}
	
	public CammaranoHostGUI hostGui = null;

	public CammaranoHostRole() {
		super();	
		leaveTime = 40;
		
		// Make some tables
		for (int i = 0; i < NTABLES; i++) {
			tables.add(new Table(i, (150 + (100 * i)), 350));
		}
		
		menu.put("steak", 15.99f);
		menu.put("chicken", 10.99f);
		menu.put("pizza", 8.99f);
		menu.put("salad", 5.99f);
	}

	public void AddTable(int x, int y) {
		NTABLES++;
		tables.add(new Table(NTABLES, x, y));
		stateChanged();
	}
	
	public void AddWaiter(CammaranoWaiter w) {
		waiters.add(new WaiterHandler(w));
		stateChanged();
	}

	@Override
	public String getName() {
		return person.getName();
	}
	
	@Override
	public void setPerson(Person p) {
		super.setPerson(p);
		for (PersonAgent.Job j : person.getJobs()) {
			if(j.getJob().equals("CammaranoHostRole")) {
				leaveTime = j.getEndTime();
			}
		}
	}

	
	/************************************************************************************************/
	/** Messages **/
	/************************************************************************************************/
	// From customer
	public void msgIWantFood(CammaranoCustomer cust) {
		print("Adding " + cust.getName());
		waitingCustomers.add(new CustomerManager(cust));
		stateChanged();
	}
	
	public void msgIAmLeavingFullRestaurant(CammaranoCustomer cust) {
		print ("Customer " + cust.getName() + " has left because we are full.");
		for (CustomerManager c : waitingCustomers) {
			if(c.customer == cust) {
				c.state = CustomerState.Gone;
				stateChanged();
			}
		}
	}

	// From waiter
	@Override
	public void msgTableEmpty(Table table) {
		for (Table t : tables) {
			if(t == table) {
				for (CustomerManager c : waitingCustomers) {
					if(c.customer == t.occupiedBy) {
						c.state = CustomerState.Gone;
					}
				}
				t.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	@Override
	public void msgAskToGoOnBreak(CammaranoWaiter waiter) {
		for (WaiterHandler w : waiters) {
			if(w.waiter == waiter) {
				int NumWaitersOnBreak = 0;
				
				for (int i = 0; i < waiters.size(); i++) {
					if(waiters.get(i).state == WaiterState.OnBreak) {
						NumWaitersOnBreak++;
					}
				}
				
				if(NumWaitersOnBreak < waiters.size()-1) {
					w.state = WaiterState.Pending;
					stateChanged();
				}
				
				else if (waiters.size() == 1) {
					w.state = WaiterState.Denied;
					stateChanged();
					
				}
				
				else {
					w.state = WaiterState.Denied;
					stateChanged();
				}
			}
		}
	}

	// From Gui
	public void msgNewWaiter() {
		stateChanged();
	}
	
	public void msgWaiterIsOffBreak(CammaranoWaiter wa) {
		for (WaiterHandler w : waiters) {
			if(w.waiter == wa) {
				w.state = WaiterState.Working;
				stateChanged();
			}
		}
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		if(waitingCustomers.size() > NTABLES) {
			try {
				for (CustomerManager c : waitingCustomers) {
					if(c.toldAboutState == false) {
						TellCustomerRestaurantIsFull(c);
						return true;
					}
				}
			}
			catch (ConcurrentModificationException c) {}
		}
		
		for (Table t : tables) {
			if(!t.isOccupied()) {
				try {
					for (CustomerManager c : waitingCustomers) {
						if(c.state == CustomerState.Waiting) {
							if(waiters.size() > 0) {
								WaiterHandler temp = new WaiterHandler();
									
								for (int i = 0; i < waiters.size(); i++) {
									if(waiters.get(i).state == WaiterState.Working) {
										temp = waiters.get(i);
									}
								}
					
								if(temp != null) {
									for (WaiterHandler w : waiters) {
										if(w.state == WaiterState.Working) {
											if(w.numCustomers < temp.numCustomers) {
												temp = w;
											}
										}
									}
									print(temp.waiter.getName());
									AssignWaiter(temp, t, c, menu);
									return true;
								}
							}
						}
					}
				}
				catch (ConcurrentModificationException c) {}
			}
		}
		
		synchronized(waiters) {
			for (WaiterHandler w : waiters) {
				if(w.state == WaiterState.Denied) {
					DenyWaiter(w);
					return true;
				}
			}
		}
		
		synchronized(waiters) {
			for (WaiterHandler w : waiters) {
				if(w.state == WaiterState.Pending) {
					ApproveWaiter(w);
					return true;
				}
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

	// Actions
	
	private void AssignWaiter(WaiterHandler waiter, Table table, CustomerManager customer, HashMap<String, Float> menu) {
		print("Assigning " + customer.customer.getName() + " to waiter " + waiter.waiter.getName());
		customer.state = CustomerState.InRestaurant;
		waiter.table = table;
		table.setOccupant(customer.customer);
		waiter.numCustomers++;
		waiter.waiter.msgSitCustomer(customer.customer, table, menu);
		waitingCustomers.remove(customer);
	}
	
	private void ApproveWaiter(WaiterHandler w) {
		w.waiter.msgBreakApproved();
		w.state = WaiterState.OnBreak;
	}
	
	private void DenyWaiter(WaiterHandler w) {
		w.waiter.msgBreakDenied();
		w.state = WaiterState.Working;
	}
	
	private void TellCustomerRestaurantIsFull(CustomerManager c) {
		print("Telling " + c.customer.getName() + " that the restaurant is full.");
		c.toldAboutState = true;
		c.customer.msgRestaurantIsFull();
	}
	
	public void LeaveWork() {
		log.add(new LoggedEvent("LeavingWork called"));
		restaurant.msgLeavingBuilding(this);
	}
	
	// Utilties
	public void setHostGui(CammaranoHostGUI hostGui) {
		this.hostGui = hostGui;
	}

	public void setRestaurant(CammaranoRestaurantAgent restaurant) {
		this.restaurant = restaurant;
	}
	
	public class CustomerManager {
		private CammaranoCustomer customer;
		private CustomerState state;
		public boolean toldAboutState;
		
		CustomerManager() {}
		
		CustomerManager(CammaranoCustomer c) {
			customer = c;
			state = CustomerState.Waiting;
			toldAboutState = false;
		}
	}
	
	public class WaiterHandler {
		private CammaranoWaiter waiter;
		private Table table;
		private WaiterState state;
		private int numCustomers;
		
		WaiterHandler() {}
		
		WaiterHandler(CammaranoWaiter w) {
			waiter = w;
			state = WaiterState.Working;
			numCustomers = 0;
		}
		
		WaiterHandler(CammaranoWaiter w, Table t) {
			waiter = w;
			table = t;
			state = WaiterState.Working;
		}
		
		public Table getTable() {
			return table;
		}
		
		public void setTable(Table table) {
			this.table = table;
		}
	}
	
	public class Table {
		CammaranoCustomer occupiedBy;
		int tableNumber;
		
		int posX;
		int posY;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
		
		public Table(int tableNumber, int x, int y) {
			this.tableNumber = tableNumber;
			this.posX = x;
			this.posY = y;
		}

		void setOccupant(CammaranoCustomer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CammaranoCustomer getOccupant() {
			return occupiedBy;
		}

		public boolean isOccupied() {
			return occupiedBy != null;
		}
		
		public int getTableNumber() {
			return tableNumber;
		}
		
		public int get_posX() {
			return posX;
		}
		
		public void set_posX(int x) {
			posX = x;
		}
		
		public int get_posY() {
			return posY;
		}
		
		public void set_posY(int y) {
			posY = y;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
}

