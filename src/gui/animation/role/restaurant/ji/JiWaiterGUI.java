package gui.animation.role.restaurant.ji;

import gui.SimCity;
import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;

import java.awt.*;

import javax.swing.ImageIcon;

import restaurant.ji.interfaces.JiWaiter;

public class JiWaiterGUI extends BaseGUI implements GUI {

    private JiWaiter role = null;
	SimCity gui;
	
    private int xPos = -40, yPos = -40;//default waiter position
    private int xDestination, yDestination;//default start position
    public static final int xNeg = 20, yNeg = 20;
    public static final int waitingAreaX = 0, waitingAreaY = 30; // want to go to beginning before seating new customer
    public static final int platingX = 150; public static final int platingY = 250; // want to go to plating area to drop off and pick up orders
    
    private int xRest; private int yRest;
    
    private int tableX = 0;
    private int tableY = 0;
    
    private int xFood = 0;
	private int yFood = 0;
	
	public final static int imageWidth = 20;
	public final static int imageHeight = 20;
	
	private boolean availableToSeat;	
	private boolean goingToCook;
	private String foodLabel;
	private String nameLabel;
    
    private ImageIcon hostImage = new ImageIcon("restaurant_host.jpg");

    public JiWaiterGUI(JiWaiter role, int xRest, int yRest, SimCity gui) {
    	this.role = role;
    	id = "ji";
		
        availableToSeat = true;
        foodLabel = "";
        nameLabel = role.getName();
        this.xRest = xRest;
        this.yRest = yRest;
        xDestination = xRest;
        yDestination = yRest;
        
        this.gui = gui;
     }

    public void updatePosition() {
    	xFood = xPos + imageWidth;
		yFood = yPos + imageHeight;
			
    	if (xPos == waitingAreaX && yPos == waitingAreaY){
    		if (!availableToSeat)
    		{
    			role.msgAtWaitingArea();
    			availableToSeat = true;
    		}
    	}
    	else
    		availableToSeat = false;
    	
    	if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == tableX + 20) & (yDestination == tableY - 20)) {
           role.msgAtTable();
        }
        
        if (xPos == xDestination && yPos == yDestination && goingToCook) {
        	goingToCook = false;
        	role.msgAtCook();
        }
        
        //System.out.print(String.valueOf(xPos) + ", " + String.valueOf(yPos) + "\n");
		  
    }

    public void draw(Graphics2D g) {
    	Image i = hostImage.getImage();
    	g.drawImage(i,  xPos, yPos, xNeg, yNeg, null);
    	g.drawString(nameLabel, xPos, yPos);
    	
    	//g.setColor(Color.MAGENTA);
        //g.fillRect(xPos, yPos, xNeg, yNeg);
    	
    	g.drawString(foodLabel, xFood, yFood);
    }

    public boolean isPresent() {
        return true;
    }

    public void setToBreak() {
		role.askToGoOnBreak();
	}
    
    public void endBreak() {
		role.finishBreak();
	}
    
    public void DoGoToTable(int x, int y) {
        tableX = x;
        tableY = y;
    	xDestination = tableX + 20;
        yDestination = tableY - 20;
    }

    public void DoRest() {
        xDestination = xRest;
        yDestination = yRest;
    }
    
    public void DoRetrieveCustomer(){
    	xDestination = waitingAreaX;
    	yDestination = waitingAreaY;
    }
    
    public void DoSeeCook() {
        xDestination = platingX;
        yDestination = platingY;
        goingToCook = true;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
	public void setChoiceText(String choice, boolean delivered) {
		foodLabel = choice;
		if (!delivered)
			foodLabel += '?';
	}
	public void clearChoicetext() {
		foodLabel = "";
	}
    
	// break enable in user interface
	
//  public void uncheckBreakBox() {
//		gui.enableCheckBoxOption(role, false);
//	}
//	public void setBreakBoxEnabled(boolean enable) {
//		gui.setBreakE(role, enable);
//	}

	
}
