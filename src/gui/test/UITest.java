/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.test;

import gui.SimCity;
import gui.ui.UserInterfacePanel;
import junit.framework.TestCase;

/**
 *
 * @author CMCammarano
 */
public class UITest extends TestCase {
	
	public UserInterfacePanel ui;
	public SimCity city;
	
	public void setUp() throws Exception {
		super.setUp();
		
		city = new SimCity();
		ui = new UserInterfacePanel(city);
	}
	
	public void testPersonPanelUI() throws Exception {
		setUp();
		
		// Check preconditions before running tests
		assertEquals("The UserInterfacePanel has no people in it. It does.", ui.people.size(), 0);	
		assertEquals("The UserInterfacePanel has 10 buildings in it. It doesn;t.", ui.buildings.size(), 10);	
		assertEquals("The UserInterfacePanel has 40 houses in it. It doesn't.", ui.houses.size(), 40);	
		
		// Check adding a person
		assertTrue("The UI should have logged \"Adding buildings\"  but didn't. Its log reads instead: " + ui.log.getLastLoggedEvent().toString(), ui.log.containsString("Adding buildings"));
		ui.AddPerson("test");
		assertTrue("The UI should have logged \"Adding person\"  but didn't. Its log reads instead: " + ui.log.getLastLoggedEvent().toString(), ui.log.containsString("Adding person"));
		assertEquals("The UserInterfacePanel has 1 person in it. It doesn't.", ui.people.size(), 1);	
	}
}
