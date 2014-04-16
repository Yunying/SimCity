/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.animation;

import gui.animation.base.GUI;
import gui.animation.base.BaseGUI;
import java.awt.*;
import global.*;

/**
 *
 * @author CMCammarano
 */
public class PersonGUI extends BaseGUI implements GUI {
	
	private PersonAgent person;
	
	public PersonGUI(PersonAgent p) {
		super();
		
		person = p;
	}

	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isPresent() {
		return true;
		// TODO Auto-generated method stub
	}

	public void DoGoToLocation(String location) {}
}
