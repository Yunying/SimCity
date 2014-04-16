/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.astar;

import global.enumerations.TileType;
import gui.astar.base.Tile;

/**
 *
 * @author CMCammarano
 */
public class PersonAStarTraversal extends AStarTraversal {

	public PersonAStarTraversal(Tile[][] grid) {
		super(grid);
	}
	
	@Override
	public boolean gridTypeOkay(Tile t) {
		if(t.tile == TileType.Sidewalk) {
			return true;
		}
		
		if(t.tile == TileType.Crosswalk) {
			return true;
		}
		return false;
	}
}
