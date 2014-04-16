package restaurant.yunying.gui;


import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CashierGui implements Gui {

    private Cashier agent = null;

    private int xPos = 100, yPos = 100;//default waiter position
    public int xDestination = -20, yDestination = -20;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;
    //private boolean present=true;
    
    public boolean hostGuiStatus = true;
    private boolean paused = false;
    BufferedImage cImage = null;
    private boolean hasDebt = false;
    private double debt;

    public CashierGui(Cashier agent) {
        this.agent = agent;
        try{
			cImage = ImageIO.read(getClass().getResource("./cashier.png"));
		}
		catch (IOException e){}
    }

    public void updatePosition() {

    }

    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawImage(cImage,xPos,yPos,null);
        if (hasDebt){
        	g.drawString("Owe Market a debt of $"+debt + ". Will pay next time", xPos+50,yPos+10);
        }
    }
    
    public void haveDebt(double d){
    	debt = d;
    	hasDebt = true;
    }
    
    public void clearDebt(){
    	hasDebt = false;
    }

    public boolean isPresent() {
        return true;
    }
    
    public void setPaused(boolean b){
    	paused = b;
    }
    
    public boolean isPaused(){
    	return paused;
    }


    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
