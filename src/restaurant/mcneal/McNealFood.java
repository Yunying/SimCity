package restaurant.mcneal;

import gui.animation.base.GUI;

import java.awt.Graphics2D;
import java.util.ArrayList;



public class McNealFood implements GUI{
	private String item;
	private int choice;
	private int cookTime;
	private ArrayList<McNealFood> items = new ArrayList<McNealFood> ();	
		private boolean truth = false;
	private int capacity = 5;
	private int quantity;
	private int steakQuantity = 0;
	private int chickenQuantity = 5;
	private int pizzaQuantity = 5;
	private int saladquantity = 5;
	public  int grill1X = 55;
	public int grill1Y = 320;
	public int grill2X = 105;
	public int grill2Y = 320;
	public int grill3X = 155;
	public int grill3Y = 320;
	private double price;
	private int pos;
	public McNealFood(String item) {
		
		if(item == "ST") {
			this.item = item;
			choice = 1;
			cookTime = 3000;
			quantity = 2;
			price = 15.99;
		}
		if(item == "CH") {
			this.item = item;
			choice = 2;
			cookTime = 4000;
			quantity = 3;
			price = 10.99;
		}
		if(item == "PI") {
			this.item = item;
			choice = 3;
			cookTime = 5000;
			quantity = 4;
			price = 8.99;
		}
		if(item == "SAL") {
			this.item = item;
			choice = 4;
			cookTime = 5500;
			quantity = 1;
			price = 5.99;
		}
	
	}
	public void populateList() {
		//items.add(new Food("ST", 1, 5, 2500));
		//items.add(new Food("CH", 2, 5, 3000));
			//items.add(new Food("PI", 3, 5, 4500));
			//items.add(new Food("SAL",4, 5, 5000));
	}
	public String getStringChoice() {
		return item;
	}
	public void setFoodLocation(int pos) {
		this.pos = pos;
	}
	public int getFoodLocation() {
		return pos;
	}
	public boolean onDaGrill () {
		return truth;
	}
	public void setStringChoice(String it) {
		item = it;	
	}
	public int getIntChoice() {
		return choice;
	}

	public int getQuant(String item) {
		/*if(item == "ST") {
			quantity = steakQuantity;
		}
		if(item == "CH") {
			 quantity = chickenQuantity;
		}
		if(item == "PI") {
			quantity = pizzaQuantity;
		}
		if(item == "SAL") {
			quantity = saladquantity;
		}*/
		return quantity;
	}
	public void decreaseQuant(String item) {
		/*if(item == "ST") {
			steakQuantity--;
		}
		if(item == "CH") {
			 chickenQuantity--;
		}
		if(item == "PI") {
			pizzaQuantity--;
		}
		if(item == "SAL") {
		saladquantity--;
		}*/
		quantity--;	
		}
	public void increaseQuant(String item) {
		quantity += 2;
	}
	public void onDaGrill( boolean truth) {
		this.truth = truth;
	}

		
	
	

	public void setFood(String item) {
		this.item = item;
	}
	public String getFood() {
		return item;
	}
	public int getCookTime() {
		return cookTime;
	}
	/*public String generateFood(int choice) {
		if (choice == 1) {
			//if (choice == eliminate)
			item = "ST";
			cookTime = 2500;
			
		}
		if(choice == 2) {
			item = "CH";
			cookTime = 3000;

		}
		if (choice == 3) {
			item = "SAL";
			cookTime = 4000;
		}
		if(choice == 4) {
			item = "PI";
			cookTime = 5000;
		}
		return item;
	}*/

	public void updatePosition() {
		// TODO Auto-generated method stub
		
	}
	
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	public void drawfood(Graphics2D g2) { 
		if(truth) {
		g2.drawString(this.getFood(), this.getFoodLocation(), 320 );
		}
	}
	
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}
}

