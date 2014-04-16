package restaurant.cammarano.interfaces;

import market.TruckDriverRole;

public interface CammaranoCashier {
	public abstract void msgComputeBill(CammaranoWaiter w, CammaranoCustomer c, String choice);
	public abstract void msgCustomerPaying(CammaranoCustomer c, float check);
	public abstract void msgHereIsTheAmountWeOwe(TruckDriverRole t, float cost);
}
