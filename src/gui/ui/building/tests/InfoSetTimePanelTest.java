package gui.ui.building.tests;

import gui.ui.building.BuildingConfigurationPanel;
import gui.ui.building.BuildingPanel;
import gui.ui.building.mock.MockBuilding;
import junit.framework.TestCase;

public class InfoSetTimePanelTest extends TestCase{
	BuildingConfigurationPanel configPanel;
	BuildingPanel buildingPanel;
	MockBuilding building;
	
	public void setUp() throws Exception{
		configPanel = new BuildingConfigurationPanel(buildingPanel);
		building = new MockBuilding("building");
	}
	
	public void testSetTime(){
		//Click a button in the buttonPanel
		configPanel.updatePanel(building);
		
		//The setTime panel should set up the current building correctly
		assertEquals("There should be one event in the config panel's log now",
				1, configPanel.log.size());
		
	}
}
