package transportation.tests.mock;

import java.util.List;

import global.test.mock.LoggedEvent;
import global.test.mock.Mock;
import transportation.BusStop;
import transportation.Interfaces.Bus;
import transportation.Interfaces.Mediator;
import transportation.Interfaces.Stop;

public class MockMediator extends Mock implements Mediator {
	String name;
	boolean returnB;
	List<Stop> stops;
	
	public MockMediator(String name){
		super();
		this.name = name;
	}

	public void setReturnValue(boolean b){
		returnB = b;
	}
	
	@Override
	public void msgUpdateTime(int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addABus(Bus s) {
	
	}

	@Override
	public void msgIBeginToWork(Bus bus) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("bus begins to work"));
	}

	@Override
	public void msgIArriveAtParkingLot(Bus bus) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean msgAPassengerIsHere(Stop stop) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("receive message to check if the bus is still running"));
		return returnB;
	}

	@Override
	public int getStopSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addBus(Bus bus) {
		// TODO Auto-generated method stub
		
	}

	public void addStop(Stop stop) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBusStop(Stop s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Stop> getBusStops() {
		// TODO Auto-generated method stub
		return stops;
	}
}
