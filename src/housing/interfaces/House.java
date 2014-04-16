package housing.interfaces;

import gui.animation.building.HouseGUI;
import interfaces.Building;
import interfaces.Person;

import java.util.ArrayList;




import java.util.List;

import global.actions.Action;
import global.roles.Role;



public interface House extends Building {
        public abstract void msgRentDue(Landlord l, int rentrate);
        public abstract HouseGUI getHouseGui();
        public abstract void setHouseGui(HouseGUI homegui);
        public abstract void msgLeavingLocation(Role r);

        public abstract String getName();
        public abstract void setName(String name);

        public abstract void msgAtLocation(Person p, Role r, List<Action> action);

        public abstract void setLandlord(Landlord l);
        public abstract void setHousePerson(HousePerson h);
        public abstract void setOccupant(HousePerson h);
        public abstract HousePerson getOccupant();
		String getLocation();

		void setLocation(String l);
}
