package housing.test.mock;

import interfaces.Person;

import java.util.ArrayList;
import java.util.List;

import global.PersonAgent;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import gui.animation.building.HouseGUI;
import housing.interfaces.House;
import housing.interfaces.HousePerson;
import housing.interfaces.Landlord;

public class MockHouse extends MockRole implements House{
	public HousePerson houseperson;
	public HouseGUI housegui;

	public Landlord landlord;
	String name;
	String location = "home";
	public MockHouse(String name, boolean isApt) {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgRentDue(Landlord l, int rentrate) {
		// TODO Auto-generated method stub
		houseperson.getBills().add(8);
		log.add(new LoggedEvent("Received rent due from Landlord"));
	}

	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved at location"));
		
	}

	@Override
	public void setLandlord(Landlord l) {
		landlord = l;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHousePerson(HousePerson h) {
		houseperson = h;
		
	}


	@Override
	public void msgLeavingLocation(Role r) {
		log.add(new LoggedEvent("Recieved Leaving Location"));// TODO Auto-generated method stub
		
	}

	

	

	@Override
	public void msgUpdateTime(int time, int day) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved Time " + time));
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return location;
	}

	@Override
	public void setLocation(String l) {
		// TODO Auto-generated method stub
		location = l;
		
	}

	@Override
	public int getStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStartTime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCloseTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCloseTime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public void setOccupant(HousePerson h) {
		// TODO Auto-generated method stub
		houseperson = h;
	}

	@Override
	public HousePerson getOccupant() {
		// TODO Auto-generated method stub
		return houseperson;
	}

	@Override
	public HouseGUI getHouseGui() {
		// TODO Auto-generated method stub
		return housegui;
	}

	@Override
	public void setHouseGui(HouseGUI homegui) {
		// TODO Auto-generated method stub
		housegui = homegui;
		
	}


}
