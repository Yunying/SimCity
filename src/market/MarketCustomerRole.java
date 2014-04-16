package market;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import market.MarketAgent.CustomerState;
import market.interfaces.MarketCustomer;
import market.interfaces.MarketEmployee;
import market.interfaces.MarketManager;
import global.PersonAgent;
import global.roles.Role;

public class MarketCustomerRole extends Role implements MarketCustomer{

	/** Data */
	//public enum CustomerState {pending, shopping, askedForOrder, ordered, hasItems, leaving};
	private CustomerState state;
	private Semaphore atShoppingArea = new Semaphore( 0, true );
	private Semaphore atManager = new Semaphore( 0, true );
	
	float bill;
	List<String> wantedItems;
	MarketManager manager;
	MarketEmployee employee;
	
	
	public MarketCustomerRole(){
		super();
		
		this.state = CustomerState.pending;
		bill = 0.0f;
		wantedItems = new ArrayList<String>();
		manager = null;
		employee = null;
	}

	/** Messages */
	public void msgGoToShoppingArea( MarketManager manager ){
		this.manager = manager;
		this.state = CustomerState.shopping;
		stateChanged();
	}
	
	public void msgWelcomeToMarket( MarketManager manager, float carBill ){
		this.bill = carBill;
		this.state = CustomerState.buyingCar;
		this.manager = manager;
		stateChanged();
	}
	
	public void msgWhatDoYouWant( MarketEmployee employee ) {
		this.employee = employee;
		this.state = CustomerState.askedForOrder;
		stateChanged();
	}

	public void msgHereAreYourItemsAndBill( MarketEmployee employee, List<String> items, float bill) {
		//TODO: for non-normative case, check if items returned is not items wanted
		this.state = CustomerState.hasItems;
		this.bill = bill;
		stateChanged();
	}
	
	public void msgHereIsYourChange( float change ) {
		( (PersonAgent) this.person ).ChangeMoney( -(this.bill) ); // change money to subtract bill
		this.state = CustomerState.leaving;
		stateChanged();
	}

	


	/** Scheduler */
	public boolean pickAndExecuteAnAction() {
		//rules pertaining to customer
		if( state == CustomerState.shopping ){
			GoToShoppingArea();
			return true;
		}
		if( state == CustomerState.buyingCar ){
			GoBuyCar();
			return true;
		}
		if( state == CustomerState.askedForOrder ){
			GiveEmployeeOrder();
			return true;
		}
		if( state == CustomerState.hasItems ){
			PayForItems();
			return true;
		}
		if( state == CustomerState.leaving ){
			LeaveMarket();
			return true;
		}
		return false;
	}
	
	/** Actions */
	
	private void GoToShoppingArea(){
		DoGoToShoppingArea();
		try{
			atShoppingArea.acquire();//make sure to release by gui
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void GiveEmployeeOrder(){
		state = CustomerState.ordered;
		employee.msgIWantTheseItems( this, wantedItems );
	}
	
	private void GoBuyCar(){
		DoGoToManager( manager );
		try{
			atManager.acquire();//make sure to release by gui
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		manager.msgIWantACar( this, this.person.getMoney() );
	}
	
	private void PayForItems(){
		DoGoToManager( manager );
		try{
			atManager.acquire();//make sure to release by gui
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		manager.msgReadyToPay( this, bill, this.person.getMoney() );
	}
	
	private void LeaveMarket(){
		this.state = CustomerState.notPresent;
		manager.msgLeavingMarket( this );
		this.person.msgLeavingLocation( this );
		DoLeaveMarket();
	}
	
	/** GUI Calls */
	private void DoGoToShoppingArea(){
		//put this in gui
		atShoppingArea.release();
	}
	
	private void DoGoToManager( MarketManager manager ){
		//put this in gui
		atManager.release();
	}
	
	private void DoLeaveMarket(){
		
	}
	
	/** Utilities */
}
