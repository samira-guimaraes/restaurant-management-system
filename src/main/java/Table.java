import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Table {
    private final int number;
    private final int capacity;
    private boolean available;

    public Table(int number, int capacity) {
        this.number = number;
        this.capacity = capacity;
        this.available = true;
    }

    public int getNumber() {
        return number;
    }
    public int getCapacity() {
        return capacity;
    }
    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Provides management functionality for tables, including options for adding
     * a new table, viewing existing tables, and returning to the main menu. The
     * interaction is done through a console-based menu system.
     *
     * @param tables the list containing all registered tables. Each table contains
     *               details such as number, capacity, and availability status.
     * @param scanner the Scanner object used for reading user input for menu navigation
     *                and table management activities.
     */
    public static void tableManagement(List<Table> tables, Scanner scanner) {
        while (true) {
            System.out.println("\n=== TABLE MANAGEMENT ===");
            System.out.println("1. Add Table");
            System.out.println("2. View Tables");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Error: invalid input! Please enter a number.");
                scanner.nextLine(); // Clear buffer
                continue;
            }
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> addTable(tables, scanner);
                case 2 -> viewTables(tables);
                case 3 -> {
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Adds a new table to the list of tables if the table number does not already exist.
     * Prompts the user to input the table number and capacity through the provided Scanner object.
     * If invalid input is provided, the table will not be added.
     *
     * @param tables the list of existing tables, where each table contains details such as number and capacity.
     * @param scanner the Scanner object used to retrieve user input for table number and capacity.
     */
    static void addTable(List<Table> tables, Scanner scanner) {
        try {
            System.out.print("Table number: ");
            int number = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (tables.stream().anyMatch(t -> t.getNumber() == number)) {
                System.out.println("Error: table number already exists! Try again.");
                return;
            }
            System.out.print("Capacity: ");
            int capacity = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            tables.add(new Table(number, capacity));
            System.out.println("Table added successfully!");
        } catch (InputMismatchException e) {
            System.out.println("Error: invalid input. Table was not added.");
            scanner.nextLine(); // Clear buffer
        }
    }

    /**
     * Displays a list of tables along with their details such as number, capacity,
     * and availability status. If no tables are available, a message indicating
     * that no tables are registered is displayed.
     *
     * @param tables the list of tables to be displayed. Each table contains details
     *               such as number, capacity, and availability status.
     */
    protected static void viewTables(List<Table> tables) {
        if (tables.isEmpty()) {
            System.out.println("\nNo tables registered.");
            return;
        }
        System.out.println("\n=== TABLE LIST ===");
        System.out.printf("%-10s %-10s %-10s\n", "Number", "Capacity", "Status");
        for (Table table : tables) {
            System.out.printf("%-10d %-10d %-10s\n",
                    table.getNumber(),
                    table.getCapacity(),
                    table.isAvailable() ? "Available" : "Occupied");
        }
    }
}
