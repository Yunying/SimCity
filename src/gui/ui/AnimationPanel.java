/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.ui;

import gui.animation.view.HouseView;
import gui.animation.view.MarketView;
import gui.animation.view.BankView;
import gui.animation.view.restaurant.JiRestaurantView;
import gui.animation.view.restaurant.McNealRestaurantView;
import gui.animation.view.restaurant.RedlandRestaurantView;
import gui.animation.view.restaurant.TuRestaurantView;
import gui.animation.view.CityView;
import gui.animation.view.restaurant.CammaranoRestaurantView;
import gui.animation.base.BuildingGUI;
import gui.animation.base.GUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;


/**
 *
 * @author CMCammarano
 */
public class AnimationPanel extends JPanel implements ActionListener {

	public ArrayList<GUI> guis;
	public List<BuildingGUI> buildings;
	
	// Animation windows
	private CityView cityView;
	private BankView bankView;
	private HouseView houseView;
	private MarketView marketView;

	// Restaurant views
	private CammaranoRestaurantView cammRestView;
	private JiRestaurantView jiRestView;
	private TuRestaurantView tuRestView;
	private McNealRestaurantView mcRestView;
	private RedlandRestaurantView redRestView;
	
	private JTabbedPane pane;
	
	// List of views to update
	public List<JPanel> views;
	
	// Main panel
	private JPanel panel;
	
	private static final int WINDOWX = 1280;
	private static final int WINDOWY = 720;

	public AnimationPanel() {
		setVisible(true);
		guis = new ArrayList<GUI>();
		views = new ArrayList<JPanel>();
		
		// Instantiating our views
		cityView = new CityView(this);
		views.add(cityView);
		
		bankView = new BankView(this);
		views.add(bankView);
		
		houseView = new HouseView(this);
		views.add(houseView);
		
		marketView = new MarketView(this);
		views.add(marketView);

		// Restaurant views
		cammRestView = new CammaranoRestaurantView(this);
		views.add(cammRestView);
		
		jiRestView = new JiRestaurantView(this);
		views.add(jiRestView);
		
		tuRestView = new TuRestaurantView(this);
		views.add(tuRestView);
		
		mcRestView = new McNealRestaurantView(this);
		views.add(mcRestView);
		
		redRestView = new RedlandRestaurantView(this);
		views.add(redRestView);
		
		pane = new JTabbedPane();
		Dimension paneDim = new Dimension((int)(0.72f * WINDOWX), (int)(0.96f * WINDOWY));
		pane.setPreferredSize(paneDim);
		pane.setMinimumSize(paneDim);
		pane.setMaximumSize(paneDim);
		
		panel = new JPanel();
		Dimension dim = new Dimension((int)(0.72f * WINDOWX), (int)(0.96f * WINDOWY));
		panel.setPreferredSize(dim);
		panel.setMinimumSize(dim);
		panel.setMaximumSize(dim);
		panel.setLayout(new BorderLayout());
		
		pane.add(cityView, "City View");
		pane.add(bankView, "Bank View");
		//pane.add(houseView, "House View");
		pane.add(marketView, "Market View");
		pane.add(cammRestView, "Cammarano Restaurant View");
		pane.add(jiRestView, "Ji Restaurant View");
		pane.add(tuRestView, "Tu Restaurant View");
		pane.add(mcRestView, "McNeal Restaurant View");
		pane.add(redRestView, "Redland Restaurant View");

		panel.add(pane);
		
		add(panel);
		
		Timer timer = new Timer(20, this);
		timer.start();
		
		//cardLayout.show(panel, "City View");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (JPanel view : views) {
			view.revalidate();
			view.repaint();
		}
		revalidate();
		repaint();
	}

    public void addGUI(GUI gui) {
        guis.add(gui);
	}
}
