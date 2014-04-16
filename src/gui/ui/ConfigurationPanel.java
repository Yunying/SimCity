/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.ui;

import global.enumerations.ConfigurationEnum;
import global.enumerations.JobEnum;

import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;

/**
 *
 * @author CMCammarano
 */
public class ConfigurationPanel extends JPanel implements ActionListener {
	// Add a reference to the parent UI panel
	private UserInterfacePanel uiPanel;
	private PersonPanel personPanel;
	
	// Misc variables
	private boolean configLoaded;
	private ConfigurationEnum configEnum;
	
	// Configuration elements
	private JLabel label;
	private JComboBox configType;
	private JButton selectConfig;
	
	// Add the main panel
	private JPanel panel;
	private JPanel configPanel;
	
	// Static final vaiables
	private static final int WINDOWX = 1280;
	private static final int WINDOWY = 720;
	
	public ConfigurationPanel(UserInterfacePanel ui, PersonPanel p) {
		uiPanel = ui;
		personPanel = p;
		
		configLoaded = false;
		
		configPanel = new JPanel();
		Dimension configDim = new Dimension((int)(0.24f * WINDOWX), (int)(0.9f * WINDOWY));
		configPanel.setPreferredSize(configDim);
		configPanel.setMinimumSize(configDim);
		configPanel.setMaximumSize(configDim);
		
		// Adding GUI elements
		label = new JLabel("Select city configuration:");
		Dimension labelDim = new Dimension((int)(0.16f * WINDOWX), (int)(0.035f * WINDOWY));
		label.setPreferredSize(labelDim);
		label.setMinimumSize(labelDim);
		label.setMaximumSize(labelDim);
		
		
		Dimension elementDim = new Dimension((int)(0.2f * WINDOWX), (int)(0.04f * WINDOWY));
		configType = new JComboBox(new DefaultComboBoxModel(ConfigurationEnum.values()));
		configType.setPreferredSize(elementDim);
		configType.setMinimumSize(elementDim);
		configType.setMaximumSize(elementDim);
		
		selectConfig = new JButton("Select Configuration");
		selectConfig.setPreferredSize(elementDim);
		selectConfig.setMinimumSize(elementDim);
		selectConfig.setMaximumSize(elementDim);
		selectConfig.addActionListener(this);
		
		configPanel.add(label);
		configPanel.add(configType);
		configPanel.add(selectConfig);
		
		panel = new JPanel();
		//Dimension dim = new Dimension((int)(0.24f * WINDOWX), (int)(0.9f * WINDOWY));
		//panel.setPreferredSize(dim);
		//panel.setMinimumSize(dim);
		//panel.setMaximumSize(dim);
		//panel.setLayout(new BorderLayout());
		
		panel.add(configPanel, BorderLayout.CENTER);	
		add(panel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == selectConfig) {
			LoadConfiguration((ConfigurationEnum)configType.getSelectedItem());
			selectConfig.setEnabled(!configLoaded);
		}
	}
	
	public void LoadConfiguration(ConfigurationEnum c) {
		if (c == ConfigurationEnum.None) {
			System.out.println("Please select a configuration");
		}
		
		else if (c == ConfigurationEnum.Full) {
			System.out.println("Loading " + c.toString());
			try {
				ReadInFile("Full.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configLoaded = true;
			selectConfig.setText("Configuration Loaded");
		}
		
		else if (c == ConfigurationEnum.Colin) {
			System.out.println("Loading " + c.toString());
			try {
				ReadInFile("Colin.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configLoaded = true;
			selectConfig.setText("Configuration Loaded");
		}
		
		else if (c == ConfigurationEnum.Cathy) {
			System.out.println("Loading " + c.toString());
			try {
				ReadInFile("Cathy.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configLoaded = true;
			selectConfig.setText("Configuration Loaded");
		}
		
		else if (c == ConfigurationEnum.Elsie) {
			System.out.println("Loading " + c.toString());
			try {
				ReadInFile("config/Elsie.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configLoaded = true;
			selectConfig.setText("Configuration Loaded");
		}
		
		else if (c == ConfigurationEnum.Kristen) {
			System.out.println("Loading " + c.toString());
			try {
				ReadInFile("Kristen.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configLoaded = true;
			selectConfig.setText("Configuration Loaded");
		}
		
		else if (c == ConfigurationEnum.Jeff) {
			System.out.println("Loading " + c.toString());
			try {
				ReadInFile("Jeff.txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			configLoaded = true;
			selectConfig.setText("Configuration Loaded");
		}
	}
	
	private void ReadInFile(String s) throws FileNotFoundException{
		String path = "src/";
		File file = new File(path + s);
		Scanner sc = new Scanner(file);
		
		int num = sc.nextInt();
		print("Number of people to be added "+num);
		
		for (int i=0; i<num; i++){
			String name = sc.next();
			String job = sc.next();
			JobEnum jobEnum = JobEnum.None;
			for (JobEnum je: JobEnum.values()){
				if ((je.name()).equals(job)){
					jobEnum = je;
				}
			}
			
			int start = sc.nextInt();
			int stop = sc.nextInt();
			int car = sc.nextInt();
			boolean hasCar;
			if (car == 0){
				hasCar = false;
			}
			else{
				hasCar = true;
			}
			
			print("Name: "+name + " Job: "+job + " StartWorking: " + start
					+ " stopWorking: "+stop + " Has car: "+ car);
			print("Job Enum: "+jobEnum.name());
			personPanel.AddPerson(name);
			
			if (jobEnum.name() != "None"){
				uiPanel.people.get(uiPanel.people.size()-1).AddJob(jobEnum, start, stop);
			}
			uiPanel.people.get(uiPanel.people.size()-1).setHasCar(hasCar);
		}
	}
	
	private void print(String s){
		System.out.println(s);
	}
}
