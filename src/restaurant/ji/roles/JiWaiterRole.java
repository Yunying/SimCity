package restaurant.ji.roles;

import restaurant.ji.JiMyTable;
import restaurant.ji.Menu;
import restaurant.ji.RevolvingStand;
import restaurant.ji.JiMyTable.TableState;
import restaurant.ji.interfaces.*;
import global.roles.Role;
import global.test.mock.LoggedEvent;
//import gui.animation.building.restaurant.ji.JiWaiterGui;

import interfaces.Building;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Waiter Agent
 * A waiter that interacts with the cook
 */

public class JiWaiterRole extends Role implements JiWaiter{
	
	public static JiRestaurant restaurant;
	
	public enum State {
		none,arrivedAtJiRestaurant, working, ALLOWEDTOBREAK, DENIEDBREAK, ONBREAK, leavingWork
		};
	private State state;
	
	// for animation
	public static final int xTable = 50;
    public static final int yTable = 50;
	private Semaphore atTable = new Semaphore(0,true); 
	private Semaphore atCook = new Semaphore(0,true);
	private Semaphore atWaitingArea = new Semaphore(0,true);
//	public JiWaiterGui //waitergui;	
	
	private List<JiMyTable> myTables = new ArrayList<JiMyTable>(); // List of tables+customers+orders the waiter will cater to
	private List<String> unavailableEntrees = new ArrayList<String>();

	private JiHost host;
	private JiCook cook;
	private JiCashier cashier;
	
	private boolean receivedPaycheck;
	
	public JiWaiterRole() {
		super();
		state = State.none;
		receivedPaycheck = false;
	}
	
	
	//**********Messages************//
	@Override
	public void msgAtBuilding(Building rest){
		log.add(new LoggedEvent("Received msgAtJiRestaurant"));
		state = State.arrivedAtJiRestaurant;
		restaurant = (JiRestaurant)rest;
		stateChanged();
	}
	
	@Override
	public void msgHeresANewCustomer(JiCustomer cust, int tableNum, int tableX, int tableY){
		state = State.working;
		JiMyTable t = new JiMyTable(cust, tableNum, tableX, tableY);
		t.setState(TableState.waitingToBeSeated);
		this.myTables.add(t);
		stateChanged();
		
	}
	@Override
	public void msgGoOnBreak(){
		state = State.ALLOWEDTOBREAK;
		stateChanged();
	}
	
	@Override
	public void msgNoBreak() {
		state = State.DENIEDBREAK;
		stateChanged();
	}
	
	@Override
	public void msgImReadyToOrder(JiCustomer cust){
		state = State.working;
		for (JiMyTable t : myTables) {
			if (t.getCustomer() == cust){
				t.setState(TableState.GOINGTOTAKEORDER);
				stateChanged();
				break;
			}
		}
	}
	
	@Override
	public void msgHeresMyOrder(JiCustomer cust, String choice){
		state = State.working;
		for (JiMyTable t : myTables) {
			if (t.getCustomer() == cust){
				t.setChoice(choice);
				t.setState(TableState.TAKINGORDER);
				stateChanged();
			 	break;
			}
		}
	}
	
	@Override
	public void msgOrderIsReady(int tableNum){
		state = State.working;
		for (JiMyTable t : myTables) {
			if (t.getTableNum() == tableNum){
				t.setState(TableState.PICKINGUPORDER);
				stateChanged();
				break;
			}
		}
	}
	
	@Override
	public void msgOutOfThis(String choice){
		state = State.working;
		unavailableEntrees.add(choice);
		for (JiMyTable t : myTables) {
			if (t.getChoice() == choice && t.getState() == TableState.WAITINGFORFOOD){
				t.setState(TableState.TELLINGTOREORDER);
				stateChanged();
				break;
			}
		}
	}

	@Override
	public void msgIWantMyCheck(JiCustomer customer) {
		state = State.working;
		for (JiMyTable t : myTables) {
			if (t.getCustomer()  == customer){
				t.setState(TableState.GETTINGCHECK);
				stateChanged();
				break;
			}
		}
	}	
	
