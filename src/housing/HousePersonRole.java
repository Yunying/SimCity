package housing;

import interfaces.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import global.actions.Action;
import global.roles.Role;
import housing.interfaces.House;
import housing.interfaces.HousePerson;
import housing.interfaces.Landlord;

public class HousePersonRole extends Role implements HousePerson {

	String name;
	//HashMap<String, Integer> foodmap = new HashMap<String, Integer>(); for V2
	public List<String> foods = new ArrayList<String>();//foods that houseperson has in house v1
	List<Action> foodsToShopFor = new ArrayList<Action>();
	Landlord l;
	public List<Integer> bills = new ArrayList<Integer>();
	public List<Action> actions = new ArrayList<Action>();
	Timer eatTimer = new Timer();
	Timer cleanTimer = new Timer();
	public enum HousePersonState {nothing, doneEating, leaving}
	HousePersonState state = HousePersonState.nothing;
	House house;
	private Semaphore atLoc = new Semaphore(0, true);


	int rent = 30;//rentrate will actually be set in the add person, but set here for the time being
	int time;
	int timeForWork;
	public int timeToClean;
	int timeToEat;
	int timeToCheckBalance;
	public int hungerLevel;// = 3; //average hunger between (0-5)
	int currentTime;



	//accessors
	public HousePersonRole() {

		timeToClean =(int) (15 + Math.random()*1.99999);
		timeToCheckBalance = (int) (24 + Math.random() * 5.9999 );//code for v2 possibly?
		hungerLevel = (int) (Math.random() * 5);
		
		//start with a certain amount of foods, but not too much, so they can go to market frequently
		foods.add("Steak");
		foods.add("Pizza");
		foods.add("Salad");
		foods.add("Soup");
	}


	public int getHungerLevel() {
		return hungerLevel;
	}
	
	public House getHouse() {
		return house;
	}
	
	public void setHouse(House house) {
		this.house = house;
	}

	public void setLandlord(Landlord landlord) {
		l =  landlord;
	}
	public Landlord getLandlord() {
		return l;

	}
	public List<Integer> getBills() {
		return bills;
	}
	public String getName() {

		return person.getName();
	}

	public boolean findTask(Action a) {

		for(int i = 0; i < actions.size(); i++) {
			if(actions.get(i).getTask().equals(a.getTask())) {
				return true;
			}
		}
		return false;
	}
	public int getRentRate() {
		return rent;
	}
	public void setRent(int rentrate) {
		rent = rentrate;
	}

	//msgs
	public void msgUpdateTime(int time, int day) {
		
		this.time =time;
		if(time == timeToClean && day == 0) {
			print("Sunday Clean Day!");
			Action a = new Action();

			a.setTask("clean");
			if(!findTask(a)){
				actions.add(a);
			}

		}
		

	}

	public void msgComeIntoHouse(House h, Person p, List<Action> action) {
		house = h;
		this.person = p;
		
		if(!action.isEmpty()) {
			
			for(int i = 0; i < action.size();i++) {
				if(action.get(i).getTask().equals("Steak") || action.get(i).getTask().equals("Soup")
				|| action.get(i).getTask().equals("Pizza") || action.get(i).getTask().equals("Salad")){
					foods.add(action.get(i).getTask());
					
				}
				
				else{
					
					actions.add(action.get(0));
					System.out.println("Added " + action.get(0).getTask());
					
				}
				
			}
			
		}
		
		//gui.GoToHomePosition();
		/*try {
			atLoc.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */
		stateChanged();

	}
	public void msgAtLoc() {//from animation
		atLoc.release();
	}
	@Override
	public boolean pickAndExecuteAnAction() {
		
		//checks to see if he has bills
		//System.out.println(bills.size() + " is bills size");
		if((!bills.isEmpty()) && state != HousePersonState.leaving) {
//			System.out.println("enter dat paybill condition yo");
			PayBill();
			return true;
		}

		if(!actions.isEmpty() && state != HousePersonState.leaving){


			

				for(int i = 0; i < actions.size(); i++){
					if(actions.get(i).getTask().equals("eat")){
//						System.out.println("enter dat eat in actions doh");

						EatAtHome(actions.get(i));
						return true;
					}
				}
					
				for(int i = 0; i < actions.size(); i++) {	
					if(actions.get(i).getTask().equals("clean")){
//						System.out.println("entered dat cleanin yo");
						cleanHouse(actions.get(i));
						return true;
					}
				}
		}
	

			//third priority to check to see if he has more food and his hunger level is higher than 4
			if(state == HousePersonState.leaving) {

				LeaveHouse();
				return true;
			}

		//System.out.println("returning false");
		return false;


	}



	private void EatAtHome(final Action a) {
		//gui.DoGoToFridge() {
		/*try {
			atLoc.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Do("At Fridge");
		if (foods.size() == 0) {
			Do("Out of food, therefore I shall go to Market");
			this.foodsToShopFor.add(new Action("pizza"));
			this.foodsToShopFor.add(new Action("chicken"));
			this.foodsToShopFor.add(new Action("steak"));
			this.foodsToShopFor.add(new Action("steak")); //steak is a very popular household item
			this.foodsToShopFor.add(new Action("salad"));
			getPerson().AddTaskBuyFoodAtMarket(foodsToShopFor);

			getPerson().AddTaskGetFood();
			house.msgLeavingLocation(this);
		
			//getPerson().msgLeavingLocation(this);
			//getPerson().GoToMarket();
			state = HousePersonState.leaving;
			stateChanged();

		}

		else{ 
			//gui.DoGoToStove() {
			/*try {
				atLoc.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//gui.DoGoToTable();
			try {
				atLoc.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 */
			foods.remove(foods.get(0));
			eatTimer.schedule(new TimerTask() {
				Object cookie = 1;
				public void run() {
					
					print("Done eating at home, cookie=" + cookie);
					state = HousePersonState.doneEating;
					
					
				}
			},
			5000);
			hungerLevel = 0;
			actions.remove(a);
			stateChanged();

			}
		
	
	}
	
	private void PayBill() {

		if(this.getPerson().getMoney() >= 30) {
			l.msgHereIsRent(this, 30 /*rent*/);

			getPerson().ChangeMoney(-30);


			bills.remove(bills.get(0));
		}
		else {
			l.msgHereIsRent(this, (float) this.getPerson().getMoney());
			getPerson().setMoney(0);
			bills.remove(bills.get(0));


		}
	}

	private void cleanHouse(final Action a) {

		//gui.DoGoToFridge();
		/*try {
			atLoc.acquire();
		} catch (InterruptedException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}*/
		cleanTimer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				//print("Done cleaning Fridge, cookie=" + cookie);

			}
		},
		1000);

		//gui.DoGoToStove();
		/*try {
			atLoc.acquire();
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		 */
		cleanTimer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				//print("Done cleaning stove, cookie=" + cookie);

			}
		},
		1000);
		//gui.DoGoToTable();
		/*try {
			atLoc.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 */
		cleanTimer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				//print("Done cleaning table, cookie=" + cookie);

			}
		},
		1000);

		//gui.DoGoToBase();
		/*try {
			atLoc.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */
		cleanTimer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				//print("Done cleaning bed, cookie=" + cookie);



			}
		},
		1000);
		actions.remove(a);
		state = HousePersonState.nothing;
		stateChanged();
	}




	public void LeaveHouse() {
		//this.getPerson().msgLeavingLocation(this);
		System.out.println("Leaving bye");
		house.msgLeavingLocation(this);
		//gui.DoExitHouse();
	}





}

