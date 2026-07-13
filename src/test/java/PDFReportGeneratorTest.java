import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PDFReportGeneratorTest {
    private List<Order> orders;
    @BeforeEach
    public void setup() {
        orders = new ArrayList<>();
        // Setup sample data
        Employee employee = new Employee(1, "Maria Carolina", "maria@example.com", "password", EmployeeRole.MANAGER);
        Table table = new Table(1, 4);
        table.setAvailable(false);
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(new MenuItem("Burger", 15.99), 2));

        Order order = new Order(table, employee, orderItems);
        orders.add(order);
    }
    @Test
    public void generateOrderReport_ShouldThrowException_WhenNoOrdersInDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(5);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PDFReportGenerator.generateOrderReport(orders, startDate, endDate);
        });
        String expectedMessage = "There are no orders registered in the selected period";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
