package restaurant.redland.UtilityClasses;

import restaurant.redland.interfaces.RedlandCustomer;

public class Table {
	private RedlandCustomer occupiedBy;//originally not private
	private int tableNumber;//originally not private
	
	private int tableXPos;
	private int tableYPos;
	

	public Table(int tableNumber, int xPos, int yPos) {
		this.tableNumber = tableNumber;
		this.tableXPos = xPos;
		this.tableYPos = yPos;
	}

	public int getXPos(){
		return tableXPos;
	}
	
	public int getYPos(){
		return tableYPos;
	}
	
	public void setOccupant( RedlandCustomer cust) {
		occupiedBy = cust;
	}

	public void setUnoccupied() {
		occupiedBy = null;
	}

	public RedlandCustomer getOccupant() {
		return occupiedBy;
	}

	public boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}
	
	public int getNumber(){
		return tableNumber;
	}
	
	public boolean equals(Table table){
		return ( this.tableNumber == table.getNumber());
	}
}
