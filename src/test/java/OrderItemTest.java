import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrderItem class.
 */
public class OrderItemTest {

    private MenuItem menuItem;
    @BeforeEach
    public void setUp() {
        menuItem = new MenuItem("Burger", 12.50);
    }
    @Test
    public void constructor_ShouldSetMenuItemAndQuantity() {
        OrderItem orderItem = new OrderItem(menuItem, 2);
        assertEquals(menuItem, orderItem.getMenuItem());
        assertEquals(2, orderItem.getQuantity());
    }
    @Test
    public void getMenuItem_ShouldReturnCorrectMenuItem() {
        OrderItem orderItem = new OrderItem(menuItem, 1);
        assertNotNull(orderItem.getMenuItem());
        assertEquals("Burger", orderItem.getMenuItem().getName());
        assertEquals(12.50, orderItem.getMenuItem().getPrice());
    }
    @Test
    public void getQuantity_ShouldReturnCorrectQuantity() {
        OrderItem orderItem = new OrderItem(menuItem, 5);
        assertEquals(5, orderItem.getQuantity());
    }
}
