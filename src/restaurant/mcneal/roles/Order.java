package restaurant.mcneal.roles;

import restaurant.mcneal.interfaces.McNealWaiter;
import restaurant.mcneal.roles.McNealCookRole.orderStates;
import restaurant.mcneal.roles.McNealHostRole.Table;

public class Order{
	McNealWaiter waiter;
	int choice;
	String schoice;
	Table table;
	Order o;
	orderStates state = orderStates.doingNothing;
	
	public Order(McNealWaiter w, String c,Table t) {
		waiter = w;
		schoice = c;
		table = t;
		if(schoice == "ST") {
			choice = 1;
		}
		if(schoice == "CH") {
			choice = 2;
		}
		if(schoice == "PI") {
			choice = 3;
		}
		if(schoice == "SAL" ) {
			choice = 4;
		}
	}

	int getChoice() {

		return choice;

	}
	String getStringChoice() {
		return schoice;
	}
	McNealWaiter getWaiter() {
		return waiter;
	}
	Table getTable() {
		return table;
	}

}
