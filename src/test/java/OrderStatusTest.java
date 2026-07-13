import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrderStatus enum.
 */
public class OrderStatusTest {

    @Test
    public void enum_ShouldContainAllExpectedStatuses() {
        OrderStatus[] statuses = OrderStatus.values();

        assertEquals(6, statuses.length);
        assertArrayEquals(
                new OrderStatus[]{
                        OrderStatus.PENDING,
                        OrderStatus.PREPARING,
                        OrderStatus.READY,
                        OrderStatus.DELIVERED,
                        OrderStatus.CANCELED,
                        OrderStatus.COMPLETED
                },
                statuses
        );
    }
    @Test
    public void valueOf_ShouldReturnCorrectEnum() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.PREPARING, OrderStatus.valueOf("PREPARING"));
        assertEquals(OrderStatus.READY, OrderStatus.valueOf("READY"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.valueOf("DELIVERED"));
        assertEquals(OrderStatus.CANCELED, OrderStatus.valueOf("CANCELED"));
        assertEquals(OrderStatus.COMPLETED, OrderStatus.valueOf("COMPLETED"));
    }
    @Test
    public void valueOf_ShouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            OrderStatus.valueOf("INVALID_STATUS");
        });
    }
}
