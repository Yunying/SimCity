package global.test.mock;

import java.util.*;
import market.MarketAgent;
import market.interfaces.Market;
import restaurant.cammarano.CammaranoRestaurantAgent;
import restaurant.ji.*;
import bank.interfaces.*;
import bank.test.mock.*;
import global.PersonAgent.Job;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import global.test.mock.Mock;
import interfaces.*;


public class MockPerson extends Mock implements Person {
	
	String name;
	List<Building> buildings;
	List<Role> roles;
	List<Job> jobs;
	float money;
	boolean hasCar;
	
	public MockPerson(String name, List<Building> b) {
		super();
		this.name = name;
		buildings = b;
		jobs = new ArrayList<Job>();
		money = 0f;
		roles = new ArrayList<Role>();
		roles.add(new MockTeller());
		roles.add(new MockSecurity());
		roles.add(new MockBankPatron());
	}

	@Override
	public void msgLeavingLocation(Role r) {
		log.add(new LoggedEvent("Received msgLeavingLocation"));
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		log.add(new LoggedEvent("Received msgUpdateTime"));
		
	}

	@Override
	public Role getRole(String role) {
		for (Role r : roles){
			if (role.toUpperCase().startsWith("BANKTELLER") && r instanceof MockTeller) {
					return r;
			}
			else if (role.toUpperCase().startsWith("BANKPATRON") && r instanceof MockBankPatron) {
				return r;
			}
			
			else if (role.toUpperCase().startsWith("BANKSECURITY") && r instanceof MockSecurity) {
				return r;
			}
		}
		return null;
	}

	@Override
	public float getMoney() {
		return money;
	}

	@Override
	public void setMoney(float f) {
		money = f;
	}

	@Override
	public void ChangeMoney(float m) {
		money += m;
	}

	@Override
	public void AddTaskWakeUpAndGetReady() {
		
	}

	@Override
	public void AddTaskGetFood() {
		
	}

	@Override
	public void AddTaskBuyFoodAtMarket(List<Action> a) {
		// TODO Auto-generated method stub
	}

	@Override
	public void AddTaskBuyCarAtMarket() {
		// TODO Auto-generated method stub
	}

	@Override
	public void AddTaskGoHome() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Building getBuilding(String building) {
		for (Building b : buildings) {
			if (b.getName().contains("Bank")) {
				if (b instanceof Bank) {
					return (Bank) b;
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

	@Override
	public void setWorking(boolean t) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void AddTaskGoToBankForLoan() {
		log.add(new LoggedEvent("AddTaskDepositEarnings called."));
	}

	@Override
	public void AddTaskDepositEarnings(Building building, float f) {
		log.add(new LoggedEvent("AddTaskDepositEarnings called"));
	}

	@Override
	public void stateChanged() {
		// TODO Auto-generated method stub
	}
	public void setHasCar(boolean hasCar) {
		this.hasCar = hasCar;
	}

	@Override
	public int getCurrentTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Job> getJobs() {
		return jobs;
	}
}
