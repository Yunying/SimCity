package market;

import interfaces.Building;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant.cammarano.CammaranoRestaurantAgent;
import restaurant.ji.JiRestaurantAgent;
import restaurant.mcneal.McNealRestaurantAgent;
import restaurant.redland.RedlandRestaurantAgent;
import restaurant.yunying.Order.OrderState;
import restaurant.yunying.TuRestaurantAgent;
import market.MarketAgent.TruckDriverState;
import market.RestaurantOrder.orderState;
import market.interfaces.Market;
import market.interfaces.MarketManager;
import market.MarketAgent;
import market.interfaces.TruckDriver;
import global.roles.Role;

public class TruckDriverRole extends Role implements TruckDriver {

	/** Data */
	Market market;
	MarketManager manager;
	Semaphore atRestaurant = new Semaphore( 0, true );
	Semaphore atMarket = new Semaphore( 0, true );
	List<RestaurantOrder> orders;
	boolean isMarketOpen;
	//public enum TruckDriverState {pending, waiting, enRoute, shiftOver, notPresent}
	TruckDriverState state;
	
	
	public TruckDriverRole(){
		super();
		
		//
		isMarketOpen = false;
		orders =  new ArrayList<RestaurantOrder>();
		this.state = TruckDriverState.pending;
	}
	
	
	
	/** Messages */
	public void msgBeginWork( Market market, MarketManager manager ){
		isMarketOpen = true;
		this.market = market;
		this.manager = manager;
		this.state = TruckDriverState.waiting;
		stateChanged();
	}
	
	public void msgGoToWaitingPosition(){
		this.state = TruckDriverState.waiting;
		stateChanged();
	}
	
	public void msgDeliverOrder( RestaurantOrder o ) {
		orders.add( o );
		stateChanged();
	}

	public void msgMarketIsClosing() {
		isMarketOpen = false;
		stateChanged();
	}

	public void msgHereIsBill( float bill, Building restaurant ){
		for( RestaurantOrder order : orders ){
			if( order.getRestaurant() == restaurant ){
				order.setMoneyGiven( bill );
				order.setState( RestaurantOrder.orderState.delivered );
			}
		}
		stateChanged();
	}
	
	
	
	
	/** Scheduler */
	public boolean pickAndExecuteAnAction() {
		// Rules pertaining to market
		if( !isMarketOpen && state == TruckDriverState.waiting ){
			LeaveMarket();
			return true;
		}
		
		
		// Rules pertaining to orders
		for( RestaurantOrder order : orders ){
			if( order.getState() == orderState.givenToDriver ){
				DeliverOrder( order );
				return true;
			}
		}
		for( RestaurantOrder order : orders ){
			if( order.getState() == orderState.delivered ){
				ReturnToMarket( order );
				return true;
			}
		}
		return false;
	}
	
	
	
	/** Actions */
	private void LeaveMarket(){
		state = TruckDriverState.shiftOver;
		manager.msgLeavingMarket( this );
		this.person.msgLeavingLocation( this );//set role inactive
		DoLeaveMarket();//animation call
	}
	
	
	private void DeliverOrder( RestaurantOrder order ){
		this.state = TruckDriverState.enRoute;
		order.setState( orderState.enroute );
		DoDeliverOrder( order.getRestaurant() );
		try{
			atRestaurant.acquire();//make sure to release by gui
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//Message the restaurant that the order is delivered
		//msgOrderDelivered(Map<String, Integer> order, Building market, float bill)
		
		//determine which restaurant to send to
		if( order.getRestaurant() instanceof CammaranoRestaurantAgent ){
			( (CammaranoRestaurantAgent) order.getRestaurant() ).msgOrderDelivered( order.getOrder(), market, this, order.getBill() );
		}
		if( order.getRestaurant() instanceof RedlandRestaurantAgent ){
			( (RedlandRestaurantAgent) order.getRestaurant() ).msgOrderDelivered( order.getOrder(), market, this, order.getBill() );
		}
		if( order.getRestaurant() instanceof JiRestaurantAgent ){
			( (JiRestaurantAgent) order.getRestaurant() ).msgOrderDelivered( order.getOrder(), market, this, order.getBill() );
		}
		if( order.getRestaurant() instanceof McNealRestaurantAgent ){
			( (McNealRestaurantAgent) order.getRestaurant() ).msgOrderDelivered( order.getOrder(), market, this, order.getBill() );
		}
		if( order.getRestaurant() instanceof TuRestaurantAgent ){
			( (TuRestaurantAgent) order.getRestaurant() ).msgOrderDelivered( order.getOrder(), market, this, order.getBill() );
		}
	}
	
	private void ReturnToMarket( RestaurantOrder order){
		DoReturnToMarket( market );
		try{
			atMarket.acquire();//make sure to release by gui
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		market.msgOrderDelivered( this, order );
		orders.remove( order );
	}
	
	
	/** GUI Calls */
	private void DoDeliverOrder( Building restaurant ){
		
	}
	
	private void DoReturnToMarket( Building market ){
		
	}
	
	private void DoLeaveMarket(){
		
	}
	
	/** Utilities */
	
	public Market getMarket(){
		return this.market;
	}
	
	public void setMarket( Market market ){
		this.market = market;
	}
}
