import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the EmployeeDAO class.
 * These tests verify correct database operations for Employee entities.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeDAOTest {
    private static final String TEST_EMAIL = "test@example.com";
    @BeforeAll
    static void setupDatabase() throws SQLException {
        EmployeeDAO.recreateTable();
    }
    @Test
    @Order(1)
    void save_ShouldInsertEmployeeIntoDatabase() throws SQLException {
        Employee employee = new Employee(1, "Test User", TEST_EMAIL, "test123", EmployeeRole.WAITER);
        EmployeeDAO.save(employee);
        Employee fetched = EmployeeDAO.findById(1);
        assertNotNull(fetched, "Employee should be saved and retrievable");
        assertEquals("Test User", fetched.getName());
        assertEquals(TEST_EMAIL, fetched.getEmail());
        assertEquals(EmployeeRole.WAITER, fetched.getRole());
    }
    @Test
    @Order(2)
    void findAll_ShouldReturnAllEmployees() throws SQLException {
        List<Employee> employees = EmployeeDAO.findAll();
        assertFalse(employees.isEmpty(), "findAll should return at least one employee");
    }
    @Test
    @Order(3)
    void update_ShouldModifyEmployeeData() throws SQLException {
        Employee employee = EmployeeDAO.findById(1);
        assertNotNull(employee, "Employee must exist to update");
        employee.setSalt("newSalt");
        EmployeeDAO.update(employee);
        Employee updated = EmployeeDAO.findById(1);
        assertNotNull(updated);
        assertEquals("newSalt", updated.getSalt(), "Salt should be updated");
    }
    @Test
    @Order(4)
    void delete_ShouldRemoveEmployeeFromDatabase() throws SQLException {
        EmployeeDAO.delete(1);
        Employee deleted = EmployeeDAO.findById(1);
        assertNull(deleted, "Employee should be deleted");
    }
    @Test
    @Order(5)
    void findById_ShouldReturnNullIfEmployeeNotFound() throws SQLException {
        Employee result = EmployeeDAO.findById(999);
        assertNull(result, "Should return null for non-existing ID");
    }
}
