import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the Manager class.
 */
public class ManagerTest {
    /**
     * Verifies that a Manager is created with the correct properties.
     */
    @Test
    void constructor_ShouldCreateManagerWithCorrectProperties() {
        // Arrange
        int expectedId = 100;
        String expectedName = "Emma Thompson";
        String expectedEmail = "emma@tastybit.com";
        String rawPassword = "ManagerPass123";
        // Act
        Manager manager = new Manager(expectedId, expectedName, expectedEmail, rawPassword);
        // Assert
        assertEquals(expectedId, manager.getId());
        assertEquals(expectedName, manager.getName());
        assertEquals(expectedEmail, manager.getEmail());
        assertEquals(EmployeeRole.MANAGER, manager.getRole());
        assertNotNull(manager.getPasswordHash());
        assertNotEquals(rawPassword, manager.getPasswordHash()); // Should be hashed
    }
    /**
     * Ensures that a Manager object is an instance of Employee.
     */
    @Test
    void manager_ShouldBeInstanceOfEmployee() {
        Manager manager = new Manager(101, "John Miller", "john@tastybit.com", "AdminPass456");
        assertTrue(manager instanceof Employee);
    }
    /**
     * Ensures that all Manager instances have the role MANAGER.
     */
    @Test
    void role_ShouldAlwaysBeManager() {
        Manager manager1 = new Manager(102, "Olivia", "olivia@tastybit.com", "SecurePass789");
        Manager manager2 = new Manager(103, "Liam", "liam@tastybit.com", "TopManager321");

        assertEquals(EmployeeRole.MANAGER, manager1.getRole());
        assertEquals(EmployeeRole.MANAGER, manager2.getRole());
    }
    /**
     * Verifies that password authentication works correctly for Manager.
     */
    @Test
    void authenticate_ShouldReturnTrueForCorrectPassword() {
        String correctPassword = "SuperSecure123";
        Manager manager = new Manager(104, "Lucas", "lucas@tastybit.com", correctPassword);
        assertTrue(manager.login("lucas@tastybit.com", correctPassword));
        assertFalse(manager.login("lucas@tastybit.com","WrongPass"));
    }
}
