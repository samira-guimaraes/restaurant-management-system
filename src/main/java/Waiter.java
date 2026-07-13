/**
 * The Waiter class represents an employee who serves as a waiter in a business establishment.
 * It extends the Employee class and inherits its properties and behaviors.
 *
 * This class is used to create instances of waiters with specific details such as
 * id, name, email, and password. The role of the waiter is predefined
 * using the EmployeeRole enumeration.
 */

public class Waiter extends Employee {
    public Waiter(int id, String name, String email, String password) {
        super(id, name, email, password, EmployeeRole.WAITER);
    }

}
