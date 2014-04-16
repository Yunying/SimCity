package restaurant.ji.test.mock;

import java.util.List;

import global.test.mock.*;
import restaurant.ji.JiMyTable;
import restaurant.ji.RevolvingStand;
import restaurant.ji.interfaces.JiCashier;
import restaurant.ji.interfaces.JiCook;
import restaurant.ji.interfaces.JiCustomer;
import restaurant.ji.interfaces.JiHost;
import restaurant.ji.interfaces.JiWaiter;

public class MockWaiter extends MockRole implements JiWaiter{
	public EventLog log = new EventLog();
	
	public MockWaiter() {
		super();
	}
	
	@Override
	public void msgHeresANewCustomer(JiCustomer cust, int tableNum, int tableX, int tableY){
		log.add(new LoggedEvent("Received msgHeresANewCustomer from host. Going to table number: " + tableNum));
	}
	
	@Override
	public void msgGoOnBreak(){
		log.add(new LoggedEvent("Received msgGoOnBreak from host"));
	}
	
	@Override
	public void msgNoBreak(){
		log.add(new LoggedEvent("Received msgNoBreak from host"));
	}
	
	@Override
	public void msgImReadyToOrder(JiCustomer cust){
		log.add(new LoggedEvent("Received msgImReadyToOrder from customer"));
	}
	
	@Override
	public void msgHeresMyOrder(JiCustomer cust, String choice){
		log.add(new LoggedEvent("Received msgHeresMyOrder from customer. Getting " + choice));
	}
	
	@Override
	public void msgOrderIsReady(int tableNum){
		log.add(new LoggedEvent("Received msgOrderIsReady from cook. For table: " + tableNum));
	}
	
	@Override
	public void msgOutOfThis(String choice){
		log.add(new LoggedEvent("Received msgOutOfThis from cook. Out of: " + choice));		
	}
	
	@Override
	public void msgIWantMyCheck(JiCustomer customer){
		log.add(new LoggedEvent("Received msgIWantMyCheck from customer"));
	}
	
	@Override
	public void msgHereIsCheck(Float amount, JiCustomer customer){
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Amount: " + amount));
	}
	
	@Override
	public void msgDoneEatingAndLeaving(JiCustomer cust){
		log.add(new LoggedEvent("Received msgDoneEatingAndLeaving from customer"));
	}

	public void msgHeresYourPaycheck(float pay) {
		log.add(new LoggedEvent("Received msgHeresYourPaycheck"));
		
	}

	public void msgStopWorkingGoHome() {
		log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
		
	}

	@Override
	public boolean hasReceivedPaycheck() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getName() {
		return null;
	}

	@Override
	public void msgAtWaitingArea() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAtTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAtCook() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void askToGoOnBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCook(JiCook cook) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCashier(JiCashier cashier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHost(JiHost host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<JiMyTable> getMyTables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrderStand(RevolvingStand stand) {
		// TODO Auto-generated method stub
		
	}

}
