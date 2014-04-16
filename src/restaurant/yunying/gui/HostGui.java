package restaurant.yunying.gui;


import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HostGui implements Gui {

    private Host agent = null;

    private int xPos = 500, yPos = 100;//default waiter position
    public int xDestination = -20, yDestination = -20;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;
    //private boolean present = true;
    private boolean paused = false;
    
    public boolean hostGuiStatus = true;
    BufferedImage hostImage = null;

    public HostGui(Host agent) {
        this.agent = agent;
        try{
			hostImage = ImageIO.read(getClass().getResource("./host.jpg"));
		}
		catch (IOException e){}
    
    }

    public void updatePosition() {
    	
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.BLUE);
        //g.fillRect(xPos, yPos, 20, 20);
        g.drawImage(hostImage,xPos,yPos,null);
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
