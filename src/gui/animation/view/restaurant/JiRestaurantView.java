/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.animation.view.restaurant;

import gui.animation.base.GUI;
import gui.animation.view.base.BaseSceneView;
import gui.ui.AnimationPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;

import javax.swing.*;


/**
 *
 * @author CMCammarano
 */
public class JiRestaurantView extends BaseSceneView implements ActionListener {

	// Caching the parent animation panel
	AnimationPanel animation;

	// Main panel
	private JPanel panel;
	
	// Window size
	private static final int WINDOW_X = 1280;
	private static final int WINDOW_Y = 720;
	
	//**layout of restaurant**//
	//seating dimensions
	static final int tableStartingX = 50;
    static final int tableStartingY = 150;
    static final int tableSide = 50;
    static final int windowStartingCoord = 0;
    
    // waiting area dimensions
    public static final int waitingAreaStartX = 0; public static final int waitingLength = 100;
	public static final int waitingAreaStartY = 30; public static final int waitingHeight = 70;
	
	//kitchen and plating dimensions
	public static final int platingX = 150; public static final int platingWidth = 50;
    public static final int platingY = 250; public static final int platingHeight = 15;
    public static final int kitchenX = 100; public static final int kitchenWidth = 150;
    public static final int kitchenY = 265; public static final int kitchenHeight = 350;
    public static final int grillX = kitchenX+kitchenWidth; public static final int grillWidth = 10;
    public static final int grillY = kitchenY; public static final int grillHeight = kitchenHeight;
    public static final int fridgeX = kitchenX+100; public static final int fridgeY = kitchenY+40; public static final int fridgeLength = 10;
    
	// waiter information
    public int numWaiters = 0;
    int xWaiterStart = 0; int yWaiterStart = 0; int waiterSideLength = 20;
    
//    private List<Gui> guis = new ArrayList<Gui>();
    
	public JiRestaurantView(AnimationPanel ap) {
		
		super(ap);
		// Setting the animation
		animation = ap;
		name = "Ji Restaurant View";
		
		// Setting the window size
		Dimension dim = new Dimension((int)(0.72f * WINDOW_X), (int)(0.96f * WINDOW_Y));
		this.setPreferredSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
		
//		Timer timer = new Timer(5, this ); // speed of animation
//    	timer.start();
    	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}


    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());//WINDOWX, WINDOWY );
        
        // waiting area
        g2.setColor(Color.BLUE);
        g2.fillRect(waitingAreaStartX, waitingAreaStartY, waitingLength, waitingHeight);
        
        // waiter resting positions
        for (int i = 0; i < numWaiters; i++){
        	g2.setColor(Color.RED);
        	//g2.fillRect(20*i + xWaiterStart, yWaiterStart, WaiterGui.imageWidth, WaiterGui.imageWidth);
        	g2.drawString(String.valueOf(i+1), waiterSideLength*i + xWaiterStart, waiterSideLength);
        }
        
        // table 1
        g2.setColor(Color.ORANGE);
        g2.fillRect(tableStartingX, tableStartingY, tableSide, tableSide);//200 and 250 need to be table params

        
    	// table 2
        g2.setColor(Color.ORANGE);
        g2.fillRect(tableStartingX+2*tableSide, tableStartingY, tableSide, tableSide);

        // table 3
        g2.setColor(Color.ORANGE);
        g2.fillRect(tableStartingX+4*tableSide, tableStartingY, tableSide, tableSide);
        
        //plating
        g2.setColor(Color.red);
        g2.fillRect(platingX, platingY, platingWidth, platingHeight);
        
        //kitchen
        g2.setColor(Color.white);
        g2.fillRect(kitchenX, kitchenY, kitchenWidth, kitchenHeight);
        
        //fridge and stove
        g2.setColor(Color.black);
        g2.drawString("grill", grillX, grillY);
        g2.fillRect(grillX, grillY, grillWidth, grillHeight);
        g2.drawString("fridge", fridgeX, fridgeY+2*fridgeLength);
        g2.fillRect(fridgeX, fridgeY, fridgeLength, fridgeLength);
        
        g2.setColor(Color.black);
        
		if(animation.guis != null) {
			for(GUI gui : animation.guis) {
				if(gui.getID().equals("ji")) {
					if (gui.isPresent()) {
						gui.updatePosition();
					}
				}
			}

			for(GUI gui : animation.guis) {
				if(gui.getID().equals("ji")) {
					if (gui.isPresent()) {
						gui.draw(g2);
					}
				}
			}
		}
    }
//        
//    
//    public void addGui(CustomerGui gui) {
//        guis.add(gui);
//    }
//
//    /*
//    public void addGui(HostGui gui) {
//        guis.add(gui);
//    }
//    */
//
//	public void addGui(WaiterGui gui) {
//		guis.add(gui);
//		numWaiters++;
//	}
//
//	public void addGui(CookGui g) {
//		// TODO Auto-generated method stub
//		guis.add(g);
//		
		
		
		
		
}
