/**
 * Represents a Chef, which is a specific type of Employee.
 * A Chef inherits all properties and behaviors of the Employee class
 * and is initialized with a role specific to chefs.
 */
public class Chef extends Employee {
    public Chef(int id, String name, String email, String password) {
        super(id, name, email, password, EmployeeRole.CHEF);
    }
}
