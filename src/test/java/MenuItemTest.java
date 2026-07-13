import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

public class MenuItemTest {
    private MenuItem menuItem;
    @BeforeEach
    void setUp() {
        menuItem = new MenuItem("Pizza", 12.50);
    }
    @Test
    void constructor_ShouldInitializeNameAndPriceCorrectly() {
        assertEquals("Pizza", menuItem.getName());
        assertEquals(12.50, menuItem.getPrice());
    }
    @Test
    void addIngredient_ShouldAddValidIngredient() {
        InventoryItem cheese = new InventoryItem("Cheese", 100, 20);
        menuItem.addIngredient(cheese, 2.5);
        Map<InventoryItem, Double> ingredients = menuItem.getIngredients();
        assertEquals(1, ingredients.size());
        assertTrue(ingredients.containsKey(cheese));
        assertEquals(2.5, ingredients.get(cheese));
    }
    @Test
    void addIngredient_ShouldOverrideQuantityIfIngredientAlreadyExists() {
        InventoryItem tomato = new InventoryItem("Tomato", 50, 10);
        menuItem.addIngredient(tomato, 1.0);
        menuItem.addIngredient(tomato, 2.0); // override
        Map<InventoryItem, Double> ingredients = menuItem.getIngredients();
        assertEquals(1, ingredients.size());
        assertEquals(2.0, ingredients.get(tomato));
    }
    @Test
    void addIngredient_ShouldThrowException_WhenIngredientIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> menuItem.addIngredient(null, 1.0)
        );
        assertEquals("Ingredient cannot be null", exception.getMessage());
    }
    @Test
    void getIngredients_ShouldReturnUnmodifiableMap() {
        InventoryItem pepperoni = new InventoryItem("Pepperoni", 80, 15);
        menuItem.addIngredient(pepperoni, 1.5);
        Map<InventoryItem, Double> ingredients = menuItem.getIngredients();
        assertThrows(UnsupportedOperationException.class, () -> {
            ingredients.put(new InventoryItem("Mushroom", 40, 10), 1.0);
        });
    }
}
