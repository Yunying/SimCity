package restaurant.yunying.test.mock;

import restaurant.yunying.interfaces.*;
import global.test.mock.LoggedEvent;
import global.test.mock.EventLog;
import global.test.mock.MockRole;

public class MockWaiter extends MockRole implements Waiter{
	
	public Cashier cashier;
	public Customer customer;
	
	public MockWaiter(String name) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	String name;

	public String getName(){
		return name;
	}

	@Override
	public void msgHereIsCheck(double money, int table) {
		log.add(new LoggedEvent("Received HereIsCheck from cashier. Check amount = " + money));
		customer.msgHereIsCheck(money);
	}

	@Override
	public void msgSitAtTable(Customer c, int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgImReadyToOrder(Customer c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsMyChoice(Customer c, String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderIsReady(String choice, int table) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("order is ready"));
	}

	@Override
	public void msgLeavingTable(Customer cust) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOutOfFood(int table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBreakReply(boolean br) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBackToWork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgImReadyForCheck(Customer c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOnBreak() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isResting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void offBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOnBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAtTable() {
		// TODO Auto-generated method stub
		
	}

}
