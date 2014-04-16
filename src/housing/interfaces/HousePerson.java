package housing.interfaces;

import global.actions.Action;
import interfaces.Person;

import java.util.ArrayList;
import java.util.List;




public interface HousePerson {
        
		
		public abstract List<Integer> getBills();
		public List<Integer> bills = new ArrayList<Integer>();
		
      //  public abstract void msgFromMarketHeresFood(List<String> foodlist);

        public abstract void msgComeIntoHouse(House h, Person p, List<Action> actionsForHouse);

        public abstract House getHouse();
		public abstract void setHouse(House h);
		public abstract int  getRentRate();
		public abstract void setRent(int rent);
		public abstract void msgUpdateTime(int time, int day);
		public abstract Person getPerson();
		public abstract void msgAtLoc();
		
		
		
		
		
}