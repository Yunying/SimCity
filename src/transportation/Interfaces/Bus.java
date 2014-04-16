package transportation.Interfaces;

import transportation.BusStop;
import transportation.TransportationRole;

public interface Bus {
	public void msgAnimation();
	
	public void addBusStop(Stop bs);
	
	public void msgUpdateTime(int time);
	
	public void msgCanIGetOnToBus(Passenger tr);
	
	public void msgImAboard(Passenger tr);
	
	public void msgYouCanGo();
	
	public void msgLeavingBus(Passenger tr);
	
	public void msgImStaying();
	
	public boolean pickAndExecuteAnAction();

	public String getBusName();
	
	public Stop getCurrentStop();

	public void msgTotalWaitingPeople(int size);

	public boolean getNumberTold();

	public void setNumberTold(boolean b);
}
