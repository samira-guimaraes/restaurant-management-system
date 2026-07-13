import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Employee class.
 */
public class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        // Setting up an employee for testing
        employee = new Employee(1, "Alice Nunes", "alice@example.com", "Secure123", EmployeeRole.WAITER);
    }

    @Test
    void constructor_ShouldCreateEmployeeWithValidData() {
        assertEquals(1, employee.getId());
        assertEquals("Alice Nunes", employee.getName());
        assertEquals("alice@example.com", employee.getEmail());
        assertEquals(EmployeeRole.WAITER, employee.getRole());
        assertNotNull(employee.getPasswordHash());
        assertNotEquals("Secure123", employee.getPasswordHash()); // Password should be hashed, not plain
    }

    @Test
    void constructor_ShouldThrowException_WhenInvalidEmail() {
        // Test invalid email format
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Employee(2, "Bob", "invalidEmail", "Password123", EmployeeRole.MANAGER)
        );
        assertEquals("Invalid email format.", exception.getMessage()); // Verifique a mensagem esperada em inglês
    }

    @Test
    void constructor_ShouldThrowException_WhenPasswordTooShort() {
        // Test password too short (less than 6 characters)
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Employee(3, "Carl", "carl@example.com", "123", EmployeeRole.CHEF)
        );
        assertEquals("Password must be at least 6 characters long.", exception.getMessage()); // Verifique a mensagem esperada em inglês
    }

    @Test
    void login_ShouldReturnTrue_WhenCredentialsAreCorrect() {
        // Test correct email and password
        assertTrue(employee.login("alice@example.com", "Secure123"));
    }

    @Test
    void login_ShouldReturnFalse_WhenEmailOrPasswordIsIncorrect() {
        // Test incorrect email
        assertFalse(employee.login("wrong@example.com", "Secure123"));
        // Test incorrect password
        assertFalse(employee.login("alice@example.com", "wrongpassword"));
    }

    @Test
    void hasAccess_ShouldReturnCorrectPermissionsForWaiter() {
        // Testing permissions for a Waiter
        assertTrue(employee.hasAccess(PermissionFeature.CREATE_ORDER)); // Waiters can create orders
        assertFalse(employee.hasAccess(PermissionFeature.MANAGE_EMPLOYEES)); // Waiters cannot manage employees
    }

    @Test
    void createEmployeeByRole_ShouldReturnCorrectSubclass() {
        // Creating employees by role and verifying their type
        Employee manager = Employee.createEmployeeByRole(10, "Maria", "maria@tastybit.com", "Admin123333", EmployeeRole.MANAGER);
        Employee chef = Employee.createEmployeeByRole(11, "Chef", "chef@tastybit.com", "Cook123333", EmployeeRole.CHEF);
        Employee waiter = Employee.createEmployeeByRole(12, "Waiter", "waiter@tastybit.com", "Serve12333", EmployeeRole.WAITER);

        assertInstanceOf(Manager.class, manager);
        assertInstanceOf(Chef.class, chef);
        assertInstanceOf(Waiter.class, waiter);
    }

    @Test
    void hasPermission_ShouldReturnTrue_ForManagerAccess() {
        // Manager should have permission to manage employees
        Employee manager = new Manager(100, "Admin", "admin@tastybit.com", "SuperPass");
        assertTrue(manager.hasPermission(PermissionFeature.MANAGE_EMPLOYEES));
    }

    @Test
    void hasPermission_ShouldReturnFalse_WhenPermissionNotGranted() {
        // Waiter shouldn't have permission to update inventory
        assertFalse(employee.hasPermission(PermissionFeature.UPDATE_INVENTORY));
    }
}
