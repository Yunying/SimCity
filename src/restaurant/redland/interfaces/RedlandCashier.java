package restaurant.redland.interfaces;

public interface RedlandCashier {
	public abstract void msgComputeBill( String food, RedlandWaiter w , RedlandCustomer c);
	
	//message from customer saying they are ready to pay
	public abstract void msgPayBill( RedlandCustomer customer, float cash );
}