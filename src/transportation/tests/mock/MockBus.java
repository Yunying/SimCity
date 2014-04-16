package transportation.tests.mock;

import global.test.mock.LoggedEvent;
import global.test.mock.Mock;
import transportation.BusStop;
import transportation.TransportationRole;
import transportation.Interfaces.Bus;
import transportation.Interfaces.Passenger;
import transportation.Interfaces.Stop;

public class MockBus extends Mock implements Bus {
	String name;
	boolean numberTold;
	public int waitingPeople;

	public MockBus(String name) {
		super();
		this.name = name;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBusStop(Stop bs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgUpdateTime(int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void msgCanIGetOnToBus(Passenger tr) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Passenger "+ tr.getPassengerName() + " is asking to get on the bus"));
		tr.msgPleaseComeAboard();

	}

	@Override
	public void msgImAboard(Passenger tr) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Passenger "+tr.getPassengerName() + " is aboard"));
	}

	@Override
	public void msgYouCanGo() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("message you can go"));
	}

	@Override
	public void msgLeavingBus(Passenger tr) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Passenger "+ tr.getPassengerName() + " is leaving bus"));

	}

	@Override
	public void msgImStaying() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Passenger is staying on bus"));

	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getBusName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public Stop getCurrentStop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgTotalWaitingPeople(int size) {
		// TODO Auto-generated method stub
		numberTold = true;
		waitingPeople = size;
		log.add(new LoggedEvent("get total waiting passengers "+size));
	}

	@Override
	public boolean getNumberTold() {
		// TODO Auto-generated method stub
		return numberTold;
	}

	@Override
	public void setNumberTold(boolean b) {
		// TODO Auto-generated method stub
		
	}

}
