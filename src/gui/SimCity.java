package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import gui.ui.*;

/**
 * @author CMCammarano
 *
 */
public class SimCity extends JFrame implements ActionListener {
	
	private JPanel panel;
	public UserInterfacePanel uiPanel;
	public AnimationPanel animationPanel;
	
	// Static variables
	static final int H_EDGE_PADDING = 30;
	static final int V_EDGE_PADDING = 1;
	static final int WINDOW_BOUNDS_BUFFER = 100;
	static final int WINDOW_BOUNDS_Y = 50;
	static final float WINDOW_SCALE_X = 0.25f;
	static final float WINDOW_SCALE_Y = 1.0f;
	static final float WINDOW_SCALE_ANIM = 0.75f;

	/**
	 * Constructor for RestaurantGui class.
	 * Sets up all the gui components.
	 */
	public SimCity() {
		int WINDOWX = 1280;
		int WINDOWY = 720;

		setBounds(WINDOW_BOUNDS_Y, WINDOW_BOUNDS_Y, WINDOWX, WINDOWY);
		
		
		// Creating the two windows
		animationPanel = new AnimationPanel();
		uiPanel = new UserInterfacePanel(this);
		
		// Setting up the main panel
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		panel.add(uiPanel, BorderLayout.LINE_START);	
		panel.add(animationPanel, BorderLayout.CENTER);
		
		add(panel);
	}
	
	public void actionPerformed(ActionEvent e) {
		
	}
	
	public static void main(String[] args) {
		SimCity city = new SimCity();
		city.setTitle("Team 25 SimCity201");
		city.setVisible(true);
		city.setResizable(false);
		city.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}