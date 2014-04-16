/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.ui;

import global.PersonAgent;
import gui.ui.person.PersonInformation;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 *
 * @author CMCammarano
 */
public class PersonPanel extends JPanel implements ActionListener {

	// Main UI Panel reference
	private UserInterfacePanel uiPanel;
	
	// Main panel
	private JPanel panel;
	
	// Person panel elements
	public JPanel personSelectionPanel;
	public JPanel personAdditionPanel;
	public JTextField personName;
	public JButton addPerson;
	public JScrollPane personPane;
	public JPanel personScrollPaneInteriorPanel;
	public ArrayList<JButton> personButtonList;
	
	// Person Configuration
	public JPanel personConfigurationPanel;
	public PersonInformation personInformation;
	
	// Static final vaiables
	private static final int WINDOWX = 1280;
	private static final int WINDOWY = 720;
	
	public PersonPanel(UserInterfacePanel ui) {
		// Setup default UI panel
		uiPanel = ui;
		
		// Person scroll pane
		personSelectionPanel = new JPanel();
		personButtonList = new ArrayList<JButton>();
		
		// Setup the panel that holds the name text field and add button
		personAdditionPanel = new JPanel();
		personAdditionPanel.setLayout(new BorderLayout());
		
		personName = new JTextField();
		personName.addActionListener(this);
		Dimension personNameFieldDim = new Dimension((int)(0.15f * WINDOWX), (int)(0.05f * WINDOWY));
		personName.setPreferredSize(personNameFieldDim);
		personName.setMinimumSize(personNameFieldDim);
		personName.setMaximumSize(personNameFieldDim);
		personAdditionPanel.add(personName, BorderLayout.WEST);
		
		addPerson = new JButton("Add person");
		addPerson.addActionListener(this);
		Dimension addPersonButtonDim = new Dimension((int)(0.08f * WINDOWX), (int)(0.05f * WINDOWY));
		addPerson.setPreferredSize(addPersonButtonDim);
		addPerson.setMinimumSize(addPersonButtonDim);
		addPerson.setMaximumSize(addPersonButtonDim);
		personAdditionPanel.add(addPerson, BorderLayout.EAST);
		
		// Setup the scroll pane
		personPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension personPaneDim = new Dimension((int)(0.22f * WINDOWX), (int)(0.3f * WINDOWY));
		personPane.setPreferredSize(personPaneDim);
		personPane.setMinimumSize(personPaneDim);
		personPane.setMaximumSize(personPaneDim);
		
		// Setting up the scroll pane's interior panel
		personScrollPaneInteriorPanel = new JPanel();
		personScrollPaneInteriorPanel.setLayout(new BoxLayout((Container) personScrollPaneInteriorPanel, BoxLayout.Y_AXIS));
		personPane.setViewportView(personScrollPaneInteriorPanel);
		
		// Setup the scroll pane int the center of its parent panel
		personSelectionPanel.setLayout(new BorderLayout());
		personSelectionPanel.add(personAdditionPanel, BorderLayout.NORTH);
		personSelectionPanel.add(personPane, BorderLayout.CENTER);
		
		// Person configuration pane
		personConfigurationPanel = new JPanel();
		personInformation = new PersonInformation(ui, this);
		
		personConfigurationPanel.add(personInformation);
		
		// Main panel to hold all elements
		panel = new JPanel();
		Dimension dim = new Dimension((int)(0.24f * WINDOWX), (int)(0.9f * WINDOWY));
		panel.setPreferredSize(dim);
		panel.setMinimumSize(dim);
		panel.setMaximumSize(dim);
		
		panel.setLayout(new BorderLayout());
		panel.add(personSelectionPanel, BorderLayout.NORTH);
		panel.add(personConfigurationPanel, BorderLayout.SOUTH);
		
		add(panel);
	}
	
	public void AddPerson(String name) {
		if (!name.equals("")) {
			// Hack to give every person a different name
			int nameCount = 0;
			for (PersonAgent p : uiPanel.people) {
				if(p.getName().equals(name)) {
					nameCount++;
				}
			}
			if(nameCount > 0) {
				name = name + nameCount;
			}
			
			// Adding the buttons
			JButton button = new JButton(name);
			button.setBackground(Color.white);
			Dimension buttonSize = new Dimension((int)(0.22f * WINDOWX), (int) ((int)(0.3f * WINDOWY) / 8));
			button.setPreferredSize(buttonSize);
			button.setMinimumSize(buttonSize);
			button.setMaximumSize(buttonSize);
			button.addActionListener(this);
			personButtonList.add(button);
			personScrollPaneInteriorPanel.add(button);
			uiPanel.AddPerson(name);	// Puts a person in the agent list
			validate();
			repaint();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addPerson) {
			AddPerson(personName.getText());
		}
		
		else {
			for (JButton temp : personButtonList){
				if (e.getSource() == temp) {
					for (int i = 0; i < uiPanel.people.size(); i++) {
						PersonAgent p = uiPanel.people.get(i);
						if (p.getName().equals(temp.getText())) {
							personInformation.UpdateInformation(p);
							validate();
							repaint();
							break;
						}
					}
				}
			}
		}
	}
	
	public void AddDefaultPeople() {
//		AddPerson("Dolly");
//		AddPerson("Coginio");
//		AddPerson("Doyle");
//		AddPerson("Mac");
//		AddPerson("O'Brian");
//		AddPerson("Jim");
//		AddPerson("Brandon");
//		
//		uiPanel.CreateDefaults();
	}
	
	public void AddJiPeople(){
//		AddPerson("CHost");
//		AddPerson("CCook");
//		AddPerson("CCashier");
//		AddPerson("CWaiterNormal");
//		AddPerson("CWaiterShared");
//		AddPerson("Rando A");
//		AddPerson("Rando B");
//		
//		AddPerson("Teller");
//		uiPanel.hackJiRestaurant();
	}
}
