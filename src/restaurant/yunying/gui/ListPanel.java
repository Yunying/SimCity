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
public class ListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    List<JButton> list = new ArrayList<JButton>();
    public List<JCheckBox> CBlist = new ArrayList<JCheckBox>();
    private JButton addPersonB = new JButton("Add Customer");

    private RestaurantPanel restPanel;
    private String type;

    public JTextField nameTextField = new JTextField(10);
    
    
    static public JCheckBox nameCheckBox = new JCheckBox("Hungry?");
    

    
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        //add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addPersonB.addActionListener(this);
        add(nameTextField);
        add(nameCheckBox);
        add(addPersonB);

        nameCheckBox.setEnabled(false);
        
        Dimension TextFieldSize = new Dimension(300,10);
        
        KeyListener kListener = new KeyListener(){
        	public void keyPressed(KeyEvent e){
        		//nameCheckBox.setEnabled(true);
        	}
        	
        	public void keyReleased(KeyEvent e){
        	}
        	
        	public void keyTyped(KeyEvent e){
        		nameCheckBox.setEnabled(true);
        	}
        };
        
        nameTextField.setMaximumSize(TextFieldSize);
        
        nameTextField.addKeyListener(kListener);
        view.setLayout(new BoxLayout((Container)view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
            addPerson(nameTextField.getText());
        }
        else {
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
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
    public void addPerson(String name) {
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
            
            nameCheckBox.setEnabled(true);
            
            restPanel.addPerson(type, name);//puts customer on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            
            nameTextField.setText("");
            nameCheckBox.setSelected(false);
            nameCheckBox.setEnabled(false);
            
            validate();
        }
    }
}
