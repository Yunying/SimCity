package restaurant.ji;

import restaurant.ji.interfaces.JiWaiter;

public class CustomerOrder {
	public enum OrderStatus {uncooked, cooked, cooking, plated};
    public String choice;
    public int tableNum;
    public OrderStatus status;
    public JiWaiter waiter;
    
    public CustomerOrder(String choice, JiWaiter waiter, int tableNum) {
        this.choice = choice;
        this.status = OrderStatus.uncooked;
        this.waiter = waiter;
        this.tableNum = tableNum;
    }

    
    /*******Accessors*********/
    
	public OrderStatus getStatus() { return status; }
	public void setStatus(OrderStatus status) {	this.status = status;}
	public String getChoice() {	return choice; }
	public void setChoice(String choice) { this.choice = choice; }
	public JiWaiter getWaiter() { return waiter; }
	public void setWaiter(JiWaiter waiter) { this.waiter = waiter; }
	public int getTableNum() { return tableNum; }
	public void setTableNum(int tableNum) {	this.tableNum = tableNum; }
}