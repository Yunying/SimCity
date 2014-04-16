package gui.animation.role.restaurant.cammarano;

import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;
import restaurant.cammarano.roles.*;
import restaurant.cammarano.roles.CammaranoHostRole.*;

import java.awt.*;

public class CammaranoWaiterGUI extends BaseGUI implements GUI {

	private CammaranoWaiterRole agent = null;
	private String name = "";
	
	// Booleans that govern Gui actions
	private boolean hasFood = false;
	private boolean isActing = false;
	
	private enum BreakState {
		Working,
		Pending,
		OnBreak
	};
	
	private BreakState state = BreakState.Working;
	
	private boolean pause = false;

	private int xPos = -20;
	private int yPos = -20;
	
	private int xDestination = -20;
	private int yDestination = -20;//default start position
	
	private int xWaitPos;
	private int yWaitPos;

	public static int xTable = 200;
	public static int yTable = 250;
	
	private static final int xCookPos = 600;
	private static final int yCookPos = 600;
	
	private static final int xCustomerPos = 50;
	private static final int yCustomerPos = 50;
	
	private static final int xCashierPos = 600;
	private static final int yCashierPos = 50;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;

	public CammaranoWaiterGUI(CammaranoWaiterRole agent, int x, int y) {
		id = "camm";
		this.agent = agent;
		
		xPos = x;
		yPos = y;
		
		xWaitPos = x;
		yWaitPos = y;
	}

	public void updatePosition() {
	
		if(pause) {
			return;
		}
		
		if (xPos < xDestination)
			xPos++;
		
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
			
		else if (yPos > yDestination)
			yPos--;

		// At destination
		if (isActing == true && xPos == xDestination && yPos == yDestination) {// & (xDestination == xTable + X_SCALE) & (yDestination == yTable - Y_SCALE)) {
			agent.msgAtDestination();
			isActing = false;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.MAGENTA);
		g.fillRect(xPos, yPos, X_SCALE, Y_SCALE);
		
		if(hasFood) {
			g.setColor(Color.BLACK);
			g.drawString(name, xPos + X_SCALE, yPos + Y_SCALE);
		}
	}

	public boolean isPresent() {
		return true;	
	}

	public void DoBringToTable(Table table) {
		xTable = table.get_posX();
		yTable = table.get_posY();
		
		
		xDestination = xTable + X_SCALE;
		yDestination = yTable - Y_SCALE;
		
		isActing = true;
	}
	
	public void DoBringFoodToCustomer(String choice, Table t) {
		switch(choice) {
			case "steak":
				name = "st";
				break;
			case "chicken":
				name = "ch";
				break;
			case "pizza":
				name = "pi";
				break;
			case "salad":
				name = "sa";
				break;
		}
		
		hasFood = true;
		
		
		xTable = t.get_posX();
		yTable = t.get_posY();
		
		xDestination = xTable + X_SCALE;
		yDestination = yTable - Y_SCALE;
		isActing = true;
	}
	
	public void DoGoToCook() {
		xDestination = xCookPos;
		yDestination = yCookPos - 2 * Y_SCALE;
		isActing = true;
	}
	
	public void DoGoToCashier() {
		xDestination = xCashierPos;
		yDestination = yCashierPos + Y_SCALE;
		isActing = true;
	}
	
	public void DoGoToCustomerArea() {
		xDestination = xCustomerPos + X_SCALE;
		yDestination = yCustomerPos - Y_SCALE;
		isActing = true;
	}

	public void DoLeaveCustomer() {
		xDestination = -X_SCALE;
		yDestination = -Y_SCALE;
		isActing = true;
	}
	
	public void DoGoToWaitPos() {
		xDestination = xWaitPos;
		yDestination = yWaitPos;
		isActing = true;
	}
	
	public void DoGoToHost() {
		xDestination = -X_SCALE;
		yDestination = -Y_SCALE;
		isActing = true;
	}

	public void GiveFood() {
		hasFood = false;
	}
	
	// From waiter, are we on break?
	public void BreakApproved() {
		System.out.println("GUI: Break was approved");
		state = BreakState.OnBreak;
	}
	
	// For button
	public void SetOnBreak() {
		System.out.println("Gui call to set waiter on break");
		state = BreakState.Pending;
		agent.msgGoOnBreak();
	}
	
	public void SetOffBreak() {
		System.out.println("Gui call to set waiter off break");
		state = BreakState.Working;
		agent.msgStartWorking();
	}
	
	public boolean OnBreak() {
		if(state == BreakState.OnBreak || state == BreakState.Pending)
			return true;
		else 
			return false;
	}
	
	public boolean CanPressBreakButton() {
		if(state == BreakState.Pending) {
			return false;
		}
		
		else
			return true;
	}
	
	public void Pause() {
		pause = true;
	}
	
	public void Resume() {
		pause = false;
	}
	
	// Accessors and mutators
	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}
	
	public int getxWaitPos() {
		return xWaitPos;
	}
	
	public void setxWaitPos(int xWaitPos) {
		this.xWaitPos = xWaitPos;
	}
	
	public int getyWaitPos() {
		return yWaitPos;
	}
	
	public void setyWaitPos(int yWaitPos) {
		this.yWaitPos = yWaitPos;
	}
}
