package restaurant.yunying;

public class Food{
	double price;
	String name;
	
	public Food(String n, double p){
		name = n;
		price = p;
	}
	
	
	public void setName(String s){
		name = s;
	}
	
	public String getName(){
		return name;
	}
	
	public void setPrice(double p){
		price = p;
	}
	
	public double getPrice(){
		return price;
	}
}
