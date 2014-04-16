/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.animation.view;

import gui.animation.base.GUI;
import gui.animation.view.base.BaseSceneView;
import gui.ui.AnimationPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author CMCammarano
 */
public class BankView extends BaseSceneView implements ActionListener {

	// Caching the parent animation panel
	AnimationPanel animation;

	// Main panel
	private JPanel panel;
	
	// Window size
	private static final int WINDOW_X = 1280;
	private static final int WINDOW_Y = 720;
	
	
	// teller window dimensions
	static final int tellerStartingX = 50;
    static final int tellerStartingY = 150;
    static final int tellerLength = 50;
    static final int tellerHeight = 70;
    
    // waiting area dimensions
    public static final int waitingAreaStartX = 300; public static final int waitingLength = WINDOW_X/2;
	public static final int waitingAreaStartY = 300; public static final int waitingHeight = WINDOW_Y/2;
	
	
	public BankView(AnimationPanel ap) {
		super(ap);
		// Setting the animation
		animation = ap;
		name = "Bank View";
		
		// Setting the window size
		Dimension dim = new Dimension((int)(0.72f * WINDOW_X), (int)(0.96f * WINDOW_Y));
		this.setPreferredSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
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

        // bank windows
        for (int i = 0; i < 5; i++){
     		g2.setColor(Color.BLACK);
     		g2.fillRect(tellerStartingX, tellerStartingY, tellerLength, tellerHeight);
     	}
        
        //waiting area
        g2.setColor(Color.RED);
        g2.fillRect(waitingAreaStartX, waitingAreaStartY, waitingLength, waitingHeight);
        
        
        for(GUI gui : animation.guis) {
			if(gui.getID().equals("bank")) {
				if (gui.isPresent()) {
					gui.updatePosition();
				}
			}
        }

        for(GUI gui : animation.guis) {
			if(gui.getID().equals("bank")) {
				if (gui.isPresent()) {
					gui.draw(g2);
				}
			}
        }
	}
}
