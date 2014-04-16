package housing;

import global.roles.Role;
import housing.interfaces.House;
import housing.interfaces.HousePerson;
import housing.interfaces.Landlord;

import java.util.ArrayList;
import java.util.List;

public class LandlordRole extends Role implements Landlord{
	public List<House> apartmentlist = new ArrayList<House>();//This should be called in the constructor adding an apt to the lanlords list
	public List<House> rentersWhoHaventBeenBilled =new ArrayList<House>();
	int rentrate = 30;
	boolean checkall = false;

	public void setRentersWhoHaventBeenBilled() {
		
		for(int i = 0; i < apartmentlist.size(); i++) {
			rentersWhoHaventBeenBilled.add(apartmentlist.get(i));
		}
	}


	//msgs
	public void msgHereIsRent(HousePerson h, float money){

		this.getPerson().ChangeMoney(money);
		if(money == h.getRentRate()) {
			h.setRent(rentrate);
			
		}
		if(money < h.getRentRate()) {
			float increasedrent = (float) (rentrate + ( rentrate- money));
			
			h.setRent((int) increasedrent);
			stateChanged();
		}

	}
	public boolean pickAndExecuteAnAction() {

		if((!apartmentlist.isEmpty()) && (!rentersWhoHaventBeenBilled.isEmpty())) {

			CollectRent(rentersWhoHaventBeenBilled.get(0));
			return true;
		}
return false;

	}


	//actions

	private void CollectRent(House h){

		h.msgRentDue(this,rentrate);
		this.rentersWhoHaventBeenBilled.remove(rentersWhoHaventBeenBilled.get(0));
	}


}









