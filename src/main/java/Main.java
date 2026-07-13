import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Project Name: TastyBit Restaurant
 * @author: Samira Cardoso Guimaraes
 * Project Description: TastyBit Restaurant Management System
 * @version 1.0
 * HND Graded Unit 2 - Software Engineering (Clyde College)
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Inventory inventory = Inventory.getInstance();
    private static final Menu menu = Menu.getInstance();

    // System components
    private static final List<Employee> employees = new ArrayList<>();
    private static final List<Table> tables = new ArrayList<>();
    private static final List<Order> orders = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();
    private static final List<WorkSchedule> schedules = new ArrayList<>();
    private static Employee currentUser = null;


    public static void main(String[] args) {
        initializeSampleData();
        runApplication();
    }

    private static void initializeSampleData() {
        try {
            // Recreate the table to ensure the correct structure
            EmployeeDAO.recreateTable();
            employees.clear();
            Manager admin = new Manager(1, "Admin", "admin@tastybit.com", "admin123");
            Waiter sara = new Waiter(2, "Sara Stuart", "sara@tastybit.com", "waiter123");
            Chef mark = new Chef(3, "Mark Master", "chef@tastybit.com", "chef123");
            // Add to list in memory
            employees.add(admin);
            employees.add(sara);
            employees.add(mark);
            // Persists in the database (with updated hashes)
            EmployeeDAO.save(admin);
            EmployeeDAO.save(sara);
            EmployeeDAO.save(mark);
            System.out.println("Initial data created successfully ✅:");
            List<Employee> loadedEmployees = EmployeeDAO.findAll();
            // System.out.println("Employees loaded from database: " + loadedEmployees.size());

        } catch (SQLException e) {
            System.err.println("Error initializing data: " + e.getMessage());
            // Fallback
            employees.add(new Manager(1, "Lucas Milanese", "admin@tastybit.com", "admin123"));
            employees.add(new Waiter(2, "Sara Duarte", "sara@tastybit.com", "waiter123"));
            employees.add(new Chef(3, "Mark Master", "chef@tastybit.com", "chef123"));
        }
        // Initializing tables (not related to login)
        tables.add(new Table(1, 4));
        tables.add(new Table(2, 6));
        tables.add(new Table(3, 2));

        inventory.addItem(new InventoryItem("Tomato", 50, 10));
        inventory.addItem(new InventoryItem("Cheese", 30, 5));
        inventory.addItem(new InventoryItem("Flour", 20, 2));
        inventory.addItem(new InventoryItem("Pasta", 40, 8));
        inventory.addItem(new InventoryItem("Smoked Bacon", 25, 15));
        inventory.addItem(new InventoryItem("Glass of Wine", 100, 12));

        MenuItem pizza = new MenuItem("Pizza Margherita", 12.99);
        pizza.addIngredient(inventory.getItem("Tomato"), 2);
        pizza.addIngredient(inventory.getItem("Cheese"), 1);
        menu.addItem(pizza);

        MenuItem carbonara = new MenuItem("Carbonara", 10.99);
        carbonara.addIngredient(inventory.getItem("Pasta"), 2);
        carbonara.addIngredient(inventory.getItem("Smoked Bacon"), 1);
        menu.addItem(carbonara);

        MenuItem drink = new MenuItem("Wine", 10.99);
        drink.addIngredient(inventory.getItem("Glass of Wine"), 1);
        menu.addItem(drink);
    }

    private static void runApplication() {
        while (true) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                logger.severe("Application error: " + e.getMessage());
                System.out.println("\n[ERROR] ❌ An unexpected error occurred. Please try again.");
                scanner.nextLine(); // Clear buffer
            }
        }
    }

    /**
     * Displays the login menu to the user, providing options to either log in
     * to the system or exit the application. If the user selects the login option,
     * the method delegates control to the {@code login()} method to handle user
     * authentication. If the user chooses to exit, the application terminates.
     *
     * If an invalid option is selected, an error message is displayed, and the
     * user is prompted to choose again.
     *
     * This method interacts with the user through the console, leveraging the
     * pre-defined {@code scanner} object for input handling. It also makes use
     * of the {@code System.exit()} method to terminate the application during
     * the exit option.
     */
    private static void showLoginMenu() {
        System.out.println("\n=== WELCOME TO TASTYBIT RESTAURANT SYSTEM ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> login();
            case 2 -> {
                System.out.println("\nThank you for using TastyBit Restaurant System!");
                System.exit(0);
            }
            default -> System.out.println("\n❌ Invalid option! Tray again to choose an option.");
        }
    }

    private static void login() {
        System.out.print("\n \uD83D\uDCE7 Email: ");
        String email = scanner.nextLine().trim();
        System.out.print(" \uD83D\uDD11 Password: ");
        String password = scanner.nextLine().trim();

        // Adding logs for debugging
        Optional<Employee> employee = employees.stream()
                .filter(emp -> emp.getEmail().equalsIgnoreCase(email))
                .filter(emp -> {
                    try {
                        boolean authResult = emp.login(email, password);
                        return authResult;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst();
        if (employee.isPresent()) {
            currentUser = employee.get();
            // System.out.println("\n✅ Login successful!");
        } else {
            System.out.println("\n[ERROR] Invalid credentials! Please check your email and password.");
        }
    }


    private static void showMainMenu() {
        System.out.println("\n\u001B[30m👤 Welcome: " + currentUser.getName() +
                " (\u001B[35m" + currentUser.getRole() + "\u001B[33m)\u001B[0m\n");
        int menuOption = 1;

        // Manager Menu Options
        if (currentUser.getRole() == EmployeeRole.MANAGER) {
            System.out.println("\u001B[34m┌─────────────────── MANAGEMENT ─────────────────┐");
            System.out.printf(" \u001B[36m%d\u001B[0m. 👥 Employee                 \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 🪑 Table                    \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 📋 Menu                     \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 🛒 Order                    \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 📅 Booking                  \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 📦 Inventory                \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 🕒 Work Schedule                      \n", menuOption++);
            System.out.print("\n");
            System.out.println("\u001B[34m├───────────────────── REPORTS ────────────────────┤");
            System.out.printf("│ \u001B[36m%d\u001B[0m. 📊 Generate Reports                   \n", menuOption++);
            System.out.print("\n");
        }
        // Waiter Menu Options
        else if (currentUser.getRole() == EmployeeRole.WAITER) {
            System.out.println("\u001B[34m┌─────────────────── SERVICE ─────────────────────┐");
            System.out.printf(" \u001B[36m%d\u001B[0m. 🪑 Table                  \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 📋 Menu                   \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 🛒 Order                  \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 📅 Booking                \n", menuOption++);
            System.out.print("\n");
            System.out.println("\u001B[34m├────────────-────-- REPORTS-- ────-──────────────┤");
            System.out.printf("│ \u001B[36m%d\u001B[0m. 📊 Generate Reports                   \n", menuOption++);
            System.out.print("\n");
        }
        // Chef Menu Options
        else if (currentUser.getRole() == EmployeeRole.CHEF) {
            System.out.println("\u001B[34m┌───────────────────── KITCHEN ───────────────────────┐");
            System.out.printf(" \u001B[36m%d\u001B[0m. 📋 Menu                     \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 📦 Inventory                \n", menuOption++);
            System.out.printf(" \u001B[36m%d\u001B[0m. 🛎️ Order Status Update               \n", menuOption++);
            System.out.println("\u001B[34m├───────────────────── REPORT ────────────────────┤");
            System.out.printf(" \u001B[36m%d\u001B[0m. 📊 Generate Reports                   \n", menuOption++);
            System.out.print("\n");
        }

        System.out.println("\u001B[34m├─────────────────────────────────────────────────┤");
        System.out.printf(" \u001B[36m%d\u001B[0m. 🚪 Logout                            \n", menuOption);
        System.out.println("\u001B[34m└─────────────────────────────────────────────────┘\u001B[0m");

        System.out.print("\n\u001B[36m🔹 Choose an option: \u001B[0m");
        System.out.println();
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        handleMenuChoice(choice, currentUser.getRole());
    }
    private static void handleMenuChoice(int choice, EmployeeRole role) {
        try {
            switch (role) {
                case MANAGER -> handleManagerChoice(choice);
                case WAITER -> handleWaiterChoice(choice);
                case CHEF -> handleChefChoice(choice);
            }
        } catch (Exception e) {
            showErrorMessage("An error occurred: " + e.getMessage());
        }
    }

    private static void handleManagerChoice(int choice) {
        switch (choice) {
            case 1 -> Employee.employeeManagement(currentUser, employees, scanner);
            case 2 -> Table.tableManagement(tables, scanner);
            case 3 -> Menu.menuManagement(currentUser, inventory);
            case 4 -> Order.orderManagement(currentUser, tables, menu, orders, scanner);
            case 5 -> Booking.bookingManagement(tables, bookings, scanner);
            case 6 -> Inventory.inventoryManagement(currentUser, scanner);
            case 7 -> WorkSchedule.scheduleManagement(currentUser, employees, schedules, scanner);
            case 8 -> generateReportsMenu();
            case 9 -> logout();
            default -> showErrorMessage("Invalid option! Choose between 1 and 9");
        }
    }

    private static void handleWaiterChoice(int choice) {
        switch (choice) {
            case 1 -> Table.tableManagement(tables, scanner);
            case 2 -> Menu.menuManagement(currentUser, inventory);
            case 3 -> Order.orderManagement(currentUser, tables, menu, orders, scanner);
            case 4 -> Booking.bookingManagement(tables, bookings, scanner);
            case 5 -> generateReportsMenu();
            case 6 -> logout();
            default -> showErrorMessage("Invalid option! Choose between 1 and 6");
        }
    }
    private static void handleChefChoice(int choice) {
        switch (choice) {
            case 1 -> Menu.menuManagement(currentUser, inventory);
            case 2 -> Inventory.inventoryManagement(currentUser, scanner);
            case 3 -> Order.updateOrderStatus(currentUser, orders, scanner);
            case 4 -> generateReportsMenu();
            case 5 -> logout();
            default -> showErrorMessage("Invalid option! Choose between 1 and 5");
        }
    }

    private static void logout() {
        System.out.println("\n[SUCCESS] Logged out successfully");
        currentUser = null;
    }

    private static void showErrorMessage(String message) {
        System.out.println("\n[ERROR] " + message);
        System.out.println("Please try again.");
    }

    private static void generateReportsMenu() {
        System.out.println("\n=== REPORT GENERATION ===");
        // Show role-specific options
        if (currentUser.getRole() == EmployeeRole.MANAGER) {
            System.out.println("1. Orders");
            System.out.println("2. Bookings");
            System.out.println("3. Inventory");
            System.out.println("4. Back");
        } else if (currentUser.getRole() == EmployeeRole.WAITER) {
            System.out.println("1. Orders");
            System.out.println("2. Bookings");
            System.out.println("3. Back");
        } else if (currentUser.getRole() == EmployeeRole.CHEF) {
            System.out.println("1. Orders");
            System.out.println("2. Inventory");
            System.out.println("3. Back");
        }
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        // Check if user chose to go back
        if ((currentUser.getRole() == EmployeeRole.MANAGER && choice == 4) ||
                (currentUser.getRole() == EmployeeRole.WAITER && choice == 3) ||
                (currentUser.getRole() == EmployeeRole.CHEF && choice == 3)) {
            return;
        }
        try {
            if (currentUser.getRole() == EmployeeRole.MANAGER) {
                switch (choice) {
                    case 1 -> {
                        DateRange dateRange = getValidDateRange();
                        if (dateRange == null) return;
                        try {
                            PDFReportGenerator.generateOrderReport(orders, dateRange.start(), dateRange.end());
                        } catch (IllegalArgumentException e) {
                            System.out.println("\n[INFO] " + e.getMessage());
                        }
                    }
                    case 2 -> {
                        DateRange dateRange = getValidDateRangeForBookings();
                        if (dateRange == null) return;
                        List<Booking> filteredBookings = Booking.filterBookingsByDate(bookings, dateRange.start(), dateRange.end());
                        PDFReportGenerator.generateBookingReport(filteredBookings, dateRange.start(), dateRange.end());
                    }
                    case 3 -> PDFReportGenerator.generateInventoryReport();
                    default -> System.out.println("Invalid option!");
                }
            } else if (currentUser.getRole() == EmployeeRole.WAITER) {
                switch (choice) {
                    case 1 -> {
                        DateRange dateRange = getValidDateRange();
                        if (dateRange == null) return;
                        try {
                            PDFReportGenerator.generateOrderReport(orders, dateRange.start(), dateRange.end());
                        } catch (IllegalArgumentException e) {
                            System.out.println("\n[INFO] " + e.getMessage());
                        }
                    }
                    case 2 -> {
                        DateRange dateRange = getValidDateRangeForBookings();
                        if (dateRange == null) return;
                        List<Booking> filteredBookings = Booking.filterBookingsByDate(bookings, dateRange.start(), dateRange.end());
                        PDFReportGenerator.generateBookingReport(filteredBookings, dateRange.start(), dateRange.end());
                    }
                    default -> System.out.println("Invalid option!");
                }
            } else if (currentUser.getRole() == EmployeeRole.CHEF) {
                switch (choice) {
                    case 1 -> {
                        DateRange dateRange = getValidDateRange();
                        if (dateRange == null) return;
                        try {
                            PDFReportGenerator.generateOrderReport(orders, dateRange.start(), dateRange.end());
                        } catch (IllegalArgumentException e) {
                            System.out.println("\n[INFO] " + e.getMessage());
                        }
                    }
                    case 2 -> PDFReportGenerator.generateInventoryReport();
                    default -> System.out.println("Invalid option!");
                }
            }
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
            logger.severe("Report generation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\n[INFO] " + e.getMessage());
        }
    }

    private static DateRange getValidDateRangeForBookings() {
        while (true) {
            try {
                System.out.print("\nStart date (YYYY-MM-DD) or 'cancel' to abort: ");
                String startInput = scanner.nextLine().trim();
                if (startInput.equalsIgnoreCase("cancel")) {
                    System.out.println("Operation cancelled.");
                    return null;
                }
                System.out.print("End date (YYYY-MM-DD) or 'cancel' to abort: ");
                String endInput = scanner.nextLine().trim();
                if (endInput.equalsIgnoreCase("cancel")) {
                    System.out.println("Operation cancelled.");
                    return null;
                }
                LocalDate startDate = LocalDate.parse(startInput, DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(endInput, DATE_FORMATTER);
                if (endDate.isBefore(startDate)) {
                    System.out.println("Error: End date must be after start date!");
                    continue;
                }
                return new DateRange(startDate, endDate);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please use exactly YYYY-MM-DD format (e.g., 2023-12-31).");
            }
        }
    }

    private static DateRange getValidDateRange() {
        final int MAX_YEARS_RANGE = 1; // Maximum of 1 year for the period
        while (true) {
            try {
                System.out.print("\nStart date (YYYY-MM-DD) or 'cancel' to abort: ");
                String startInput = scanner.nextLine().trim();
                if (startInput.equalsIgnoreCase("cancel")) {
                    System.out.println("Operation cancelled.");
                    return null;
                }
                System.out.print("End date (YYYY-MM-DD) or 'cancel' to abort: ");
                String endInput = scanner.nextLine().trim();
                if (endInput.equalsIgnoreCase("cancel")) {
                    System.out.println("Operation cancelled.");
                    return null;
                }
                // Parse of data
                LocalDate startDate = LocalDate.parse(startInput, DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(endInput, DATE_FORMATTER);
                LocalDate today = LocalDate.now();
                // Validations
                if (startDate.isAfter(today)) {
                    System.out.println("Error: Start date cannot be in the future!");
                    continue;
                }
              if (endDate.isBefore(startDate)) {
                    System.out.println("Error: End date must be after start date!");
                    continue;
                }
                if (startDate.plusYears(MAX_YEARS_RANGE).isBefore(endDate)) {
                    System.out.printf("Error: Maximum date range is %d year%s!%n",
                            MAX_YEARS_RANGE, MAX_YEARS_RANGE > 1 ? "s" : "");
                    continue;
                }
                return new DateRange(startDate, endDate);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please use exactly YYYY-MM-DD format (e.g., 2023-12-31).");
            }
        }
    }

    private record DateRange(LocalDate start, LocalDate end) {}
}
