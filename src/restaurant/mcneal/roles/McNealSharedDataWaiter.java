package restaurant.mcneal.roles;

import interfaces.Building;



public class McNealSharedDataWaiter extends WaiterAgent {
	
	
	public McNealSharedDataWaiter() {
		super();
	}
	
    public void taketoCook(myCustomer c) {
        Do("Your order will be ready soon. Taking to cook");
       // waiterGui.DoLeaveCustomer();
        //waiterGui.DoGoToCook();
        //try {
          //      atCook.acquire();
        //} catch (InterruptedException e) {
          //      e.printStackTrace();
        //} waiterGui.DoLeaveCook();
        revolver.add(c.getmyCustomer().getCustomer().getWaiter(), c.getStringChoice(), c.getTable());
        for(int i = 0; i < super.getWaitingCustomers().size(); i++) {
                if(super.waitingCustomers.get(i).getTable().getNumber() == c.getTable().getNumber()) {
                       // c.msgHereisAnOrder(this, super.waitingCustomers.get(i).getStringChoice(), super.waitingCustomers.get(i).getTable());
                        Do(" added to revolver " + super.waitingCustomers.get(i).getStringChoice());
                }

        }
}

	@Override
	public void msgAtBuilding(Building building) {
		// TODO Auto-generated method stub
		
	}

}