	@Override
	public void msgHereIsCheck(Float amount, JiCustomer customer) {
		state = State.working;
		for (JiMyTable t : myTables) {
			if (t.getCustomer()  == customer){
				t.setBill(amount);
				t.setState(TableState.DELIVERINGCHECK);
				stateChanged();
				break;
			}
		}
	}

	@Override
	public void msgDoneEatingAndLeaving(JiCustomer cust) {
		state = State.working;
		for (JiMyTable t : myTables) {
			if (t.getCustomer() == cust) {
				t.setState(TableState.CUSTOMERLEFT);
				stateChanged();
				break;
			}
		}
	}
	
	@Override
	public void msgStopWorkingGoHome() {
		state = State.leavingWork;
		log.add(new LoggedEvent("Received msgStopWorkingGoHome"));
		stateChanged();
	}

	@Override
	public void msgHeresYourPaycheck(float paycheck) {
		log.add(new LoggedEvent("Received msgHeresYourPaycheck"));
		person.ChangeMoney(paycheck);
		receivedPaycheck = true;
		stateChanged();
	}

	// messages from animation
	@Override public void msgAtTable() { atTable.release(); }
	@Override public void msgAtWaitingArea() { atWaitingArea.release(); }
	@Override public void msgAtCook() { atCook.release(); }
	

