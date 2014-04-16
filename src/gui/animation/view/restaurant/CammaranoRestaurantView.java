/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.animation.view.restaurant;

import gui.animation.base.GUI;
import gui.animation.view.base.BaseSceneView;
import gui.ui.AnimationPanel;
import java.awt.BorderLayout;
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
public class CammaranoRestaurantView extends JPanel implements ActionListener {

	private int tableCount = 0;
	
	// Caching the parent animation panel
	private AnimationPanel animation;
	
	// Main panel
	private JPanel panel;
	
	// Window size
	private static final int WINDOW_X = 1280;
	private static final int WINDOW_Y = 720;
	
	public CammaranoRestaurantView(AnimationPanel ap) {
		
		// Setting the animation
		animation = ap;
		
		// Setting the window size
		Dimension dim = new Dimension((int)(0.72f * WINDOW_X), (int)(0.96f * WINDOW_Y));
		this.setPreferredSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
	}
	
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());//WINDOWX, WINDOWY );

        if(tableCount >= 6) {
        	tableCount = 6;
        }
        
        // Base code for filling in tables. I can make a list of them here, then set their coordinates for the agents when they're drawn.
     	for(int cnt = 0; cnt < 6; cnt++) {
     		//Here is the table
     		g2.setColor(Color.ORANGE);
     		g2.fillRect(180 + (100 * cnt), 180 + 100, 50, 50);//200 and 250 need to be table params
     	}
     		
		// Base code for filling in tables. I can make a list of them here, then set their coordinates for the agents when they're drawn.
		for(int cnt = 0; cnt < tableCount; cnt++) {
			//Here is the table
			g2.setColor(Color.ORANGE);
			g2.fillRect(150 + (100 * cnt), 100, 50, 50);//200 and 250 need to be table params
		}

		// Grill
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(700, 540, 30, 40);
		
		g2.setColor(Color.BLACK);
		g2.fillRect(705, 545, 20, 30);
		
		// Refrigerator
		g2.setColor(Color.GRAY);
		g2.fillRect(600, 590, 30, 30);
		
		// Plating Area
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(500, 540, 30, 40);
	
		// Food Area
		g2.setColor(Color.GRAY);
		g2.fillRect(500, 500, 200, 20);
		
        for(GUI gui : animation.guis) {
			if(gui.getID().equals("camm")) {
				if (gui.isPresent()) {
					gui.updatePosition();
				}
			}
        }

        for(GUI gui : animation.guis) {
			if(gui.getID().equals("camm")) {
				if (gui.isPresent()) {
					gui.draw(g2);
				}
			}
        }
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}
