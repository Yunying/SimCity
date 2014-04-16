package gui.animation.role.restaurant.ji;

import restaurant.ji.interfaces.JiCook;
import gui.SimCity;
import gui.animation.base.BaseGUI;
import gui.animation.base.GUI;

import java.awt.*;
import java.util.Random;

import javax.swing.ImageIcon;

public class JiCookGUI extends BaseGUI implements GUI {

    private JiCook role = null;	
	private SimCity gui;

    private int xPos = kitchenX, yPos = kitchenY;//default cook position
    private int xDestination = kitchenX, yDestination = kitchenY;//default start position
    public static final int xNeg = 20, yNeg = 20;
    
    public static final int platingX = 150; public static final int platingWidth = 50;
    public static final int platingY = 250; public static final int platingHeight = 15;
    public static final int kitchenX = 100; public static final int kitchenWidth = 150;
    public static final int kitchenY = 265; public static final int kitchenHeight = 350;
    public static final int grillX = kitchenX+kitchenWidth; public static final int grillWidth = 10;
    public static final int grillY = kitchenY; public static final int grillHeight = kitchenHeight;
    public static final int fridgeX = kitchenX+100; public static final int fridgeY = kitchenY+40; public static final int fridgeLength = 10;
    
    public enum Status {idle, plating, gettingIngredients, cooking};
    public Status status = Status.idle; 
	
	private String foodLabel;
	private String nameLabel;
	
	private int xFood = 0;
	private int yFood = 0;
    
    private ImageIcon cookImage = new ImageIcon("restaurant_cook.jpg");
    int imageHeight = 20;
    int imageWidth = 20;

    public JiCookGUI(JiCook role, SimCity gui) {
        this.role = role;
        id = "ji";
        foodLabel = "";
        this.gui = gui;
        nameLabel = role.getName();
    }

    public void updatePosition() {	
    	xFood = xPos + imageWidth;
		yFood = yPos + imageHeight;
    	boolean atPlating = (xPos == platingX && yPos == platingY);
    	boolean atFridge = (xPos == fridgeX && yPos == fridgeY);
    	boolean atStove = (xPos == grillX && yPos == grillY);
    	
    	if (atPlating && status != Status.idle){
			role.msgAtPlating();
			clearChoicetext();
			DoGoIdle();
		}
    	if (atFridge && status == Status.gettingIngredients){
    		role.msgAtFridge();
    	}
    	if (atStove && status != Status.idle){
    		role.msgAtStove();
    		clearChoicetext();
    		DoGoIdle();
    	}
    	
    	if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;
        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
    }

    public void draw(Graphics2D g) {
    	Image i = cookImage.getImage();
    	g.drawImage(i,  xPos, yPos, xNeg, yNeg, null);
    	g.drawString(nameLabel, xPos, yPos);
		g.drawString(foodLabel, xFood, yFood);
    }

    public boolean isPresent() { return true; }
    public int getXPos() { return xPos;}
    public int getYPos() { return yPos; }
    
	public void DoGoToStove(boolean cooking){
		xDestination = grillX;
		yDestination = grillY;
		status = cooking? Status.cooking : Status.plating;
	}
	public void DoGoToFridge() {
		xDestination = fridgeX;
		yDestination = fridgeY;
		status = Status.gettingIngredients;
	}
	public void DoGoToPlating(boolean plating) {
		xDestination = platingX;
		yDestination = platingY;
		status = plating? Status.plating : Status.gettingIngredients;
	}
	public void DoCook(){
		xDestination = grillX-imageWidth;
		yDestination = grillY;
		status = Status.cooking;
	}
	public void DoGoIdle() {
		xDestination = kitchenX;
		yDestination = kitchenY;
		status = Status.idle;
	}
	public void setChoiceText(String choice, boolean cooked) {
		foodLabel = choice;
		if (!cooked)
			foodLabel += "...";
	}
	public void clearChoicetext() {
		foodLabel = "";
	}
	
	
	
}
