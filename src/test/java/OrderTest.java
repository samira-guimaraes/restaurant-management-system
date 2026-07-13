import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    private Employee employee;
    private Table table;
    private MenuItem item1;
    private MenuItem item2;
    private Order order;

    @BeforeEach
    public void setUp() {
        employee = new Employee(1, "John Doe", "john@example.com", "password123", EmployeeRole.WAITER);
        table = new Table(1, 4);
        table.setAvailable(true);
        item1 = new MenuItem("Burger", 10.0);
        item2 = new MenuItem("Fries", 5.0);
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(item1, 2));
        items.add(new OrderItem(item2, 1));
        order = new Order(table, employee, items);
    }
    @Test
    public void constructor_ShouldAssignCorrectValues() {
        assertNotNull(order);
        assertEquals(table, order.getTable());
        assertEquals(employee, order.getEmployee());
        assertEquals(2, order.getItems().size());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getCreatedAt());
    }
    @Test
    public void calculateTotal_ShouldReturnCorrectAmount() {
        double expectedTotal = (2 * 10.0) + (1 * 5.0); // 25.0
        assertEquals(expectedTotal, order.calculateTotal(), 0.01);
    }
    @Test
    public void setStatus_ShouldUpdateStatusSuccessfully() {
        order.setStatus(OrderStatus.READY);
        assertEquals(OrderStatus.READY, order.getStatus());
    }
    @Test
    public void getItems_ShouldReturnUnmodifiableList() {
        List<OrderItem> items = order.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(new OrderItem(item1, 1)));
    }
    @Test
    public void getId_ShouldBeUnique() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(item1, 1));

        Order secondOrder = new Order(table, employee, items);
        assertNotEquals(order.getId(), secondOrder.getId());
    }
    @Test
    public void getCreatedAt_ShouldNotBeNull() {
        assertNotNull(order.getCreatedAt());
    }
}
