



	
	
	
	package restaurant.mcneal.roles;

	import global.roles.Role;
import interfaces.Employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import agent.Agent;
import restaurant.mcneal.McNealCheck;
import restaurant.mcneal.McNealFood;
import restaurant.mcneal.Revolver;
import gui.animation.role.restaurant.mcneal.McNealWaiterGUI;
import restaurant.mcneal.interfaces.McNealCashier;
import restaurant.mcneal.interfaces.McNealCook;
//import restaurant.mcneal.gui.RestaurantPanel;
	import restaurant.mcneal.interfaces.McNealCustomer;
import restaurant.mcneal.interfaces.McNealHost;
import restaurant.mcneal.interfaces.McNealWaiter;
	
import restaurant.mcneal.roles.McNealHostRole.Table;
import restaurant.yunying.roles.OrderStand;
	public abstract class WaiterAgent extends Role implements McNealWaiter, Employee{
	     

	        private Table t;
	        private boolean withCustomer =false;
	        private boolean canGoOnBreak = false;
	        private boolean askedforbreak = false;
	        private boolean WantToGoOnBreak = false;
	        private boolean paycheckhere = false;
	        private int choice;
	        private Semaphore atTable = new Semaphore(0,true);
	        private Semaphore atCook = new Semaphore(0,true);
	        public Revolver revolver;
	        public McNealWaiterGUI waiterGui = null;
	        private McNealHost host;
	        public McNealCustomer customer;
	        public myCustomer m;
	        private McNealCashier cashier;
	        public Order o;
	        public McNealCook c;
	        private boolean isWorking = false;
	        private boolean OnBreak = false;
	        private McNealCheck check;
	        private McNealFood f;
	        //private RestaurantPanel r;
	        private boolean readyToGoOffBreak = false;
	        public boolean getbreakstatus= false;
	        public WaiterAgent() {
	                super();
	                
	                setwithCustomer(false);

	        }
	        public String getName() {
	        	return getPerson().getName();
	        }
	        public void getbreakstatus(boolean j) {
	                getbreakstatus = j;
	        }
	        public void readyToGoOffBreak(boolean h) {
	                readyToGoOffBreak = h;
	                stateChanged();
	        }
	        public boolean askBreak() {
	                return askedforbreak;
	        }

	        public void setAskBreak(boolean askedforbreak) {
	                System.out.println("aking for break: " + askedforbreak);
	                this.askedforbreak = askedforbreak;
	        }
	        public boolean onBreak() {
	                return OnBreak;
	        }
	        public void setOnBreak(boolean onbreak){
	                System.out.println("changin break status to " + onbreak);
	                OnBreak = onbreak;
	        }
	        public boolean withCustomer() {
	                return withCustomer;
	        }


	        public void setwithCustomer(boolean withCustomer) {
	                this.withCustomer = withCustomer;
	        }
	        public boolean isWorking() {
	                return isWorking;
	        }
	        public void setWorking(boolean working) {
	                isWorking = working;
	        }
	        public void setWatier(McNealWaiterGUI gui) {
	                waiterGui = gui;
	        }
	        public void setCashier(McNealCashier c) {
	                cashier = c;
	        }
	        public void setHost(McNealHost host) {
	                this.host = host;

	        }
	        public void setCustomer(McNealCustomer cust) {
	                customer =cust;
	        }
	        public void setCook(McNealCook cook){
	                this.c = cook;
	        }
	      
	        public McNealCook getCook() {
	        	return c;
	        }
	        public McNealWaiterGUI getGui() {
	                return waiterGui;
	        }
	        public void setGui(McNealWaiterGUI gui) {
	                waiterGui = gui;
	        }
	        int getChoice() {
	                return choice;
	        }


	        public Table getTable() {
	                return t;
	        }

	        public myCustomer getmyCustomer() {
	                return m;
	        }
	        private void setgetCustomer(myCustomer my) {
	                m = my;

	        }
	        public List<myCustomer> getWaitingCustomers() {

	                return waitingCustomers;
	        }
	        public List<myCustomer> waitingCustomers = new ArrayList <myCustomer>();
	    



	        public class Menu {
	                private int numofchoices =4;
	                private String choice;
	                private double price;
	                private String thechoice;
	                private Map<Integer, String> m = new HashMap<Integer, String>();

	                private ArrayList<Integer> choices = new ArrayList<Integer>();
	                private ArrayList<Double> prices = new ArrayList<Double>();
	                public String getChoice() {

	                        if(choices.get(0) == 1) {
	                                choice = "ST";
	                                price = 15.99;
	                                return choice;
	                        }
	                        if(choices.get(1) == 2) {
	                                choice = "CH";
	                                price = 10.99;
	                                return choice;
	                        }
	                        if(choices.get(2) == 3){
	                                choice = "SAL";
	                                price = 5.99;
	                                return choice;
	                        }
	                        if (choices.get(3) == 4) {

	                                choice = "PI";
	                                price = 8.99;
	                                return choice;
	                        }



	                        return choice;
	                }
	                public List<Integer> getlistChoices() {
	                        return choices;
	                }
	                
	                public Map<Integer, String> getMap () {
	                        return m;
	                }
	                public Menu() {
	                        m.put(1, "ST");
	                        m.put(2, "CH");
	                        m.put(3, "SAL");
	                        m.put(4, "PI");
	                        prices.add(15.99);
	                        prices.add(10.99);
	                        prices.add(5.99);
	                        prices.add(8.99);

	                }
	                public String choose(int c) {
	                        thechoice = m.get(c);
	                        return thechoice;
	                }
	                public String gtChoice() {
	                        return thechoice;
	                }
	                private double getPrice() {
	                        return price;
	                }
	                public ArrayList<Double> getPriceList() {
	                        return prices;
	                }
	                public void removeChoice(String choice) {
	                        m.remove(thechoice);
	                        numofchoices--;

	                }
	                public int getNumChoices() {
	                        return numofchoices;
	                }
	        }


	        public class myCustomer {
	                myCustomer m; //= //new myCustomer(this);
	                McNealCustomer c;
	                Table tablet;
	                String choice;
	                
	                public CustomerState state = CustomerState.DoingNothing;
	                public CustomerEvent event = CustomerEvent.none;

	                myCustomer(McNealCustomer cust, Table table, String choice) {
	                        c = cust;
	                        tablet = table;
	                        state = CustomerState.DoingNothing;
	                        this.choice = choice;
	                        event = CustomerEvent.none;
	                        //this.state = state;
	                }
	                myCustomer(myCustomer m) {
	                        this.m = m;

	                }
	                public McNealCustomer getCustomer( ) {
	                        return c;
	                }
	                public myCustomer getmyCustomer() {
	                        return m;        

	                }
	              
	                public Table getTable() {
	                        return tablet;
	                }
	                public Order getOrder() {
	                        return o;
	                }
	                public String getStringChoice() {
	                
	                        return choice;
	                }


	                
	    	      



	        }
	        public enum WaiterState { NotOnBreak, CanGetBill, OnBreak,FinishedBreak};
	        public WaiterState ws = WaiterState.NotOnBreak;
	        public enum CustomerState {DoingNothing, WaitingInRestaurant, WaitingtoBeSeated, BeingSeated,Seated,LookAtMenu, ReadytoOrder, Ordered, WaitingforFood, ReOrder, ReOrdered, BeingServed, Eating, CanGetBill, DoneEating, ReceivedBill, Leaving };
	        CustomerState state = CustomerState.DoingNothing;
	        public enum CustomerEvent { none, gotHungry, followWaiter,beingseated, seated, ordering,beingserved, outoffood, doneEating, doneLeaving, readytoorder, ordered, cangetbill, billready };
	        CustomerEvent event = CustomerEvent.none;


	        //msgs
	        public void msgSitAtTable( McNealWaiter wa, McNealCustomer customer, Table table) {
	                if(wa.getName().equals("On Break")){
	                        WantToGoOnBreak = true;
	                        stateChanged();
	                }
	                else{
	                        t = table;
	                        waitingCustomers.add(new myCustomer(customer, table, ""));
	                        setgetCustomer(waitingCustomers.get(0));
	                        for(int i = 0; i < waitingCustomers.size(); i++) {
	                                if (table.getNumber() == waitingCustomers.get(i).getTable().getNumber()) {
	                                        waitingCustomers.get(i).state = CustomerState.WaitingInRestaurant;
	                                        setWorking(true);
	                                        System.out.println(waitingCustomers.size() + " is the current customer size");
	                                        stateChanged();
	                                }
	                        }
	                }}

	        public void msgAtTable() { //called from animation that releases the semaphore when the agent arrives at the table
	                atTable.release();
	                //System.out.println("brokeoutthou");
	                stateChanged();
	        }
	        public void msgServedFood(myCustomer c) {
	                c.event = CustomerEvent.cangetbill;

	                stateChanged();

	        }
	        public void msgAtCook() {
	                atCook.release();
	                stateChanged();
	        }
	        public void msgReadyToOrder(Table t) {
	                for(int i = 0; i < waitingCustomers.size();i++) {
	                        if(t.getNumber() == waitingCustomers.get(i).getTable().getNumber()) {
	                                waitingCustomers.get(i).event = CustomerEvent.readytoorder;
	                                stateChanged();//called from animation that releases the semaphore when the agent arrives at the table
	                        }
	                }


	        }
	        public void msgHereIsmyChoice(String choice, Table t) {
	                for(int i = 0; i < waitingCustomers.size(); i++ ) {
	                        if(waitingCustomers.get(i).getTable().getNumber() == (t.getNumber())) {
	                                print("Recieved choice.");
	                                waitingCustomers.get(i).choice = choice;
	                                System.out.println(choice + "is the choice");
	                                waitingCustomers.get(i).event = CustomerEvent.ordered;
	                                //waitingCustomers.get(i).state = CustomerState.ReOrdered;


	                                stateChanged();
	                        }
	                }
	        }
	        public void msgOrderIsReady(McNealWaiter w, String choice, Table t) {

	                for(int i = 0; i < waitingCustomers.size(); i++ ) {
	                        if(waitingCustomers.get(i).getTable().getNumber() == (t.getNumber())) {

	                                waitingCustomers.get(i).event = CustomerEvent.beingserved;
	                                stateChanged();

	                                //o = new Order(w, choice,t);
	                                System.out.println("order is ready " + t.getNumber());
	                        }
	                }

	        }

	        public void msgDoneEatingandPaying(Table t){
	                for(int i = 0; i < waitingCustomers.size(); i++ ) {
	                        if(waitingCustomers.get(i).getTable().getNumber() == (t.getNumber())) {
	                                print("message recieved hes leaving");
	                                waitingCustomers.get(i).event =CustomerEvent.doneLeaving;
	                                //my.event = CustomerEvent.none;
	                                //ws = WaiterState.OnBreak;
	                                stateChanged();
	                        }
	                }

	        }

	        public void msgBreakReply(boolean reply) {
	                if (reply) {
	                        getbreakstatus(true);
	                        //ws = WaiterState.OnBreak;
	                        canGoOnBreak = true;
	                        WantToGoOnBreak = false;
	                        stateChanged();

	                }
	                else {
	                        getbreakstatus(false);
	                        WantToGoOnBreak = false;
	                        print("Still working, can't go on break");
	                        canGoOnBreak = false;
	                        stateChanged();
	                }
	        }

	        public void msgOutOFFood(McNealFood f, Table t) {
	                for(int i = 0; i < waitingCustomers.size(); i++ ) {
	                        if(waitingCustomers.get(i).getTable().getNumber() == (t.getNumber())) {
	                                print("going to tell the cust that we are out of this item");
	                                waitingCustomers.get(i).event =CustomerEvent.outoffood;
	                                //my.event = CustomerEvent.none;
	                                //ws = WaiterState.OnBreak;
	                                stateChanged();
	                        }
	                }

	        }
	        public void msgreadyforbill(String choice, Table t) {
	                for(int i = 0; i < waitingCustomers.size(); i++ ) {
	                        if(waitingCustomers.get(i).getTable().getNumber() == (t.getNumber())) {
	                                print("Received that " + waitingCustomers.get(i).getCustomer().getName() + " is ready for bill");

	                                //check = new Check(cost);
	                                waitingCustomers.get(i).event = CustomerEvent.cangetbill;



	                                stateChanged();
	                        }
	                }

	        }

	        public void msgComputedBill(double cost, Table t){

	                for(int i = 0; i < waitingCustomers.size(); i++ ) {
	                        if(waitingCustomers.get(i).getTable().getNumber() == (t.getNumber())) {
	                                print("bill ready for " + waitingCustomers.get(i).getCustomer().getName());

	                                check = new McNealCheck(cost, waitingCustomers.get(i).getCustomer());
	                                //check.setCheckCustomer( waitingCustomers.get(i).getCustomer());
	                                waitingCustomers.get(i).event = CustomerEvent.billready;



	                                stateChanged();
	                        }
	                }


	        }
	        public void msgCantAffordAndLeave(Table t) {
	                Do("Customer Cant Afford Anything and Leaving");
	                for(int i = 0; i < waitingCustomers.size(); i++ ) {
	                        if(waitingCustomers.get(i).getTable().getNumber() == (t.getNumber())) {
	                                waitingCustomers.get(i).event = CustomerEvent.doneLeaving;
	                                stateChanged();
	                        }
	                }

	        }

	    	@Override
	    	public void msgStopWorkingGoHome() {
	    		// TODO Auto-generated method stub
	    		
	    	}

	    	@Override
	    	public void msgHeresYourPaycheck(float paycheck) {
	    		person.ChangeMoney(paycheck);
	    		paycheckhere = true;
	    		
	    	}

	    	@Override
	    	public boolean hasReceivedPaycheck() {
	    		// TODO Auto-generated method stub
	    		return paycheckhere;
	    	}
	            
	        //Actions
	        public void seatCustomer(myCustomer customer) {
	                this.setwithCustomer(true);
	                Do("Seating Customer");
	                customer.getCustomer().msgFollowMe(customer.getTable(), this, (new Menu()));

	                /*waiterGui.DoBringToTable(customer.getTable());
	                try {
	                        atTable.acquire();
	                } catch (InterruptedException e) {
	                        e.printStackTrace();
	                }
	                */customer.getTable().setOccupant(customer.getCustomer());
	                //waiterGui.DoLeaveCustomer();
	                
	                this.setwithCustomer(false);
	        }
	        public void takeOrder(myCustomer c) {
	                //waiterGui.DoLeaveCustomer();

	                /*waiterGui.DoBringToTable(c.getTable());

	                try {
	                        atTable.acquire();
	                } catch (InterruptedException e) {
	                        e.printStackTrace();
	                } */c.getCustomer().msgWhatWouldYouLike();
						
	                //waiterGui.DoLeaveCustomer();

	        }



	        public abstract void taketoCook(myCustomer c);
	        
	        public void serveFood(myCustomer c) {
	                
	                
	                
	                Do("serving food to " + c.getCustomer().getName() + " whos order is " + c.getCustomer().getFoodChoice());
	               /* waiterGui.DoGoToCook();
	                try {
	                        atCook.acquire();
	                } catch (InterruptedException e) {
	                        e.printStackTrace();
	                }
	                waiterGui.sethasOrder(true);
	                System.out.println(c.getTable().getNumber());
	                waiterGui.DoServeFood(c.getTable());

	                try {
	                        atTable.acquire();
	                } catch (InterruptedException e) {
	                        e.printStackTrace();
	                }*/ c.getCustomer().msgHereIsYourFood();
	                /*waiterGui.DoLeaveCustomer();

	                waiterGui.sethasOrder(false);
					*/
	        }

	        public void retakeOrder(myCustomer c) {
	               /* waiterGui.DoGoToCook();
	                try {
	                        atCook.acquire();
	                } catch (InterruptedException e) {
	                        e.printStackTrace();
	                } waiterGui.DoLeaveCook();
	                
	                waiterGui.DoBringToTable(c.getTable());*/
	                Menu g = new Menu();
	              /*  g.removeChoice(c.getStringChoice());try {
	                        atTable.acquire();
	                } catch (InterruptedException e) {
	                        e.printStackTrace();
	                }
	                */
	                c.getCustomer().msgWhatWouldYouLikeToo(g);
	                /*        for(int i = 0; i < m.getlistChoices().size(); i++){
	                        if(c.getCustomer().getFoodChoice().equals(m.getChoice())){
	                                m.getlistChoices().remove(i)

	                }
	                 c.getCustomer().msgWhatWouldYouLike();//
	                 */                

	        }

	        public void clearTable(Table table) {
	        		if(host == null) {
	        			print("host is null");
	        		}
	                host.msgTableFree(table, this);
	                
	                setwithCustomer(false);

	        }
	        public void askForBreak() { //(RestaurantPanel r) {
	                //this.r = r;
	                host.msgGoOnBreakPlease(); //(r);
	                WantToGoOnBreak = true;

	        }
	        public void goOnBreak(){
	                Do("going on break ");

	                /*timer.schedule(new TimerTask() {
	                        Object cookie = 1;
	                        public void run() {
	                        //        if(readyToGoOffBreak) {
	                                print( " Done breaking, cookie=" + cookie);
	                                //event = AgentEvent.doneEating;
	                                //isHungry = false;
	                                System.out.println("changin dem states");
	                                OnBreak = false;
	                                setOnBreak(false);
	                                canGoOnBreak = false;
	                                ws = WaiterState.FinishedBreak;
	                                stateChanged();
	                                }


	                },
	                10000);*/
	        }
	        public void offBreak() {
	                Do("Going off dat break doh");
	                host.msgOffBreak();
	        }
	        public void getBill(myCustomer a) {
	                Do("Will go to cashier to get bill");
	                waiterGui.DoGoToCashier();
	                cashier.msgBillForCustomer(a.getCustomer().getFoodChoice(), a.getCustomer());
	        }
	        public void giveCheck(myCustomer c) {
	                Do("Giving check to " + c.getCustomer().getName());
	              /*  waiterGui.DoBringToTable(c.getTable());
	                try{
	                        atTable.acquire();
	                }catch (InterruptedException e) {
	                        e.printStackTrace();
	                }*/
	                c.getCustomer().msgHereIsBill(check);
	               // waiterGui.DoLeaveCustomer();
	        }
	        public void leave() {
	        	Do("Leaving see ya later bye");
	        	person.msgLeavingLocation(this);
	        }
	        @Override
	        public boolean pickAndExecuteAnAction() {
	                //System.err.println("WAITER THREAD");
	        //        if(!paused){
	        				if(paycheckhere && waitingCustomers.isEmpty()) {
	        					paycheckhere = false;
	        					leave();
	        					return true;
	        				}
	                        if(canGoOnBreak) {
	                                if(waitingCustomers.size() == 0 ) {System.out.print(this.getName() + " canna goa ona a breaka");
	                                canGoOnBreak = false;
	                                goOnBreak();
	                                return true;
	                                }
	                        }
	                        if(this.readyToGoOffBreak) {        
	                                offBreak();
	                                ws = WaiterState.NotOnBreak;
	                                this.readyToGoOffBreak = false;
	                                return true;
	                        }

	                        if(ws == WaiterState.NotOnBreak){
	                                if(!waitingCustomers.isEmpty()){



	                                        for(int i = 0; i < waitingCustomers.size(); i++ ) {
	                                                if(waitingCustomers.get(i).state == CustomerState.WaitingInRestaurant && waitingCustomers.get(i).event == CustomerEvent.none ) {
	                                                        waitingCustomers.get(i).state = CustomerState.BeingSeated;
	                                                        Do("the person i will take to table: " + waitingCustomers.get(i).getCustomer().getName());
	                                                        seatCustomer(waitingCustomers.get(i) );
	                                                        return true;
	                                                }
	                                        }

	                                        for(int i = 0; i < waitingCustomers.size(); i++) {
	                                                if( waitingCustomers.get(i).state == CustomerState.BeingSeated && waitingCustomers.get(i).event == CustomerEvent.readytoorder) {
	                                                        waitingCustomers.get(i).state = CustomerState.Ordered;
	                                                        takeOrder(waitingCustomers.get(i));
	                                                        return true;

	                                                }
	                                        }
	                                        for(int i =0; i < waitingCustomers.size(); i++) {
	                                                if((waitingCustomers.get(i).state == CustomerState.Ordered && waitingCustomers.get(i).event == CustomerEvent.ordered) || (waitingCustomers.get(i).event == CustomerEvent.ordered && waitingCustomers.get(i).state == CustomerState.ReOrder)) {
	                                                        System.out.println(waitingCustomers.get(i).getCustomer().getName() + " orderd");
	                                                        waitingCustomers.get(i).state = CustomerState.WaitingforFood;
	                                                        taketoCook(waitingCustomers.get(i));
	                                                        return true;
	                                                }
	                                        }

	                                        for(int i = 0; i < waitingCustomers.size(); i++) {
	                                                System.out.println("state of " + waitingCustomers.get(i).getCustomer().getName() + " : " + waitingCustomers.get(i).state + " and event" + waitingCustomers.get(i).event);
	                                                if( waitingCustomers.get(i).state == CustomerState.WaitingforFood && waitingCustomers.get(i).event == CustomerEvent.outoffood) {
	                                                        System.out.println(waitingCustomers.get(i).getCustomer().getName() + " is waiting fit");
	                                                        waitingCustomers.get(i).state = CustomerState.ReOrder;
	                                                        retakeOrder(waitingCustomers.get(i));
	                                                        return true;
	                                                }
	                                        }


	                                        for(int i = 0; i < waitingCustomers.size(); i++) {
	                                               // System.out.println("state of " + waitingCustomers.get(i).getCustomer().getName() + " : " + waitingCustomers.get(i).state + " and event" + waitingCustomers.get(i).event);
	                                                if( waitingCustomers.get(i).state == CustomerState.WaitingforFood && waitingCustomers.get(i).event == CustomerEvent.beingserved) {
	                                                        System.out.println(waitingCustomers.get(i).getCustomer().getName() + " is waiting fit");
	                                                        waitingCustomers.get(i).state = CustomerState.Eating;
	                                                        serveFood(waitingCustomers.get(i));
	                                                        return true;
	                                                }
	                                        }

	                                        for(int i = 0; i < waitingCustomers.size(); i++) {

	                                                if(waitingCustomers.get(i).state == CustomerState.Eating && waitingCustomers.get(i).event == CustomerEvent.cangetbill) {
	                                                        waitingCustomers.get(i).state = CustomerState.DoneEating;
	                                                        getBill(waitingCustomers.get(i));

	                                                        return true;
	                                                }
	                                        }
	                                        for(int i = 0; i < waitingCustomers.size(); i++) {

	                                                if( waitingCustomers.get(i).state == CustomerState.DoneEating && waitingCustomers.get(i).event == CustomerEvent.billready) {
	                                                        System.out.println("bill is ready");
	                                                        waitingCustomers.get(i).state = CustomerState.ReceivedBill;
	                                                        giveCheck(waitingCustomers.get(i));

	                                                        return true;
	                                                }
	                                        }


	                                        for(int i = 0; i < waitingCustomers.size(); i++) {
	                                                if( waitingCustomers.get(i).event == CustomerEvent.doneLeaving ) {
	                                                        clearTable(waitingCustomers.get(i).getTable());
	                                                        waitingCustomers.get(i).state = CustomerState.DoingNothing;
	                                                        waitingCustomers.remove(waitingCustomers.get(i));

	                                                        return true;
	                                                }

	                                        }
	                                }

	                        }

	                //}
	                        
	                return false;


	        }
	        @Override
	        public int getXPos() {
	                // TODO Auto-generated method stub
	                return 0;
	        }

	}



	
	
