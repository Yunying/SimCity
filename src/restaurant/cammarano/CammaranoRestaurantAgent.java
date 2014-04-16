/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package restaurant.cammarano;

import restaurant.cammarano.roles.base.CammaranoRevolvingStand;
import bank.BankAgent;
import global.BusinessAgent;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import gui.animation.building.restaurant.*;
import interfaces.Building;
import interfaces.Person;
import java.util.ArrayList;
import java.util.*;
import market.MarketAgent;
import market.TruckDriverRole;
import market.interfaces.Market;
import restaurant.cammarano.roles.*;
import restaurant.cammarano.interfaces.*;

/**
 *
 * @author CMCammarano
 */

public class CammaranoRestaurantAgent extends BusinessAgent implements Building {

	/***************************************************************************************************/
	// Data
	/***************************************************************************************************/
	private String name;
	private float money;
	
	private CammaranoHostRole host = null;
	private CammaranoCookRole cook = null;
	private CammaranoCashierRole cashier = null;

	private CammaranoRevolvingStand stand;
	
	private MarketAgent market = null;
	
	private CammaranoRestaurantGUI gui = null;
	
	private enum RestaurantPersonState {
		Idle,
		WaitingForRestaurantWorkers,
		AtRestaurant,
		AtRestaurantWorking,
		Gone,
	};
		
	public List<RestaurantPerson> people;
	private List<Role> peopleInBuilding;
	
	public CammaranoRestaurantAgent(String n, MarketAgent m, BankAgent b) {
		super();
		money = 50000.0f;
		startTime = 18;
		closeTime = 44;
		
		stand = new CammaranoRevolvingStand();
		
		people = Collections.synchronizedList(new ArrayList<RestaurantPerson>());
		peopleInBuilding = new ArrayList<>();
		
		wages.put("host", 20.0f);
		wages.put("waiter", 10.0f);
		wages.put("cashier", 15.0f);
		wages.put("cook", 20.0f);
		
		name = n;
		
		market = m;
		bank = b;
		
		bankAccount = 425;
	}

