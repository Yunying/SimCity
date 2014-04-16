package gui.animation.role.restaurant.ji;

/*
 * 
 * package restaurant.gui;



import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;

import javax.swing.ImageIcon;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public static final int xNeg = 20, yNeg = 20;
    
    public static final int xStart = -20, yStart = -20; // want to go to beginning before seating new customer
    
    private int tableX = 0;
    private int tableY = 0;
    
    
    private ImageIcon hostImage = new ImageIcon("C:/Users/Cathy/Documents/1. USC Sophomore year/CSCI 201/restaurant_host.jpg");

    public HostGui(HostAgent agent) {
        this.agent = agent;
     }

    public void updatePosition() {
    	if (xPos == xStart && yPos == yStart){
    		agent.msgAtStart();
    	}
    	
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
           agent.msgAtTable();
           
        }
           
    }

    public void draw(Graphics2D g) {
    	Image i = hostImage.getImage();
    	g.drawImage(i,  xPos, yPos, xNeg, yNeg, null);
    	
    	//g.setColor(Color.MAGENTA);
        //g.fillRect(xPos, yPos, xNeg, yNeg);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer, HostAgent.Table table) {
        tableX = table.getX();
        tableY = table.getY();
    	xDestination = tableX + 20;
        yDestination = tableY - 20;
    }

    public void DoLeaveCustomer() {
        xDestination = xStart;
        yDestination = yStart;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}

*/