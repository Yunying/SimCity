package restaurant.yunying.roles;

import interfaces.Person;

public class TuShareDataWaiterRole extends TuWaiterAgent {

	
	public TuShareDataWaiterRole() {
		super();
	}
	
	public void setPerson(Person p){
		super.setPerson(p);
		this.name = p.getName();
	}

	@Override
	protected void takeOrderToCook(MyCustomer c) {
		// TODO Auto-generated method stub
		c.state = CustomerState.waitingForFood;
		orderStand.add(this, c.hisChoice,c.table);
		waiterState = WaiterState.available;
		stateChanged();
	}

	public OrderStand getOrderStand() {
		// TODO Auto-generated method stub
		return orderStand;
	}
	
}