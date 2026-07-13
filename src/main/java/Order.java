
import java.time.LocalDateTime;
import java.util.*;


public class Order {
    private static int nextId = 1; // Sequential unique ID for each order

    private final int id;
    private Table table;
    private final Employee employee;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(Table table, Employee employee, List<OrderItem> items) {
        this.id = nextId++;
        this.table = table;
        this.employee = employee;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Calculates the order total by summing the price of each item multiplied by the quantity
    public double calculateTotal() {
        return items.stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
    }

    public int getId() {
        return id;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    public Table getTable() {
        return table;
    }
    public Employee getEmployee() {
        return employee;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Main order management menu
    public static void orderManagement(Employee currentUser, List<Table> tables,
                                       Menu menu, List<Order> orders, Scanner scanner) {
        while (true) {
            System.out.println("\n=== ORDER MANAGEMENT ===");
            System.out.println("1. Create Order");
            System.out.println("2. View Orders");
            System.out.println("3. Update Order Status");
            System.out.println("4. Return to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createOrder(currentUser, tables, menu, orders, scanner);
                case 2 -> viewOrders(orders);
                case 3 -> updateOrderStatus(currentUser, orders, scanner);
                case 4 -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    // Creates a new order, requesting menu items and linking to a table
    /**
     * Creates a new order for a specific table by taking input from the user.
     * This method allows waiters or managers with the necessary permissions to
     * create a new order, including selecting a table and adding items to the order.
     *
     * @param currentUser The employee currently logged in and executing the operation.
     * @param tables      The list of tables available in the system to assign an order.
     * @param menu        The menu containing items that can be added to the order.
     * @param orders      The list of existing orders, to which the new order will be added.
     * @param scanner     The scanner instance used for user input during the order creation process.
     */
    private static void createOrder(Employee currentUser, List<Table> tables,
                                    Menu menu, List<Order> orders, Scanner scanner) {
        if (!currentUser.hasAccess(PermissionFeature.CREATE_ORDER)) {
            System.out.println("Access denied!");
            return;
        }
        if (!(currentUser.getRole() == EmployeeRole.WAITER ||
                currentUser.getRole() == EmployeeRole.MANAGER)) {
            System.out.println("Only waiters or managers can create orders!");
            return;
        }
        System.out.println("\nCreate New Order");
        showAvailableTables(tables);
        System.out.print("Table number: ");

        int tableNumber = scanner.nextInt();
        scanner.nextLine();
        Table selectedTable = tables.stream()
                .filter(t -> t.getNumber() == tableNumber)
                .findFirst()
                .orElse(null);
        if (selectedTable == null || !selectedTable.isAvailable()) {
            System.out.println("Invalid or occupied table!");
            return;
        }
        List<OrderItem> items = new ArrayList<>();
        boolean addingItems = true;
        while (addingItems) {
            System.out.println("\nMenu Items:");
            menu.getItems().values().forEach(item ->
                    System.out.printf("%s - R$ %.2f\n", item.getName(), item.getPrice()));
            System.out.print("Item name (or 'done' to finish): ");
            String itemName = scanner.nextLine();
            if (itemName.equalsIgnoreCase("done")) {
                addingItems = false;
            } else {
                MenuItem menuItem = menu.getItem(itemName);
                if (menuItem == null) {
                    System.out.println("Item not found!");
                } else {
                    System.out.print("Quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();
                    items.add(new OrderItem(menuItem, quantity));
                }
            }
        }
        if (items.isEmpty()) {
            System.out.println("Order needs at least one item!");
            return;
        }
        Order newOrder = new Order(selectedTable, currentUser, items);
        orders.add(newOrder);
        selectedTable.setAvailable(false);
        System.out.println("Order created successfully!");
        System.out.printf("Total: R$ %.2f\n", newOrder.calculateTotal());
    }

    // Shows only tables that are available

    /**
     * Displays the list of available tables along with their number and capacity.
     * Only tables marked as available are displayed in a formatted structure.
     *
     * @param tables the list of {@code Table} objects to be processed. Each table's
     *               availability is checked, and only available tables are displayed.
     */
    private static void showAvailableTables(List<Table> tables) {
        System.out.println("\n=== AVAILABLE TABLES ===");
        System.out.printf("%-10s %-10s\n", "Number", "Capacity");
        tables.stream()
                .filter(Table::isAvailable)
                .forEach(table -> System.out.printf("%-10d %-10d\n",
                        table.getNumber(), table.getCapacity()));
    }

    // Displays all orders in tabular format
    /**
     * Displays a list of orders in a formatted table structure.
     * If the list of orders is empty, a message is displayed indicating
     * there are no registered orders.
     *
     * @param orders A list of Order objects to be displayed. Each order's details,
     *               including ID, table number*/
    private static void viewOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("\nNo orders registered!");
            return;
        }
        System.out.println("\n=== ORDER LIST ===");
        System.out.printf("%-10s %-10s %-15s %-15s %-10s %-20s\n",
                "ID", "Table", "Employee", "Status", "Total", "Created at");
        orders.forEach(order ->
                System.out.printf("%-10d %-10d %-15s %-15s R$%-9.2f %-20s\n",
                        order.getId(),
                        order.getTable().getNumber(),
                        order.getEmployee().getName(),
                        order.getStatus(),
                        order.calculateTotal(),
                        order.getCreatedAt()));
    }

    // Updates the status of an existing order

    /**
     * Updates the status of an existing order based on the user's role and input.
     * This method allows employees with the appropriate permissions to view and
     * modify the status of an order. Depending on their role, employees can set
     * specific statuses such as PREPARING, READY, DELIVERED, COMPLETED, or CANCELED.
     *
     * The method verifies the user's permissions and role before proceeding. If the
     * new status is COMPLETED or CANCELED, the associated table will be marked as
     * available.
     *
     * @param currentUser The employee currently logged in and executing the operation.
     *                    Their permissions and role determine the statuses they can set.
     * @param orders The list of existing orders.
     * @param scanner The scanner instance used for user input.
     */
    public static void updateOrderStatus(Employee currentUser, List<Order> orders, Scanner scanner) {
        if (!currentUser.hasAccess(PermissionFeature.CHANGE_ORDER_STATUS)) {
            System.out.println("Access denied!");
            return;
        }
        viewOrders(orders);
        if (orders.isEmpty()) return;
        System.out.print("Order ID: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Enter a valid ID:");
            scanner.next();
        }
        int orderId = scanner.nextInt();
        scanner.nextLine();
        Order order = orders.stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .orElse(null);
        if (order == null) {
            System.out.println("Order not found!");
            return;
        }
        System.out.println("\nCurrent status: " + order.getStatus());
        if (currentUser.getRole() == EmployeeRole.CHEF) {
            System.out.println("1. PREPARING");
            System.out.println("2. READY");
            System.out.print("Choose (1-2): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 1) order.setStatus(OrderStatus.PREPARING);
            else if (choice == 2) order.setStatus(OrderStatus.READY);
            else {
                System.out.println("Invalid choice!");
                return;
            }

        } else if (currentUser.getRole() == EmployeeRole.WAITER ||
                currentUser.getRole() == EmployeeRole.MANAGER) {
            System.out.println("3. DELIVERED");
            System.out.println("4. COMPLETED");
            System.out.println("5. CANCELED");
            System.out.print("Choose (3-5): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 3 -> order.setStatus(OrderStatus.DELIVERED);
                case 4 -> order.setStatus(OrderStatus.COMPLETED);
                case 5 -> order.setStatus(OrderStatus.CANCELED);
                default -> {
                    System.out.println("Invalid choice!");
                    return;
                }
            }
        }
        // Releases the table if the order is completed or canceled
        if (order.getStatus() == OrderStatus.COMPLETED ||
                order.getStatus() == OrderStatus.CANCELED) {
            order.getTable().setAvailable(true);
        }
        System.out.println("Status updated successfully!");
    }
}