package transportation;

import java.util.ArrayList;
import java.util.List;

import transportation.Interfaces.Bus;
import transportation.Interfaces.Mediator;
import transportation.Interfaces.Stop;

public class TransportationMediator implements Mediator{
	List<Bus> buses;
	List<Stop> stops;
	boolean active;
	int time;
	String name;
	
	public TransportationMediator(){
		stops = new ArrayList<Stop>();
		buses = new ArrayList<Bus>();
	}
	
	public void msgUpdateTime(int time){
		this.time = time;
		for (Bus b: buses){
			b.msgUpdateTime(time);
		}
		for (Stop s: stops){
			s.msgUpdateTime(time);
		}
	}

	
	public void addABus(Bus s){
		for(Stop bs: stops){
			s.addBusStop(bs);
		}
		buses.add(s);
		//buses.get(buses.size()-1).msgUpdateTime(7);
	}
	
	public void addBusStop(Stop s){
		stops.add(s);
		s.setNumber(stops.size());
	}
	
	public void msgIBeginToWork(Bus bus){
		buses.add(bus);
	}
	
	public void msgIArriveAtParkingLot(Bus bus){
		buses.remove(bus);
	}
	
	public boolean msgAPassengerIsHere(Stop stop){
		for (Bus b:buses){
			if (b.getCurrentStop() == null){
//				System.out.println("bus is null");
				return false;
			}
			
			if (b.getCurrentStop().getNumber()<stop.getNumber()){
				return true;
			}
		}
		return false;
	}
	
	public int getStopSize(){
		return stops.size();
	}
	
	public List<Stop> getBusStops(){
		return stops;
	}
}
