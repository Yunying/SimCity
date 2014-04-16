package transportation.Interfaces;

import interfaces.Building;
import transportation.BusAgent;
import transportation.BusStop;

public interface Passenger {
	public void addBusStop(Stop bs);
	
	public void setActive();
	
	public void msgAnimation();
	
	public void msgTransportationStopped();
	
	public void msgGoToBuilding(Building start, Building end, boolean c);
	
	public void msgPleaseComeAboard();
	
	public void msgHereIsBus(Bus bus);
	
	public void msgYouHaveToWait();
	
	public void msgAtStop(Stop bs, Stop bs2);
	
	public boolean isAsked();
	
	public void setAsked(boolean a);
	
	public Building getStartBuilding();
	
	public Building getEndBuilding();
	
	public Stop getStartStop();
	
	public Stop getEndStop();
	
	public String getPassengerName();

	public void msgWeAreGoing(Stop next);

}
