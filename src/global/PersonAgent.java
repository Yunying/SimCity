package global;

import gui.animation.PersonGUI;
import restaurant.cammarano.roles.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import global.roles.*;
import global.actions.*;
import interfaces.*;
import agent.*;
import bank.*;
import bank.interfaces.Bank;
import global.enumerations.*;
import global.test.mock.LoggedEvent;
import market.*;
import market.interfaces.Market;
import housing.*;
import transportation.*;
import restaurant.cammarano.*;
import restaurant.ji.*;
import restaurant.yunying.TuRestaurantAgent;
import restaurant.yunying.roles.*;
import restaurant.ji.interfaces.*;
import restaurant.ji.roles.*;
import restaurant.mcneal.McNealRestaurantAgent;
import restaurant.mcneal.roles.*;
//import restaurant.redland.RedlandRestaurantAgent;
//import restaurant.mcneal.roles.McNealCustomerRole;
import restaurant.yunying.roles.TuCustomerRole;


/**
 * @author Colin Cammarano
 *
 */
public class PersonAgent extends Agent implements Person {

	//------------------------------------------------------------------------//
	// Data
	//------------------------------------------------------------------------//
	private String name;
	private int currentTime;
	private int currentDay;
	private int bedTime;
	private int wakeTime;
	private int lunchTime;
	private int dinnerTime;
	private float money; // Our current money
	private boolean hasCar;
	public boolean isWorking;
	
	public boolean testingDisableAnimationCalls;
	
	private PersonGUI gui;
	private Semaphore atLocation;
	private HouseAgent home;
	private Building currentBuilding;

	// Public only for unit testing
	public List<Role> roles; // Our main list of roles.
	public List<Job> jobs; // Keeps track of any jobs the person has (for assigning roles)
	public List<PersonAction> actionsToComplete; // This governs what we do when we do not have a role to complete.
	public List<Time> times; // Keep track of the time of day.
	public List<Building> buildings;

	
	public PersonAgent(String n, List<Building> b, HouseAgent h) {
		super();
		testingDisableAnimationCalls = false;
		this.name = n;
		currentTime = 0;
		currentDay = 0;
		bedTime = 42;
		wakeTime = 14;
		lunchTime = 24;
		dinnerTime = 34;
		money = 100.0f;
		hasCar = false;
		isWorking = false;
				
		gui = null;
		atLocation = new Semaphore(0, true);

		roles = Collections.synchronizedList(new ArrayList<Role>());
		jobs =  Collections.synchronizedList(new ArrayList<Job>());
		actionsToComplete =  Collections.synchronizedList(new ArrayList<PersonAction>());
		times = Collections.synchronizedList(new ArrayList<Time>());
		buildings = b;

		if(h != null) {
			// Add basic house person role -- this role is never removed or replaced
			home = h;
			HousePersonRole hPR = new HousePersonRole();
			hPR.setPerson(this);
			home.setOccupant(hPR);
			roles.add(hPR);
			currentBuilding = home;
		}

		else {
			if(buildings != null) {
				for (Building bu : buildings) {
					if(bu instanceof HouseAgent) {
						home = (HouseAgent)bu;
					}
				}
				HousePersonRole hPR = new HousePersonRole();
				hPR.setPerson(this);
				home.setOccupant(hPR);
				roles.add(hPR);
				currentBuilding = home;
			}
		}
	}

	//------------------------------------------------------------------------//
	// Messages
	//------------------------------------------------------------------------//
	@Override
	public void msgLeavingLocation(Role r) {
		r.isActive = false;
		this.AddTaskGoHome();
		stateChanged();
	}
	
	// Fromt the animation
	public void msgAtDestination() {
		atLocation.release();
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		currentTime = time;
		times.add(new Time(time));
		
		if(home != null) {
			HousePersonRole r = null;
			for (Role role : roles) {
				if(role instanceof HousePersonRole) {
					r = (HousePersonRole)role;
				}
			}
			r.msgUpdateTime(time, day);
		}
		
		stateChanged();
	}

