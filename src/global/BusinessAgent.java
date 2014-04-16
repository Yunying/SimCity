package global;

import interfaces.Building;
import interfaces.Employee;
import interfaces.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import restaurant.ji.interfaces.JiCustomer;
import bank.interfaces.Bank;
import global.actions.Action;
import global.roles.Role;
import agent.Agent;

public class BusinessAgent extends Agent implements Building {

	protected Bank bank;
	protected int bankAccount;
	
	protected int startTime;
	protected int closeTime;
	protected boolean open;
	protected boolean payDay;
	protected int currentTime;
	protected int currentDay;
	protected Map<String, Float> wages = new HashMap<String, Float>();
	protected List<Role> employees = new ArrayList<Role>();
	protected List<Role> customers = new ArrayList<Role>();
	
	protected String location;
	protected String name;
	
	
	public BusinessAgent(){
		super();
	}
	
	public void msgUpdateTime(int time, int day) {
		this.currentTime = time;
	}
	public void msgAtLocation(Person p, Role r, List<Action> actions){
		if (r instanceof Employee){
			employees.add(r);
		}
		else
			customers.add(r);
	}

	public void msgLeavingWork(Role employee) {
		if (employees.contains(employee)){
			employees.remove(employee);
		}
	}

	public void msgLeavingAsCustomer(Role customer) {
		if (customers.contains(customer)){
			customers.remove(customer);
		}
	}
		
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}
	
	//**********Accessors and Mutators************//
	public String getName() { return name; }
	public int getBankAccount(){  return bankAccount; }
	public Bank getBank(){ return bank; }
	public String getLocation() {return location;}
	public void setLocation(String l) {location = l;}
	public int getStartTime(){return startTime;}
	public int getCloseTime(){return closeTime;}
	public void setStartTime(int time){startTime = time;}
	public void setCloseTime(int time){closeTime = time;}
	public int getCurrentTime() {return currentTime;}
	public int getCurrentDay() {return currentDay;}
	public List<Role> getPeopleInTheBuilding() {return getEmployees();}
	public List<Role> getEmployees() {return employees;}
	public void setEmployees(List<Role> employees) {this.employees = employees; }
	public boolean isPayDay() {return payDay;}
	public void setPayDay(boolean payDay) {this.payDay = payDay;}
	public boolean isOpen() {return open;}
	public void setOpen(boolean open) {this.open = open;}
}
