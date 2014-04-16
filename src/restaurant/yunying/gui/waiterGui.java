package restaurant.yunying.gui;


import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;

import java.awt.*;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class waiterGui implements Gui {

    private Waiter agent = null;
    int number;
    

    private int xPos, yPos;//default waiter position
    private int xDestination, yDestination;//default start position
    int DEFAULTX;

    public static final int xTable = 200;
    public static final int yTable = 250;
    
    private Map<Integer, Dimension> tableMap = new HashMap<Integer, Dimension>();
    
    BufferedImage waiterImage = null;
    private boolean showOrder = false;
    private String choice;
    RestaurantGui gui = null;
    boolean isOnBreak = false;
    
    private boolean hostGuiStatus = false;
    private boolean takingOrder = false;
    private int num = 0;
    private Vector<Integer> tables
	= new Vector<Integer>();
    private boolean paused = false;
    Timer timer = new Timer();
    
    public waiterGui(Waiter w, RestaurantGui gui, int n){
    	number = n;
    	DEFAULTX = 33*n;
    	this.agent = w;
    	this.gui = gui;
    	tableMap.put(1, new Dimension(200,250));
    	tableMap.put(2, new Dimension(300,250));
    	tableMap.put(3, new Dimension(400,250));
    	try{
			waiterImage = ImageIO.read(getClass().getResource("./waiter.jpg"));
		}
		catch (IOException e){}
    	xPos = 0; yPos = 0;//default waiter position
    	xDestination = DEFAULTX; yDestination = 30;
    }

    boolean arrive = false;

    public void updatePosition() {
//    	if (xPos != -20 && yPos != 20){
//    		hostGuiStatus = false;
//    	}
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;

        if (xPos == xDestination && yPos == yDestination && xDestination != DEFAULTX && !takingOrder && yDestination != 100) 
        {
           //System.out.println("Release");
           //System.out.println("Here");
           agent.msgAtTable();
        }
        
        if (xPos == xDestination && yPos == yDestination && xDestination != DEFAULTX && takingOrder){
        	if (num == 0){
        		agent.msgAtTable();
        		num++;
        	}
        }
        
        if (yDestination == 100 && yPos == 100 && xPos == 450 && !isOnBreak){
        	isOnBreak = true;
        	agent.msgAtTable();
        	System.out.println("Waiting to get on work again");
//        	timer.schedule(new TimerTask() {
//    			public void run() {
//    				System.out.println("Done break");
//    				gui.setWaiterEnabled(agent);
//    				startWorking();
//    				agent.msgBackToWork();
//    			}
//    		},
//    		4000);
        }

        
        if (xPos == DEFAULTX && yPos == 30){
        	hostGuiStatus = false;
        }
        
    }

    public void draw(Graphics2D g) {
        
        g.setColor(Color.magenta);
        //g.fillRect(xPos, yPos, 20, 20);
        g.drawImage(waiterImage,xPos,yPos,null);
        if (showOrder){
			g.drawString(choice, xPos+20,yPos+10);
		}
        try{
	        if (!tables.isEmpty()){
	        	for (int t:tables){
	        		Dimension tableDimension = tableMap.get(t);
	        		g.drawString("toBeCleaned", (int)(tableDimension.getWidth()), (int)(tableDimension.getHeight()));
	        	}
	        }
        }catch(java.util.ConcurrentModificationException c){
        	return;
        }
    }
    
    public void offBreak(){
    	gui.setWaiterEnabled(agent);
    	startWorking();
    	agent.msgBackToWork();
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

    public void DoBringToTable( int table) {
    	takingOrder = false; num = 0;
    	Dimension tableDimension = tableMap.get(table);
        xDestination = (int)(tableDimension.getWidth()) + 20;
        yDestination = (int)(tableDimension.getHeight()) + 20;
    }
    
    public void DoGoToCashier(){
    	xDestination = 120;
    	yDestination = 150;
    }
    
    public void DoAskForAnOrder(int table){
    	takingOrder = false; num = 0;
    	Dimension tableDimension = tableMap.get(table);
        xDestination = (int)(tableDimension.getWidth()) + 20;
        yDestination = (int)(tableDimension.getHeight()) +20;
        takingOrder = true;
    }
    
    public void doSeatCustomer1(int waiting){
    	takingOrder = false; num = 0;
    	xDestination = waiting*30-10;
    	yDestination = 20;
    }
    
    public void DoGoToCook(){
    	takingOrder = false; num = 0;
    	xDestination = 300;
    	yDestination = 420;
    }
    
    public void takeOrderToCook(String customerChoice){
    	DoGoToCook();
    }

    
    public void foodDeliveredToCustomer(){
    	showOrder = false;
    	choice = "";
    }

    
    public void takeFoodToCustomer(int table, String customerChoice){
    	DoBringToTable(table);
    	showOrder = true;
    	choice = customerChoice;
    }
    
    public void DoCleanTable(int table){
    	DoBringToTable(table);
    	tables.add(table);
    }
    
    public void isCleaned(int table){

    	tables.removeElement(table);
    }

    public void DoLeaveCustomer() {
    	takingOrder = false; num = 0;
        xDestination = DEFAULTX;
        yDestination = 30;
        
    }
    
    public void startWorking(){
    	xDestination = DEFAULTX;
        yDestination = 30;
        arrive = true;
    }
    
    public void setBreakGui(){
    	gui.setWaiterEnabled(agent);
    }
    
    public void DoGoToBreak(){
    	xDestination = 450;
    	yDestination = 100;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
