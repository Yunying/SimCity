package gui.animation.role.restaurant.cammarano;

import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;
import restaurant.cammarano.roles.*;
import restaurant.cammarano.roles.CammaranoHostRole.*;

import java.awt.*;

public class CammaranoCashierGUI extends BaseGUI implements GUI {

	private CammaranoCashierRole agent = null;
	private String name = "";
	
	private enum BreakState {
		Working,
		Pending,
		OnBreak
	};
	
	private BreakState state = BreakState.Working;
	
	private boolean pause = false;

	private int xPos = 600, yPos = 50;//default Cashier position
	private int xDestination = 600, yDestination = 50;//default start position
	private int xWaitPos = 600;
	private int yWaitPos = 50;
	
	private static final int X_SCALE = 20;
	private static final int Y_SCALE = 20;

	public CammaranoCashierGUI(CammaranoCashierRole agent) {
		this.agent = agent;
		id = "camm";
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
		if (xPos == xDestination && yPos == yDestination) {// & (xDestination == xTable + X_SCALE) & (yDestination == yTable - Y_SCALE)) {
		
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		g.fillRect(xPos, yPos, X_SCALE, Y_SCALE);
	}

	public boolean isPresent() {
		return true;	
	}
	
	public void Pause() {
		pause = true;
	}
	
	public void Resume() {
		pause = false;
	}
}
