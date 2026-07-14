import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Representa um funcionário no sistema, encapsulando identidade, nível de acesso, autenticação
 * e gerenciamento de turnos.
 */
public class Employee {
    private final int id;
    private final String name;
    private final String email;
    private String passwordHash;
    private String salt;
    private final EmployeeRole role;
    private List<Shift> shifts;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Employee(int id, String name, String email, String password, EmployeeRole role) {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive.");
        this.id = id;
        this.name = Objects.requireNonNull(name, "Name cannot be null.");
        this.email = validateEmail(email);
        this.salt = HashingAlgorithm.generateSalt();
        this.passwordHash = HashingAlgorithm.hashPassword(validatePassword(password), salt);
        this.role = Objects.requireNonNull(role, "Function cannot be null.");
        this.shifts = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public EmployeeRole getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    public List<Shift> getShifts() { return new ArrayList<>(shifts); }

    private String validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        return email.trim();
    }

    private String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        return password;
    }

    public boolean login(String email, String password) {
        if (email == null || password == null) return false;
        return this.email.equalsIgnoreCase(email.trim()) &&
                HashingAlgorithm.verifyPassword(password.trim(), this.passwordHash, this.salt);
    }

    public boolean hasAccess(PermissionFeature feature) {
        Objects.requireNonNull(feature, "Function cannot be null.");
        switch (role) {
            case MANAGER:
                return true;
            case WAITER:
                return isWaiterAllowed(feature);
            case CHEF:
                return isChefAllowed(feature);
            case EMPLOYEE:
                return false;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }


    private boolean isWaiterAllowed(PermissionFeature feature) {
        return switch (feature) {
            case CREATE_ORDER, EDIT_ORDER, MANAGE_TABLES, MANAGE_BOOKINGS, VIEW_MENU -> true;
            default -> false;
        };
    }

    private boolean isChefAllowed(PermissionFeature feature) {
        return switch (feature) {
            case UPDATE_INVENTORY, VIEW_INVENTORY, CHANGE_ORDER_STATUS, MANAGE_MENU, VIEW_MENU -> true;
            default -> false;
        };
    }

    /**
     * Manages employee-related functions such as registering new employees
     * and viewing the list of existing employees. Only users with the appropriate
     * access privileges are allowed to perform these operations.
     *
     * @param currentUser the Employee object representing the current logged-in user
     *                    whose permissions are checked to ensure access to this feature
     * @param employees the list of Employee objects representing all employees in the system,
     *                  which can be modified by this method (e.g., adding new employees)
     * @param scanner the Scanner object that is used to capture user input for various operations
     */
    public static void employeeManagement(Employee currentUser, List<Employee> employees, Scanner scanner) {
        if (!currentUser.hasAccess(PermissionFeature.MANAGE_EMPLOYEES)) {
            System.out.println("Access denied! Only managers can manage employees.");
            return;
        }

        while (true) {
            System.out.println("\n=== EMPLOYEE MANAGEMENT ===");
            System.out.println("1. Register Employee");
            System.out.println("2. View Employees");
            System.out.println("3. Back to Main Menu ");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> registerEmployee(employees, scanner);
                case 2 -> viewEmployees(employees);
                case 3 -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Registers a new employee by prompting the user for details such as name, email, password, and role.
     * Validates the input and adds the new employee to the provided list if successful.
     *
     * @param employees the list of employees to which the new employee will be added
     * @param scanner the Scanner object used to capture input from the user
     */
    private static void registerEmployee(List<Employee> employees, Scanner scanner) {
        System.out.println("\nRegister New Employee");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty!");
            return;
        }
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.println("Password (min. 6 characters): ");
        String password = scanner.nextLine();
        System.out.print("Function (1-Manager, 2-Waiter, 3-Chef): ");
        int roleChoice = scanner.nextInt();
        scanner.nextLine();
        EmployeeRole role = switch (roleChoice) {
            case 1 -> EmployeeRole.MANAGER;
            case 2 -> EmployeeRole.WAITER;
            case 3 -> EmployeeRole.CHEF;
            default -> {
                System.out.println("Invalid function choice!");
                yield null;
            }
        };

        if (role != null) {
            try {
                int newId = employees.isEmpty() ? 1 : employees.get(employees.size() - 1).getId() + 1;
                Employee newEmployee = createEmployeeByRole(newId, name, email, password, role);
                employees.add(newEmployee);
                System.out.println("Employee registered successfully! ID: " + newId);
            } catch (Exception e) {
                System.out.println("Error registering employee: " + e.getMessage());
            }
        }
    }

    public static void viewEmployees(List<Employee> employees) {
        if (employees.isEmpty()) {
            System.out.println("\nNo staff available.");
            return;
        }
        System.out.println("\n=== EMPLOYEES ===");
        System.out.printf("%-5s %-20s %-15s%n", "ID", "Nome", "Função");
        for (Employee e : employees) {
            System.out.printf("%-5d %-20s %-15s%n", e.getId(), e.getName(), e.getRole());
        }
    }

    public static Employee createEmployeeByRole(int id, String name, String email, String password, EmployeeRole role) {
        return switch (role) {
            case MANAGER -> new Manager(id, name, email, password);
            case WAITER -> new Waiter(id, name, email, password);
            case CHEF -> new Chef(id, name, email, password);
            default -> throw new IllegalArgumentException("Unknown function: " + role);
        };
    }

    public boolean hasPermission(PermissionFeature permission) {
        Objects.requireNonNull(permission, "Permission cannot be void.");
        switch (role) {
            case MANAGER:
                return true;
            case CHEF:
                return permission == PermissionFeature.VIEW_INVENTORY ||
                        permission == PermissionFeature.MANAGE_MENU ||
                        permission == PermissionFeature.CHANGE_ORDER_STATUS;
            case WAITER:
                return permission == PermissionFeature.CREATE_ORDER ||
                        permission == PermissionFeature.EDIT_ORDER ||
                        permission == PermissionFeature.MANAGE_BOOKINGS ||
                        permission == PermissionFeature.MANAGE_TABLES;
            case EMPLOYEE:
                return false;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

}
