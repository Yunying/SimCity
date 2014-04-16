package restaurant.mcneal.interfaces;



import restaurant.mcneal.McNealCheck;
import restaurant.mcneal.roles.McNealHostRole.Table;






/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface McNealCustomer {
	
	
	/**
	 * @param total The cost according to the cashier
	 *
	 * Sent by the cashier prompting the customer's money after the customer has approached the cashier.
	 */
	//public abstract void HereIsYourTotal(double total);

	/**
	 * @param total change (if any) due to the customer
	 *
	 * Sent by the cashier to end the transaction between him and the customer. total will be >= 0 .
	 */
	public abstract void msgHereIsChange(double change); //HereIsYourChange(double total);


	/**
	 * @param remaining_cost how much money is owed
	 * Sent by the cashier if the customer does not pay enough for the bill (in lieu of sending {@link #HereIsYourChange(double)}
	 */
	public abstract void msgNotEnoughMoney();// YouOweUs(double remaining_cost);
	
	public abstract String getName();

	
	public abstract Table getTableNum();


	public abstract McNealWaiter getWaiter();

	public abstract void setWaiter(McNealWaiter w);
	public abstract String getFoodChoice();


//	public abstract Waiter getGui();


	public abstract void msgHereIsBill(McNealCheck check);


	public abstract void msgWhatWouldYouLike();


	public abstract void msgFollowMe(Table table, McNealWaiter waiterRole,
			restaurant.mcneal.roles.WaiterAgent.Menu menu);


	public abstract void msgHereIsYourFood();


	


	public abstract void msgTableFull();


	public abstract void msgMeetHost(McNealHost host);


	public abstract void msgWhatWouldYouLikeToo(restaurant.mcneal.roles.WaiterAgent.Menu g);


	public abstract void setHost(McNealHost host);


	


//	public abstract McNealCustomerGui getGui();
}