package gui.animation.base;

import java.awt.Graphics2D;


public class BaseGUI implements GUI {

	protected String id;
	
	public BaseGUI() {
		id = "";
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getID() {
		return this.id;
	}

}
