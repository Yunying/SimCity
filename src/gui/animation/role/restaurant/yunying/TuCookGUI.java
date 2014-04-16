package gui.animation.role.restaurant.yunying;


import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;
import gui.animation.base.BaseGUI;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TuCookGUI extends BaseGUI implements Gui {

    private Cook agent = null;
    String item = "";

    private int xPos = 301, yPos = 550;//default waiter position
    public int xDestination = 301, yDestination = 550;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;
    //private boolean present=true;
    
    public boolean hostGuiStatus = true;
    private boolean paused = false;
    BufferedImage cookImage = null;
    public boolean cooking;
    private int cookingNum = 0;

    public TuCookGUI(Cook agent) {
        this.agent = agent;
		id = "tu";
        try{
			cookImage = ImageIO.read(getClass().getResource("./cook.jpg"));
		}
		catch (IOException e){}
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

        if (xPos == xDestination && yPos == yDestination && !cooking && xDestination!= 301) 
        {
           agent.msgAnimation();
        }
        
        if (xPos == xDestination && yPos == yDestination && cooking && cookingNum == 0) 
        {
           agent.msgAnimation();
           cookingNum++;
        }
        
        
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        //g.fillRect(xPos, yPos, 20, 20);
        g.drawImage(cookImage,xPos,yPos,null);
        if (!item.equals("")){
        	g.drawString(item, xPos+20, yPos+10);
        }
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
    
    public void DoPlating(){
    	//print("Do plating");
    	cooking = false;
    	cookingNum = 0;
    	xDestination = 300;
    	yDestination = 450;
    }
    
    public void DoWaiting(){
    	//print("Do waiting");
    	item = "";
    	cookingNum = 0;
    	cooking = true;
    	xDestination = 300;
    	yDestination = 550;
    }
    
    public void DoCooking(String choice){
    	//print("Do cooking");
    	cooking = true;
    	cookingNum = 0;
    	xDestination = 300;
    	yDestination = 500;
    	item = choice;
    }
    
    public void DoGetIngredients(){
    	//print("Do get indredients");
    	item = "";
    	cooking = false;
    	cookingNum = 0;
    	xDestination = 350;
    	yDestination = 500;
    }



    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    void print(String s){
    	System.out.println(s);
    }
}
