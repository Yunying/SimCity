package gui.animation.role.restaurant.cammarano;

import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;
import restaurant.cammarano.roles.CammaranoHostRole;

import java.awt.*;

public class CammaranoHostGUI extends BaseGUI implements GUI {

	private CammaranoHostRole agent = null;
	private String name = "";
	
	private enum BreakState {
		Working,
		Pending,
		OnBreak
	};
	
	private BreakState state = BreakState.Working;
	
	private boolean pause = false;

	private int xPos = 100, yPos = 50;//default Host position
	private int xDestination = 100, yDestination = 50;//default start position
	private int xWaitPos;
	private int yWaitPos;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;

	public CammaranoHostGUI(CammaranoHostRole agent) {
		id = "camm";
		this.agent = agent;
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
		if (xPos == xDestination && yPos == yDestination) {}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.fillRect(xPos, yPos, X_SCALE, Y_SCALE);
	}

	public boolean isPresent() {
		return true;	
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
