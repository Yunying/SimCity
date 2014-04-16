package restaurant.redland.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import restaurant.redland.interfaces.*;
import restaurant.redland.UtilityClasses.*;
import global.roles.Role;

public class RedlandCashierRole extends Role implements RedlandCashier{
	
	/********** Data *********/
	public List<CustomerBill> customerBills = Collections.synchronizedList( new ArrayList<CustomerBill>() );
	public List<MarketBill> marketBills = new ArrayList<MarketBill>();
	private Map<String, Float> foods = new HashMap<String, Float>();//map linking food choice and price for customer order
	
	private enum billState {pending, computed, needsPaid, done}

	//public CashierGui cashierGui = null;
	String name;
	float totalCash;
	private RedlandCook cook;//Change to cook list when we implement multiple cooks
	
	public RedlandCashierRole() {
		super();
		//this.name = name;
		float initialCash = 100;
		totalCash = initialCash;
		//construct <foods, price> map for customer order
			foods.put("Steak", 15.99f);
			foods.put("Chicken", 10.99f);
			foods.put("Salad", 5.99f);
			foods.put("Pizza", 8.99f);
		//construct <ingredients, price> map for market order
	}

	
	
	
	
	/********** Messages **********/
	
	//message from waiter asking to compute bill price
	public void msgComputeBill( String food, RedlandWaiter w , RedlandCustomer c){
		synchronized( customerBills ){
			customerBills.add( new CustomerBill( w, c, foods.get(food) ) );
		}
		stateChanged();
	}
	
	//message from customer saying they are ready to pay
	public void msgPayBill( RedlandCustomer customer, float cash ){
		synchronized( customerBills ){
			for ( CustomerBill bill : customerBills ){
				if ( bill.customer == customer ){
					bill.cashPaid = cash;
					bill.s = billState.needsPaid;
					stateChanged();
				}
			}
		}
	}
	
	
	
	
	/********** Scheduler **********/
	
	public boolean pickAndExecuteAnAction() {
		synchronized( customerBills ){
			//compute pending bills
			for (CustomerBill bill : customerBills ){
				if ( bill.s == billState.pending ){
					bill.s = billState.computed;
					ComputeBill( bill );
					return true;
				}
			}
			//if customer asks to pay bill take his money and give change
			for (CustomerBill bill : customerBills ){
				if ( bill.s == billState.needsPaid ){
					bill.s = billState.done;
					PayBill( bill );
					return true;
				}
			}
		}
			
		//Non-Normative Scenario
		return false;
	}

	/********** Actions **********/
	
	private void ComputeBill( CustomerBill bill ){
		//animation?
		bill.waiter.msgHereIsBill( this );
	}
	
	//customer pays bill, gets their change, then bill is removed
	private void PayBill( CustomerBill bill ){
		float change = bill.cashPaid - bill.bill;//what if customer does not have enought money???
		bill.customer.msgHereIsYourChange( change );
		synchronized( customerBills ){
			customerBills.remove( bill );
		}
	}
	/**Non-normative actions*/



	
	/********** GUI Calls ***********/

	private void DoGoToCustomer() {
	}
	
	
	
	
	
	/********** Utilities **********/
	
	/*
	public void setHost(Host host){
		this.host = host;
	}
	*/
	
	public void setCook( RedlandCook cook){
		//TODO: change this to addCook(...) when we implement multiple cooks
		this.cook = cook;
	}
	
	public String getName() {
		return name;
	}
	/*
	public void setGui(CashierGui gui) {
		cashierGui = gui;
	}

	public CashierGui getGui() {
		return cashierGui;
	}
	*/
	
	public class CustomerBill{
		//should customer be able to add to bill?
		RedlandWaiter waiter;
		RedlandCustomer customer;
		float bill = 0;
		float cashPaid = 0;
		billState s;
	
		public CustomerBill( RedlandWaiter w, RedlandCustomer c, float bill ){
			this.s = billState.pending;
			this.customer = c;
			this.waiter = w;
			this.bill = bill;
		}
	}
	
	public class MarketBill{
		RedlandCook cook;// is this needed?
		float bill = 0;
		billState s;
	
		public MarketBill( RedlandCook c, float bill ){
			this.s = billState.pending;
			this.cook = c;
			this.bill = bill;
		}
		
		public RedlandCook getCook(){
			return cook;
		}
		
		public void setCook( RedlandCook cook ){
			this.cook = cook;
		}
		
		public float getBill(){
			return bill;
		}
		
		public void addToBill( float price ){
			bill += price;
		}
	}
}
