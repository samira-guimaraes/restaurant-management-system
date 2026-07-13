/**
 * The EmployeeRole enum represents the various roles an employee can have within the system.
 * Each role defines a specific set of responsibilities and permissions, which are utilized
 * to manage role-based access control and functionality in the application.
 *
 * Available roles:
 * - MANAGER: Represents an employee with managerial responsibilities, overseeing operations
 *   and making administrative decisions.
 * - WAITER: Represents an employee responsible for managing customer interactions, taking
 *   orders, and serving food or beverages.
 * - CHEF: Represents an employee tasked with food preparation, inventory management,
 *   and order fulfillment in the kitchen.
 */
public enum EmployeeRole {
    MANAGER("Manager"),
    WAITER("Waiter"),
    CHEF("Chef"),
    EMPLOYEE("Employee");

    private final String displayName;
    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
