/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.ui.building;

import gui.ui.UserInterfacePanel;
import interfaces.Building;

import java.awt.*;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 *
 * @author CMCammarano
 */
public class BuildingPanel extends JPanel implements ActionListener {

	// Main UI Panel
	private UserInterfacePanel uiPanel;
	
	// Main panel
	private JPanel panel;
	
	//Building List
	public ArrayList<Building> buildings;
	
	// Component Panels
	public BuildingButtonPanel buttonPanel;
	public BuildingConfigurationPanel configurationPanel;
	public BuildingInfoPanel informationPanel;
	
	// Static final vaiables
	private static final int WINDOWX = 1280;
	private static final int WINDOWY = 720;
	
	public BuildingPanel(UserInterfacePanel ui) {
		//Higher: UI Panel
		uiPanel = ui;
			
		//Lower: BuildingPanel components
		configurationPanel = new BuildingConfigurationPanel(this);
		informationPanel = new BuildingInfoPanel(this);
		buttonPanel = new BuildingButtonPanel(this, informationPanel, configurationPanel);
		
		//Data
		buildings = new ArrayList<Building>();
		
		// Here is where you would write all building editing code
		// Make a package in the ui package(building)
		// Make a new class BuildingInfoPanel
		// Add all GUI elements
		// buildingConfigurationPanel.add(new thing);
		
		// Main panel to hold all elements
		panel = new JPanel();
		Dimension dim = new Dimension((int)(0.24f * WINDOWX), (int)(0.9f * WINDOWY));
		panel.setPreferredSize(dim);
		panel.setMinimumSize(dim);
		panel.setMaximumSize(dim);
		
		panel.setLayout(new BorderLayout());
		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.add(configurationPanel, BorderLayout.CENTER);
		panel.add(informationPanel, BorderLayout.SOUTH);
		
		add(panel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	
	public void AddBuilding(String name, Building b) {
		//System.out.println("Building Panel add building");
		JButton button = new JButton(name);
		button.setBackground(Color.white);
		buildings.add(b);
		Dimension buttonSize = new Dimension((int)(0.22f * WINDOWX), (int) ((int)(0.3f * WINDOWY) / 8));
		button.setPreferredSize(buttonSize);
		button.setMinimumSize(buttonSize);
		button.setMaximumSize(buttonSize);
		button.addActionListener(this);
		buttonPanel.addButton(button, b);
		repaint();
	}
	
	public BuildingInfoPanel getInfoPanel(){
		return informationPanel;
	}
	
	public ArrayList<Building> getBuildings(){
		return buildings;
	}
	
	public BuildingConfigurationPanel getConfigPanel(){
		return configurationPanel;
	}
}
