package gui.ui.person;

import global.PersonAgent;
import global.enumerations.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import gui.ui.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;


/**
 *
 * @author CMCammarano
 */
public class PersonInformation extends JPanel implements ActionListener {
	
	// Caching the current person agent
	private PersonAgent person;
	
	// Caching the parent GUI windows
	private UserInterfacePanel uiPanel;
	private PersonPanel personPanel;
	
	// Enumerations for the combo boxes
	
	// Configuration panel
	private JPanel infoPanel;
	private JPanel configPanel;
	
	// Information label
	private JLabel personName;
	private JLabel personWakeLabel;
	private JLabel personSleepLabel;
	private JLabel personLunchLabel;
	private JLabel personDinnerLabel;
	
	// Config Panel
	private JComboBox jobType;
	private JComboBox wakeTime;
	private JComboBox bedTime;
	private JComboBox lunchTime;
	private JComboBox dinnerTime;
	private JComboBox task;
	
	private JButton addJob;
	private JButton setWake;
	private JButton setBed;
	private JButton setLunch;
	private JButton setDinner;
	private JButton addTask;
	private JButton addCar;
	
	// Main panel
	private JTabbedPane pane;
	private JPanel panel;
	
	private static final int WINDOWX = 1280;
	private static final int WINDOWY = 720;
	
