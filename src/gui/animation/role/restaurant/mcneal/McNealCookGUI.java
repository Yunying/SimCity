package gui.animation.role.restaurant.mcneal;

import gui.animation.base.BaseGUI;
import gui.animation.role.restaurant.cammarano.*;
import gui.animation.base.GUI;
import restaurant.cammarano.roles.*;
import restaurant.cammarano.roles.CammaranoHostRole.*;

import java.awt.*;

public class McNealCookGUI extends BaseGUI implements GUI {

	private CammaranoCookRole agent = null;
	private String name = "";
	
	// Booleans that govern GUI actions
	private boolean hasFood = false;
	private boolean isActing = false;
	
	private boolean pause = false;

	private int xPos = 600, yPos = 600;//default Cook position
	private int xDestination = 600, yDestination = 600;//default start position
	private int xWaitPos = 600;
	private int yWaitPos = 600;
	
	private int xGrill = 700;
	private int yGrill = 620;
	
	private int xPlatingArea = 500;
	private int yPlatingArea = 620;
	
	private int xRefrigerator = 600;
	private int yRefrigerator = 650;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;

	public McNealCookGUI(CammaranoCookRole agent) {
		this.agent = agent;
		id = "mc";
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
		g.setColor(Color.BLUE);
		g.fillRect(xPos, yPos, X_SCALE, Y_SCALE);
		
		if(hasFood) {
			g.setColor(Color.BLACK);
			g.drawString(name, xPos + X_SCALE, yPos + Y_SCALE);
		}
	}

	public boolean isPresent() {
		return true;	
	}
	
	public void DoGrabFood(String choice) {
		switch(choice) {
			case "steak":
				name = "st?";
				break;
			case "chicken":
				name = "ch?";
				break;
			case "pizza":
				name = "pi?";
				break;
			case "salad":
				name = "sa?";
				break;
		}
		
		hasFood = true;
	}
	
	public void DoGoToPlatingArea(String choice) {
		switch(choice) {
		case "steak":
			name = "st!";
			break;
		case "chicken":
			name = "ch!";
			break;
		case "pizza":
			name = "pi!";
			break;
		case "salad":
			name = "sa!";
			break;
		}
		
		hasFood = true;
		
		xDestination = xPlatingArea + X_SCALE;
		yDestination = yPlatingArea + Y_SCALE;
		isActing = true;
	}
	
	public void DoCooking() {
		xDestination = xGrill - X_SCALE;
		yDestination = yGrill + Y_SCALE;
		
		hasFood = true;
		
		isActing = true;
	}
	
	public void DoGoToRefrigerator() {
		xDestination = xRefrigerator;
		yDestination = yRefrigerator - Y_SCALE;
		isActing = true;
	}
	
	public void DoGoToFoodArea(String choice) {
		switch(choice) {
		case "steak":
			name = "st!!";
			break;
		case "chicken":
			name = "ch!!";
			break;
		case "pizza":
			name = "pi!!";
			break;
		case "salad":
			name = "sa!!";
			break;
		}
		
		hasFood = true;
		
		xDestination = xWaitPos;
		yDestination = yWaitPos;
		isActing = true;
	}
	
	public void DoGoToWaitPos() {
		xDestination = xWaitPos;
		yDestination = yWaitPos;
		isActing = true;
	}

	public void FinishedOrder() {
		hasFood = false;
	}
	
	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}
	
	public void Pause() {
		pause = true;
	}
	
	public void Resume() {
		pause = false;
	}
}
