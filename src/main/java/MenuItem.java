import java.util.HashMap;
import java.util.Map;

public class MenuItem {
    private final String name;
    private final double price;
    private final Map<InventoryItem, Double> ingredients;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
        this.ingredients = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public void addIngredient(InventoryItem item, double quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        this.ingredients.put(item, quantity);
    }

    public Map<InventoryItem, Double> getIngredients() {
        return this.ingredients; // Retorna o mapa de ingredientes diretamente
    }
}
