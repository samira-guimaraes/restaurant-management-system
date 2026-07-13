import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashingAlgorithmTest {
    @Test
    void generateSalt_ShouldReturnUniqueNonNullValues() {
        String salt1 = HashingAlgorithm.generateSalt();
        String salt2 = HashingAlgorithm.generateSalt();

        assertNotNull(salt1, "Salt should not be null");
        assertNotNull(salt2, "Salt should not be null");
        assertNotEquals(salt1, salt2, "Two generated salts should be different");
    }
    @Test
    void hashPassword_ShouldReturnConsistentHash_ForSameInputAndSalt() {
        String password = "SecurePass123";
        String salt = HashingAlgorithm.generateSalt();
        String hash1 = HashingAlgorithm.hashPassword(password, salt);
        String hash2 = HashingAlgorithm.hashPassword(password, salt);
        assertNotNull(hash1, "Hash should not be null");
        assertEquals(hash1, hash2, "Hashes should match for the same input and salt");
    }
    @Test
    void verifyPassword_ShouldReturnTrue_ForCorrectPassword() {
        String password = "MyTestPassword!";
        String salt = HashingAlgorithm.generateSalt();
        String hashed = HashingAlgorithm.hashPassword(password, salt);
        boolean result = HashingAlgorithm.verifyPassword(password, hashed, salt);
        assertTrue(result, "Verification should return true for correct password");
    }
    @Test
    void verifyPassword_ShouldReturnFalse_ForIncorrectPassword() {
        String password = "OriginalPassword";
        String wrongPassword = "WrongPassword";
        String salt = HashingAlgorithm.generateSalt();
        String hashed = HashingAlgorithm.hashPassword(password, salt);
        boolean result = HashingAlgorithm.verifyPassword(wrongPassword, hashed, salt);
        assertFalse(result, "Verification should fail for incorrect password");
    }
    @Test
    void verifyPassword_ShouldReturnFalse_WhenNullInputs() {
        assertFalse(HashingAlgorithm.verifyPassword(null, "someHash", "someSalt"));
        assertFalse(HashingAlgorithm.verifyPassword("pass", null, "someSalt"));
        assertFalse(HashingAlgorithm.verifyPassword("pass", "someHash", null));
    }
}
