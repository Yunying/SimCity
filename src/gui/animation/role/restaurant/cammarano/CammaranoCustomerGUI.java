package gui.animation.role.restaurant.cammarano;

import gui.SimCity;
import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;
import restaurant.cammarano.roles.*;
import restaurant.cammarano.roles.CammaranoHostRole.Table;

import java.awt.*;

public class CammaranoCustomerGUI extends BaseGUI implements GUI {

	private CammaranoCustomerRole agent = null;
	private String name = "";
	private boolean pause = false;
	private boolean isPresent = false;
	private boolean isHungry = false;

	private CammaranoHostRole host;
	SimCity gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToWaitArea, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private enum IconState { None, OrderPending, OrderReceived };
	private IconState iconState = IconState.None;

	public static int xWaitPos = 50;
	public static int yWaitPos = 50;

	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;
	
	public CammaranoCustomerGUI(CammaranoCustomerRole c, SimCity gui){ //HostAgent m) {
		id = "camm";
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		//maitreD = m;
		this.gui = gui;
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat)
				agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, X_SCALE, Y_SCALE);
		
		if(iconState == IconState.OrderPending) {
			g.setColor(Color.BLACK);
			g.drawString(name + "?", xPos + X_SCALE, yPos + Y_SCALE);
		}
		
		if(iconState == IconState.OrderReceived) {
			g.setColor(Color.BLACK);
			g.drawString(name, xPos + X_SCALE, yPos + Y_SCALE);
		}
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToHost() {
		xDestination = xWaitPos;
		yDestination = yWaitPos;
		
		command = Command.GoToWaitArea;
	}
	
	public void DoGoToSeat(Table table) {		//later you will map seatnumber to table coordinates.
		xDestination = table.get_posX();
		yDestination = table.get_posY();
		
		//xDestination = xTable;
		//yDestination = yTable;

		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
	
	public void MadeOrder(String choice) {
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
		
		iconState = IconState.OrderPending;
	}
	
	public void GotFood() {
		iconState = IconState.OrderReceived;
	}
	
	public void FinishedFood() {
		iconState = IconState.None;
	}
	
	public void Pause() {
		pause = true;
	}
	
	public void Resume() {
		pause = false;
	}
}
