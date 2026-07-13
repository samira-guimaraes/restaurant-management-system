import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class MenuTest {

    private Menu menu;
    private MenuItem item;

    @BeforeEach
    void setUp() {
        menu = Menu.getInstance();  // Get the singleton instance of Menu
        item = new MenuItem("Pizza", 10.99);  // Create a test menu item
    }

    @Test
    void addItem_ShouldAddItemSuccessfully() {
        menu.addItem(item);
        assertNotNull(menu.getItem("Pizza"));  // Check if the item is added to the menu
        assertEquals(10.99, menu.getItem("Pizza").getPrice());  // Verify the price of the item
    }

    @Test
    void addItem_ShouldThrowException_WhenItemIsNull() {
        assertThrows(IllegalArgumentException.class, () -> menu.addItem(null), "Item cannot be null.");
    }

    @Test
    void getItem_ShouldReturnNull_WhenItemDoesNotExist() {
        MenuItem nonExistentItem = menu.getItem("NonExistent");
        assertNull(nonExistentItem);  // Check if null is returned when the item doesn't exist
    }

    @Test
    void getItems_ShouldReturnAllItemsInMenu() {
        menu.addItem(item);
        Map<String, MenuItem> items = menu.getItems();
        assertFalse(items.isEmpty());  // Ensure the menu is not empty
        assertTrue(items.containsKey("Pizza"));  // Check if the item is in the menu
    }

    @Test
    void menuManagement_ShouldAllowAddingItem() {
        // Simulate user input for menuManagement
        String simulatedInput = "1\nPizza\n10.99\n";  // Option 1 -> Add item -> Pizza -> 10.99
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));  // Simulate input for adding a menu item

        Employee testEmployee = new Employee(1, "Test", "test@example.com", "password", EmployeeRole.MANAGER);
        Inventory inventory = Inventory.getInstance();  // Use getInstance() for Inventory

        menu.menuManagement(testEmployee, inventory);  // Simulate menu management with valid employee

        assertTrue(menu.getItems().containsKey("Pizza"));  // Check if the item was added successfully
    }

    @Test
    void menuManagement_ShouldRejectWithoutPermission() {
        // Simulate user input for menuManagement
        String simulatedInput = "1\n";  // Option 1 -> Try to add item
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));  // Simulate input

        Employee testEmployee = new Employee(1, "Test", "test@example.com", "password", EmployeeRole.EMPLOYEE);  // No manager permission
        Inventory inventory = Inventory.getInstance();  // Use getInstance() for Inventory

        menu.menuManagement(testEmployee, inventory);  // Try to simulate menu management with no permission

        // The item should not be added, as the user does not have permission
        assertFalse(menu.getItems().containsKey("Pizza"));
    }

    @Test
    void addMenuItem_ShouldAddItemWithIngredients() {
        // Test adding a menu item with ingredients
        menu.addItem(item);

        Inventory inventory = Inventory.getInstance();  // Use getInstance() for Inventory
        inventory.addItem(new InventoryItem("Cheese", 100, 20));
        inventory.addItem(new InventoryItem("Tomato", 50, 10));

        // Simulate adding ingredients
        String simulatedInput = "Cheese\n2\nTomato\n3\ndone\n";  // Add ingredients: Cheese 2, Tomato 3, then done
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));  // Simulate input for adding ingredients

        MenuItem pizza = menu.getItem("Pizza");
        assertNotNull(pizza);
        assertEquals(2, pizza.getIngredients().size());  // Check that ingredients were added
    }
}
