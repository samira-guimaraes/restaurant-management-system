/**
 * Represents an item in a customer's order, which includes a menu item, the quantity ordered,
 * and any special requests associated with the item.
 * This class is used to manage individual items within an order.
 */
public class OrderItem {
    private final MenuItem menuItem;
    private int quantity;
    private String specialRequests;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return this.menuItem;
    }
    public int getQuantity() {
        return this.quantity;
    }

}