package housing.test.mock;

import java.util.ArrayList;
import java.util.List;

import housing.interfaces.House;
import interfaces.Building;
import interfaces.Person;
import global.PersonAgent.Job;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import global.test.mock.Mock;

public class MockPersonForHouse extends Mock implements Person{
	String name;
	int time;
	float money = 100f;
	String b;
	ArrayList<Building> buildings = new ArrayList<Building>();
	public MockPersonForHouse(String name, ArrayList<Building> buildings, House h ){
	
		super();
		this.name = name;
		
	}

	@Override
	public void msgLeavingLocation(Role r) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("recieved leaving location"));
		
	}

	@Override
	public void msgUpdateTime(int time, int day){
		
		this.time = time;
		log.add(new LoggedEvent("recieved update time"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public Role getRole(String role) {//dont need to test this message hence the null return
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public float getMoney() {
		// TODO Auto-generated method stub
		return money;
	}

	@Override
	public void setMoney(float f) {
		// TODO Auto-generated method stub
		money = f;
		
	}

	@Override
	public void ChangeMoney(float m) {
		// TODO Auto-generated method stub
		money += m;
		
	}

	@Override
	public void AddTaskWakeUpAndGetReady() {
		
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void AddTaskGetFood() {
		log.add(new LoggedEvent("Recieved log buy food"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void AddTaskBuyFoodAtMarket(List<Action> a) {
		log.add(new LoggedEvent("Recieved Ask Buy Food at Market"));
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void AddTaskBuyCarAtMarket() {//no need for this message
		// TODO Auto-generated method stub
		
	}

	@Override
	public void AddTaskGoHome() {//no need for this message
		// TODO Auto-generated method stub
		
	}

	@Override
	public Building getBuilding(String building) {//no need for this message
		return null;
		// TODO Auto-generated method stub
		//return buildings.;
	}

	@Override
	public void setWorking(boolean t) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void AddTaskGoToBankForLoan() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void AddTaskDepositEarnings(Building building, float f) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setHasCar(boolean hasCar) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void stateChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCurrentTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Job> getJobs() {
		// TODO Auto-generated method stub
		return null;
	}
}
