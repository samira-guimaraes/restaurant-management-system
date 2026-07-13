import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Waiter class.
 */
public class WaiterTest {
    /**
     * Tests that the Waiter object is correctly created with expected properties.
     */
    @Test
    void constructor_ShouldCreateWaiterWithCorrectProperties() {
        // Arrange
        int expectedId = 10;
        String expectedName = "Alice Johnson";
        String expectedEmail = "alice@tastybit.com";
        String rawPassword = "ServicePass456";
        // Act
        Waiter waiter = new Waiter(expectedId, expectedName, expectedEmail, rawPassword);
        // Assert
        assertEquals(expectedId, waiter.getId());
        assertEquals(expectedName, waiter.getName());
        assertEquals(expectedEmail, waiter.getEmail());
        assertEquals(EmployeeRole.WAITER, waiter.getRole());
        assertNotNull(waiter.getPasswordHash());
        assertNotEquals(rawPassword, waiter.getPasswordHash()); // Password should be hashed
    }

    /**
     * Confirms that a Waiter is an instance of Employee.
     */
    @Test
    void waiter_ShouldBeInstanceOfEmployee() {
        Waiter waiter = new Waiter(11, "Bob Smith", "bob@tastybit.com", "WaiterPass789");
        assertTrue(waiter instanceof Employee);
    }
    /**
     * Ensures that the role is always WAITER.
     */
    @Test
    void role_ShouldAlwaysBeWaiter() {
        Waiter waiter1 = new Waiter(12, "Clara", "clara@tastybit.com", "Welcome123");
        Waiter waiter2 = new Waiter(13, "David", "david@tastybit.com", "Hello456");

        assertEquals(EmployeeRole.WAITER, waiter1.getRole());
        assertEquals(EmployeeRole.WAITER, waiter2.getRole());
    }
    /**
     * Validates that password authentication works as expected.
     */
    @Test
    void login_ShouldReturnTrueForCorrectPassword() {
        String correctPassword = "WaiterSecure123";
        Waiter waiter = new Waiter(14, "Eve", "eve@tastybit.com", correctPassword);

        assertTrue(waiter.login("eve@tastybit.com", correctPassword)); // Usa login ao invés de authenticate
        assertFalse(waiter.login("eve@tastybit.com", "WrongPassword")); // Teste com senha incorreta
    }

}
