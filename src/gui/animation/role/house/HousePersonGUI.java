package gui.animation.role.house;

import java.awt.Color;
import java.awt.Graphics2D;

import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;
import housing.interfaces.HousePerson;

public class HousePersonGUI extends BaseGUI implements GUI {

	
	
	boolean  isPresent;
	HousePerson houseperson;
	
	private int xPos = -20, yPos = -20;//default waiter position
	private int xDestination = -20, yDestination = -20;//default start position
	private enum Command {noCommand, GoToBase, GoToFridge, GoToStove, GoToTable, GoToExit};
	Command command = Command.noCommand;
	
	/**Accessors**/
	
	public HousePerson getHousePerson() {
		
		return houseperson;
	}
	
	public void setPerson(HousePerson hp) {
		
		  houseperson = hp;
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
			if(command == Command.GoToBase) {
				houseperson.msgAtLoc();
				
			}
			else if(command == Command.GoToExit) {
				houseperson.msgAtLoc();
				
			}
		}
		
	}

	@Override
	public void draw(Graphics2D g) {
		
		g.setColor(Color.YELLOW);
		//g.fillRect(xpos, ypos, int width, int height); dont know the fills, dont even know if we will have an image
		
	}

	@Override
	public boolean isPresent() {
		
		return isPresent;
	}
	public void setPresent(boolean isPresent){
		this.isPresent = isPresent;
	}
	
	public void DoGoToBase() {
		//xDestination = 20;
		//yDestination = 20;
	}
	public void DoGoToFridge() {
		//xDestination = 40;
		//yDestination = 40;
	}
	public void DoGoToStove() {
		//xDestination = 30;
		//yDestination = 40;
	}
	public void DoGoToTable() {
		
	}

}
