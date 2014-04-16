package gui.animation.role.restaurant.mcneal;

import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;
import java.awt.Color;
import java.awt.Graphics2D;



import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.mcneal.roles.McNealWaiterRole;

public class McNealWaiterGUI extends BaseGUI implements GUI {
	private McNealWaiterRole role = null;
	//public CustomerGui c = null;
	private int xPos = -20, yPos = -20;//default waiter position
	private int xDestination = -20, yDestination = -20;//default start position
	//public List<> positions = new ArrayList<int>();
	private Table tableNumber;
	public  int xTable = 75;
	public  int yTable =  75;
	//public McNealRestaurantGui gui;
	public  int xTableRed =  150;
	public  int yTableRed = 150 ;
	public  int xTableBlue = 250 ;
	public  int yTableBlue =  250;
	public int yinit = 0;
	public int count = 0;
	private boolean hasOrder = false;
	public enum Command {doingNothing, seatCustomer, leaveCustomer, takeOrder, takeToCook, serveFood, gotoCahsier, giveCheck}; 
	public Command command = Command.doingNothing;
	public void sethasOrder(boolean o) {
		hasOrder = o;
	}
	public boolean hasOrder() {
		return hasOrder;
	}
	public McNealWaiterGUI(McNealWaiterRole agent) { //, McNealRestaurantGui gui) {
		id = "mc";
		this.role = agent;
		//this.gui = gui;
		xPos = 350;
		yinit = (int) (0 + Math.random() * 300);
		yPos =yinit;
		xDestination = 350;
		yDestination = yPos;
	}
	/*
	public void setGui (McNealRestaurantGui gui) {
		this.gui = gui;
	}
	*/
	public void chooseDestination(Table t) {
		tableNumber = t;
		if (t.getNumber()  == 1) { System.out.println(" waiter going to orangetable");
		xDestination = xTable + 20;
		yDestination = yTable - 20;
		//c.gotoDestination(xDestination, yDestination);
		}
		else {
			if (t.getNumber() == 2) { System.out.println("waiter going to redtable");
			xDestination = xTableRed + 20;
			yDestination = yTableRed - 20;
			//c.gotoDestination(xDestination, yDestination);
			}

			else {
				if (t.getNumber() == 3) { System.out.println("waiter going to bluetable");
				xDestination = xTableBlue + 20;
				yDestination = yTableBlue - 20;
				//c.gotoDestination(xDestination, yDestination);

				}
			}
		}
	}
	public void updatePosition() {
		if (xPos < xDestination )
			xPos++;
		else if (xPos > xDestination )
			xPos--;
		//System.out.println("x position changing " + xPos);
		if (yPos < yDestination )
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if ((xPos == xDestination && yPos == yDestination
				&( xDestination == xTable + 20) & (yDestination == yTable - 20) )||(xPos == xDestination && yPos == yDestination
				&( xDestination == xTableRed + 20) & (yDestination == yTableRed- 20) ) ||(xPos == xDestination && yPos == yDestination 
				&(xDestination == xTableBlue + 20) & (yDestination == yTableBlue -20 ))) { //System.out.println("arrived at table " + xDestination);
				role.msgAtTable(); 
				/*if(command == Command.serveFood) { System.err.println("SERVE IT");
				agent.msgServedFood();
				}*/
		}
		/*else {
			if((xPos == xDestination && yPos == yDestination) & 
					(yDestination == -20 && xDestination == -20)){
					//System.out.println("back at place");
			//agent.msgAtStart();
			}*/
			
			else {
					if(xDestination == 150  && yDestination == 25) {
						
						System.err.println("at the cheff");
						role.msgAtCook();
						
					}
			}
			}
		//}
		
		
		public void DoMeeetCustomer() {
			xDestination = -20;//agent.getmyCustomer().getCustomer().getGui().getXPos() + 20;
			yDestination = -20;//agent.getmyCustomer().getCustomer().getGui().getXPos() + 20;
		}
		
	public void drawfood(Graphics2D g2) {
		
		g2.drawString(role.getmyCustomer().getStringChoice(), xPos, yPos);	// TODO Auto-generated method stub
		
	}

	public void draw(Graphics2D g) { 
		g.setColor(Color.MAGENTA);
		g.fillRect(xPos, yPos, 20, 20);
	}

	public boolean isPresent() {
		return true;
	}
	
//need to change tableNumber variable
	public void DoBringToTable(Table table) {
		chooseDestination(table);
		//command = Command.seatCustomer;
	}
	public void DoServeFood(Table table) {
		chooseDestination(table);
	//	command = Command.serveFood;
	}
	public void DoGoToCashier() {
		xDestination = -20;
		yDestination = -20;
	}
	public void DoGoToCook() {
		xDestination = 150;
		yDestination = 25;
	}
	
	public void DoLeaveCook() {
		xDestination = 200;
		yDestination = 25;
	}
	public void DoLeaveCustomer() {
	
		xDestination = 350;
		yDestination = yinit;
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}	
}