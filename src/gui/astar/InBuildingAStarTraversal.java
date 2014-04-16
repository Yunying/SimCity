/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.astar;

import gui.astar.base.Tile;

/**
 *
 * @author CMCammarano
 */
public class InBuildingAStarTraversal extends AStarTraversal {

	public InBuildingAStarTraversal(Tile[][] grid) {
		super(grid);
	}

	@Override
	public boolean gridTypeOkay(Tile t) {
		return true;
	}
}