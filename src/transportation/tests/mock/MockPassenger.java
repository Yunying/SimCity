package transportation.tests.mock;

import global.test.mock.LoggedEvent;
import global.test.mock.Mock;
import global.test.mock.MockRole;
import interfaces.Building;
import transportation.Interfaces.Bus;
import transportation.Interfaces.Passenger;
import transportation.Interfaces.Stop;

public class MockPassenger extends MockRole implements Passenger {
	String name;
	boolean isAsked;
	Stop start;
	Stop end;
	Stop current;
	Stop next;
	
	public MockPassenger(String name){
		super();
		this.name = name;
		isAsked = false;
	}


	@Override
	public void setActive() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgTransportationStopped() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Transportation stopped. You have to leave"));
		
	}


	@Override
	public void msgPleaseComeAboard() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("please come aboard"));
	}


	@Override
	public void msgYouHaveToWait() {
		// TODO Auto-generated method stub

	}


	@Override
	public void addBusStop(Stop bs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsBus(Bus bus) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("msg bus is here"));
	}

	@Override
	public void msgAtStop(Stop bs, Stop bs2) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("msg we are at stop"));
		current = bs;
		next = bs2;
	}

	@Override
	public boolean isAsked() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("being asked if I'm asked"));
		return isAsked;
	}

	@Override
	public void setAsked(boolean a) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("I have been asked"));
		isAsked = a;
	}


	@Override
	public Building getStartBuilding() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Building getEndBuilding() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Stop getStartStop() {
		// TODO Auto-generated method stub
		return start;
	}


	@Override
	public Stop getEndStop() {
		// TODO Auto-generated method stub
		return end;
	}


	@Override
	public String getPassengerName() {
		// TODO Auto-generated method stub
		return name;
	}


	@Override
	public void msgWeAreGoing(Stop next) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("we are going to next stop"));
	}


	@Override
	public void msgGoToBuilding(Building start, Building end, boolean c) {
		// TODO Auto-generated method stub
		print("The role is active");
	}



	public void setStop(MockBusStop bs1, MockBusStop bs2) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("set start and end stop"));
		start = bs1;
		end = bs2;
	}
	
	public Stop getCurrentStop(){
		return current;
	}
	
	public Stop getNextStop(){
		return next;
	}

}
