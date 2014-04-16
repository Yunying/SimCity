/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.ui;

import gui.animation.PersonGUI;
import java.util.*;
//import java.util.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.Timer;

import interfaces.*;
import global.PersonAgent;
import gui.*;
import gui.ui.building.*;
import housing.*;
import market.*;
import restaurant.ji.*;
import restaurant.cammarano.*;
import restaurant.mcneal.*;
import restaurant.yunying.*;
import transportation.BusAgent;
import transportation.BusStop;
import transportation.TransportationMediator;
import transportation.Interfaces.*;
import bank.*;
import global.enumerations.JobEnum;
import global.test.mock.*;

public class UserInterfacePanel extends JPanel implements ActionListener {
	
	// Reference to overarching UI Window
	private SimCity baseUI;
	
	// Window elements
	private JTabbedPane pane;
	
	private PersonPanel personPanel;
	private BuildingPanel buildingPanel;
	private ConfigurationPanel config;
	
	// List of person agents and buildings
	public ArrayList<PersonAgent> people;
	public ArrayList<Building> buildings;
	public ArrayList<HouseAgent> houses;
	public ArrayList<Bus> buses;
	
	// THE GLOBAL TIMER!!!! :D
	private Timer timer;
	public int currentTime;
	public int currentDay;	// For V2
	
	// Static variables
	private static final int WINDOWX = 1280;
	private static final int WINDOWY = 720;
	
	//Transportation Mediator
	private Mediator mediator;
	
	// For testing 
	public EventLog log;
	
