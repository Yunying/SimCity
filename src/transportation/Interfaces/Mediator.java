package transportation.Interfaces;

import java.util.List;

public interface Mediator {
	public void msgUpdateTime(int time);
	
	public void addABus(Bus s);
	
	public void msgIBeginToWork(Bus bus);
	
	public void msgIArriveAtParkingLot(Bus bus);
	
	public boolean msgAPassengerIsHere(Stop stop);
	
	public int getStopSize();
	
	public void addBusStop(Stop s);
	
	public List<Stop> getBusStops();
}
