import java.util.*;
import java.util.stream.Collectors;

/**
 * The Inventory class manages a collection of inventory items and their associated information.
 * It is implemented as a singleton, meaning only one instance of the class exists in the application.
 * This class provides functionality for adding, updating, and managing inventory items, as well
 * as notifying observers of stock changes.
 */
public class Inventory {
    private static Inventory instance;
    private final Map<String, InventoryItem> items = new HashMap<>();
    private final List<StockObserver> observers = new ArrayList<>();

    private Inventory() {}

    public static synchronized Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    public void addObserver(StockObserver observer) {
        this.observers.add(observer);
    }

    public void addItem(InventoryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        if (items.containsKey(item.getName())) {
            throw new IllegalArgumentException("Item '" + item.getName() + "' already exists in inventory.");
        }
        if (item.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (item.getMinStock() < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative.");
        }
        items.put(item.getName(), item);
    }

    public static void inventoryManagement(Employee currentUser, Scanner scanner) {
        if (!currentUser.hasAccess(PermissionFeature.UPDATE_INVENTORY)) {
            System.out.println("Access denied. You don't have permission to manage the inventory.");
            return;
        }

        while (true) {
            System.out.println("\n=== INVENTORY MANAGEMENT ===");
            System.out.println("1. View Inventory");
            System.out.println("2. Add New Item");
            System.out.println("3. Update Stock");
            System.out.println("4. Set Minimum Stock");
            System.out.println("5. View Low Stock Items");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    viewInventory();
                    break;
                case 2:
                    addInventoryItem(scanner);
                    break;
                case 3:
                    updateStock(scanner);
                    break;
                case 4:
                    setMinStock(scanner);
                    break;
                case 5:
                    viewLowStockItems();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewInventory() {
        Inventory inventory = Inventory.getInstance();
        Map<String, InventoryItem> items = inventory.getItems();
        if (items.isEmpty()) {
            System.out.println("\nYour inventory is currently empty.");
            return;
        }
        System.out.println("\n=== CURRENT INVENTORY ===");
        System.out.printf("%-20s %-10s %-10s %-10s\n", "Item", "Quantity", "Min Stock", "Status");
        List<InventoryItem> itemList = new ArrayList<>(items.values());
        itemList.sort(Comparator.comparing(InventoryItem::getName, String::compareToIgnoreCase));
        for (InventoryItem item : itemList) {
            String status = item.getQuantity() < item.getMinStock() ? "LOW" : "OK";
            System.out.printf("%-20s %-10.2f %-10.2f %-10s\n",
                    item.getName(),
                    item.getQuantity(),
                    item.getMinStock(),
                    status);
        }
    }

    private static void addInventoryItem(Scanner scanner) {
        Inventory inventory = Inventory.getInstance();
        System.out.println("\nAdd a New Inventory Item");
        System.out.print("Item name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Item name cannot be empty.");
            return;
        }
        if (inventory.getItem(name) != null) {
            System.out.println("An item with that name already exists.");
            return;
        }
        System.out.print("Initial quantity: ");
        double quantity = scanner.nextDouble();
        System.out.print("Minimum stock level: ");
        double minStock = scanner.nextDouble();
        scanner.nextLine();
        if (quantity < 0 || minStock < 0) {
            System.out.println("Both quantity and minimum stock must be non-negative.");
            return;
        }
        try {
            inventory.addItem(new InventoryItem(name, quantity, minStock));
            System.out.println("Item added successfully!");
            if (quantity < minStock) {
                System.out.println("Heads up: The quantity is already below the minimum stock level.");
            }
        } catch (Exception e) {
            System.out.println("Couldn't add the item: " + e.getMessage());
        }
    }

    private static void updateStock(Scanner scanner) {
        Inventory inventory = Inventory.getInstance();
        viewInventory();
        System.out.print("Enter the name of the item to update: ");
        String itemName = scanner.nextLine().trim();
        InventoryItem item = inventory.getItem(itemName);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        System.out.printf("Current quantity: %.2f\n", item.getQuantity());
        System.out.print("Enter quantity change (use negative number to decrease): ");
        double change = scanner.nextDouble();
        scanner.nextLine();
        double newQuantity = item.getQuantity() + change;
        if (newQuantity < 0) {
            System.out.println("Quantity can't go below zero.");
            return;
        }
        item.setQuantity(newQuantity);
        System.out.println("Stock updated successfully!");
        if (newQuantity < item.getMinStock()) {
            System.out.println("Warning: Stock is now below the minimum threshold.");
            inventory.notifyObservers(item);
        }
    }

    private static void setMinStock(Scanner scanner) {
        Inventory inventory = Inventory.getInstance();
        viewInventory();
        System.out.print("Enter the item name to update its minimum stock: ");
        String itemName = scanner.nextLine().trim();
        InventoryItem item = inventory.getItem(itemName);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }
        System.out.printf("Current minimum stock: %.2f\n", item.getMinStock());
        System.out.print("New minimum stock level: ");
        double minStock = scanner.nextDouble();
        scanner.nextLine();
        if (minStock < 0) {
            System.out.println("Minimum stock cannot be negative.");
            return;
        }
        item.setMinStock(minStock);
        System.out.println("Minimum stock updated.");
        if (item.getQuantity() < minStock) {
            System.out.println("Warning: Current stock is below the new minimum.");
            inventory.notifyObservers(item);
        }
    }

    private static void viewLowStockItems() {
        Inventory inventory = Inventory.getInstance();
        Map<String, InventoryItem> allItems = inventory.getItems();
        List<InventoryItem> lowStockItems = new ArrayList<>();
        for (InventoryItem item : allItems.values()) {
            if (item.getQuantity() < item.getMinStock()) {
                lowStockItems.add(item);
            }
        }

        if (lowStockItems.isEmpty()) {
            System.out.println("\nGood news! All items are above minimum stock levels.");
            return;
        }
        lowStockItems.sort(Comparator.comparing(InventoryItem::getName, String::compareToIgnoreCase));
        System.out.println("\n=== ITEMS BELOW MINIMUM STOCK ===");
        System.out.printf("%-20s %-10s %-10s %-10s\n", "Item", "Quantity", "Min Stock", "Deficit");
        for (InventoryItem item : lowStockItems) {
            double deficit = item.getMinStock() - item.getQuantity();
            System.out.printf("%-20s %-10.2f %-10.2f %-10.2f\n",
                    item.getName(),
                    item.getQuantity(),
                    item.getMinStock(),
                    deficit);
        }
    }

    public boolean hasEnoughItems(Map<InventoryItem, Double> requiredItems) {
        for (Map.Entry<InventoryItem, Double> entry : requiredItems.entrySet()) {
            InventoryItem requestedItem = entry.getKey();
            double requestedQuantity = entry.getValue();
            InventoryItem stockItem = this.items.get(requestedItem.getName());
            if (stockItem == null || stockItem.getQuantity() < requestedQuantity) {
                return false;
            }
        }
        return true;
    }

    public void decreaseStock(Map<InventoryItem, Double> itemsToDecrease) {
        itemsToDecrease.forEach((item, quantity) -> {
            if (quantity <= 0.0) {
                throw new IllegalArgumentException("Invalid quantity for " + item.getName() +
                        ": " + quantity + " (must be positive)");
            }
            InventoryItem inventoryItem = this.items.get(item.getName());
            if (inventoryItem == null) {
                throw new IllegalArgumentException("Item '" + item.getName() + "' not found in inventory.");
            }
            if (inventoryItem.getQuantity() < quantity) {
                throw new IllegalStateException("Not enough stock for " + item.getName() +
                        " (available: " + inventoryItem.getQuantity() + ", requested: " + quantity + ")");
            }
            double oldQuantity = inventoryItem.getQuantity();
            inventoryItem.setQuantity(oldQuantity - quantity);
            System.out.printf("[STOCK] %s: %.2f → %.2f (reduced by %.2f)\n",
                    item.getName(), oldQuantity, inventoryItem.getQuantity(), quantity);
            if (inventoryItem.getQuantity() < inventoryItem.getMinStock()) {
                System.out.printf("[ALERT] %s stock is now below minimum (%.2f < %.2f)\n",
                        item.getName(), inventoryItem.getQuantity(), inventoryItem.getMinStock());
                notifyObservers(inventoryItem);
            }
        });
    }

    private void notifyObservers(InventoryItem item) {
        this.observers.forEach(observer -> observer.update(item));
    }

    public InventoryItem getItem(String name) {
        return this.items.get(name);
    }

    public Map<String, InventoryItem> getItems() {
        return Collections.unmodifiableMap(this.items);
    }

    public void addAllItems(List<InventoryItem> items) {
        items.forEach(this::addItem);
    }

    public void clearItemsForTest() {
        this.items.clear();
    }
}