	//------------------------------------------------------------------------//
	// Scheduler
	//------------------------------------------------------------------------//
	@Override
	public boolean pickAndExecuteAnAction() {
		//--------------------------------------------------------------------//
		// Scheduler actions for primary roles (jobs etc)
		//--------------------------------------------------------------------//
		// First rule is always transportation -- do we need to go somewhere and not walk?
		boolean anytrue = false;
		
		try {
			for (Role r : roles) {
				if (r instanceof TransportationRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r.isActive == true) {
					if (r instanceof CammaranoHostRole) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof CammaranoWaiterRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof CammaranoSharedWaiterRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof CammaranoCashierRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof CammaranoCookRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof JiHostRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof JiWaiterRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			} 
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof JiSharedWaiterRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			} 
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof JiCashierRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof JiCookRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof McNealHostRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof McNealWaiterRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof McNealCashierRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof McNealCookRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof TuHostRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof TuWaiterRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof TuShareDataWaiterRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof TuCashierRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof TuCookRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		/*
		for (Role r : roles) {
			if (r instanceof RedlandHostRole) {
				if (r.isActive == true) {
					anytrue = r.pickAndExecuteAnAction();
					return true;
				}
			}
		}

		for (Role r : roles) {
			if (r instanceof RedlandWaiterRole) {
				if (r.isActive == true) {
					anytrue = r.pickAndExecuteAnAction();
					return true;
				}
			}
		}
		
		for (Role r : roles) {
			if (r instanceof RedlandCashierRole) {
				if (r.isActive == true) {
					anytrue = r.pickAndExecuteAnAction();
					return true;
				}
			}
		}
		
		for (Role r : roles) {
			if (r instanceof RedlandCookRole) {
				if (r.isActive == true) {
					anytrue = r.pickAndExecuteAnAction();
					return true;
				}
			}
		}
		*/
		
		try {
			for (Role r : roles) {
				if (r instanceof BankTellerRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof BankSecurityRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof LandlordRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		try {
			for (Role r : roles) {
				if (r instanceof MarketEmployeeRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		try {
			for (Role r : roles) {
				if (r instanceof MarketManagerRole) {
					if (r.isActive == true) {
						anytrue = r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}

		//--------------------------------------------------------------------//
		// Scheduler actions for filling the action list. These times should come before checking the house role.
		//--------------------------------------------------------------------//
		if(jobs.size() > 0) {
			synchronized(times) {
				for(Time t : times) {
					if(t.hasPassed == false) {
						for (Job job : jobs) {
							if(t.time == job.startTime) {
								AddTaskGoToWork(job);
								t.hasPassed = true;
								return true;
							}
						}
					}
				}
			}
		}
		
		synchronized(times) {
			for (Time t : times) {
				if(t.hasPassed == false) {
					if (t.time == wakeTime) {
						AddTaskWakeUpAndGetReady();
						t.hasPassed = true;
						return true;
					}
				}
			}
		}
		
		synchronized(times) {
			for (Time t : times) {
				if(t.hasPassed == false) {
					if(isWorking == false) {
						if (t.time == lunchTime) {
							AddTaskGetFood();
							t.hasPassed = true;
							return true;
						}
					}
				}
			}
		}

		synchronized(times) {
			for (Time t : times) {
				if(t.hasPassed == false) {
					if(isWorking == false) {
						if (t.time == dinnerTime) {
							AddTaskGetFood();
							t.hasPassed = true;
							return true;
						}
					}
				}
			}
		}
		
		synchronized(times) {
			for (Time t : times) {
				if(t.hasPassed == false) {
					if (t.time == bedTime) {
						AddTaskGoHome();
						t.hasPassed = true;
						return true;
					}
				}
			}
		}
		
		//--------------------------------------------------------------------//
		// Primary person actions
		//--------------------------------------------------------------------//
		// Working
		for (PersonAction action : actionsToComplete) {
			if (action.task.equals("work")) {
				if(action.isActive == true) {
					GoToWork(action);
					return true;
				}
			}
		}

		for (PersonAction action : actionsToComplete) {
			if(action.task.equals("wakeup")) {
				if(action.isActive == true) {
					GoHome(action);
					return true;
				}
			}
		}
		
		for (PersonAction action : actionsToComplete) {
			if (action.task.equals("cashbusiness")) {
				if(action.isActive == true) {
					GoToTheBank(action);
					return true;
				}
			}
		}
		
		//--------------------------------------------------------------------//
		// Scheduler actions for secondary roles (going to the bank, market, restaurant etc)
		//--------------------------------------------------------------------//
		for (Role r : roles) {
			if (r.isActive == true) {
				if (r instanceof BankPatronRole) {
					r.pickAndExecuteAnAction();
					return true;
				}
			}
		}
		
		for (Role r : roles) {
			if (r.isActive == true) {
				if (r instanceof MarketCustomerRole) {
					r.pickAndExecuteAnAction();
					return true;
				}
			}
		}
		
		try {
			for (Role r : roles) {
				if (r.isActive == true) {
					if (r instanceof CammaranoCustomerRole) {
						r.pickAndExecuteAnAction();
						return true;
					}
				}
			}
		} catch (ConcurrentModificationException c) {}
		
		for (Role r : roles) {
			if (r.isActive == true) {
				if (r instanceof JiCustomerRole) {
					r.pickAndExecuteAnAction();
					return true;
				}
			}
		}
		
		/*
		for (Role r : roles) {
			if (r.isActive == true) {
				if (r instanceof RedlandCustomerRole) {
					anytrue = r.pickAndExecuteAnAction();
					return true;
				}
			}
		}
		*/
		
		for (Role r : roles) {
			if (r.isActive == true) {
				if (r instanceof McNealCustomerRole) {
					r.pickAndExecuteAnAction();
					return true;
				}
			}
		}

		for (Role r : roles) {
			if (r.isActive == true) {
				if (r instanceof TuCustomerRole) {
					r.pickAndExecuteAnAction();
					return true;
				}
			}
		}
		
		//--------------------------------------------------------------------//
		// Secondary person actions
		//--------------------------------------------------------------------//
		for (PersonAction action : actionsToComplete) {
			if (action.task.equals("bank")) {
				if(action.isActive == true) {
					GoToTheBank(action);
					return true;
				}
			}
		}
		
		for (PersonAction action : actionsToComplete) {
			if (action.task.equals("market")) {
				if(action.isActive == true) {
					GoToMarket(action);
					return true;
				}
			}
		}
		
		for (PersonAction action : actionsToComplete) {
			if (action.task.equals("eatrest")) {
				if(action.isActive == true) {
					GoToRestaurant(action);
					return true;
				}
			}
		}
		
		for (PersonAction action : actionsToComplete) {
			if (action.task.equals("gohome")) {
				if(action.isActive == true) {
					GoHome(action);
					return true;
				}
			}
		}

		//--------------------------------------------------------------------//
		// Staying at home -- last priority
		//--------------------------------------------------------------------//
		for (Role r : roles) {
			if (r.isActive == true) {
				if (r instanceof HousePersonRole) {
					return r.pickAndExecuteAnAction();
				}
			}
		}
		
		return false;
	}

	public void AddTaskGoToWork(Job j) {
		log.add(new LoggedEvent("AddTaskGoToWork called"));
		Role role = null;
		try {
			role = (Role)Class.forName(j.job).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		role.setPerson(this);
		roles.add(role);
		actionsToComplete.add(new PersonAction("work", j.building, role, null));
	}

	// This task wakes up the person agent and has them begin by eating at home.
	@Override
	public void AddTaskWakeUpAndGetReady() {
		log.add(new LoggedEvent("AddTaskWakeUpAndGetReady called"));
		HousePersonRole r = null;
		for (Role role : roles) {
			if(role instanceof HousePersonRole) {
				r = (HousePersonRole)role;
			}
		}
		
		ArrayList<Action> eat = new ArrayList<Action>();
		eat.add(new Action("eat"));
			
		actionsToComplete.add(new PersonAction("gohome", home, r, eat));
	}
	
	// This task has the person going to get food. They pick to eat at home or a restaurant.
	@Override
	public void AddTaskGetFood() {
		log.add(new LoggedEvent("AddTaskGetFood called"));
		print("I'm hungry, it's time to get food.");
		
		// We need to check if we are working. If we are, delay this and eat at home
		if(isWorking == true) {
			for (PersonAction action : actionsToComplete) {
				print("I have to wait until I get off of work.");
				if(action.task.equals("gohome")) {
					return;
				}
			}
			
			AddTaskEatAfterWork();
			return;
		}
		
		if(money >= 6.00f) {
			// Restaurant is randomly selected
			Random rand = new Random();
			int random = rand.nextInt(5);
			if (name.equals("CCustomer")){
				random = 0;
			}
			else if (name.equals("JCustomer")){
				random = 1;
			}
			else if (name.equals("ECustomer")){
				random = 3;
			}
			else if (name.equals("KCustomer")){
				random = 4;
			}
			else if (name.equals("RCustomer")){
				random = 2;
			}

			switch(random) {
				case 0:
					print("Going to Colin's restaurant.");
					CammaranoRestaurantAgent rc = null;
					for (Building b : buildings) {
						if (b instanceof CammaranoRestaurantAgent) {
							rc = (CammaranoRestaurantAgent) b;
						}
					}
					
					CammaranoCustomerRole rcRole = new CammaranoCustomerRole();
					rcRole.setPerson(this);
					roles.add(rcRole);

					actionsToComplete.add(new PersonAction("eatrest", rc, rcRole, null));
					break;
				
				case 1:
					print("Going to Cathy's restaurant.");
					JiRestaurantAgent rj = null;
					for (Building b : buildings) {
						if (b instanceof JiRestaurantAgent) {
							rj = (JiRestaurantAgent) b;
						}
					}

					JiCustomerRole rjRole = new JiCustomerRole();
					rjRole.setPerson(this);
					roles.add(rjRole);

					actionsToComplete.add(new PersonAction("eatrest", rj, rjRole, null));
					break;
					
				case 2:
					print("Going to Jeff's restaurant.");
					CammaranoRestaurantAgent rr = null;
					for (Building b : buildings) {
						if (b instanceof CammaranoRestaurantAgent) {
							rr = (CammaranoRestaurantAgent) b;
						}
					}
					
					CammaranoCustomerRole rrRole = new CammaranoCustomerRole();
					rrRole.setPerson(this);
					roles.add(rrRole);

					actionsToComplete.add(new PersonAction("eatrest", rr, rrRole, null));
					break;
					/*
					RedlandRestaurantAgent rr = null;
					for (Building b : buildings) {
						if (b instanceof RedlandRestaurantAgent) {
							rr = (RedlandRestaurantAgent) b;
						}
					}

					RedlandCustomerRole rrRole = new RedlandCustomerRole();
					rrRole.setPerson(this);
					roles.add(rrRole);

					actionsToComplete.add(new PersonAction("eatrest", rj, rrRole));
					
					break;
					*/
	
				case 3:
					print("Going to Elsie's restaurant.");
					TuRestaurantAgent rt = null;
					for (Building b : buildings) {
						if (b instanceof TuRestaurantAgent) {
							rt = (TuRestaurantAgent) b;
						}
					}

					TuCustomerRole rtRole = new TuCustomerRole();
					rtRole.setPerson(this);
					roles.add(rtRole);

					actionsToComplete.add(new PersonAction("eatrest", rt, rtRole, null));
					break;
					
				case 4:
					print("Going to Kristen's restaurant.");
					McNealRestaurantAgent rm = null;
					for (Building b : buildings) {
						if (b instanceof McNealRestaurantAgent) {
							rm = (McNealRestaurantAgent) b;
						}
					}

					McNealCustomerRole rmRole = new McNealCustomerRole();
					rmRole.setPerson(this);
					roles.add(rmRole);

					actionsToComplete.add(new PersonAction("eatrest", rm, rmRole, null));
					break;

			}
		}
		
		else {
			print("Going home to eat.");
			AddTaskGoHomeToEat();
		}
	}
	
	public void AddTaskEatAfterWork() {
		AddTaskGoHomeToEat();
	}
	
	// This task has the person going to buy food. The ShoppingList function must be called first.
	@Override
	public void AddTaskBuyFoodAtMarket(List<Action> a) {
		log.add(new LoggedEvent("AddTaskBuyFoodAtMarket called"));
		MarketAgent temp = null;
		for (Building b : buildings) {
			if(b instanceof MarketAgent) {
				temp = (MarketAgent)b;
			}
		}
		
		MarketCustomerRole m = new MarketCustomerRole();
		m.setPerson(this);
		roles.add(m);
		
		actionsToComplete.add(new PersonAction("market", temp, m, a));
	}

	// This task has the person going to the market to buy a car
	@Override
	public void AddTaskBuyCarAtMarket() {
		log.add(new LoggedEvent("AddTaskBuyCarAtMarket called"));
		MarketAgent temp = null;
		for (Building b : buildings) {
			if(b instanceof MarketAgent) {
				temp = (MarketAgent)b;
			}
		}
	
		MarketCustomerRole m = new MarketCustomerRole();
		m.setPerson(this);
		roles.add(m);
		
		// hacky method of telling market that we want a car
		List<Action> buyCar = new ArrayList<Action>();
		buyCar.add(new Action("car"));
		
		actionsToComplete.add(new PersonAction("market", temp, m, buyCar));
	}

	// This task has the person going home
	@Override
	public void AddTaskGoHome() {
		
		HousePersonRole r = null;
		for (Role role : roles) {
			if(role instanceof HousePersonRole) {
				r = (HousePersonRole)role;
			}
		}
			
		actionsToComplete.add(new PersonAction("gohome", home, r, new ArrayList<Action>()));
	}
	
	public void AddTaskGoHomeToEat() {
		// key is eat
		HousePersonRole r = null;
		for (Role role : roles) {
			if(role instanceof HousePersonRole) {
				r = (HousePersonRole)role;
			}
		}
		
		ArrayList<Action> eat = new ArrayList<Action>();
		eat.add(new Action("eat"));
			
		actionsToComplete.add(new PersonAction("gohome", home, r, eat));
	}
	
	public void AddTaskGoToBank() {
		log.add(new LoggedEvent("AddTaskGoToBank called"));
		// determine key based on money
		BankAgent temp = null;
		for (Building b : buildings) {
			if(b instanceof BankAgent) {
				temp = (BankAgent)b;
			}
		}
		
		BankPatronRole bP = new BankPatronRole();
		bP.setPerson(this);
		roles.add(bP);
		
		ArrayList<Action> bankActions = new ArrayList<Action>();
		bankActions.add(new Action("getsummary"));
		
		if(name.equalsIgnoreCase("Chris-R")) {
			bankActions.add(new Action("robBank"));
		}
		
		if(money <= 5) {
			bankActions.add(new Action("withdraw$" + 20.0f));
		}
		
		if(money >= 20000) {
			bankActions.add(new Action("deposit$" + 2000.0f));
		}
		
		actionsToComplete.add(new PersonAction("bank", temp, bP, bankActions));
	}
	
	@Override
	public void AddTaskGoToBankForLoan() {
		// determine key based on money
		BankAgent temp = null;
		for (Building b : buildings) {
			if(b instanceof BankAgent) {
				temp = (BankAgent)b;
			}
		}
		
		BankPatronRole bP = new BankPatronRole();
		bP.setPerson(this);
		roles.add(bP);
		
		ArrayList<Action> bankActions = new ArrayList<Action>();
		bankActions.add(new Action("getsummary"));
		bankActions.add(new Action("borrow$" + 10000.0f));
		
		actionsToComplete.add(new PersonAction("bank", temp, bP, bankActions));
	}
	
	@Override
	public void AddTaskDepositEarnings(Building building, float f) {
		Do("deposit earnings task called");
		log.add(new LoggedEvent("AddTaskDepositEarnings called"));
		BankAgent temp = null;
		for (Building b : buildings) {
			if(b instanceof BankAgent) {
				temp = (BankAgent)b;
			}
		}
		
		BankPatronRole bP = new BankPatronRole((BusinessAgent)building);
		bP.setPerson(this);
		roles.add(bP);
		
		ArrayList<Action> bankActions = new ArrayList<Action>();
		bankActions.add(new Action("deposit$" + f));
		actionsToComplete.add(new PersonAction("bank", temp, bP, bankActions));
	}
	
	// Going to work
	private void GoToWork(PersonAction a) {
		print("I'm going to work at " + a.building.getName());
		log.add(new LoggedEvent("GoToWork called"));
		
		
        //Transportation
        TransportationRole tR = new TransportationRole(a.actions);
        tR.setPerson(this);
        roles.add(tR);
        for (Building b:buildings){
        	if (b instanceof BusStop){
        		BusStop bs = (BusStop) b;
        		tR.addBusStop(bs);
        	}
        }
                for(Role r : roles) {
                     if (r instanceof TransportationRole) {
                     tR = (TransportationRole)r;
                     tR.isActive = true;
                     tR.setNextRole(a.role);
                     tR.msgGoToBuilding(currentBuilding, a.building, hasCar);
                     break;
                 }
        }
		
		if(testingDisableAnimationCalls == false) {
			DoGoToLocation(a.building.getLocation());

			try {
				atLocation.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		isWorking = true;
		
		roles.get(0).isActive = false;
		
		a.role.isActive = true;
		currentBuilding = a.building;
//		if (a.building.getClass().toString().contains("Bank") || a.building.getClass().toString().contains("Ji")){
//			((Employee)a.role).msgAtBuilding(a.building);
//		}
//		else
//			a.building.msgAtLocation(this, a.role, a.actions);
		a.isActive = false;
	}
	
	// Go home
	private void GoHome(PersonAction a) {
		print("Going home to " + a.building.getName());
		log.add(new LoggedEvent("GoHome called"));
		
		//Transportation
		TransportationRole tR = new TransportationRole(a.actions);
        tR.setPerson(this);
        roles.add(tR);
        for (Building b:buildings){
        	if (b instanceof BusStop){
        		BusStop bs = (BusStop) b;
        		tR.addBusStop(bs);
        	}
        }
                for(Role r : roles) {
                     if (r instanceof TransportationRole) {
                     tR = (TransportationRole)r;
                     tR.isActive = true;
                     tR.setNextRole(a.role);
                     tR.msgGoToBuilding(currentBuilding, a.building, hasCar);
                     break;
                 }
        }
		
		if(testingDisableAnimationCalls == false) {
			DoGoToLocation(a.building.getLocation());

			try {
				atLocation.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		a.role.isActive = true;
		//a.building.msgAtLocation(this, a.role, a.actions);
//		if (a.role != null){
//			print("role is not null in person agent");
//		}
		currentBuilding = a.building;
		a.isActive = false;
	}
	
	// Go to the predetermined restaurant
	private void GoToRestaurant(PersonAction a) {
		print("Going to eat at " + a.building.getName());
		log.add(new LoggedEvent("GoToRestaurantToEat called"));
		
		
		TransportationRole tR = new TransportationRole(a.actions);
        tR.setPerson(this);
        roles.add(tR);
        for (Building b:buildings){
        	if (b instanceof BusStop){
        		BusStop bs = (BusStop) b;
        		tR.addBusStop(bs);
        	}
        }
                for(Role r : roles) {
                     if (r instanceof TransportationRole) {
                     tR = (TransportationRole)r;
                     tR.isActive = true;
                     tR.setNextRole(a.role);
                     tR.msgGoToBuilding(currentBuilding, a.building, hasCar);
                     break;
                 }
        }
		
		
		if(testingDisableAnimationCalls == false) {
			DoGoToLocation(a.building.getLocation());

			try {
				atLocation.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		a.role.isActive = true;
		//a.building.msgAtLocation(this, a.role, a.actions);
		currentBuilding = a.building;
		
		a.isActive = false;
	}

	private void GoToTheBank(PersonAction a) {
		print("Going to bank " + a.building.getName());
		log.add(new LoggedEvent("GoToTheBank called"));
		

		TransportationRole tR = new TransportationRole(a.actions);
        tR.setPerson(this);
        roles.add(tR);
        for (Building b:buildings){
        	if (b instanceof BusStop){
        		BusStop bs = (BusStop) b;
        		tR.addBusStop(bs);
        	}
        }
                for(Role r : roles) {
                     if (r instanceof TransportationRole) {
                     tR = (TransportationRole)r;
                     tR.isActive = true;
                     tR.setNextRole(a.role);
                     tR.msgGoToBuilding(currentBuilding, a.building, hasCar);
                     break;
                 }
        }
		
		if(testingDisableAnimationCalls == false) {
			DoGoToLocation(a.building.getLocation());

			try {
				atLocation.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		a.role.isActive = true;
		a.role.actions = a.actions;
		//a.role.msgAtBuilding(a.building);
		//a.building.msgAtLocation(this, a.role, a.actions);
		currentBuilding = a.building;
		
		a.isActive = false;
	}

	private void GoToMarket(PersonAction a) {
		print("Going to market " + a.building.getName());
		log.add(new LoggedEvent("GoToMarket called"));
		
		TransportationRole tR = new TransportationRole(a.actions);
        tR.setPerson(this);
        roles.add(tR);
        for (Building b:buildings){
        	if (b instanceof BusStop){
        		BusStop bs = (BusStop) b;
        		tR.addBusStop(bs);
        	}
        }
                for(Role r : roles) {
                     if (r instanceof TransportationRole) {
                     tR = (TransportationRole)r;
                     tR.isActive = true;
                     tR.setNextRole(a.role);
                     tR.msgGoToBuilding(currentBuilding, a.building, hasCar);
                     break;
                 }
        }
		
		if(testingDisableAnimationCalls == false) {
			DoGoToLocation(a.building.getLocation());

			try {
				atLocation.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(testingDisableAnimationCalls == false) {
			DoGoToLocation(a.building.getLocation());

			try {
				atLocation.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		a.role.isActive = true;
		//a.building.msgAtLocation(this, a.role, a.actions);
		currentBuilding = a.building;
		
		a.isActive = false;
	}

	//------------------------------------------------------------------------//
	// Animation
	//------------------------------------------------------------------------//
	private void DoGoToLocation(String location) {
		gui.DoGoToLocation(location);
	}

	//------------------------------------------------------------------------//
	// Utilities
	//------------------------------------------------------------------------//
	public class Job {

		private String job;
		private int startTime;
		private int endTime;
		private Building building;

		public Job(String j, int s, int e, Building b) {
			job = j;
			startTime = s;
			endTime = e;
			building = b;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public int getStartTime() {
			return startTime;
		}

		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}

		public int getEndTime() {
			return endTime;
		}

		public void setEndTime(int endTime) {
			this.endTime = endTime;
		}
	}
	
	public class Time {
		private int time;
		public boolean hasPassed;
		
		public Time(int t) {
			time = t;
			hasPassed = false;
		}

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}
	}

	public class PersonAction {

		private String task;
		private Building building;
		private Role role;
		private List<Action> actions;
		private boolean isActive;

		public PersonAction(String t, Building b, Role r, List<Action> a) {
			task = t;
			building = b;
			role = r;
			actions = a;
			isActive = true;
		}

		public String getTask() {
			return task;
		}

		public void setTask(String task) {
			this.task = task;
		}

		public Building getBuilding() {
			return building;
		}

		public void setBuilding(Building building) {
			this.building = building;
		}

		public Role getRole() {
			return role;
		}
		
		public void setRole(Role role) {
			this.role = role;
		}
		
		public boolean getIsActive() {
			return isActive;
		}

		public void setIsActive(boolean isActive) {
			this.isActive = isActive;
		}
	}

	//------------------------------------------------------------------------//
	// Accessors and Mutators
	//------------------------------------------------------------------------//
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public float getMoney() {
		return money;
	}

	@Override
	public void setMoney(float money) {
		this.money = money;
	}

	public boolean getHasCar() {
		return hasCar;
	}

	public void setHasCar(boolean hasCar) {
		this.hasCar = hasCar;
	}
	
	@Override
	public void setWorking(boolean t) {
		isWorking = t;
	}

	public PersonGUI getGui() {
		return gui;
	}

	public void setGui(PersonGUI gui) {
		this.gui = gui;
	}

	public Role getRole(String role) {
		for (Role r : roles) {
			if(r.isActive) {
				if (role.equalsIgnoreCase("BankTellerRole")) {
					if (r instanceof BankTellerRole) {
						return (BankTellerRole) r;
					}
				}

				else if (role.equalsIgnoreCase("BankTellerRole")) {
					if (r instanceof BankSecurityRole) {
						return (BankSecurityRole) r;
					}
				}

				else if (role.equalsIgnoreCase("CammaranoWaiterRole")) {
					if (r instanceof BankSecurityRole) {
						return (BankSecurityRole) r;
					}
				}

				else if (role.equalsIgnoreCase("CammaranoHostRole")) {
					if (r instanceof BankSecurityRole) {
						return (BankSecurityRole) r;
					}
				}

				else if (role.equalsIgnoreCase("CammaranoCookRole")) {
					if (r instanceof BankSecurityRole) {
						return (BankSecurityRole) r;
					}
				}

				else if (role.equalsIgnoreCase("CammaranoCashierRole")) {
					if (r instanceof BankSecurityRole) {
						return (BankSecurityRole) r;
					}
				}
			}
		}

		return null;
	}
	

	public Building getBuilding(String building) {
		for (Building b : buildings) {
			if (b.getName().contains("Bank")) {
				if (b instanceof Bank) {
					return (BankAgent) b;
				}
			}
			else if (b.getName().contains("Market")) {
				if (b instanceof Market) {
					return (MarketAgent) b;
				}
			}
			else if (b.getName().contains("Cammarano")) {
				if (b instanceof CammaranoRestaurantAgent) {
					return (CammaranoRestaurantAgent) b;
				}
			}
			else if (b.getName().contains("Ji")) {
				if (b instanceof JiRestaurantAgent) {
					return (JiRestaurantAgent) b;
				}
			}
		}
			
		return null;
	
	}
	
	// Getting and setting the current time
	public int getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}
	
	// Getting and setting the morning wake up time
	public int getWakeTime() {
		return wakeTime;
	}
	
	public void setWakeTime(int wakeTime) {
		this.wakeTime = wakeTime;
	}
	
	// Getting and setting the person's lunch time
	public int getLunchTime() {
		return lunchTime;
	}
	
	public void setLunchTime(int lunchTime) {
		this.lunchTime = lunchTime;
	}
	
	// Getting and setting the person's dinner time
	public int getDinnerTime() {
		return dinnerTime;
	}
	
	public void setDinnerTime(int dinnerTime) {
		this.dinnerTime = dinnerTime;
	}

	// Getting and setting the person's bedtime
	public int getBedTime() {
		return bedTime;
	}
	
	public void setBedTime(int bedTime) {
		this.bedTime = bedTime;
	}

	public HouseAgent getHome() {
		return home;
	}

	public void setHome(HouseAgent home) {
		this.home = home;
	}
	
	//------------------------------------------------------------------------//
	// Configuration
	//------------------------------------------------------------------------//
	
	// This function handles the person changing their money when they buy things/get paid.
	public void ChangeMoney(float m) {
		money += m;
	}
	
	// For the GUI, this allows jobs to be added to a person
	public void AddJob(JobEnum job, int start, int stop) {
		String pkg = null;
		Building building = null;
		if(job == JobEnum.CammaranoCashierRole || job == JobEnum.CammaranoHostRole || job == JobEnum.CammaranoCookRole || job == JobEnum.CammaranoWaiterRole || job == JobEnum.CammaranoSharedWaiterRole) {
			pkg = "restaurant.cammarano.roles.";
			
			for (Building b : buildings) {
				if(b instanceof CammaranoRestaurantAgent) {
					building = b;
				}
			}
		}
		
		else if(job == JobEnum.JiCashierRole || job == JobEnum.JiHostRole || job == JobEnum.JiCookRole || job == JobEnum.JiWaiterRole|| job == JobEnum.JiSharedWaiterRole) {
			pkg = "restaurant.ji.roles.";
			
			for (Building b : buildings) {
				if(b instanceof JiRestaurantAgent) {
					building = b;
				}
			}
		}
		
//		else if(job == JobEnum.RedlandCashierRole || job == JobEnum.RedlandHostRole || job == JobEnum.RedlandCookRole || job == JobEnum.RedlandWaiterRole) {
//			pkg = "restaurant.redland.roles.";
//			
//			for (Building b : buildings) {
//				if(b instanceof RedlandRestaurantAgent) {
//					building = b;
//				}
//			}
//		}
		
		else if(job == JobEnum.McNealCashierRole || job == JobEnum.McNealHostRole || job == JobEnum.McNealCookRole || job == JobEnum.McNealWaiterRole || job == JobEnum.McNealSharedDataWaiter) {
			pkg = "restaurant.mcneal.roles.";
			
			for (Building b : buildings) {
				if(b instanceof McNealRestaurantAgent) {
					building = b;
				}
			}
		}
		
		else if(job == JobEnum.TuCashierRole || job == JobEnum.TuHostRole || job == JobEnum.TuCookRole || job == JobEnum.TuWaiterRole || job == JobEnum.TuShareDataWaiterRole) {
			pkg = "restaurant.yunying.roles.";
			
			for (Building b : buildings) {
				if(b instanceof TuRestaurantAgent) {
					building = b;
				}
			}
		}
		
		else if(job == JobEnum.BankSecurityRole || job == JobEnum.BankTellerRole) {
			pkg = "bank.";
			
			for (Building b : buildings) {
				if(b instanceof BankAgent) {
					building = b;
				}
			}
		}
		
		else if(job == JobEnum.MarketEmployeeRole || job == JobEnum.MarketManagerRole || job == JobEnum.TruckDriverRole) {
			pkg = "market.";
			
			for (Building b : buildings) {
				if(b instanceof MarketAgent) {
					print("Here set market building");
					building = b;
				}
			}
		}
		
		else if(job == JobEnum.LandlordRole) {
			pkg = "housing.";
			
			for (Building b : buildings) {
				if(b instanceof HouseAgent) {
					building = b;
				}
			}
		}
		
		jobs.add(new Job(pkg + job.toString(), start, stop, building));
	}
	
	// For the GUI, this is for removing jobs.
	public void RemoveJob(JobEnum job) {
		for (Job j : jobs) {
			if(j.job.equals(job.toString())) {
				jobs.remove(j);
			}
		}
	}

	public List<Job> getJobs() { return jobs;} // simple getter to return the person's jobs 
}
