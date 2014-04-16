package restaurant.ji;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Menu {
    
	private static  Map<String, Float> menu;
    
	public Menu() { 
		menu = new HashMap<String, Float>();
		menu.put("Steak", 15.99f);
		menu.put("Chicken", 10.99f);
		menu.put("Salad", 5.99f);
		menu.put("Pizza", 8.99f);
	}
	
	public int size(){	return menu.size();	}

	public Set<String> keySet() {
		return menu.keySet();
	}
	
	public boolean removeOption(String choice){
		if (menu.containsKey(choice))
		{
			menu.remove(choice);
			return true;
		}
		return false;
	}
	
	public static float getPrice(String choice){
		if (menu.containsKey(choice))
			return menu.get(choice);
		System.out.println(choice);
		return -1f;
	}
	
	public boolean contains(String foodname){
		return (menu.containsKey(foodname));
	}
		
}
