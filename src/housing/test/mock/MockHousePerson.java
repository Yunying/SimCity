package housing.test.mock;

import global.actions.Action;
import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import housing.interfaces.House;
import housing.interfaces.HousePerson;
import interfaces.Person;

import java.util.ArrayList;
import java.util.List;



public class MockHousePerson extends MockRole implements HousePerson {

	public House house;
	int rentrate = 30;
	public List<Integer> bills = new ArrayList<Integer>();
	public MockHousePerson(String name) {
		super();
		// TODO Auto-generated constructor stub
	}

	

	
	/*public void msgFromMarketHeresFood(List<String> foodlist) {
		log.add(new LoggedEvent("Received food from market"));
		// TODO Auto-generated method stub
		
	}
*/
	@Override
	public void msgComeIntoHouse(House h, Person p, List<Action> listofActions) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved Message to Come into house"));
		
		if(bills.isEmpty() == false) {
			log.add(new LoggedEvent("Recieved Bills to pay"));
		}
	}

	@Override
	public void setHouse(House h) {
		house = h;
	}
	public House getHouse() {
		// TODO Auto-generated method stub
		return house;
	}

	@Override
	public List<Integer> getBills() {
		// TODO Auto-generated method stub
		return bills;
	}



	@Override
	public int getRentRate() {
		// TODO Auto-generated method stub
		return rentrate;
	}



	@Override
	public void setRent(int rent) {
		rentrate = rent;
		// TODO Auto-generated method stub
		
	}




	@Override
	public void msgUpdateTime(int time, int day) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Time Updated to " + time));
	}




	@Override
	public void msgAtLoc() {
		// TODO Auto-generated method stub
		
	}







}
