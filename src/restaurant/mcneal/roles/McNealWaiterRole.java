package restaurant.mcneal.roles;

import restaurant.mcneal.interfaces.McNealWaiter;
import interfaces.Building;






//import restaurant.mcneal.gui.RestaurantPanel;





public class McNealWaiterRole extends WaiterAgent {
        
	public McNealWaiterRole() {
		super();
	}

    public void taketoCook(myCustomer cust) {
            Do ("Your order will be ready soon. Taking to cook");
           // waiterGui.DoLeaveCustomer();
            //waiterGui.DoGoToCook();
            //try {
              //      atCook.acquire();
            //} catch (InterruptedException e) {
              //      e.printStackTrace();
            //revolver.add(cust.getMyWaiter(), cust.getStringChoice(), cust.getTable());
            //} waiterGui.DoLeaveCook();
            System.out.println(cust.getCustomer().getFoodChoice());
            System.out.println("the choice was " + cust.getCustomer().getFoodChoice());
            System.out.println(cust.getTable());
            if( this.getCook()== null) { 
            	print("c is null fool");
            }
            this.getCook().msgHereisAnOrder(this, cust.getCustomer().getFoodChoice(), cust.getTable());
                          
                    

            
    
    }

	@Override
	public void msgAtBuilding(Building building) {
		// TODO Auto-generated method stub
		
	}
   

}