package restaurant.ji.test.mock;

import interfaces.Building;
import interfaces.Person;

import java.util.List;
import java.util.Map;

import market.interfaces.TruckDriver;
import bank.interfaces.Bank;
import restaurant.ji.JiRestaurantAgent.Table;
import restaurant.ji.RevolvingStand;
import restaurant.ji.interfaces.*;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import global.test.mock.Mock;

public class MockJiRestaurant extends Mock implements JiRestaurant {

	String name;
	public float currentAssets;
	public Bank bank;
	public int companyAccount;
	
	public MockJiRestaurant(String name){
		this.name = name;
	}
	
	@Override
	public void msgAtLocation(Person p, Role r, List<Action> actions) {
		log.add(new LoggedEvent("Received msgAtLocation"));
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void msgIWantFood(JiCustomer customer) {
//		log.add(new LoggedEvent("Received msgIWantFood"));
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void msgOrderDelivered(Map<String, Integer> order, Building market, TruckDriver driver, float bill) {
		log.add(new LoggedEvent("Received msgOrderDelivered"));
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void msgPaidBillToMarket(float expense) {
		log.add(new LoggedEvent("Received msgPaidBillToMarket"));
		currentAssets -= expense;
	}
    
	@Override
	public void msgReceivedPaymentFromCustomer(float income) {
		log.add(new LoggedEvent("Received msgReceivedPaymentFromCustomer"));
		currentAssets += income;
	}
	
    @Override
	public void msgLeavingWork(Role employee) {
		log.add(new LoggedEvent("Received msgLeavingWork"));
		// TODO Auto-generated method stub
	}

	@Override
	public void msgUpdateTime(int time, int day) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStartTime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCloseTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCloseTime(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Role> getPeopleInTheBuilding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getCurrentAssets() {
		return currentAssets;
	}

	@Override
	public Bank getBank() {
		return bank;
	}
	@Override
	public int getBankAccount() {
		return companyAccount;
	}

	@Override
	public List<JiWaiter> getWaiters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiCook getCook() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiHost getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JiCashier getCashier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RevolvingStand getOrderStand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgLeavingAsCustomer(JiCustomer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Table> getTables() {
		// TODO Auto-generated method stub
		return null;
	}

}
