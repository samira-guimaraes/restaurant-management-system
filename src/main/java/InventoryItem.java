/**
 * Represents an item in stock, containing name, current quantity and the desired minimum level.
 * This class helps monitor and manage the application's stock control.
 */
public class InventoryItem {
    private String name;
    private double quantity;
    private double minStock;

    /**
     * Creates a new inventory item with a defined name, initial quantity, and minimum stock.
     *
     * @param name the name of the item
     * @param initialQuantity the initial quantity available in inventory
     * @param minStock the minimum desired value in inventory for this item
     */
    public InventoryItem(String name, double initialQuantity, double minStock) {
        if (isInvalidName(name)) {
            throw new IllegalArgumentException("Item name cannot be empty.");
        }
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Initial quantity cannot be negative.");
        }
        if (minStock < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative.");
        }
        this.name = name;
        this.quantity = initialQuantity;
        this.minStock = minStock;
    }

    /**
     * Returns the name of the item.
     *
     * @return the name of the inventory item
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the item.
     *
     * @param name the new name
     */
    public void setName(String name) {
        if (isInvalidName(name)) {
            throw new IllegalArgumentException("Item name cannot be empty.");
        }
        this.name = name;
    }

    /**
     * Returns the current quantity in stock.
     *
     * @return the available quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Updates the quantity in stock.
     *
     * @param quantity new quantity
     */
    public void setQuantity(double quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    /**
     * Returns the minimum stock value set for this item.
     *
     * @return the minimum stock
     */
    public double getMinStock() {
        return minStock;
    }

    /**
     * Sets a new minimum stock value for the item.
     *
     * @param minStock new minimum value
     */
    public void setMinStock(double minStock) {
        if (minStock < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative.");
        }
        this.minStock = minStock;
    }

    // Helper method to check if the name is invalid
    private boolean isInvalidName(String name) {
        return name == null || name.trim().isEmpty();
    }
}
