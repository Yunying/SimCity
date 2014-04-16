package gui.animation.base;

import java.awt.Graphics2D;

/**
 * @author CMCammarano
 *
 */
public interface GUI {
	public void updatePosition();
    public void draw(Graphics2D g);
    public boolean isPresent();
	public String getID();
}
