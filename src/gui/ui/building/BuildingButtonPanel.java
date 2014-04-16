package gui.ui.building;

import interfaces.Building;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BuildingButtonPanel extends JPanel implements ActionListener{
	//import other related panels
	BuildingPanel buildingPanel;
	BuildingInfoPanel infoPanel;
	BuildingConfigurationPanel configPanel;
	List<Building> buildings;
	
	// Button panel elements
	public JPanel buildingSelectionPanel;
	public JScrollPane buildingPane;
	public JPanel buildingScrollPaneInteriorPanel;
	public ArrayList<JButton> buttons;
	private static final int WINDOWX = 1280;
	private static final int WINDOWY = 720;
	
	public BuildingButtonPanel(BuildingPanel b, BuildingInfoPanel i, BuildingConfigurationPanel c){
		//Set Up
		buildingPanel = b;
		infoPanel = i;
		buildings = buildingPanel.getBuildings();
		configPanel = c;
		buildings = new ArrayList<Building>();
		
		// Building scroll pane
		buildingSelectionPanel = new JPanel();
		buttons = new ArrayList<JButton>();
				
		buildingPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension buildingPaneDim = new Dimension((int)(0.22f * WINDOWX), (int)(0.22f * WINDOWY));
		buildingPane.setPreferredSize(buildingPaneDim);
		buildingPane.setMinimumSize(buildingPaneDim);
		buildingPane.setMaximumSize(buildingPaneDim);
				
		// Setting up the scroll pane's interior panel
		buildingScrollPaneInteriorPanel = new JPanel();
		buildingScrollPaneInteriorPanel.setLayout(new BoxLayout((Container) buildingScrollPaneInteriorPanel, BoxLayout.Y_AXIS));
		buildingPane.setViewportView(buildingScrollPaneInteriorPanel);
				
		buildingSelectionPanel.setLayout(new BorderLayout());
		buildingSelectionPanel.add(buildingPane, BorderLayout.CENTER);
		
		//Set the scroll pane visible
		add(buildingPane);
	}

	public JScrollPane getScrollPane(){
		return buildingPane;
	}
	
	public void addButton(JButton b, Building bd){
		print("add buttons");
		b.addActionListener(this);
		buildings.add(bd);
		buttons.add(b);
		buildingScrollPaneInteriorPanel.add(b);
		validate();
	}
	
	public void print(String s){
		System.out.println(s);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		for (JButton b: buttons){
			if (e.getSource().equals(b)){
				for (Building bd: buildings){
					if (bd.getName().equals(b.getText())){
						infoPanel.updateBuildingInfo(bd);
						configPanel.updatePanel(bd);
					}
				}
			}
		}
	}
}
