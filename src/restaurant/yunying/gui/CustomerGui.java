package restaurant.yunying.gui;

import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class CustomerGui implements Gui{

	private Customer agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	RestaurantGui gui;
	private JLabel choice = null;
	double workingTime;
	Timer timer = new Timer();

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, Paying, LeaveRestaurant, Working, inRestaurant, adjusting};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;
	
	private boolean showMyChoice = false;
	private String myChoice;
	private boolean paused = false;
	
	BufferedImage customerImage = null;
	
	public CustomerGui(Customer c, RestaurantGui gui){ //HostAgent m) {
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		//maitreD = m;
		this.gui = gui;
		try{
			customerImage = ImageIO.read(getClass().getResource("./customer.jpg"));
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if(command == Command.adjusting) agent.msgAnimationFinishedAdjusting();
			else if (command == Command.inRestaurant) agent.msgAnimationFinishedGoToRestaurant();
			else if (command == Command.Paying)agent.msgAnimationFinishedGoToCashier(); 
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			else if (command == Command.Working){
				timer.schedule(new TimerTask() {
	    			public void run() {
	    				System.out.println("Working");
	    				agent.doneWorking();
	    			}
	    		},
	    		(long) workingTime);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		
		g.setColor(Color.RED);
        g.fillRect(xPos, yPos, 20, 20);
        g.drawImage(customerImage,xPos,yPos,null);
		if (showMyChoice){
			g.drawString(myChoice, xPos+20,yPos+10);
		}
		
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void DoAdjustPosition(int num){
		xDestination = 30*num - 29;
		yDestination = 0;
		command = command.adjusting;
	}

	
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	
	public void showChoice(String CustomerChoice){
		myChoice = CustomerChoice + "?";
		showMyChoice = true;
	}
	
	public void getFood(String CustomerChoice){
		myChoice = CustomerChoice;
	}
	
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void setPaused(boolean p){
		paused = p;
	}
	
    public boolean isPaused(){
    	return paused;
    }

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		xDestination = xTable + 100*(seatnumber-1);
		yDestination = yTable;
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		showMyChoice = false;
		myChoice = "";
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
	
	public void DoGoToCashier(){
		yDestination = 130;
		xDestination = 100;
		command = Command.Paying;
	}
	
	public void DoGoToCook(double owe){
		xDestination = 300;
		yDestination = 450;
		command = Command.Working;
		workingTime = owe*1000;
	}
	
	public void DoGoToRestaurant(){
		xDestination = 0;
		yDestination = 0;
		command = Command.inRestaurant;
	}
}
