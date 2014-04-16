/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.animation.view;

import global.enumerations.ViewEnum;
import gui.animation.base.BuildingGUI;
import gui.animation.building.*;
import gui.animation.building.restaurant.*;
import gui.animation.view.base.BaseSceneView;
import gui.ui.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author CMCammarano
 */
public class CityView extends BaseSceneView {

	// Caching the parent animation panel
	AnimationPanel animation;
	
	// List of buildings for us to instantiate
	public List<BuildingGUI> buildings;
		
	// Window size
	private static final int WINDOW_X = 1280;
	private static final int WINDOW_Y = 720;
	
	public CityView(AnimationPanel ap) {
		super(ap);
		
		// Setting the animation
		animation = ap;
		name = "City View";
		
		// Setting up the list
		buildings = new ArrayList<BuildingGUI>();
		buildings.add(new CammaranoRestaurantGUI(270, 570, 60, 60));
		buildings.add(new JiRestaurantGUI(570, 150, 60, 60));
		buildings.add(new McNealRestaurantGUI(570, 570, 60, 60));
		buildings.add(new TuRestaurantGUI(570, 0, 60, 60));
		buildings.add(new RedlandRestaurantGUI(690, 570, 60, 60));
		buildings.add(new BankGUI(570, 450, 60, 60));
		buildings.add(new MarketGUI(270, 450, 60, 60));
		buildings.add(new ApartmentGUI(780, 0, 120, 120));
		
		// Setting the window size
		Dimension dim = new Dimension((int)(0.72f * WINDOW_X), (int)(0.96f * WINDOW_Y));
		this.setPreferredSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
	}
	
	@Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// Create our streets
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(390, 0, 120, 720);
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 270, 921, 120);
		
		// Create our sidewalks
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(330, 0, 60, 720);
		
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(510, 0, 60, 720);
		
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(0, 210, 921, 60);
		
		g2.setColor(Color.DARK_GRAY);
		g2.fillRect(0, 390, 921, 60);
		
		// Create our sidewalks
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(390, 0, 120, 720);
		
		/*	
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(510, 0, 60, 720);
		*/
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 270, 921, 120);
		/*
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 390, 921, 60);
		*/
		// Create houses, row 1
		for(int cnt = 0; cnt < 11; cnt++) {
			g2.setColor(Color.BLACK);
			g2.fillRect((cnt * 30), 0, 30, 30);
			
			g2.setColor(Color.ORANGE);
			g2.fillRect((cnt * 30), 1, 28, 28);
		}
		
		// Create houses, row 2
		for(int cnt = 0; cnt < 11; cnt++) {
			g2.setColor(Color.BLACK);
			g2.fillRect((cnt * 30), 90, 30, 30);
			
			g2.setColor(Color.ORANGE);
			g2.fillRect((cnt * 30), 91, 28, 28);
		}
		
		// Create houses, row 3
		for(int cnt = 0; cnt < 11; cnt++) {
			g2.setColor(Color.BLACK);
			g2.fillRect((cnt * 30), 180, 30, 30);
			
			g2.setColor(Color.ORANGE);
			g2.fillRect((cnt * 30), 181, 28, 28);
		}
		
		// Create houses, row 1
		for(int cnt = 0; cnt < 5; cnt++) {
			g2.setColor(Color.BLACK);
			g2.fillRect((cnt * 30), 450, 30, 30);
			
			g2.setColor(Color.ORANGE);
			g2.fillRect((cnt * 30), 451, 28, 28);
		}
		
		// Create houses, row 2
		for(int cnt = 0; cnt < 5; cnt++) {
			g2.setColor(Color.BLACK);
			g2.fillRect((cnt * 30), 540, 30, 30);
			
			g2.setColor(Color.ORANGE);
			g2.fillRect((cnt * 30), 541, 28, 28);
		}
		
		// Create market
		g2.setColor(Color.BLACK);
		g2.fillRect(270, 450, 60, 60);
		
		g2.setColor(Color.BLUE);
		g2.fillRect(272, 452, 56, 56);
		
		// Create Colin's Restaurant
		g2.setColor(Color.BLACK);
		g2.fillRect(270, 570, 60, 60);
		
		g2.setColor(Color.RED);
		g2.fillRect(272, 572, 56, 56);
		
		// Create Kristen's Restaurant
		g2.setColor(Color.BLACK);
		g2.fillRect(570, 570, 60, 60);
		
		g2.setColor(Color.CYAN);
		g2.fillRect(572, 572, 56, 56);
		
		// Create Cathy's Restaurant
		g2.setColor(Color.BLACK);
		g2.fillRect(570, 150, 60, 60);
		
		g2.setColor(Color.YELLOW);
		g2.fillRect(572, 152, 56, 56);
		
		// Create Elsie's Restaurant
		g2.setColor(Color.BLACK);
		g2.fillRect(570, 0, 60, 60);
		
		g2.setColor(Color.PINK);
		g2.fillRect(572, 2, 56, 56);
		
		// Create Jeff's Restaurant
		g2.setColor(Color.BLACK);
		g2.fillRect(690, 570, 60, 60);
		
		g2.setColor(Color.GREEN);
		g2.fillRect(692, 572, 56, 56);
		
		// Create the bank
		g2.setColor(Color.BLACK);
		g2.fillRect(570, 450, 60, 60);
		
		g2.setColor(Color.MAGENTA);
		g2.fillRect(572, 452, 56, 56);
		
		// Create the apartment
		g2.setColor(Color.BLACK);
		g2.fillRect(780, 0, 120, 120);
		
		g2.setColor(Color.WHITE);
		g2.fillRect(782, 2, 116, 116);
		/*
        for(GUI gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(GUI gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
		*/
				
    }
}
