package housing.test.mock;

import global.test.mock.LoggedEvent;
import global.test.mock.MockRole;
import housing.interfaces.House;
import housing.interfaces.HousePerson;
import housing.interfaces.Landlord;

public class MockLandlord extends MockRole implements Landlord{

	public MockLandlord(String name) {
		super();
		// TODO Auto-generated constructor stub
	}





	public void msgHereIsRent(HousePerson h, float money) {
		log.add(new LoggedEvent("Received rent"));
	
		
	}





	
}
