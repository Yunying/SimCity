package restaurant.mcneal;

import interfaces.Building;

import java.util.ArrayList;








import market.interfaces.Market;
import restaurant.mcneal.interfaces.McNealCustomer;

public class McNealCheck {
	double custamount;
	double markamount;
	McNealCustomer c;
	Building m;
	
		public McNealCheck(double cost, McNealCustomer c){
			this.custamount = cost;
			this.c = c;
		}
		
		public McNealCheck(double cost, Building m) {
			this.markamount = cost;
			this.m = m;
		}
		
		public double getCustAmount() {
			return custamount;
		}
		public double getMarketAmount() {
			return markamount;
		}
		public void setCheckCustomer(McNealCustomer c) {
			this.c= c;
		}
		public void setCheckMarket(Market m) {
			this.m = m;
		}
		
		public Building getMarket() {
			return m;
		}
		
		public McNealCustomer getCheckCustomer() {
			return c;
		}
		public void setCheckCustAmount(double amount) {
			custamount = amount;
		
		}
		public void setCheckMarkAmount(double amount){
			markamount = amount;
		}
	
}
