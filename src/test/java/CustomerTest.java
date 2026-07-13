import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Customer class.
 */
public class CustomerTest {
    private Customer customer;
    @BeforeEach
    void setUp() {
        customer = new Customer("John Doe", "1234567890", "john.doe@example.com");
    }
    @Test
    void constructor_ShouldCreateCustomerWithValidData() {
        assertEquals("John Doe", customer.getName());
        assertEquals("1234567890", customer.getPhone());
        assertEquals("john.doe@example.com", customer.getEmail());
        assertNotNull(customer.getEmail(), "Email must not be null");
    }
    @Test
    void setName_ShouldUpdateCustomerName() {
        customer.setName("Jane Smith");
        assertEquals("Jane Smith", customer.getName());
    }
    @Test
    void setName_ShouldThrowException_WhenNull() {
        assertThrows(NullPointerException.class, () -> customer.setName(null));
    }
    @Test
    void setPhone_ShouldUpdateCustomerPhone() {
        customer.setPhone("0987654321");
        assertEquals("0987654321", customer.getPhone());
    }
    @Test
    void setPhone_ShouldThrowException_WhenNull() {
        assertThrows(NullPointerException.class, () -> customer.setPhone(null));
    }
    @Test
    void getEmail_ShouldReturnImmutableEmail() {
        assertEquals("john.doe@example.com", customer.getEmail());
    }
}
