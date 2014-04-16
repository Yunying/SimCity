/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package restaurant.cammarano.misc;

import restaurant.cammarano.interfaces.CammaranoWaiter;
import restaurant.cammarano.roles.CammaranoHostRole.Table;

/**
 *
 * @author CMCammarano
 */
public class CammaranoOrder {
	private String choice;
	private CammaranoWaiter waiter;
	private Table table;
	public CammaranoOrderEnum state;

	public CammaranoOrder(String c, CammaranoWaiter w, Table t) {
		this.choice = c;
		this.waiter = w;
		this.table = t;
		state = CammaranoOrderEnum.Pending;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public CammaranoWaiter getWaiter() {
		return waiter;
	}

	public void setWaiter(CammaranoWaiter waiter) {
		this.waiter = waiter;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
}