	//**********Scheduler************//
	@Override
	public boolean pickAndExecuteAnAction() {
		try{
			if (state == State.arrivedAtJiRestaurant){
				beginWork();
				return true;
			}
			else if (state == State.DENIEDBREAK){
				deniedBreak();
				return true;
			}
			else if (myTables.isEmpty()){
				if (state == State.leavingWork){
					leaveWork();
					return true;
				}
				else if (state == State.ALLOWEDTOBREAK){
					goOnBreak();
					return true;
				}
			}
			else if (state == State.working || state == State.ALLOWEDTOBREAK){
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.waitingToBeSeated){
						seatCustomer(t);
						return true;
					}
				}
				
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.GOINGTOTAKEORDER){
						goToTakeOrder(t);
						return true;
					}
				}
				
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.TAKINGORDER){
						takeOrderAndGiveToCook(t);
						return true;
					}
				}
				
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.TELLINGTOREORDER){
						notifyCustomerOfReorder(t);
						return true;
					}
				}
				
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.PICKINGUPORDER){
						deliverOrder(t);
						return true;
					}
				}
				
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.GETTINGCHECK){
						askForCheck(t);
						return true;
					}
				}
				
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.DELIVERINGCHECK){
						deliverCheck(t);
						return true;
					}
				}
				
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.CUSTOMERLEFT){
						finishCustomer(t);	
						return true;
					}
				}
				
				for (JiMyTable t : myTables) {
					if (t.getState() == TableState.IDLE){
						//waitergui.DoRest();
						return true;
					}
				}
			}
			else if (state == State.leavingWork){
	    		leaveWork();
	    	}
			return false;
		}
	
		catch(ConcurrentModificationException e){
			return false;
		}
	}

	
	//**********Actions************//
	private void beginWork(){
		state = State.working;
		Do("Working as waiter now!");
		receivedPaycheck = false;
		restaurant.msgAtLocation(person, this, null); // change this message in Building so we're not always passing in unnecessary information
	}
	
	/*
	 * Tells the WaiterGui to move to the table specified. Makes sure no other actions are taken before the waiter reaches the table (when semaphore is released)
	 */
	private void goToTable(JiMyTable t){
//		waitergui.DoGoToTable(t.tableX, t.tableY);
//		try { 
//			atTable.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	/*
	 * Tells the WaiterGui to go off screen to the start position. Makes sure no other actions are taken before the waiter reaches this position (when semaphore is released)
	 */
	private void goRetrieveCustomer(){
//		waitergui.DoRetrieveCustomer();
//		try {
//			atWaitingArea.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
	
	/*
	 * Tells the WaiterGui to go to the Cook. Makes sure no other actions are taken before the waiter reaches the cook (when semaphore is released)
	 */
	private void goToCook(){
		Do("Going to Cook");
//		waitergui.DoSeeCook();
//		try {
//			atCook.acquire();
//		} catch (InterruptedException e) {	
//			e.printStackTrace();
//		}
	}

	/*
	 * Tells the waiter to bring a customer to his table and hand him a menu
	 * 
	 * @param JiMyTable t the JiMyTable containing the customer and his table
	 */
	public void seatCustomer(JiMyTable t){
		goRetrieveCustomer();
		t.setState(TableState.IDLE);
		Do("Follow me");
		t.getCustomer().msgFollowMe(this, new Menu(), t.getTableX(), t.getTableY());
		goToTable(t);
	}

	public void goToTakeOrder(JiMyTable t){
		goToTable(t);
		t.setState(TableState.IDLE);
		Do("What would you like, " + ((JiCustomerRole) t.getCustomer()).getName() + "?");
		t.getCustomer().msgWhatWouldYouLike();
	}
	
	public void takeOrderAndGiveToCook(JiMyTable t){
		//goToCook();
		t.setState(TableState.WAITINGFORFOOD);
		Do("ORDER IN: " + t.getChoice() + " " + t.getTableNum() + " from " + this.getName());
		cook.msgCookThisOrder(t.getChoice(), this, t.getTableNum());
		////waitergui.clearChoicetext();
		//waitergui.DoRest();
	}

	public void notifyCustomerOfReorder(JiMyTable t){
		goToTable(t);
		t.setState(TableState.IDLE);
		for (String choice : unavailableEntrees)
		{
			t.getCustomer().msgWeAreOut(choice);
		}
//		waitergui.DoRest();
		
	}
	
	public void deliverOrder(JiMyTable t){
		goToCook();
		t.setState(TableState.IDLE);
		//waitergui.setChoiceText(t.choice.substring(0,2), true);
		goToTable(t);
		Do("Delivering " + t.getChoice() + " to " + t.getCustomer().getName());
		t.getCustomer().msgHeresYourFood(t.getChoice(), cashier);
		//waitergui.clearChoicetext();
	}
	
	public void askForCheck(JiMyTable t){
		goToTable(t);
		t.setState(TableState.IDLE);
		Do("Asking for check");
		cashier.msgComputeBill(t.getChoice(), t.getCustomer(), this);
	}
	
	public void deliverCheck(JiMyTable t){
		//waitergui.setChoiceText(Float.toString(t.bill), false);
		goToTable(t);
		t.setState(TableState.IDLE);
		Do("Delivering check to " + ((JiCustomerRole) t.getCustomer()).getName());
		t.getCustomer().msgHereIsCheck(t.getBill());
	}
	
	public void finishCustomer(JiMyTable t){
		t.setState(TableState.IDLE);
		//waitergui.clearChoicetext();
		host.msgTableIsFree(t.getTableNum());
		myTables.remove(t);
	}
	
	@Override
	public void askToGoOnBreak(){ // from //waitergui
		Do("Can I go on break?");
		host.msgCanIGoOnBreak(this);
		//waitergui.setBreakBoxEnabled(false);
	}
	
	public void goOnBreak(){
		state = State.ONBREAK;
		Do("Going on break");
		//waitergui.DoRest();
		//waitergui.setBreakBoxEnabled(true);
	}
	public void deniedBreak(){
		state = State.working;
		//waitergui.setBreakBoxEnabled(true);
		//waitergui.uncheckBreakBox();
	}
		
	@Override
	public void finishBreak(){
		state = State.working;
		Do("Going back to work");
		host.msgBackFromBreak(this);	
	}
	
	void leaveWork(){
		state = State.none;
		Do("Done working bye");
		restaurant.msgLeavingWork(this);
		//gui.DoLeaveBuilding();
		person.msgLeavingLocation(this); // inactivate JiWaiterRole
	}
	
	//**********Utilities and Accessors************//
//	public void setGui(JiWaiterGui gui) { waitergui = gui; }
//	public JiWaiterGui getGui() { return waitergui; }
	@Override public void setHost(JiHost host) { this.host = host; }
	@Override public void setCook(JiCook cook) { this.cook = cook; }
	public void setOrderStand(RevolvingStand stand) {;} // I don't need this but it's in Waiter
	@Override public void setCashier(JiCashier cashier) { this.cashier = cashier; }
	@Override public String getName() { return person.getName(); }	
	@Override public boolean hasReceivedPaycheck() { return receivedPaycheck; }
	@Override public List<JiMyTable> getMyTables() { return myTables; }
	
	
}

