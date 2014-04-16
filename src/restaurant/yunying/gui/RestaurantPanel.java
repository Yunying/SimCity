package restaurant.yunying.gui;

import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import restaurant.yunying.roles.*;
import global.roles.Role;

import javax.swing.*;

import market.MarketAgent;
import market.interfaces.Market;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
    TuHostRole host = new TuHostRole();
    private HostGui hostGui = new HostGui(host);
    private TuCookRole cook = new TuCookRole();
    private CookGui cookGui = new CookGui(cook);
    private TuCashierRole cashier = new TuCashierRole();
    private CashierGui cashierGui = new CashierGui(cashier);

    private Vector<TuCustomerRole> customers = new Vector<TuCustomerRole>();
    private Vector<TuWaiterRole> waiters = new Vector<TuWaiterRole>();
    private Vector<Market> markets = new Vector<Market>();

    private JPanel restLabel = new JPanel();
    //private ListPanel customerPanel = new ListPanel(this, "Customers");
    private JPanel group = new JPanel();
    private boolean paused = false;

    private RestaurantGui gui; 

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        //host.setGui(hostGui);
        //waiter.setGui(TuWaiterGUI);
        cook.setGui(cookGui);
        cook.setCashier(cashier);
        cashier.setGui(cashierGui);

        gui.animationPanel.addGui(hostGui);
        gui.animationPanel.addGui(cookGui);
        gui.animationPanel.addGui(cashierGui);
//        host.startThread();
//        cook.startThread();
//        cashier.startThread();

        setLayout(new GridLayout(1, 2,10,10));
        initRestLabel();
        add(restLabel);
        
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("                  "), BorderLayout.EAST);
        restLabel.add(new JLabel("                  "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                TuCustomerRole temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        else if (type.equals("Waiters")){
        	for (int i=0; i<waiters.size();i++){
        		TuWaiterRole tmpWaiter = waiters.get(i);
        		if (tmpWaiter.getName() == name){
        			gui.updateInfoPanel(tmpWaiter);
        		}
        	}
        }
        
        else if (type.equals("Markets")){
        	for (int i=0; i<markets.size();i++){
        		Market tmpMarket = markets.get(i);
        		if (tmpMarket.getName() == name){
        			gui.updateInfoPanel(tmpMarket);
        		}
        	}
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		TuCustomerRole c = new TuCustomerRole();	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);
    		c.setHost(host);
    		c.setGui(g);
    		c.setCashier(cashier);
    		if (ListPanel.nameCheckBox.isSelected()){
    			c.getGui().setHungry();
    			
    		}
    		customers.add(c);
    		//c.startThread();
    	}
    }
    
    public void addWaiter(String name){
    	TuWaiterRole w = new TuWaiterRole();
    	waiterGui wg = new waiterGui(w, gui, waiters.size());
    	System.out.println(waiters.size());
    	gui.animationPanel.addGui(wg);
    	w.setCook(cook);
    	w.setHost(host);
    	w.setGui(wg);
    	w.setCashier(cashier);
    	host.addWaiter(w);
    	
    	waiters.add(w);
    	//w.startThread();
    }
    
    public void addMarket(String name){
    	Market m = new MarketAgent(name, "Tu");
    	//m.setCook(cook);
    	cook.addMarket(m);
    	markets.add(m);
    	//m.startThread();
    }
    
//    public void pauseAgents(){
//    	System.out.println("Paused");
//    	if (!paused){
//    		host.pauseAgent();
//    		cook.pauseAgent();	
//    		cashier.pauseAgent();
//    		for (TuCustomerRole c:customers){
//    			c.pauseAgent();
//    		}
//    		for (TuWaiterRole w:waiters){
//    			w.pauseAgent();
//    		}
//    		for (TuMarketRole m:markets){
//    			m.pauseAgent();
//    		}
//    		for (Gui g: gui.animationPanel.guis){
//    			g.setPaused(true);
//    		}
//    		
//    		paused = true;
//    	}
//    
//    	else{
//    		host.resumeAgent();
//    		cook.resumeAgent();
//    		cashier.resumeAgent();
//    		for (TuCustomerRole c:customers){
//    			c.resumeAgent();
//    		}
//    		for (TuWaiterRole w:waiters){
//    			w.resumeAgent();
//    		}
//    		for (TuMarketRole m:markets){
//    			m.resumeAgent();
//    		}
//    		for (Gui g: gui.animationPanel.guis){
//    			g.setPaused(false);
//    		}
//    		paused = false;
//    	}
//    }

}
