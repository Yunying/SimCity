package restaurant.mcneal.roles;




import gui.animation.role.restaurant.mcneal.McNealWaiterGUI;
import restaurant.mcneal.interfaces.McNealCustomer;
import restaurant.mcneal.interfaces.McNealHost;
import restaurant.mcneal.interfaces.McNealRestaurant;
import restaurant.mcneal.interfaces.McNealWaiter;
import global.roles.Role;



import interfaces.Building;
import interfaces.Employee;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class McNealHostRole extends Role implements McNealHost,Employee {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	private int lowest;
	private boolean asked; //whether the waiter has asked to go on break
	private boolean paycheckhere =false;
	private McNealRestaurant ra;
	public List<McNealWaiter> workingWaiters = new ArrayList<McNealWaiter>();
	public List<McNealCustomer> waitingCustomers
	= new ArrayList<McNealCustomer>();
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	public List<McNealWaiter> waiterList = new ArrayList<McNealWaiter>();
	private String name;
	//private RestaurantPanel r;

	//private int waitersonduty = 0;
	public McNealCustomer c = null;
	//private WaiterAgent lowest;
	public McNealWaiterGUI waiterGui = null;
	public McNealWaiter waiter = null;
	
	private boolean tableO = false;
	public McNealHostRole() {
		super();
	
		//waiterList.add(waiter);

		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
			//System.out.println("These are how many tables there are: " + tables.size());
		}
	}

	public String getMaitreDName() {
		return name;
	}
	public void setWaiter(McNealWaiter waiter) {
		this.waiter = waiter;

	}
	public String getName() {
		return getPerson().getName();
	}

	public List<McNealCustomer> getWaitingCustomers() {
		return waitingCustomers;
	}
	public List<McNealWaiter> getWaiterList() {
		return waiterList;
	}
	public List<McNealWaiter> getWorkingWatiers() {
		return workingWaiters;
	}
	public void setWaiterAsk(boolean asked) {
		this.asked = asked;
	}
	public boolean waiterAsk() {
		return asked;
	}

	public Collection getTables() {
		return tables;
	}
	
	
	public McNealRestaurant getRestaurant() {
		
		return ra;
	}
	
	public void setRestaurant(McNealRestaurant ra) {
		this.ra = ra;
	}
	// Messages

	public void msgIWantFood(McNealCustomer customer) { 
		print("Recieved message im hungry");
		waitingCustomers.add(customer);
		stateChanged();
	}


	public void msgTableFree(Table t, McNealWaiter wa) {

		print( "clearing  " + t.getNumber());
		t.setUnoccupied();

		wa.setOnBreak(true);
		stateChanged();
	}

	public void msgGoOnBreakPlease() {// (RestaurantPanel r) {
		//this.r = r;
		print("Recieved go on Break by waiter ");
		setWaiterAsk(true);
		stateChanged();
	}
	
	public void msgOffBreak() {
		print("Waiter off break");
		//waiterBreak =false;
		stateChanged();
	}

	@Override
	public void msgStopWorkingGoHome() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		// TODO Auto-generated method stub
		person.ChangeMoney(paycheck);
		paycheckhere = true;
	}

	@Override
	public boolean hasReceivedPaycheck() {
		// TODO Auto-generated method stub
		return paycheckhere;
	}


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() { 
	
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		//System.out.println("table0 = " + tableO);
		//if(!paused){
		if(paycheckhere) {
			paycheckhere = false;
			leave();
			return true;
		}
			for(Table table : tables){
				if(!table.isOccupied()){
					
					//System.err.println("there is a table not occupied");
					//System.out.println("table isnt occupied!!!");
					//table.tableNumber++;System.out.println("yes");

					if(!waiterList.isEmpty()) {
						for(int i = 0; i < waiterList.size(); i++) { 
							
							if(waiterAsk()) { 
								System.out.println("have asked break " + waiterList.get(i).getName());
								if(waiterList.size() <= 1) {

									RespondNo(waiterList.get(i));
									//setWaiterAsk(false);
									return true;
								}
								else{

									RespondYes(waiterList.get(i));

									//setWaiterAsk(false);
									return true;
								}


							}
						}
						
						if (!waitingCustomers.isEmpty()) {
						
							//for(WaiterAgent w : waiterList) {
							if(waiterList.size() > 1){
								for(int i = 0; i < waiterList.size() - 1; i++){
									if((waiterList.get(i).getWaitingCustomers().size() >= 
											waiterList.get(i+1).getWaitingCustomers().size()) && (!waiterList.get(i+1).onBreak())) {

										lowest = waiterList.get(i+ 1).getWaitingCustomers().size();
									}
									else {
										if(!waiterList.get(i).onBreak()) {
											lowest = waiterList.get(i).getWaitingCustomers().size();}
									}
								}

								for(int i = 0; i < waiterList.size(); i++){
									if(lowest == waiterList.get(i).getWaitingCustomers().size() && (!waiterList.get(i).onBreak())) {
										System.out.println("going to seat customer");
										seatCustomer(waiterList.get(i),waitingCustomers.get(0), table);
										
										return true;
									}	
									else {
										seatCustomer(waiterList.get(i +1 ), waitingCustomers.get(0), table);
								
										return true;
									}
								}
							}

							else if(waiterList.size() == 1){

								seatCustomer(waiterList.get(0), waitingCustomers.get(0), table);
								
								return true;
							}
						}
						/*if(!tablef) { System.err.println("TABLE FULL");
						tellCustomerfull(waitingCustomers.get(0));
						return true;
						}*/
					}
				}

			}

			



		//}




			
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.

	}

	// Actions

	private void seatCustomer(McNealWaiter wa,  McNealCustomer customer, Table table) {
		System.out.println("on that seat customer grind");


		wa.msgSitAtTable(wa, customer, table);
		table.setOccupant(customer);
		wa.setwithCustomer(true);
		waitingCustomers.remove(customer);


	}
	private void RespondNo(McNealWaiter waiterAgent) { 
		Do(" NOT OK for break, " + waiterAgent.getName());
		setWaiterAsk(false);
		waiterAgent.msgBreakReply(false);
		//wa.setOnBreak(false);
		//r.uncheckk(wa.getName());

	}

	private void RespondYes(McNealWaiter waiterAgent) { 
		Do(" OK you may go on break after serving customers, " + waiterAgent.getName());
		//wa.setOnBreak(false);
		setWaiterAsk(false);
		waiterAgent.msgBreakReply(true);
		//r.uncheckkenable(wa.getName());

	}

	private void tellCustomerfull(McNealCustomer c) {
		c.msgTableFull();
	}



private void leave() {
	person.msgLeavingLocation(this);
}





	// The animation DoXYZ() routines


	//utilities



	public class Table {
		McNealCustomer occupiedBy;
		int tableNumber; 

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
			//System.out.println("table number " + tableNumber);
		}
		public int getNumber() {
			return tableNumber;
		}

		void setOccupant(McNealCustomer customer) {
			occupiedBy = customer;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		McNealCustomer getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}





	@Override
	public void msgAtBuilding(Building building) {
		// TODO Auto-generated method stub
		
	}










}

