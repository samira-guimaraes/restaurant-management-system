import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the Chef class.
 */
public class ChefTest {
    /**
     * Tests the constructor to ensure a Chef object is created with the correct values.
     */
    @Test
    void constructor_ShouldCreateChefWithCorrectProperties() {
        // Arrange
        int expectedId = 1;
        String expectedName = "Luiza Laura";
        String expectedEmail = "laura@tastybit.com";
        String password = "StrongPass123";
        // Act
        Chef chef = new Chef(expectedId, expectedName, expectedEmail, password);
        // Assert
        assertEquals(expectedId, chef.getId());
        assertEquals(expectedName, chef.getName());
        assertEquals(expectedEmail, chef.getEmail());
        assertEquals(EmployeeRole.CHEF, chef.getRole());
        assertNotNull(chef.getPasswordHash()); // hashed password should not be null
        assertNotEquals(password, chef.getPasswordHash()); // it should not be the plain password
    }

    /**
     * Verifies that Chef objects are instances of Employee.
     */
    @Test
    void chef_ShouldBeInstanceOfEmployee() {
        // Act
        Chef chef = new Chef(2, "Jamie Oliver", "jamie@tastybit.com", "AnotherStrongPass");
        // Assert
        assertTrue(chef instanceof Employee);
    }
    /**
     * Verifies that the role is always set to CHEF for all Chef instances.
     */
    @Test
    void role_ShouldAlwaysBeChef() {
        // Act
        Chef chef1 = new Chef(3, "Julia Cardoso", "julia@tastybit.com", "FrenchCuisine123");
        Chef chef2 = new Chef(4, "Massimo Mota", "massimo@tastybit.com", "ItalianFood456");
        // Assert
        assertEquals(EmployeeRole.CHEF, chef1.getRole());
        assertEquals(EmployeeRole.CHEF, chef2.getRole());
    }
    /**
     * Tests that password authentication works correctly.
     */
    @Test
    void login_ShouldReturnTrueForCorrectPassword() {
        // Arrange
        String rawPassword = "SecretPass123";
        Chef chef = new Chef(5, "Test Chef", "test@tastybit.com", rawPassword);
        // Act & Assert
        assertTrue(chef.login("test@tastybit.com", rawPassword));  // Usando o método login com email e senha
        assertFalse(chef.login("test@tastybit.com", "WrongPassword"));  // Teste com senha incorreta
    }

}
