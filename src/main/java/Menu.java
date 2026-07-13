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
    private static final Scanner scanner = new Scanner(System.in);

    private Menu() {}

    /**
     * Retrieves the singleton instance of the Menu class. The instance is lazily
     * initialized and ensures thread safety by being synchronized.
     *
     * @return the single instance of the Menu class
     */
    public static synchronized Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void addItem(MenuItem item) {
        // Cria uma nova lista mutável das chaves
        Set<InventoryItem> ingredients = new HashSet<>(item.getIngredients().keySet());
        // Remove as chaves nulas
        ingredients.removeIf(Objects::isNull);
        // Agora, atualize o mapa de ingredientes sem a chave nula
        for (InventoryItem ingredient : ingredients) {
            item.getIngredients().put(ingredient, item.getIngredients().get(ingredient));
        }
        // Adiciona o item no menu
        this.items.put(item.getName(), item);
    }


    /**
     * Retrieves a {@code MenuItem} from the menu based on its name.
     *
     * @param name the name of the menu item to retrieve; must not be null
     * @return the {@code MenuItem} associated with the given name, or null if no such item exists
     */
    public MenuItem getItem(String name) {
        return this.items.get(name);
    }

    /**
     * Retrieves the complete menu in the form of a map where the keys are item names
     * and the values are the associated {@code MenuItem} objects.
     *
     * @return a map containing all menu items, with the item names as keys and the corresponding
     *         {@code MenuItem} objects as values
     */
    public Map<String, MenuItem> getItems() {
        return this.items;
    }

    /**
     * Provides functionality for managing the menu, including options to add menu items,
     * view the current menu, or exit back to the main menu. Access to this functionality
     * is restricted to users with the appropriate permission.
     *
     * @param currentUser the currently logged-in employee attempting to manage the menu;
     *                    must contain permissions to access menu management features
     * @param inventory   the inventory system to be used when adding new menu items
     */
    public static void menuManagement(Employee currentUser, Inventory inventory) {
        // Check if the user has permission to manage the menu
        if (!currentUser.hasAccess(PermissionFeature.MANAGE_MENU)) {
            System.out.println("Access denied!");
            return;
        }
        System.out.println("\n=== MENU MANAGEMENT ===");
        System.out.println("1. Add Menu Item");
        System.out.println("2. View Menu");
        System.out.println("3. Back to Main Menu");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1 -> addMenuItem(inventory);
            case 2 -> viewMenu();
            case 3 -> { return; }
            default -> System.out.println("Invalid option!");
        }
    }

    /**
     * Adds a new menu item to the menu. This includes defining the item's name,
     * price, and specifying its ingredients by selecting from the inventory.
     *
     * @param inventory the inventory system from which ingredients can be added
     *                  to the menu item
     */
    protected static void addMenuItem(Inventory inventory) {
        System.out.println("\nAdd New Menu Item");
        System.out.print("Item name: ");
        String name = scanner.nextLine();
        System.out.print("Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        MenuItem newItem = new MenuItem(name, price);
        Menu.getInstance().addItem(newItem);

        // Add ingredients
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

    /**
     * Displays the menu in a formatted manner, listing the name, price, and ingredients
     * of each menu item. The menu is retrieved from the singleton instance of the Menu
     * class. Each ingredient is displayed with its name and quantity, or marked as
     * removed if the ingredient is no longer available.
     *
     * If a menu item has no ingredients, it will display "No ingredients" in place
     * of its ingredient list.
     *
     * This method uses standard output for displaying the menu contents.
     */
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
}
