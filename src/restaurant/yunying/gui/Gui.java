package restaurant.yunying.gui;

import java.awt.*;

public interface Gui {

    public void updatePosition();
    public void draw(Graphics2D g);
    public boolean isPresent();
	public void setPaused(boolean b);
	public boolean isPaused();

}