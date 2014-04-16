package restaurant.yunying.gui;

import restaurant.yunying.interfaces.*;
import restaurant.yunying.gui.*;
import restaurant.yunying.test.*;
import global.roles.Role;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class MarketPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    List<JButton> list = new ArrayList<JButton>();
    private JButton addMarket = new JButton("AddMarket");

    private RestaurantGui restGui = null;
    private RestaurantPanel restP = null;
    private String type;

    public JTextField nameTextField = new JTextField(10);
    


    
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public MarketPanel(RestaurantGui gui, RestaurantPanel restPanel, String type) {
        restGui = gui;
        restP = restPanel;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        //add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addMarket.addActionListener(this);
        add(nameTextField);
        add(addMarket);
        
        Dimension TextFieldSize = new Dimension(300,10);
        
        nameTextField.setMaximumSize(TextFieldSize);

        view.setLayout(new BoxLayout((Container)view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addMarket) {
            addMarket(nameTextField.getText());
        }
        else {
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restP.showInfo(type, temp.getText());
            }
        }

    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addMarket(String name) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 0,
                   (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);

            restP.addMarket(name);//puts customer on list

            nameTextField.setText("");

            
            validate();
        }
    }
}
