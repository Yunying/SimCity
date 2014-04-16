package gui.animation.role.restaurant.ji;

import gui.SimCity;
import gui.animation.base.*;

import java.awt.*;
import java.util.Random;

import javax.swing.ImageIcon;

import restaurant.ji.interfaces.JiCustomer;

public class JiCustomerGUI extends BaseGUI implements GUI {

	private JiCustomer role = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	SimCity gui;
	
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private ImageIcon customerImage = new ImageIcon("restaurant_customer.jpg");

	public static final int xTable = 200;
	public static final int yTable = 250;
	
	public static final int xNeg = 20;
	public static final int yNeg = 20;
	public static final int xLeftRestaurant = -40;
	public static final int yLeftRestaurant = -40;
	
	public int startingX = -20;
	public int startingY = -20;
	
	public static final int waitingAreaStartX = 0;
	public static final int waitingAreaStartY = 30;
	public static final int waitingAreaHeight = 70;
	public static final int waitingAreaLength = 100;
	
	/* in the animation panel
	public static final int waitingAreaStartX = 0;
	public static final int waitingAreaStartY = 30;
	public static final int waitingHeight = 70;
	public static final int waitingLength = 100;
	*/
	
	public final int imageWidth = xNeg - xPos;
	public final int imageHeight = yNeg - xPos;
	
	private int xFood = 0;
	private int yFood = 0;
	
	private String foodLabel = "";
	private String nameLabel;

	
	public JiCustomerGUI(JiCustomer c, int waitX, int waitY, SimCity gui){ //HostAgent m) {
		role = c;
		id = "ji";
		xDestination = new Random().nextInt(waitingAreaLength-imageWidth)+waitingAreaStartX;
		yDestination = new Random().nextInt(waitingAreaHeight-imageHeight)+waitingAreaStartY;
		xPos = startingX;
		yPos = startingY;
		nameLabel = c.getName();
		
		this.gui = gui;
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
			if (command==Command.GoToSeat){
				role.msgAnimationFinishedGoToSeat();
				xFood = imageWidth + xPos;
				yFood = imageHeight + yPos;
			}
			else if (command==Command.LeaveRestaurant) {
				role.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(role);");
				isHungry = false;
//				gui.setCustomerEnabled(role);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		Image i = customerImage.getImage();
		g.drawImage(i, xPos, yPos, xNeg, yNeg, null);
		g.drawString(nameLabel, xPos, yPos);
		g.drawString("$" + role.getPerson().getMoney(), xPos, yPos + 2*imageHeight);
		
		g.drawString(foodLabel, xFood, yFood);
		
		/*
		  g.setColor(Color.GREEN);
		  g.fillRect(xPos, yPos, xNeg, yNeg);
		*/
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
//		role.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int x, int y) {//later you will map seatnumber to table coordinates.
		xDestination = x;
		yDestination = y;
		command = Command.GoToSeat;
	}
	
	public void DoExitRestaurant() {
		xDestination = xLeftRestaurant;
		yDestination = yLeftRestaurant;
		command = Command.LeaveRestaurant;
	}

	public void setChoiceText(String text, boolean delivered) {
		foodLabel = text;
		if (!delivered)
			foodLabel += '?';
	}
	public void clearChoicetext() {
		foodLabel = "";
	}
}
