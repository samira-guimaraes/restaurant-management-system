import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the InventoryItem class.
 */
public class InventoryItemTest {

    private InventoryItem item;

    @BeforeEach
    void setUp() {
        // Create a new InventoryItem for testing
        item = new InventoryItem("Tomato", 100, 20);
    }

    @Test
    void constructor_ShouldCreateItemWithValidData() {
        assertEquals("Tomato", item.getName());
        assertEquals(100, item.getQuantity());
        assertEquals(20, item.getMinStock());
    }

    @Test
    void constructor_ShouldThrowException_WhenNameIsEmpty() {
        // Test with empty name
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("", 100, 20), "Item name cannot be empty.");
    }

    @Test
    void constructor_ShouldThrowException_WhenInitialQuantityIsNegative() {
        // Test with negative initial quantity
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("Cheese", -10, 5), "Initial quantity cannot be negative.");
    }

    @Test
    void constructor_ShouldThrowException_WhenMinStockIsNegative() {
        // Test with negative minimum stock
        assertThrows(IllegalArgumentException.class, () -> new InventoryItem("Flour", 50, -5), "Minimum stock cannot be negative.");
    }

    @Test
    void setName_ShouldUpdateNameSuccessfully() {
        item.setName("Lettuce");
        assertEquals("Lettuce", item.getName());
    }

    @Test
    void setName_ShouldThrowException_WhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> item.setName(""), "Item name cannot be empty.");
    }

    @Test
    void setQuantity_ShouldUpdateQuantitySuccessfully() {
        item.setQuantity(150);
        assertEquals(150, item.getQuantity());
    }

    @Test
    void setQuantity_ShouldThrowException_WhenQuantityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> item.setQuantity(-50), "Quantity cannot be negative.");
    }

    @Test
    void setMinStock_ShouldUpdateMinStockSuccessfully() {
        item.setMinStock(10);
        assertEquals(10, item.getMinStock());
    }

    @Test
    void setMinStock_ShouldThrowException_WhenMinStockIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> item.setMinStock(-5), "Minimum stock cannot be negative.");
    }
}
