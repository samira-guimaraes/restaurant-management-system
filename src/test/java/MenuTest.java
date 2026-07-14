import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Scanner;

public class MenuTest {

    private Menu menu;
    private MenuItem item;

    @BeforeEach
    void setUp() {
        menu = Menu.getInstance();
        menu.clearItemsForTest();
        Inventory.getInstance().clearItemsForTest();
        item = new MenuItem("Pizza", 10.99);
    }

    @Test
    void addItem_ShouldAddItemSuccessfully() {
        menu.addItem(item);
        assertNotNull(menu.getItem("Pizza"));
        assertEquals(10.99, menu.getItem("Pizza").getPrice());
    }

    @Test
    void addItem_ShouldThrowException_WhenItemIsNull() {
        assertThrows(IllegalArgumentException.class, () -> menu.addItem(null), "Item cannot be null.");
    }

    @Test
    void getItem_ShouldReturnNull_WhenItemDoesNotExist() {
        MenuItem nonExistentItem = menu.getItem("NonExistent");
        assertNull(nonExistentItem);
    }

    @Test
    void getItems_ShouldReturnAllItemsInMenu() {
        menu.addItem(item);
        Map<String, MenuItem> items = menu.getItems();
        assertFalse(items.isEmpty());
        assertTrue(items.containsKey("Pizza"));
    }

    @Test
    void menuManagement_ShouldAllowAddingItem() {
        String simulatedInput = "1\nPizza\n10.99\ndone\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Employee testEmployee = new Employee(1, "Test", "test@example.com", "password", EmployeeRole.MANAGER);
        Inventory inventory = Inventory.getInstance();

        menu.menuManagement(testEmployee, inventory);

        assertTrue(menu.getItems().containsKey("Pizza"));
    }

    @Test
    void menuManagement_ShouldRejectWithoutPermission() {
        String simulatedInput = "1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Employee testEmployee = new Employee(1, "Test", "test@example.com", "password", EmployeeRole.EMPLOYEE);
        Inventory inventory = Inventory.getInstance();

        menu.menuManagement(testEmployee, inventory);

        assertFalse(menu.getItems().containsKey("Pizza"));
    }

    @Test
    void addMenuItem_ShouldAddItemWithIngredients() {
        Inventory inventory = Inventory.getInstance();
        inventory.addItem(new InventoryItem("Cheese", 100, 20));
        inventory.addItem(new InventoryItem("Tomato", 50, 10));

        String simulatedInput = "Pizza\n10.99\nCheese\n2\nTomato\n3\ndone\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        Scanner scanner = new Scanner(System.in);

        Menu.addMenuItem(inventory, scanner);

        MenuItem pizza = menu.getItem("Pizza");
        assertNotNull(pizza);
        assertEquals(2, pizza.getIngredients().size());
    }
}