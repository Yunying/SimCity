package housing;

import java.util.ArrayList;
import java.util.List;

import gui.animation.building.HouseGUI;


import global.PersonAgent;
import global.actions.Action;
import global.roles.Role;
import global.test.mock.LoggedEvent;
import gui.animation.building.HouseGUI;

import agent.Agent;
import interfaces.Person;
import housing.interfaces.House;
import housing.interfaces.HousePerson;
import housing.interfaces.Landlord;
import interfaces.Building;

public class HouseAgent  extends Agent implements House, Building {
	Landlord l;
	HousePerson h;
	HousePerson occupiedBy;
	Person person;
	HouseGUI homegui;
	public List<HousePerson> waitingPeople;//maybe be useful for a requirement with two or more people living in same house
	private boolean apartment;
	private boolean isOccupied;
	String  name;
	public List<Action> actionsForHouse = new ArrayList<Action>();
	String location = "home";


	//Constructors, Accessors, Methods
	public HouseAgent(String name ,boolean Isapt) {
		super();
		this.name = name;
		apartment = Isapt;
		waitingPeople = new ArrayList<HousePerson>();
	}

	public String getName() {

		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setHouseGui(HouseGUI homegui) {
		this.homegui = homegui;
	}

	public HouseGUI getHouseGui() {
		return homegui;
	}


	public boolean isOccupied() {

		if (h == null) {

			if (this.getHousePerson() == null) {

				isOccupied = true;
			}
			else {
				isOccupied = false;
			}

		}
		return isOccupied;
	}

	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;

	}
	public void setOccupant(HousePerson h) {
		occupiedBy = h;

	}
	public HousePerson getOccupant() {
		return occupiedBy;
	}
	public void setLandlord(Landlord landlord) {
		l = landlord;

	}

	public void setHousePerson(HousePerson houseperson) {
		h = houseperson;
	}

	public HousePerson getHousePerson(){

		return h;
	}

	public String getLocation() {

		return location;
	}

	public Person getPerson() {
		return person;
	}
	public void setLocation(String l) {
		location = l;

	}





	//messages

	public void msgRentDue(Landlord l, int rentrate) {
		this.l = l;
		if(h == null) {

			System.out.print("h is null");
		}
		else {
			h.getBills().add(rentrate);

		}
		stateChanged();



	}


	public void msgAtLocation(Person p, Role r, List<Action> action){
		print(p.getName() + " is walking into the house");
		if(r instanceof HousePerson){
			waitingPeople.add((HousePerson) r);
			r.setPerson(p);
		}

		if( action == null){
//			print("no actions to complete");
		}
		if(r == null) {
//			System.out.println("The person is null!");
		}

//		print(r.getPerson().getName() + "'s Residence");
//		waitingPeople.add(h);	
		
//		System.out.println("waiting people size is " + waitingPeople.size() );
		if (action == null){
			
		}
		else if(!action.isEmpty()) {
			for(int i = 0; i < action.size(); i++) {
				this.actionsForHouse.add(action.get(i));
			}
		}
		stateChanged();	

	}



	private void setPerson(Person p) {
		// TODO Auto-generated method stub
		
	}

	public void msgLeavingLocation(Role r){
		//since only one role, one person for that matter will be entering. a for loop is not needed

		if(r instanceof HousePerson){
			r.isActive = false;

		}
		stateChanged();
	}

	public boolean pickAndExecuteAnAction(){
		if (!waitingPeople.isEmpty()) {

			PersonPresent(waitingPeople.get(0));



			return true;
		}
		return false;
	}





	///
	public void PersonPresent(HousePerson p) {
		//System.out.println("null? " + p.getPerson().equals(null));
		p.msgComeIntoHouse(this, p.getPerson(), this.actionsForHouse);
		actionsForHouse.clear();
		waitingPeople.remove(p);
	}



	//functions i dont need but are part of building

	public void msgUpdateTime(int time, int day) {

		// TODO Auto-generated method stub

	}

	public int getStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setStartTime(int t) {
		// TODO Auto-generated method stub

	}

	public int getCloseTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setCloseTime(int t) {
		// TODO Auto-generated method stub

	}

	public List<Role> getPeopleInTheBuilding() {
		// TODO Auto-generated method stub
		return null;
	}













}






