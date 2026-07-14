import java.util.*;
import java.util.stream.Collectors;

/**
 * The Menu class provides functionality for managing a menu in a restaurant
 * management system. It allows adding, retrieving, and displaying menu items,
 * as well as managing those items' ingredients and respective quantities.
 * The class uses a singleton design pattern to ensure that only one instance
 * of the menu exists throughout the application.
 *
 * Thread Safety:
 * This class is thread-safe as the singleton instance is retrieved in a
 * synchronized block.
 */
public class Menu {
    private static Menu instance;
    private Map<String, MenuItem> items = new HashMap<>();

    private Menu() {}

    public static synchronized Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void addItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        item.removeNullIngredients();
        this.items.put(item.getName(), item);
    }

    public MenuItem getItem(String name) {
        return this.items.get(name);
    }

    public Map<String, MenuItem> getItems() {
        return this.items;
    }

    public static void menuManagement(Employee currentUser, Inventory inventory) {
        if (!currentUser.hasAccess(PermissionFeature.MANAGE_MENU)) {
            System.out.println("Access denied!");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== MENU MANAGEMENT ===");
        System.out.println("1. Add Menu Item");
        System.out.println("2. View Menu");
        System.out.println("3. Back to Main Menu");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1 -> addMenuItem(inventory, scanner);
            case 2 -> viewMenu();
            case 3 -> { return; }
            default -> System.out.println("Invalid option!");
        }
    }

    protected static void addMenuItem(Inventory inventory, Scanner scanner) {
        System.out.println("\nAdd New Menu Item");
        System.out.print("Item name: ");
        String name = scanner.nextLine();
        System.out.print("Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        MenuItem newItem = new MenuItem(name, price);
        Menu.getInstance().addItem(newItem);

        boolean addingIngredients = true;
        while (addingIngredients) {
            System.out.println("\nCurrent Inventory Items:");
            inventory.getItems().values().forEach(item ->
                    System.out.println("- " + item.getName() + " (Qty: " + item.getQuantity() + ")"));

            System.out.print("Add ingredient (name or 'done' to finish): ");
            String ingredientName = scanner.nextLine();

            if (ingredientName.equalsIgnoreCase("done")) {
                addingIngredients = false;
            } else {
                InventoryItem ingredient = inventory.getItem(ingredientName);
                if (ingredient == null) {
                    System.out.println("Ingredient not found!");
                } else {
                    System.out.print("Quantity needed: ");
                    double quantity = scanner.nextDouble();
                    scanner.nextLine();
                    newItem.addIngredient(ingredient, quantity);
                    System.out.println("Ingredient added!");
                }
            }
        }
        System.out.println("Menu item added successfully!");
    }

    private static void viewMenu() {
        System.out.println("\n=== MENU ===");
        System.out.printf("%-20s %-10s %-30s\n", "Name", "Price", "Ingredients");

        Menu.getInstance().getItems().values().forEach(item -> {
            String ingredients = item.getIngredients().entrySet().stream()
                    .map(e -> {
                        InventoryItem ingredient = e.getKey();
                        return ingredient != null ?
                                ingredient.getName() + " (" + e.getValue() + ")" :
                                "[Ingredient removed] (" + e.getValue() + ")";
                    })
                    .collect(Collectors.joining(", "));

            System.out.printf("%-20s $%-9.2f %-30s\n",
                    item.getName(),
                    item.getPrice(),
                    ingredients.isEmpty() ? "No ingredients" : ingredients);
        });
    }

    public void clearItemsForTest() {
        this.items.clear();
    }
}