	public UserInterfacePanel(SimCity ui) {
		
		log = new EventLog();
		
		// Setup SimCity reference
		baseUI = ui;
		
		// Instantiate the list of people and buildings
		people = new ArrayList<>();
		buildings = new ArrayList<>();
		houses = new ArrayList<>();
		buses = new ArrayList<>();
		
		// Begin setting up UI elements
		pane = new JTabbedPane();
		buildingPanel = new BuildingPanel(this);
		personPanel = new PersonPanel(this);
		config = new ConfigurationPanel(this, personPanel);
		
		Dimension dim = new Dimension((int)(0.26f * WINDOWX), (int)(0.92f * WINDOWY));
		pane.setPreferredSize(dim);
		pane.setMinimumSize(dim);
		pane.setMaximumSize(dim);
		
		// Add all windows to the tabbed pane
		pane.addTab("People", personPanel);
		pane.addTab("Buildings", buildingPanel);
		pane.addTab("Presets", config);
		
		// Finish creating the window
		add(pane);
		setBorder(BorderFactory.createTitledBorder("Configuration Pane"));
		
		currentTime = 0;
		
		// Populate the list of buildings
		AddBuildings();
		
		// Add transportation
		//AddTransportation();
		
		// Add default people to our city
		//personPanel.AddDefaultPeople();
		// hacks for cathy's testing purposes
		//personPanel.AddJiPeople();

		timer = new Timer(12766, this); // Timer fires every 10/47ths of a minute
		timer.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timer) {
			if(currentTime % 2 == 0) {
				System.out.println("It is now " + (int)(currentTime/2) + ":00!");
			}
			
			if(currentTime % 2 != 0) {
				System.out.println("It is now " + (int)(currentTime/2) + ":30!");
			}
			
//			switch(currentDay) {
//				case 0:
//					System.out.println("It is Sunday!");
//					break;
//				case 1:
//					System.out.println("It is Monday!");
//					break;
//				case 2:
//					System.out.println("It is Tuesday!");
//					break;
//				case 3:
//					System.out.println("It is Wednesday!");
//					break;
//				case 4:
//					System.out.println("It is Thursday!");
//					break;
//				case 5:
//					System.out.println("It is Friday!");
//					break;
//				case 6:
//					System.out.println("It is Saturday!");
//					break;
//			}
			
			if(currentTime == 47) {
				currentTime = 0;
				currentDay++;
				
				if(currentDay > 6) {
					currentDay = 0;
				}
			}
			
			else {
				currentTime++;
			}
			
			for (Person p : people) {
				p.msgUpdateTime(currentTime, currentDay);
			}
	
			for (Building b : buildings) {
				b.msgUpdateTime(currentTime, currentDay);
			}
			
			for (Bus b: buses){
				b.msgUpdateTime(currentTime);
			}
			
		}
	}
	
	
	public void AddPerson(String name) {
		log.add(new LoggedEvent("Adding person"));
		// This code adds a basic person with no job to the animation. This can be edited in the Person Panel
		// People are assigned the next vacant houses. If there are no more houses, people are sent to the apartment.
		for (HouseAgent h : houses) {
			if(h.getOccupant() == null) {
				PersonAgent p = new PersonAgent(name, buildings, h);	
				PersonGUI g = new PersonGUI(p);
				h.setOccupied(true);
				p.testingDisableAnimationCalls = true; // Testing without our GUI
				
				baseUI.animationPanel.addGUI(g);
				p.setGui(g);
				people.add(p);
				p.startThread();
				System.out.println("Adding " + name + " to the person list.");
				validate();
				repaint();
				return;
			}
		}
		
		// Assuming we have no empty houses, this hack assigns people to the apartment
		PersonAgent p = new PersonAgent(name, buildings, null);	
		PersonGUI g = new PersonGUI(p);

		baseUI.animationPanel.addGUI(g);
		p.setGui(g);
		people.add(p);
		p.startThread();
		System.out.println("Adding " + name + " to the person list.");
		validate();
		repaint();
	}
	
	private void AddBuildings() {
		log.add(new LoggedEvent("Adding buildings"));
		System.out.println("Adding buildings.");
		
		BankAgent bank = new BankAgent("Cathy's Bank");
		
		buildings.add(bank);
		bank.startThread();
		buildingPanel.AddBuilding(bank.getName(), bank);
		
		MarketAgent market = new MarketAgent("Jeff's Market", "mrklocation");

		buildings.add(market);
		market.startThread();
		buildingPanel.AddBuilding(market.getName(), market);
		
		CammaranoRestaurantAgent cRA = new CammaranoRestaurantAgent("Ye Olde Irish Pub", market, bank);
		bank.createBankAccountForBusiness(cRA, cRA.getBankAccount());
		
		buildings.add(cRA);
		cRA.startThread();
		buildingPanel.AddBuilding(cRA.getName(), cRA);
		
		JiRestaurantAgent jRA = new JiRestaurantAgent("Cathy's Restaurant", market, bank);
		bank.createBankAccountForBusiness(jRA, jRA.getBankAccount());
		
		buildings.add(jRA);
		jRA.startThread();
		buildingPanel.AddBuilding(jRA.getName(), jRA);
		/*
		RedlandRestaurantAgent rRA = new RedlandRestaurantAgent("Jeff's Restaurant");
		RedlandRestaurantGUI rrGUI = new RedlandRestaurantGUI(rRA);
		bank.createBankAccountForBusiness(rRA, rRA.getBankAccount());
		
		baseUI.animationPanel.addGUI(crGUI);
		cRA.setGui(crGUI);
		buildings.add(cRA);
		cRA.startThread();
		//buildingPanel.AddBuilding(cRA);
		*/
		McNealRestaurantAgent mRA = new McNealRestaurantAgent("Kristen's Restaurant", market, bank);
		bank.createBankAccountForBusiness(mRA, mRA.getBankAccount());

		buildings.add(mRA);
		mRA.startThread();
		buildingPanel.AddBuilding(mRA.getName(), mRA);
		
		TuRestaurantAgent tRA = new TuRestaurantAgent("Elsie's Restaurant", market, bank);
		bank.createBankAccountForBusiness(tRA, tRA.getBankAccount());
		
		buildings.add(tRA);
		tRA.startThread();
		buildingPanel.AddBuilding(tRA.getName(), tRA);

		HouseAgent apartment = new HouseAgent("Apartment", true);
	
		buildings.add(apartment);
		apartment.startThread();
		buildingPanel.AddBuilding(apartment.getName(), apartment);
		
		for(int cnt = 1; cnt <= 40; cnt++) {
			HouseAgent house = new HouseAgent("House" + cnt, false);
			
			houses.add(house);
			house.startThread();
			//buildingPanel.AddBuilding(house.getName(), house);
		}
		
		// Add bus stops like houses
		// Instantiate Transportation mediator
		// Add bus like other agents -- local variable here.
		// transport.addBus();
		
		// Just need bus stops now.
		System.out.println("Adding transportation");
		mediator = new TransportationMediator();
		BusStop bs1 = new BusStop(mediator, "bs1");
		
		for (int i=0; i<40; i++){
			bs1.addBuilding(houses.get(i));
		}
		
		buildings.add(bs1);
		bs1.startThread();
		
		BusStop bs2 = new BusStop(mediator, "bs2");
		bs2.addBuilding(cRA);
		bs2.addBuilding(tRA);
		bs2.addBuilding(mRA);
		bs2.addBuilding(jRA);
		buildings.add(bs2);
		bs2.startThread();
		
		BusStop bs3 = new BusStop(mediator, "bs3");
		bs3.addBuilding(bank);
		buildings.add(bs3);
		bs3.startThread();
		
		BusStop bs4 = new BusStop(mediator, "bs4");
		bs4.addBuilding(market);
		buildings.add(bs4);
		bs4.startThread();
		
		mediator.addBusStop(bs1);
		mediator.addBusStop(bs2);
		mediator.addBusStop(bs3);
		mediator.addBusStop(bs4);
		
		BusAgent bus1 = new BusAgent(mediator, "bus1");
		bus1.startThread();
		buses.add(bus1);
		mediator.addABus(bus1);
		
		validate();
		repaint();
	}
	
	public void CreateDefaults() {
		for (PersonAgent p : people) {
			if(p.getName().equals("Dolly")) {
				p.setWakeTime(11);
				p.setDinnerTime(37);
				p.setBedTime(42);
//				p.AddJob(JobEnum.CammaranoHostRole, 13, 36);
//				p.setHasCar(true);
				p.AddJob(JobEnum.TuHostRole, 13, 36);
				p.setHasCar(false);
			}
			
			else if(p.getName().equals("Coginio")) {
				p.setWakeTime(13);
				p.setDinnerTime(37);
				p.setBedTime(42);
//				p.AddJob(JobEnum.CammaranoCookRole, 14, 36);
//				p.setHasCar(true);
				p.AddJob(JobEnum.TuCookRole, 14, 36);
				p.setHasCar(false);
			}
			
			if(p.getName().equals("Doyle")) {
				p.setWakeTime(14);
				p.setDinnerTime(37);
				p.setBedTime(42);
				p.AddJob(JobEnum.TuWaiterRole, 15, 36);
				p.setHasCar(false);
			}
			
			if(p.getName().equals("O'Brian")) {
				p.setWakeTime(13);
				p.setDinnerTime(37);
				p.setBedTime(42);
				p.AddJob(JobEnum.TuWaiterRole, 15, 36);
				p.setHasCar(false);
			}
			
			if(p.getName().equals("Mac")) {
				p.setWakeTime(12);
				p.setDinnerTime(37);
				p.setBedTime(42);
				p.AddJob(JobEnum.TuCashierRole, 13, 36);
				p.setHasCar(false);
			}
			if(p.getName().equals("Jim")) {

				p.setHasCar(true);
			}
		}
	}
	public void hackJiRestaurant(){
		for (PersonAgent p : people){
			p.setWakeTime(12);
			p.setDinnerTime(37);
			p.setBedTime(42);
			p.setHasCar(false);
			if (p.getName().equals("CHost")){
				p.AddJob(JobEnum.JiHostRole, 4, 35);
			}
			else if (p.getName().equals("CWaiterNormal")){
				p.AddJob(JobEnum.JiWaiterRole, 4, 35);
			} 
			else if (p.getName().equals("CWaiterShared")){
				p.AddJob(JobEnum.JiSharedWaiterRole, 4, 35);
			}
			else if (p.getName().equals("CCashier")){
				p.AddJob(JobEnum.JiCashierRole, 4, 35);
			}
			else if (p.getName().equals("CCook")){
				p.AddJob(JobEnum.JiCookRole, 4, 35);
			}
			
			else if (p.getName().equals("Teller")){
				p.AddJob(JobEnum.BankTellerRole, 10, 9);
			}
		}
	}
}
