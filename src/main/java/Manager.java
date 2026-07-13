/**
 * The Manager class represents an employee with a managerial role
 * in the system. It extends the Employee class and initializes
 * a Manager-specific role upon instantiation.
 */
public class Manager extends Employee {
    public Manager(int id, String name, String email, String password) {
        super(id, name, email, password, EmployeeRole.MANAGER);
    }
}