/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.animation.view.base;

import gui.ui.AnimationPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

/**
 *
 * @author CMCammarano
 */
public class BaseSceneView extends JPanel implements ActionListener {

	private AnimationPanel animation;
	public String name;
	
	public BaseSceneView(AnimationPanel a) {
		animation = a;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {}
	
	public void displayScene() {}
}
