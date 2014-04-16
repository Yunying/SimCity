package gui.ui.building;

import global.roles.Role;
import gui.ui.*;
import housing.LandlordRole;
import housing.interfaces.House;
import interfaces.Building;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import restaurant.cammarano.CammaranoRestaurantAgent;
import restaurant.cammarano.roles.CammaranoCashierRole;
import restaurant.cammarano.roles.CammaranoCookRole;
import restaurant.cammarano.roles.CammaranoHostRole;
import restaurant.cammarano.roles.CammaranoWaiterRole;
import restaurant.ji.JiRestaurantAgent;
import restaurant.ji.roles.JiCashierRole;
import restaurant.ji.roles.JiCookRole;
import restaurant.ji.roles.JiHostRole;
import restaurant.ji.roles.JiWaiterRole;
import restaurant.mcneal.McNealRestaurantAgent;
import restaurant.mcneal.roles.McNealCashierRole;
import restaurant.mcneal.roles.McNealCookRole;
import restaurant.mcneal.roles.McNealHostRole;
import restaurant.mcneal.roles.McNealWaiterRole;
import restaurant.redland.RedlandRestaurantAgent;
import restaurant.redland.roles.RedlandCashierRole;
import restaurant.redland.roles.RedlandCookRole;
import restaurant.redland.roles.RedlandHostRole;
import restaurant.redland.roles.RedlandWaiterRole;
//import restaurant.redland.RedlandRestaurantAgent;
import restaurant.yunying.TuRestaurantAgent;
import restaurant.yunying.roles.TuCashierRole;
import restaurant.yunying.roles.TuCookRole;
import restaurant.yunying.roles.TuHostRole;
import restaurant.yunying.roles.TuWaiterRole;
import market.MarketAgent;
import market.MarketEmployeeRole;
import market.MarketManagerRole;
import market.TruckDriverRole;
import bank.BankAgent;
import bank.BankSecurityRole;
import bank.BankTellerRole;
import bank.interfaces.Bank;

public class BuildingInfoPanel extends JPanel implements ActionListener{
	//Import other related panels
	private UserInterfacePanel uiPanel;
	BuildingPanel buildingPanel;
	
	//Main Panel
	private JPanel infoPanel;
	
	//Final int
	private static final int WINDOWX = 1280;
	private static final int WINDOWY = 720;
	
	//Components
	Building currentBuilding;
	JPanel timeInfoPanel;
	JPanel peopleInfoPanel;
	JPanel currentPanel;
	Vector<String> choice = new Vector<String>();
	
	//JLabels
	JLabel info1 = new JLabel();
	JLabel info2 = new JLabel();
	JLabel[] names = new JLabel[7];
	List<Role> people;
	JComboBox[] choices = new JComboBox[7];

	
	public BuildingInfoPanel(BuildingPanel b){
		//Initialize
		infoPanel = new JPanel();
		buildingPanel = b;
		infoPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		//Other Panels
		timeInfoPanel = new JPanel();
		Dimension timePanelDim = new Dimension((int)(0.22f * WINDOWX), (int)(0.12f * WINDOWY));
		timeInfoPanel.setPreferredSize(timePanelDim);
		timeInfoPanel.setMinimumSize(timePanelDim);
		timeInfoPanel.setMaximumSize(timePanelDim);
		timeInfoPanel.add(info1);
		timeInfoPanel.add(info2);
		
		peopleInfoPanel = new JPanel();
		Dimension peoplePanelDim = new Dimension((int)(0.22f * WINDOWX), (int)(0.36f * WINDOWY));
		Dimension comboDim = new Dimension((int)(0.10f * WINDOWX), (int)(0.36f * WINDOWY));
		peopleInfoPanel.setPreferredSize(peoplePanelDim);
		peopleInfoPanel.setMinimumSize(peoplePanelDim);
		peopleInfoPanel.setMaximumSize(peoplePanelDim);
		peopleInfoPanel.setLayout(new GridLayout(0,2));
		choice.add("Break");
		choice.add("Fire");
		choice.add("Go Home");
		for (int i=0; i<7; i++){
			names[i] = new JLabel();
			peopleInfoPanel.add(names[i]);
			choices[i] = new JComboBox(choice);
			choices[i].setPreferredSize(comboDim);
			choices[i].setMinimumSize(comboDim);
			choices[i].setMaximumSize(comboDim);
			peopleInfoPanel.add(choices[i]);
			choices[i].setVisible(false);
		}
		
		//set dimension
		Dimension buildingPaneDim = new Dimension((int)(0.22f * WINDOWX), (int)(0.48f * WINDOWY));
		infoPanel.setPreferredSize(buildingPaneDim);
		infoPanel.setMinimumSize(buildingPaneDim);
		infoPanel.setMaximumSize(buildingPaneDim);
		infoPanel.setLayout(new BorderLayout());
		
		//other functions
		people = new ArrayList<Role>();
		initializeInfoPanel();
		
		//Show
		infoPanel.add(timeInfoPanel, BorderLayout.NORTH);
		infoPanel.add(peopleInfoPanel, BorderLayout.CENTER);
		add(infoPanel);
	}
	
