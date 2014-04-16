/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.animation.view;

import gui.animation.view.base.BaseSceneView;
import gui.ui.AnimationPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author CMCammarano
 */
public class MarketView extends BaseSceneView implements ActionListener {

	// Caching the parent animation panel
	AnimationPanel animation;
	
	// Main panel
	private JPanel panel;
	
	// Window size
	private static final int WINDOW_X = 1280;
	private static final int WINDOW_Y = 720;
	
	public MarketView(AnimationPanel ap) {
		super(ap);
		
		// Setting the animation
		animation = ap;
		name = "Market View";
		
		// Setting the window size
		Dimension dim = new Dimension((int)(0.72f * WINDOW_X), (int)(0.96f * WINDOW_Y));
		this.setPreferredSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
