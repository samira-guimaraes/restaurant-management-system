import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Inventory class.
 */
public class InventoryTest {

    private Inventory inventory;
    private InventoryItem item;

    @BeforeEach
    void setUp() {
        inventory = Inventory.getInstance();  // Get the singleton instance of Inventory
        item = new InventoryItem("Tomato", 100, 20); // Create a test inventory item
        inventory.clearItemsForTest(); // Clear existing items before each test
    }

    @Test
    void addItem_ShouldAddItemSuccessfully() {
        inventory.addItem(item);
        assertNotNull(inventory.getItem("Tomato"));  // Check if the item has been added to inventory
        assertEquals(100, inventory.getItem("Tomato").getQuantity());  // Verify the quantity
    }

    @Test
    void addItem_ShouldThrowException_WhenItemIsNull() {
        assertThrows(IllegalArgumentException.class, () -> inventory.addItem(null), "Item cannot be null.");
    }

    @Test
    void addItem_ShouldThrowException_WhenItemAlreadyExists() {
        inventory.addItem(item);
        assertThrows(IllegalArgumentException.class, () -> inventory.addItem(item), "Item 'Tomato' already exists in inventory.");
    }

    @Test
    void addItem_ShouldThrowException_WhenQuantityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("Cheese", -10, 5), "Quantity cannot be negative.");
    }

    @Test
    void addItem_ShouldThrowException_WhenMinStockIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("Flour", 20, -5), "Minimum stock cannot be negative.");
    }

    @Test
    void updateStock_ShouldUpdateStockSuccessfully() {
        inventory.addItem(item);
        item.setQuantity(150);  // Update the quantity using setQuantity method
        assertEquals(150, inventory.getItem("Tomato").getQuantity());  // Verify the updated quantity
    }

    @Test
    void updateStock_ShouldThrowException_WhenStockIsNegative() {
        inventory.addItem(item);
        assertThrows(IllegalArgumentException.class, () -> item.setQuantity(-50), "Quantity can't go below zero.");
    }

    @Test
    void setMinStock_ShouldUpdateMinStockSuccessfully() {
        inventory.addItem(item);
        item.setMinStock(15);  // Set new minimum stock for the item
        assertEquals(15, inventory.getItem("Tomato").getMinStock());  // Verify if the minimum stock has been updated correctly
    }

    @Test
    void setMinStock_ShouldThrowException_WhenMinStockIsNegative() {
        inventory.addItem(item);
        assertThrows(IllegalArgumentException.class, () -> item.setMinStock(-5), "Minimum stock cannot be negative.");
    }

    @Test
    void hasEnoughItems_ShouldReturnTrue_WhenStockIsSufficient() {
        inventory.addItem(item);
        Map<InventoryItem, Double> requiredItems = new HashMap<>();
        requiredItems.put(item, 50.0);
        assertTrue(inventory.hasEnoughItems(requiredItems), "There should be enough items in stock.");
    }

    @Test
    void hasEnoughItems_ShouldReturnFalse_WhenStockIsInsufficient() {
        inventory.addItem(item);
        Map<InventoryItem, Double> requiredItems = new HashMap<>();
        requiredItems.put(item, 150.0);  // Request more items than available
        assertFalse(inventory.hasEnoughItems(requiredItems), "There should not be enough items in stock.");
    }

    @Test
    void decreaseStock_ShouldReduceStockSuccessfully() {
        inventory.addItem(item);
        Map<InventoryItem, Double> itemsToDecrease = new HashMap<>();
        itemsToDecrease.put(item, 50.0);
        inventory.decreaseStock(itemsToDecrease);  // Reduce stock by 50
        assertEquals(50, inventory.getItem("Tomato").getQuantity());  // Verify stock is reduced
    }

    @Test
    void decreaseStock_ShouldThrowException_WhenNotEnoughStock() {
        inventory.addItem(item);
        Map<InventoryItem, Double> itemsToDecrease = new HashMap<>();
        itemsToDecrease.put(item, 150.0);  // Try to reduce more than available
        assertThrows(IllegalStateException.class, () -> inventory.decreaseStock(itemsToDecrease), "Not enough stock for Tomato.");
    }

    @Test
    void decreaseStock_ShouldThrowException_WhenInvalidQuantity() {
        inventory.addItem(item);
        Map<InventoryItem, Double> itemsToDecrease = new HashMap<>();
        itemsToDecrease.put(item, -10.0);  // Try to decrease with negative quantity
        assertThrows(IllegalArgumentException.class, () -> inventory.decreaseStock(itemsToDecrease), "Invalid quantity for Tomato: -10.0 (must be positive)");
    }
}
