package restaurant.ji;

import restaurant.ji.interfaces.JiCustomer;

public class JiMyTable {
	public static enum TableState {
		waitingToBeSeated, SEATING, GOINGTOTAKEORDER, TAKINGORDER, WAITINGFORFOOD, 
		PICKINGUPORDER, TELLINGTOREORDER, IDLE, GETTINGCHECK, DELIVERINGCHECK, CUSTOMERLEFT
		};
		
	private int tableNum; private int tableX; private int tableY;
	private JiCustomer customer;
	private String choice;
	private TableState state;
	private Float bill;
	
	public JiMyTable(JiCustomer c, int tableNum, int tableX, int tableY) {
		this.setCustomer(c);
		this.setTableNum(tableNum);
		this.setTableX(tableX);
		this.setTableY(tableY);
		this.setChoice(null);
		this.setState(null);
		this.setBill(null);
	}

	public TableState getState() {
		return state;
	}

	public void setState(TableState state) {
		this.state = state;
	}

	public Float getBill() {
		return bill;
	}

	public void setBill(Float bill) {
		this.bill = bill;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public int getTableNum() {
		return tableNum;
	}

	public void setTableNum(int tableNum) {
		this.tableNum = tableNum;
	}

	public JiCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(JiCustomer customer) {
		this.customer = customer;
	}

	public int getTableX() {
		return tableX;
	}

	public void setTableX(int tableX) {
		this.tableX = tableX;
	}

	public int getTableY() {
		return tableY;
	}

	public void setTableY(int tableY) {
		this.tableY = tableY;
	}
}
