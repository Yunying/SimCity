/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package global.actions;

/**
 *
 * @author CMCammarano
 */
public class Action {
	public String task;
	public boolean isActive;
	
	public Action() {
		task = "";
		isActive = false;
	}

	public Action(String t) {
		task = t;
		isActive = false;
	}
	
	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}
		
	public boolean getState() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
}
