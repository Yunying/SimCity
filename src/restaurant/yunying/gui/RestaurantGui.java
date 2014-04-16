package restaurant.yunying.gui;

import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;

import javax.swing.*;

import market.interfaces.Market;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	//JFrame animationFrame = new JFrame("Restaurant Animation");
	AnimationPanel animationPanel = new AnimationPanel();
	boolean paused = false;
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    private WaiterPanel waiterPanel = new WaiterPanel(this, restPanel, "Waiters");
    private ListPanel customerPanel = new ListPanel(restPanel, "Customers");
    private MarketPanel marketPanel = new MarketPanel(this,restPanel,"Markets");
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    //JCheckBox stateCB = ListPanel.nameCheckBox;
    private JPanel myPanel;
    private JLabel myLabel;
    
    private JPanel controlPanel = new JPanel();
    
    private JButton pauseButton = new JButton("Pause/Resume");
    
    
    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 600;
        int WINDOWY = 800;
        
        pauseButton.addActionListener(this);

    	setBounds(80,80,2*WINDOWX,2*WINDOWY);
    	controlPanel.setBounds(80, 80, WINDOWX, WINDOWY);
    	animationPanel.setBounds(80,80,WINDOWX,WINDOWY);
        controlPanel.setLayout(new GridLayout(3, 2));
        
        setLayout(new GridLayout(1,2));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .4));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        //restPanel.setMaximumSize(restDim);
        //restPanel.add(pauseButton);
        controlPanel.add(restPanel);
        restPanel.add(customerPanel);
        
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .1));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        //infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(2, 1));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers and waiters</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB); 
        Dimension myDim = new Dimension(WINDOWX, (int) (WINDOWY * .5));
        myPanel = new JPanel();
        myPanel.setPreferredSize(myDim);
        myPanel.setMinimumSize(myDim);
        //myPanel.setMaximumSize(myDim);
        myPanel.setBorder(BorderFactory.createTitledBorder("Waiter and Market"));
        myLabel = new JLabel(); 
        myPanel.setLayout(new GridLayout(1, 2,10,10));
        myPanel.add(waiterPanel);
        //myPanel.add(customerPanel);
        myPanel.add(marketPanel);
        controlPanel.add(myPanel);
        controlPanel.add(infoPanel);
        animationPanel.add(pauseButton);
//        
        add(controlPanel);
        add(animationPanel);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof Customer) {
            Customer customer = (Customer) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        
        else if (person instanceof Waiter){
        	Waiter waiter = (Waiter) person;
        	
        	if (!waiter.isOnBreak()){
        		stateCB.setText("Go On Break?");
        		stateCB.setSelected(false);
	        	if (restPanel.host.waiters.size() == 1 || waiter.isOnBreak()){
	        		stateCB.setEnabled(false);
	        	}
	        	else {stateCB.setEnabled(true);}
	        	infoLabel.setText("<html><pre>     Name: " + waiter.getName() + " </pre></html>");
        	}
        	else{
        		if (!waiter.isResting()){
        			stateCB.setText("Go On Break?");
        			stateCB.setSelected(true);
        			stateCB.setEnabled(false);
        		}
        		else{
        			stateCB.setText("Off Break?");
        			stateCB.setSelected(false);
        			stateCB.setEnabled(true);
        			
        		}
        	}
        }
        
        else if (person instanceof Market){
        	stateCB.setVisible(false);
        	Market market = (Market) person;
        	//infoLabel.setText(market.printInfo());
        }
        
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof Customer) {
                Customer c = (Customer) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
            else if (currentPerson instanceof Waiter){
            	System.out.println("State box check");
            	Waiter w = (Waiter) currentPerson;
            	if(stateCB.getText() == "Go On Break?"){
            		w.setOnBreak();
            		stateCB.setEnabled(false);
            	}
            	else{
            		w.offBreak();
            		stateCB.setEnabled(false);
            	}
            }
        }
        if (e.getSource() == pauseButton){
        		//restPanel.pauseAgents();
        		//paused = true; 	
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(Customer c) {
        if (currentPerson instanceof Customer) {
            Customer cust = (Customer) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    
    public void setWaiterEnabled(Waiter w){
    	if (currentPerson instanceof Waiter) {
    		Waiter wt = (Waiter) currentPerson;
    		if (w.equals(wt)){
    			if (waiterPanel.list.size() == 1){
            		stateCB.setEnabled(false);
            	}
            	else {stateCB.setEnabled(true);}
    			stateCB.setSelected(false);
    		}
    	}
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
