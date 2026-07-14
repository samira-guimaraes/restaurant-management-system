import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeRoleTest {
    @Test
    void managerRole_ShouldHaveCorrectDisplayName() {
        assertEquals("Manager", EmployeeRole.MANAGER.getDisplayName(),
                "Manager display name should be 'Manager'");
    }
    @Test
    void waiterRole_ShouldHaveCorrectDisplayName() {
        assertEquals("Waiter", EmployeeRole.WAITER.getDisplayName(),
                "Waiter display name should be 'Waiter'");
    }
    @Test
    void chefRole_ShouldHaveCorrectDisplayName() {
        assertEquals("Chef", EmployeeRole.CHEF.getDisplayName(),
                "Chef display name should be 'Chef'");
    }
    @Test
    void employeeRole_ShouldHaveCorrectDisplayName() {
        assertEquals("Employee", EmployeeRole.EMPLOYEE.getDisplayName(),
                "Employee display name should be 'Employee'");
    }
    @Test
    void values_ShouldContainAllRoles() {
        EmployeeRole[] roles = EmployeeRole.values();
        assertArrayEquals(
                new EmployeeRole[]{EmployeeRole.MANAGER, EmployeeRole.WAITER, EmployeeRole.CHEF, EmployeeRole.EMPLOYEE},
                roles,
                "EmployeeRole enum should contain MANAGER, WAITER, CHEF, and EMPLOYEE in order"
        );
    }
}