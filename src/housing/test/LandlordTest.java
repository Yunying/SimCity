package housing.test;

import java.util.ArrayList;
import java.util.List;

import interfaces.Building;
import interfaces.Person;
import global.PersonAgent;
import housing.HouseAgent;
import housing.HousePersonRole;
import housing.LandlordRole;
import housing.test.mock.MockHouse;
import housing.test.mock.MockHousePerson;
import housing.test.mock.MockLandlord;
import housing.test.mock.MockPersonForHouse;
import junit.framework.TestCase;

public class LandlordTest extends TestCase{

	
	
	MockHousePerson houseperson1;
	MockHousePerson houseperson2;
	MockHouse house1;
	MockHouse house2;
	MockPersonForHouse landperson;
	MockPersonForHouse person1;
	MockPersonForHouse person2;
	List<MockHouse> renters = new ArrayList<MockHouse>();
	LandlordRole landlord;
	ArrayList<Building> b = new ArrayList<Building>();
	public void setUp() throws Exception{
		super.setUp();
		
		
		house1 = new MockHouse("Dathousedoh", true);	
		house2 = new MockHouse("ButDisHousedoh", true);
		houseperson1 = new MockHousePerson("dabest");
		houseperson2 = new MockHousePerson("dahbestest");
		landperson = new MockPersonForHouse("StevetheLandlord", b, null);
		person1 = new MockPersonForHouse("Lisa ", b, house1);
		person2 = new MockPersonForHouse("Frank" , b, house2);
		b.add(house1);
		b.add(house2);
		landlord = new LandlordRole();
		landlord.apartmentlist.add(house1);
		landlord.apartmentlist.add(house2);

	}
	
	public void testReceivingRentNormalAndSimultaneouslyChargingRent() {
		int rentrate = 30;
		int money = 100;
		landlord.setPerson(landperson);
		houseperson1.setPerson(person1);
		houseperson2.setPerson(person2);
		house1.setHousePerson(houseperson1);
		house1.setLandlord(landlord);
		house2.setHousePerson(houseperson2);
		house2.setLandlord(landlord);
		houseperson1.setHouse(house1);
		houseperson2.setHouse(house2);
		houseperson1.setRent(rentrate);
		houseperson2.setRent(rentrate);
		houseperson1.getPerson().setMoney(money);
		houseperson2.getPerson().setMoney(money);
		
		
		landlord.setRentersWhoHaventBeenBilled();
		landlord.getPerson().setMoney(100f);
		//preconditions
		assertTrue("houseperson1 rent is $30. Its not ", rentrate == houseperson1.getRentRate());
		assertTrue("houseperson2 rent is $30. Its not ", rentrate == houseperson2.getRentRate());
		assertTrue("houseperson1s money should be 100. It isnt ", money == houseperson1.getPerson().getMoney() );
		assertTrue("houseperson2s money should be 100. It isnt ", money == houseperson2.getPerson().getMoney());
		assertTrue("apartment list is not empty. it isnt not empty", landlord.apartmentlist.size() == 2 );
		assertTrue("renters who havent been billed is also 2. Its not." , landlord.rentersWhoHaventBeenBilled.size() == 2);
		
		
		
		//houseperson1 interaction with landlord
		landlord.msgHereIsRent(houseperson1, rentrate);
		assertEquals("Houseperson paid the right amount. They did not",rentrate, houseperson1.getRentRate());
		assertEquals("Landlords money should be 130. It's not" , 130f , landlord.getPerson().getMoney());
		//assertEquals("houseperson 1s money should be " + (money - 30) + " . It doesnt. ", (money - 30) , houseperson1.getPerson().getMoney());
		//postconidtions for houseperson1
		assertTrue("Returns true ", landlord.pickAndExecuteAnAction());
		assertTrue("Size of renters who need to pay today is one. Its not one", landlord.rentersWhoHaventBeenBilled.size() == 1);
		assertTrue("Apartmentlist is still 2", landlord.apartmentlist.size() == 2);;
		
		
		//houseperson2 interaction with landlord
		landlord.msgHereIsRent(houseperson2, rentrate);
		assertEquals("Landlords money should be 160. It's not" , 160f , landlord.getPerson().getMoney());
		assertEquals("Houseperson paid the right amount. They did not",rentrate, houseperson2.getRentRate());
		//post condition for houseperson2
		assertTrue("Returns true ", landlord.pickAndExecuteAnAction());
		assertTrue("Size of renters who still have to pay is empty", landlord.rentersWhoHaventBeenBilled.isEmpty());
		assertTrue("Apartmentlist is still 2", landlord.apartmentlist.size() == 2);
		
		//final condition
		assertFalse("Lanlords scheduler should return false because renter list is empty", landlord.pickAndExecuteAnAction());
		
	}
	public void testTwoRenterDoesntPayEnough() {
		int rentrate = 30;
		landlord.setPerson(landperson);
		house1.setHousePerson(houseperson1);
		house1.setLandlord(landlord);
		house2.setHousePerson(houseperson2);
		house2.setLandlord(landlord);
		houseperson1.setHouse(house1);
		houseperson2.setHouse(house2);
		houseperson1.setRent(20);
		houseperson2.setRent(0);
		System.out.println(houseperson1.getRentRate());
		landlord.setRentersWhoHaventBeenBilled();
		landlord.getPerson().setMoney(100f);
		//preconditions
		assertTrue("houseperson1 rent is $20. Its not ", 20 == houseperson1.getRentRate());
		assertTrue("houseperson2 rent is $0. Its not ", 0 == houseperson2.getRentRate());
		assertTrue("apartment list is not empty. it isnt not empty", landlord.apartmentlist.size() == 2 );
		assertTrue("renters who havent been billed is also 2. Its not." , landlord.rentersWhoHaventBeenBilled.size() == 2);
		
		//houseperson1 nonnorm interaction
		landlord.msgHereIsRent(houseperson1, 20);
		assertFalse("Houseperson should have paid wrong amount. They did", rentrate == houseperson1.getRentRate());
		assertEquals("Landlords money should be 120. It's not" , 120f , landlord.getPerson().getMoney());
		assertEquals("Renters rent should now be increased to " + ((rentrate - 20) +rentrate) + ". Its hasnt .",( (rentrate - 20) +rentrate)  , houseperson1.getRentRate());
		
		//postconidtions for houseperson1
		assertTrue("Returns true ", landlord.pickAndExecuteAnAction());
		assertTrue("Size of renters who need to pay today is one. Its not one", landlord.rentersWhoHaventBeenBilled.size() == 1);
		assertTrue("Apartmentlist is still 2", landlord.apartmentlist.size() == 2);;
		
		
		//houseperson2 nonnom interraction
		landlord.msgHereIsRent(houseperson2, 0);
		assertFalse("Houseperson2 should have paid wrong amount. They paid correct amount", rentrate == houseperson2.getRentRate());
		assertEquals("Landlords money should be 120. It's not" , 120f , landlord.getPerson().getMoney());
		assertEquals("Renters rent should now be increased to " + (rentrate * 2) + ". Its hasnt .",( (rentrate * 2) ) , houseperson2.getRentRate());
		
		
		//post conditions of houseperson2
		assertTrue("Returns true ", landlord.pickAndExecuteAnAction());
		assertTrue("Size of renters who still have to pay is empty", landlord.rentersWhoHaventBeenBilled.isEmpty());
		assertTrue("Apartmentlist is still 2", landlord.apartmentlist.size() == 2);
		
		
		//post conditions
		assertFalse("Scheduler of houseperson return false ", landlord.pickAndExecuteAnAction());
		
		
		
		
		
		
	}
	
}
