/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package restaurant.cammarano.roles.base;

import global.test.mock.EventLog;
import global.test.mock.LoggedEvent;

import java.util.ArrayList;
import java.util.List;

import restaurant.cammarano.misc.CammaranoOrder;

/**
 *
 * @author CMCammarano
 */
public class CammaranoRevolvingStand {

	public EventLog log;
	public List<CammaranoOrder> orders;
	
	public CammaranoRevolvingStand() {
		log = new EventLog();
		orders = new ArrayList<CammaranoOrder>();
	}

	public void addOrder(CammaranoOrder c) {
		log.add(new LoggedEvent("Order " + c.getChoice() + " added to the stand."));
		orders.add(c);
		System.out.println("Added to order, now has " + Integer.toString(orders.size()));
	}
}