	private void initializeInfoPanel(){
		
		
	}
	
	
	public void updateBuildingInfo(Building b){
		System.out.println("Update Building Info");
		currentBuilding = b;
		writeTitle();
		showEmployeeInfomation();
	}
	
	private void writeTitle(){
		timeInfoPanel.setBorder(BorderFactory.createTitledBorder("Info-" + currentBuilding.getName()));
		timeInfoPanel.setLayout(new GridLayout(2,1));
		String name = currentBuilding.getName();
		info1.setText("Information: " + name);
		
		String sb = ("Hours: " + (currentBuilding.getStartTime()/2) + ":00 - " +
				(currentBuilding.getCloseTime()/2) + ":00");
		info2.setText(sb);
		System.out.println(sb);
		
		validate();
		repaint();
	}
	
	private void showEmployeeInfomation(){
		//Initialize Panel
		//peopleInfoPanel.removeAll();
		peopleInfoPanel.setBorder(BorderFactory.createTitledBorder("Info-" + currentBuilding.getName()));
		peopleInfoPanel.setLayout(new GridLayout(0,1));
		
		if (currentBuilding instanceof Bank){
			people = currentBuilding.getPeopleInTheBuilding();
			if (people.isEmpty()){
				names[0].setText("There are no employees");
				print("People is null. No information to show");
				validate();
				repaint();
			}
			int rows = people.size();
			//peopleInfoPanel.setLayout(new GridLayout(rows,1));
			int i=0;
			for (Role r: people){
				if (r instanceof BankTellerRole){
					names[i].setText("Teller: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof BankSecurityRole){
					names[i].setText("Guard: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
			}
			i++;
			while (i<7){
				names[i].setText("");
				choices[i].setVisible(false);
				i++;
			}
		}
		else if (currentBuilding instanceof MarketAgent){
			people = currentBuilding.getPeopleInTheBuilding();
			if (people.isEmpty()){
				names[0].setText("There are no employees");
				choices[0].setVisible(false);
				print("People is null. No information to show");
				validate();
				repaint();
			}
			int rows = people.size();
			//peopleInfoPanel.setLayout(new GridLayout(rows,1));
			int i=0;
			for (Role r: people){
				if (r instanceof MarketEmployeeRole){
					names[i].setText("Employee: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof MarketManagerRole){
					names[i].setText("Manager: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof TruckDriverRole){
					names[i].setText("TruckDriver: " + r.getPerson().getName());
					choices[i].setVisible(true);
				    i++;
				}
			}
			i++;
			while (i<7){
				names[i].setText("");
				choices[i].setVisible(false);
				i++;
			}
		}
		else if (currentBuilding instanceof House){
			people = currentBuilding.getPeopleInTheBuilding();
			if (people.isEmpty()){
				names[0].setText("There are no employees");
				choices[0].setVisible(false);
				print("People is null. No information to show");
				validate();
				repaint();
			}
			int rows = people.size();
			//peopleInfoPanel.setLayout(new GridLayout(rows,1));
			int i=0;
			for (Role r: people){
				if (r instanceof LandlordRole){
					names[i].setText("Landlord: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}

			}
			i++;
			while (i<7){
				names[i].setText("");
				choices[i].setVisible(false);
				i++;
			}
		}
		else if (currentBuilding instanceof CammaranoRestaurantAgent){
			people = currentBuilding.getPeopleInTheBuilding();
			if (people.isEmpty()){
				names[0].setText("There are no employees");
				choices[0].setVisible(false);
				print("People is null. No information to show");
				validate();
				repaint();
			}
			int rows = people.size();
			//peopleInfoPanel.setLayout(new GridLayout(rows,1));
			int i=0;
			for (Role r: people){
				if (r instanceof CammaranoHostRole){
					names[i].setText("Host: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof CammaranoWaiterRole){
					names[i].setText("Waiter: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof CammaranoCashierRole){
					names[i].setText("Cashier: " + r.getPerson().getName());
					choices[i].setVisible(true);
				    i++;
				}
				else if (r instanceof CammaranoCookRole){
					names[i].setText("Cook: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
			}
			i++;
			while (i<7){
				names[i].setText("");
				choices[i].setVisible(false);
				i++;
			}
		}
		else if (currentBuilding instanceof JiRestaurantAgent){
			people = currentBuilding.getPeopleInTheBuilding();
			if (people.isEmpty()){
				names[0].setText("There are no employees");
				choices[0].setVisible(false);
				print("People is null. No information to show");
				validate();
				repaint();
			}
			int rows = people.size();
			//peopleInfoPanel.setLayout(new GridLayout(rows,1));
			int i=0;
			for (Role r: people){
				if (r instanceof JiHostRole){
					names[i].setText("Host: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof JiWaiterRole){
					names[i].setText("Waiter: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof JiCashierRole){
					names[i].setText("Cashier: " + r.getPerson().getName());
					choices[i].setVisible(true);
				    i++;
				}
				else if (r instanceof JiCookRole){
					names[i].setText("Cook: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
			}
			i++;
			while (i<7){
				names[i].setText("");
				choices[i].setVisible(false);
				i++;
			}
		}
		else if (currentBuilding instanceof TuRestaurantAgent){
			people = currentBuilding.getPeopleInTheBuilding();
			if (people.isEmpty()){
				names[0].setText("There are no employees");
				choices[0].setVisible(false);
				print("People is null. No information to show");
				validate();
				repaint();
			}
			int rows = people.size();
			//peopleInfoPanel.setLayout(new GridLayout(rows,1));
			int i=0;
			for (Role r: people){
				if (r instanceof TuHostRole){
					names[i].setText("Host: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof TuWaiterRole){
					names[i].setText("Waiter: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof TuCashierRole){
					names[i].setText("Cashier: " + r.getPerson().getName());
					choices[i].setVisible(true);
				    i++;
				}
				else if (r instanceof TuCookRole){
					names[i].setText("Cook: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
			}
			i++;
			while (i<7){
				names[i].setText("");
				choices[i].setVisible(false);
				i++;
			}
			
		}
		else if (currentBuilding instanceof McNealRestaurantAgent){
			people = currentBuilding.getPeopleInTheBuilding();
			if (people.isEmpty()){
				names[0].setText("There are no employees");
				choices[0].setVisible(false);
				print("People is null. No information to show");
				validate();
				repaint();
			}
			int rows = people.size();
			//peopleInfoPanel.setLayout(new GridLayout(rows,1));
			int i=0;
			for (Role r: people){
				if (r instanceof McNealHostRole){
					names[i].setText("Host: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof McNealWaiterRole){
					names[i].setText("Waiter: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof McNealCashierRole){
					names[i].setText("Cashier: " + r.getPerson().getName());
					choices[i].setVisible(true);
				    i++;
				}
				else if (r instanceof McNealCookRole){
					names[i].setText("Cook: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
			}
			i++;
			while (i<7){
				names[i].setText("");
				choices[i].setVisible(false);
				i++;
			}
		}
		else if (currentBuilding instanceof RedlandRestaurantAgent){
			people = currentBuilding.getPeopleInTheBuilding();
			if (people.isEmpty()){
				names[0].setText("There are no employees");
				choices[0].setVisible(false);
				print("People is null. No information to show");
				validate();
				repaint();
			}
			int rows = people.size();
			//peopleInfoPanel.setLayout(new GridLayout(rows,1));
			int i=0;
			for (Role r: people){
				if (r instanceof RedlandHostRole){
					names[i].setText("Host: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof RedlandWaiterRole){
					names[i].setText("Waiter: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
				else if (r instanceof RedlandCashierRole){
					names[i].setText("Cashier: " + r.getPerson().getName());
					choices[i].setVisible(true);
				    i++;
				}
				else if (r instanceof RedlandCookRole){
					names[i].setText("Cook: " + r.getPerson().getName());
					choices[i].setVisible(true);
					i++;
				}
			}
			i++;
			while (i<7){
				names[i].setText("");
				choices[i].setVisible(false);
				i++;
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//If the source is setting time
		
	}
	
	/************For Test Use****************/
	public void setBuilding(Building b){
		currentBuilding = b;
	}
	
	public void print(String s){
		System.out.println("BuildingUI: "+s);
	}

}
