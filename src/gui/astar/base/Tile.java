/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.astar.base;

import global.enumerations.TileType;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Colin
 */
public class Tile extends Semaphore {
	
	public TileType tile;
	
	public Tile(int permits, boolean fair) {
		super(permits, fair);
	}
}