	/***************************************************************************************************/
	// Messages
	/***************************************************************************************************/
	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		print("We are adding " + p.getName() + " to the restaurant.");
		people.add(new RestaurantPerson(p, r));
		if(!(r instanceof CammaranoCustomerRole)) {
			peopleInBuilding.add(r);
		}
		stateChanged();
	}
	
	public void msgLeavingBuilding(Role r) {
		print(r.getPerson().getName() + " is leaving the restaurant.");
		r.isActive = false;
		PayEmployees(r);
		
		if(r instanceof CammaranoCashierRole) {
			print(r.getPerson().getName() + " is going to deposit our capital");
			r.getPerson().AddTaskDepositEarnings(this, (float)(money / 10));
		}
		
		r.getPerson().AddTaskGoHome();
	}
	
	public void msgOrderDelivered(Map<String,Integer> order, Market market, TruckDriverRole driver, float bill) {
		//cook.msgFoodIsIn(order);
		cashier.msgHereIsTheAmountWeOwe(driver, bill);
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		this.currentTime = time;
		this.currentDay = day;
		stateChanged();
	}
	
	/***************************************************************************************************/
	// Scheduler
	/***************************************************************************************************/
	@Override
	public boolean pickAndExecuteAnAction() {	
		
		synchronized(people) {
			for (RestaurantPerson p : people) {
				if(p.state == RestaurantPersonState.Idle) {
					if(p.role instanceof CammaranoHostRole) {
						AssignHost(p);
						return true;
					}
				}
			}
		}
		
		synchronized(people) {
			for (RestaurantPerson p : people) {
				if(p.state == RestaurantPersonState.Idle) {
					if(p.role instanceof CammaranoCookRole) {
						AssignCook(p);
						return true;
					}
				}
			}
		}
		
		synchronized(people) {
			for (RestaurantPerson p : people) {
				if(p.state == RestaurantPersonState.Idle) {
					if(p.role instanceof CammaranoWaiterRole) {
						AssignWaiter(p);
						return true;
					}
				}
			}
		}
		
		synchronized(people) {
			for (RestaurantPerson p : people) {
				if(p.state == RestaurantPersonState.Idle) {
					if(p.role instanceof CammaranoSharedWaiterRole) {
						AssignSharedWaiter(p);
						return true;
					}
				}
			}
		}
		
		synchronized(people) {
			for (RestaurantPerson p : people) {
				if(p.state == RestaurantPersonState.Idle) {
					if(p.role instanceof CammaranoCashierRole) {
						AssignCashier(p);
						return true;
					}
				}
			}
		}
		
		synchronized(people) {
			for (RestaurantPerson p : people) {
				if(p.state == RestaurantPersonState.Idle) {
					if(p.role instanceof CammaranoCustomerRole) {
						AssignCustomer(p);
						return true;
					}
				}
			}
		}
		
		if(currentTime > closeTime || currentTime < startTime) {
			if(open == true) {
				CloseRestaurant();
				return true;
			}
		}
		
		if(currentTime >= startTime && currentTime <= closeTime) {
			if(open == false) {
				OpenRestaurant();
				return true;
			}
		}
		
		return false;
	}
	/***************************************************************************************************/
	// Actions
	/***************************************************************************************************/
	private void AssignHost(RestaurantPerson p) {
		log.add(new LoggedEvent("AssignHost called"));
		if(host != null) {
			if(host.isActive == true) {
				host.LeaveWork();
			}
		}
		
		host = (CammaranoHostRole)(p.role);
		host.setRestaurant(this);
		host.isActive = true;
		
		print("" + host.getName() + ": I am ready to work as the host!");
		
		p.state = RestaurantPersonState.AtRestaurantWorking;
	}
	
	private void AssignCook(RestaurantPerson p) {
		log.add(new LoggedEvent("AssignCook called"));
		if(cook != null) {
			if(cook.isActive == true) {
				cook.LeaveWork();
			}
		}
		
		p.state = RestaurantPersonState.AtRestaurantWorking;
		cook = (CammaranoCookRole)(p.role);
		cook.cashier = cashier;
		cook.setRestaurant(this);
		cook.setStand(stand);
		print("" + cook.getName() + ": I am ready to work as the cook!");
		cook.isActive = true;
	}
	
	private void AssignCashier(RestaurantPerson p) {
		log.add(new LoggedEvent("AssignCashier called"));
		if(cashier != null) {
			if(cashier.isActive == true) {
				cashier.LeaveWork();
			}
		}
		
		p.state = RestaurantPersonState.AtRestaurantWorking;
		cashier = (CammaranoCashierRole)(p.role);
		cashier.setRestaurant(this);
		print("" + cashier.getName() + ": I am ready to work as the cashier!");
		cashier.isActive = true;
	}
	
	private void AssignSharedWaiter(RestaurantPerson p) {
		log.add(new LoggedEvent("AssignSharedWaiter called"));
		p.state = RestaurantPersonState.AtRestaurantWorking;
		
		CammaranoSharedWaiterRole temp = null;
		if(p.role instanceof CammaranoSharedWaiterRole) {
			temp = (CammaranoSharedWaiterRole)p.role;
			temp.setCashier(cashier);
			temp.setCook(cook);
			temp.setHost(host);
			temp.setStand(stand);
			temp.setRestaurant(this);
		}
		
		print("" + temp.getName() + ": I am ready to work as a waiter!");
		host.AddWaiter(temp);
		temp.isActive = true;
	}
	
	private void AssignWaiter(RestaurantPerson p) {
		log.add(new LoggedEvent("AssignWaiter called"));
		p.state = RestaurantPersonState.AtRestaurantWorking;
		
		CammaranoWaiterRole temp = null;
		if(p.role instanceof CammaranoWaiterRole) {
			temp = (CammaranoWaiterRole)p.role;
			temp.setCashier(cashier);
			temp.setCook(cook);
			temp.setHost(host);
			temp.setRestaurant(this);
		}
		
		print("" + temp.getName() + ": I am ready to work as a waiter!");
		host.AddWaiter(temp);
		temp.isActive = true;
	}
	
	private void AssignCustomer(RestaurantPerson p) {
		log.add(new LoggedEvent("AssignCustomer called"));
		p.state = RestaurantPersonState.AtRestaurant;
		CammaranoCustomerRole temp = null;
		if(p.role instanceof CammaranoCustomerRole) {
			temp = (CammaranoCustomerRole)p.role;
		}
		
		print("" + temp.getName() + ": I am ready to eat!");
		
		if(host == null || cashier == null || cook == null) {
			log.add(new LoggedEvent("Restaurant closed"));
			temp.msgRestaurantClosed();
			return;
		}
		
		temp.setHost(host);
		temp.setCashier(cashier);
		temp.isActive = true;
		temp.gotHungry();
	}
	
	public void OpenRestaurant() {
		log.add(new LoggedEvent("OpenRestaurant called"));
		open = true;
	}
	
	public void CloseRestaurant() {
		log.add(new LoggedEvent("CloseRestaurant called"));
		open  = false;
	}

	public void PayEmployees(Role r) {
		log.add(new LoggedEvent("PayEmployees called"));
		String job = null;
		if(r instanceof CammaranoHostRole) {
			job = "host";	
		}
		
		if(r instanceof CammaranoCashierRole) {
			job = "cashier";	
		}
		
		if(r instanceof CammaranoCookRole) {
			job = "cook";	
		}
		
		if(r instanceof CammaranoWaiterRole) {
			job = "waiter";	
		}
		
		if(r instanceof CammaranoSharedWaiterRole) {
			job = "waiter";	
		}
		
		r.getPerson().ChangeMoney(wages.get(job));
		money -= wages.get(job);
	}
	/***************************************************************************************************/
	// Utilities
	/***************************************************************************************************/
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public float getMoney() {
		return money;
	}
	
	public void setMoney(float money) {
		this.money = money;
	}

	public CammaranoRestaurantGUI getGui() {
		return gui;
	}

	public void setGui(CammaranoRestaurantGUI gui) {
		this.gui = gui;
	}
	
	private class RestaurantPerson {
		private Person person;
		private Role role;
		private RestaurantPersonState state;
		
		public RestaurantPerson(Person p, Role r) {
			person = p;
			role = r;
			state = RestaurantPersonState.Idle;
		}

		public Person getPerson() {
			return person;
		}

		public void setPerson(Person person) {
			this.person = person;
		}

		public Role getRole() {
			return role;
		}

		public void setRole(Role role) {
			this.role = role;
		}
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		return peopleInBuilding;
	}
}
