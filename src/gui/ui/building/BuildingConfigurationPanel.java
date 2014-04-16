package gui.ui.building;

import housing.interfaces.House;
import interfaces.Building;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import restaurant.yunying.TuRestaurantAgent;
import market.MarketAgent;
import bank.interfaces.Bank;
import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;
import gui.ui.*;

public class BuildingConfigurationPanel extends JPanel implements ActionListener{
		//For test use
		public EventLog log = new EventLog();
	
		//Import other related panels
		private UserInterfacePanel uiPanel;
		BuildingPanel buildingPanel;
		
		//Main Panel
		private JTabbedPane Config;
		private JPanel timePanel;
		private JPanel jobPanel;
		
		//Global Components
		private Building currentBuilding;
		
		//Time Components
		JPanel twoRadioButton;
		private JComboBox time;
		private JComboBox job;
		private Vector<Integer> times = new Vector<Integer>();
		ButtonGroup group = new ButtonGroup();
		private JRadioButton start;
		private JRadioButton close;
		private JButton setTime;
		private JButton hire;
		
		
		//Final int
		private static final int WINDOWX = 1280;
		private static final int WINDOWY = 720;
		final static String TIME = "Operating Hours";
		final static String JOB = "Jobs";
		Dimension buildingPaneDim = new Dimension((int)(0.22f * WINDOWX), (int)(0.19f * WINDOWY));
		Dimension timePanelDim = new Dimension((int)(0.15f * WINDOWX), (int)(0.16f * WINDOWY));
		Dimension buttonSize = new Dimension((int)(0.1f * WINDOWX), (int)(0.04f * WINDOWY));
		Dimension buttonSize2 = new Dimension((int)(0.1f * WINDOWX), (int)(0.1f * WINDOWY));
		
		public BuildingConfigurationPanel(BuildingPanel b){
			//Initialize
			Config = new JTabbedPane();
			buildingPanel = b;
			Config.setBorder(BorderFactory.createRaisedBevelBorder());
			//Config.setBorder(BorderFactory.createTitledBorder("Set OperationTime"));
			
			//set dimension
			Config.setPreferredSize(buildingPaneDim);
			Config.setMinimumSize(buildingPaneDim);
			Config.setMaximumSize(buildingPaneDim);
			
			//Main Panels
			timePanel = new JPanel();
			jobPanel = new JPanel();
			
			//other functions
			initializeConfigPanel();
			initializeJobPanel();
			
			//Card Layout
			Config.add("Time",timePanel);
			Config.add("Job", jobPanel);

			//Show
			add(Config);
		}
	
		private void initializeConfigPanel(){
			
			//Initialize
			timePanel.setPreferredSize(timePanelDim);
			timePanel.setMinimumSize(timePanelDim);
			timePanel.setMaximumSize(timePanelDim);
			timePanel.setLayout(new BorderLayout());
			
			//Drop-down List
			for (int i=0; i<24; i++){
				times.add(i);
			}
			time = new JComboBox(times);
			time.addActionListener(this);
			time.setPreferredSize(buttonSize);
			time.setMinimumSize(buttonSize);
			time.setMaximumSize(buttonSize);
			timePanel.add(time, BorderLayout.WEST);
			
			//Choose Start or End
			twoRadioButton = new JPanel();
			twoRadioButton.setPreferredSize(buttonSize2);
			twoRadioButton.setMinimumSize(buttonSize2);
			twoRadioButton.setMaximumSize(buttonSize2);
			start = new JRadioButton("Start");
			close = new JRadioButton("Close");
			group.add(start);
			group.add(close);
			twoRadioButton.add(start);
			twoRadioButton.add(close);
			timePanel.add(twoRadioButton, BorderLayout.EAST);
			
			//Set-up button
			setTime = new JButton("Set Time");
			setTime.addActionListener(this);
	        setTime.setPreferredSize(buttonSize);
	        setTime.setMinimumSize(buttonSize);
	        setTime.setMaximumSize(buttonSize);
			timePanel.add(setTime, BorderLayout.SOUTH);
		}
		
		private void initializeJobPanel(){
			//Initialize
			jobPanel.setPreferredSize(timePanelDim);
			jobPanel.setMinimumSize(timePanelDim);
			jobPanel.setMaximumSize(timePanelDim);
			jobPanel.setLayout(new GridLayout(2,1,0,5));
			
			//Drop Box
			//Drop-down List
			job = new JComboBox();
			job.addActionListener(this);
			job.setPreferredSize(buttonSize);
			job.setMinimumSize(buttonSize);
			job.setMaximumSize(buttonSize);
			jobPanel.add(job);
			
			//Hire button
			hire = new JButton("Hire");
			hire.addActionListener(this);
	        hire.setPreferredSize(buttonSize);
	        hire.setMinimumSize(buttonSize);
	        hire.setMaximumSize(buttonSize);
			jobPanel.add(hire);
		}
		
		public void updatePanel(Building b){
			log.add(new LoggedEvent("choose a building "+b.getName()));
			System.out.println("Button pressed "+ b.getName());
			currentBuilding = b;
		}
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//setTime button action listener
			if (e.getSource().equals(setTime)){
				int newTime = time.getSelectedIndex();
				if (start.isSelected()){
					log.add(new LoggedEvent("set start time "+start));
					try {
						currentBuilding.setStartTime(newTime);
					}catch(NullPointerException c){
						System.out.println("Please choose a building first");
					}
				}
				else{
					log.add(new LoggedEvent("set close time "+close));
					try {
						currentBuilding.setCloseTime(newTime);
					}catch(NullPointerException c){
						System.out.println("Please choose a building first");
					}
				}
				
			}
			
			//Hire
			if (e.getSource().equals(hire)){
				if (currentBuilding instanceof Bank){
					
				}
				else if (currentBuilding instanceof MarketAgent){
					
				}
				else if (currentBuilding instanceof House){
					
				}
				else {
					
				}
			}
		}

}
