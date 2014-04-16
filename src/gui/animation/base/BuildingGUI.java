/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.animation.base;

import gui.animation.view.base.*;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author CMCammarano
 */
public class BuildingGUI extends Rectangle2D.Double {

	private BaseSceneView scene;
	
	public BuildingGUI( int x, int y, int width, int height ) {
		super( x, y, width, height );
	}

	public void setPanel(BaseSceneView p) {
		this.scene = p;
	}
	
	public void displayBuilding() {
		scene.displayScene();
	}
}