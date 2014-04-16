package gui.animation.role.restaurant.mcneal;

import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;

import java.awt.*;
import java.util.Random;


import restaurant.mcneal.roles.McNealCustomerRole;
import restaurant.mcneal.roles.McNealHostRole.Table;

public class McNealCustomerGUI extends BaseGUI implements GUI {

	private McNealCustomerRole role = null;
	private boolean isPresent = true;
	private boolean isHungry = false;
	private boolean ordered = false;
	//private HostAgent host;
	//McNealRestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, Pay, LeaveRestaurant};
	private Command command=Command.noCommand;

	private Table tableNumber;
	public  int xTable = 75;
	public  int yTable =  75;

	public  int xTableRed =  150;
	public  int yTableRed =  150 ;
	public  int xTableBlue =  250 ;
	public  int yTableBlue =  250;
	public int o = 0;
	public McNealCustomerGUI(McNealCustomerRole c) { //, McNealRestaurantGui gui){ //HostAgent m) {
		id = "mc";
		role = c;
		xPos = 400;
		yPos = (int) (0 + Math.random() * 350); System.err.println(yPos);
		xDestination = 400;
		yDestination = yPos;
		//maitreD = m;
		//this.gui = gui;
		o += 75;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
			
		else if (xPos > xDestination)
			xPos--;
		
		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) {
				
				role.msgAnimationFinishedGoToSeat();
			}
			else if (command==Command.LeaveRestaurant) {
				role.msgAnimationFinishedLeaveRestaurant();
				
				setPresent(false);
				isHungry = false;
				//gui.setCustomerEnabled(role);
				
			}
			else if(command == Command.Pay) {
				role.msgAtCashier();
			}
			command=Command.noCommand;
		}
	}
	
	 public void chooseDestination(Table tableNum) {
		 tableNumber = tableNum;
		 if (tableNum.getNumber() == 1) { System.out.println("orangetable");
			xDestination = xTable + 20;
			yDestination = yTable + 10;
			//c.gotoDestination(xDestination, yDestination);
			}
			else {
				if (tableNum.getNumber() == 2) { System.out.println("redtable");
				xDestination = xTableRed + 20;
				yDestination = yTableRed + 10;
				//c.gotoDestination(xDestination, yDestination);
				}

				else {
					if (tableNum.getNumber()  == 3) { System.out.println("bluetable");
					xDestination = xTableBlue + 20 ;
					yDestination = yTableBlue + 10;
					//c.gotoDestination(xDestination, yDestination);

					}
				}
			}
	  
	    	
	    }

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, 20, 20);
	}
	public void drawfood(Graphics2D g) {
		g.drawString(role.getFoodChoice() + " ?", xPos, yPos);
		//g.fillRect(, yPos,10,10);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		role.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	public void setOrdered(boolean o) {
		ordered = o;
		
	}
	public boolean hasOrdered() {
		return ordered;
	}

	public void DoGoToSeat(int seatnumber, Table tableNum) {//later you will map seatnumber to table coordinates.
		chooseDestination(tableNum);
		System.out.println("following to table " + tableNum.getNumber());
		//xDestination =  xTable;
		//yDestination = yTable;
		
		command = Command.GoToSeat;
	}
	
	

	public void DoExitRestaurant() {
		xDestination = 400;
		yDestination = (int) (0 + Math.random() * 350);
		command = Command.LeaveRestaurant;
		
	}
	public void DoPay() {
		xDestination = -40;
		yDestination = -40;
		command = Command.Pay;
	}
	public int getXPos() {
		return xPos;
	}
	public int getYPos() {
		return yPos;
	}
}