	public PersonInformation(UserInterfacePanel ui, PersonPanel p) {
		uiPanel = ui;
		personPanel = p;
		
		// Set default person to null
		person = null;
		
		// Set person name label
		Dimension labelDim = new Dimension((int)(0.16f * WINDOWX), (int)(0.035f * WINDOWY));
		
		personName = new JLabel("Name: ");
		personName.setPreferredSize(labelDim);
		personName.setMinimumSize(labelDim);
		personName.setMaximumSize(labelDim);
		
		personWakeLabel = new JLabel("Wake Time: ");
		personWakeLabel.setPreferredSize(labelDim);
		personWakeLabel.setMinimumSize(labelDim);
		personWakeLabel.setMaximumSize(labelDim);
		
		personSleepLabel = new JLabel("Bed Time: ");
		personSleepLabel.setPreferredSize(labelDim);
		personSleepLabel.setMinimumSize(labelDim);
		personSleepLabel.setMaximumSize(labelDim);
		
		personLunchLabel = new JLabel("Lunch Time: ");
		personLunchLabel.setPreferredSize(labelDim);
		personLunchLabel.setMinimumSize(labelDim);
		personLunchLabel.setMaximumSize(labelDim);
		
		personDinnerLabel = new JLabel("Dinner Time: ");
		personDinnerLabel.setPreferredSize(labelDim);
		personDinnerLabel.setMinimumSize(labelDim);
		personDinnerLabel.setMaximumSize(labelDim);
		
		// Setting up each config element
		Dimension elementDim = new Dimension((int)(0.1f * WINDOWX), (int)(0.04f * WINDOWY));
		
		jobType = new JComboBox(new DefaultComboBoxModel(JobEnum.values()));
		jobType.setPreferredSize(elementDim);
		jobType.setMinimumSize(elementDim);
		jobType.setMaximumSize(elementDim);
		
		addJob = new JButton("Add Job");
		addJob.addActionListener(this);
		addJob.setPreferredSize(elementDim);
		addJob.setMinimumSize(elementDim);
		addJob.setMaximumSize(elementDim);
		
		wakeTime = new JComboBox(new DefaultComboBoxModel(TimeEnum.values()));
		wakeTime.setPreferredSize(elementDim);
		wakeTime.setMinimumSize(elementDim);
		wakeTime.setMaximumSize(elementDim);
		
		setWake = new JButton("Set Wake Time");
		setWake.addActionListener(this);
		setWake.setPreferredSize(elementDim);
		setWake.setMinimumSize(elementDim);
		setWake.setMaximumSize(elementDim);
		
		bedTime = new JComboBox(new DefaultComboBoxModel(TimeEnum.values()));
		bedTime.setPreferredSize(elementDim);
		bedTime.setMinimumSize(elementDim);
		bedTime.setMaximumSize(elementDim);
		
		setBed = new JButton("Set Bed Time");
		setBed.addActionListener(this);
		setBed.setPreferredSize(elementDim);
		setBed.setMinimumSize(elementDim);
		setBed.setMaximumSize(elementDim);
		
		lunchTime = new JComboBox(new DefaultComboBoxModel(TimeEnum.values()));
		lunchTime.setPreferredSize(elementDim);
		lunchTime.setMinimumSize(elementDim);
		lunchTime.setMaximumSize(elementDim);
		
		setLunch = new JButton("Set Lunch Time");
		setLunch.addActionListener(this);
		setLunch.setPreferredSize(elementDim);
		setLunch.setMinimumSize(elementDim);
		setLunch.setMaximumSize(elementDim);
		
		dinnerTime = new JComboBox(new DefaultComboBoxModel(TimeEnum.values()));
		dinnerTime.setPreferredSize(elementDim);
		dinnerTime.setMinimumSize(elementDim);
		dinnerTime.setMaximumSize(elementDim);
		
		setDinner = new JButton("Set Dinner Time");
		setDinner.addActionListener(this);
		setDinner.setPreferredSize(elementDim);
		setDinner.setMinimumSize(elementDim);
		setDinner.setMaximumSize(elementDim);
		
		task  = new JComboBox(new DefaultComboBoxModel(TaskEnum.values()));
		task.setPreferredSize(elementDim);
		task.setMinimumSize(elementDim);
		task.setMaximumSize(elementDim);
		
		addTask = new JButton("Add Task");
		addTask.addActionListener(this);
		addTask.setPreferredSize(elementDim);
		addTask.setMinimumSize(elementDim);
		addTask.setMaximumSize(elementDim);
		
		addCar = new JButton("Add Car");
		addCar.addActionListener(this);
		addCar.setPreferredSize(elementDim);
		addCar.setMinimumSize(elementDim);
		addCar.setMaximumSize(elementDim);
		
		// Config Panel
		infoPanel = new JPanel();
		Dimension infoDim = new Dimension((int)(0.24f * WINDOWX), (int)(0.4f * WINDOWY));
		infoPanel.setPreferredSize(infoDim);
		infoPanel.setMinimumSize(infoDim);
		infoPanel.setMaximumSize(infoDim);
		
		// Adding labels
		infoPanel.add(personName);
		infoPanel.add(personWakeLabel);
		infoPanel.add(personSleepLabel);
		infoPanel.add(personLunchLabel);
		infoPanel.add(personDinnerLabel);
		
		// Config Panel
		configPanel = new JPanel();
		Dimension configDim = new Dimension((int)(0.24f * WINDOWX), (int)(0.4f * WINDOWY));
		configPanel.setPreferredSize(configDim);
		configPanel.setMinimumSize(configDim);
		configPanel.setMaximumSize(configDim);
		
		// Adding everything
		configPanel.add(jobType);
		configPanel.add(addJob);
		configPanel.add(wakeTime);
		configPanel.add(setWake);
		configPanel.add(bedTime);
		configPanel.add(setBed);
		configPanel.add(lunchTime);
		configPanel.add(setLunch);
		configPanel.add(dinnerTime);
		configPanel.add(setDinner);
		configPanel.add(task);
		configPanel.add(addTask);
		configPanel.add(addCar);
		
		pane = new JTabbedPane();
		Dimension paneDim = new Dimension((int)(0.24f * WINDOWX), (int)(0.50f * WINDOWY));
		pane.setPreferredSize(paneDim);
		pane.setMinimumSize(paneDim);
		pane.setMaximumSize(paneDim);
		
		pane.add(infoPanel, "Info");
		pane.add(configPanel, "Configuration");
		
		panel = new JPanel();
		Dimension dim = new Dimension((int)(0.24f * WINDOWX), (int)(0.52f * WINDOWY));
		panel.setPreferredSize(dim);
		panel.setMinimumSize(dim);
		panel.setMaximumSize(dim);
		
		panel.setLayout(new BorderLayout());
		panel.add(pane, BorderLayout.CENTER);
		
		add(panel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(person != null) {
			if(e.getSource() == addJob) {
				AddJob((JobEnum)jobType.getSelectedItem());
			}

			else if(e.getSource() == setWake) {
				SetWakeTime((TimeEnum)wakeTime.getSelectedItem());
			}

			else if(e.getSource() == setBed) {
				SetBedTime((TimeEnum)bedTime.getSelectedItem());
			}

			else if(e.getSource() == setLunch) {
				SetLunchTime((TimeEnum)lunchTime.getSelectedItem());
			}

			else if(e.getSource() == setDinner) {
				SetDinnerTime((TimeEnum)dinnerTime.getSelectedItem());
			}

			else if(e.getSource() == addTask) {
				AddTask((TaskEnum)task.getSelectedItem());
			}

			else if(e.getSource() == addCar) {
				SetHasCar();
				addCar.setEnabled(!person.getHasCar());
			}
			
			validate();
			repaint();
		}
	}
	
	public void UpdateInformation(PersonAgent p) {
		person = p;
		personName.setText("Name: " + p.getName());
		personWakeLabel.setText("Wake Time: " + p.getWakeTime());
		personSleepLabel.setText("Bed Time: " + p.getBedTime());
		personLunchLabel.setText("Lunch Time: " + p.getLunchTime());
		personDinnerLabel.setText("Dinner Time: " + p.getDinnerTime());
		addCar.setEnabled(!person.getHasCar());
		validate();
		repaint();
	}
	
	public void AddJob(JobEnum j) {
		person.AddJob(j, 18, 34);
	}
	
	public void AddTask(TaskEnum t) {
		if(t == TaskEnum.GoHome) {
			person.AddTaskGoHome();
		}
		
		else if(t == TaskEnum.GoHomeAndEat) {
			person.AddTaskGoHomeToEat();
		}
		
		else if(t == TaskEnum.GoToTheBank) {
			person.AddTaskGoToBank();
		}
		
		else if(t == TaskEnum.GoToTheMarketToBuyACar) {
			person.AddTaskBuyCarAtMarket();
		}
		
		else if(t == TaskEnum.GoEatFood) {
			person.AddTaskGetFood();
		}
	}
	
	public void SetWakeTime(TimeEnum t) {
		person.setWakeTime(t.ordinal());
	}
	
	public void SetBedTime(TimeEnum t) {
		person.setBedTime(t.ordinal());
	}
	
	public void SetLunchTime(TimeEnum t) {
		person.setLunchTime(t.ordinal());
	}
	
	public void SetDinnerTime(TimeEnum t) {
		person.setDinnerTime(t.ordinal());
	}
	
	public void SetHasCar() {
		person.setHasCar(true);
	}
}
