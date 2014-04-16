package transportation.Interfaces;

import gui.animation.base.GUI;
import interfaces.Building;

import java.util.List;

import interfaces.Building;
import transportation.BusAgent;
import transportation.TransportationRole;

public interface Stop {

        public void msgUpdateTime(int time);
        
        public String getLocation();
        
        public void setLocation(String l);
        
        public void msgAtLocation(Passenger tr);
        
        public void msgAtLocation(Bus bus);
        
        public void msgLeavingStop(Passenger tr);

        public void msgLeavingStop();
        
        public boolean pickAndExecuteAnAction();

        public List<Building> getBuildings();

        public int getNumber();
        
        public void addBuilding(Building b);
        
        public Bus getBus();
        
        public Mediator getMediator();
        
        public void setGui(GUI gui);

		public void setNumber(int size);

}
