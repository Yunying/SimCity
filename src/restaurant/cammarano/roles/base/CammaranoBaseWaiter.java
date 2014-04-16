/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package restaurant.cammarano.roles.base;

import global.PersonAgent.Job;
import global.actions.Action;
import global.roles.Role;
import interfaces.Person;
import java.util.HashMap;
import restaurant.cammarano.interfaces.CammaranoCustomer;
import restaurant.cammarano.interfaces.CammaranoWaiter;
import restaurant.cammarano.roles.CammaranoHostRole;

/**
 *
 * @author CMCammarano
 */
public class CammaranoBaseWaiter extends Role implements CammaranoWaiter {

	protected String name;
	protected int leaveTime;
	
	@Override
	public boolean pickAndExecuteAnAction() {
		return false;
	}

	@Override
	public void msgHereIsTheCheck(CammaranoCustomer c) {
		stateChanged();
	}

	@Override
	public void msgOutOfFood(String choice, CammaranoHostRole.Table table) {
		stateChanged();
	}

	@Override
	public void msgOrderDone(String choice, CammaranoHostRole.Table table) {
		stateChanged();
	}

	@Override
	public void msgHereIsMyChoice(CammaranoCustomer customerAgent, String choice) {
		stateChanged();
	}

	@Override
	public void msgSitCustomer(CammaranoCustomer customer, CammaranoHostRole.Table table, HashMap<String, Float> menu) {
		stateChanged();
	}

	@Override
	public void msgReadyToOrder(CammaranoCustomer customerAgent) {
		stateChanged();
	}

	@Override
	public void msgLeavingTable(CammaranoCustomer customerAgent) {
		stateChanged();
	}

	@Override
	public void msgReadyToPay(CammaranoCustomer customerAgent) {
		stateChanged();
	}

	@Override
	public void msgBreakDenied() {
		stateChanged();
	}

	@Override
	public void msgBreakApproved() {
		stateChanged();
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	public void setName(String n) {
		this.name = n;
	}
}
