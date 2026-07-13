import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * The HashingAlgorithm class provides a utility for generating secure salts, hashing passwords,
 * and verifying hashed passwords. This class is designed to support cryptographic functions
 * that enhance data security by protecting against attacks like brute force or precomputed hash attacks.
 *
 * The hashing process leverages the SHA-512 algorithm and performs multiple iterations to
 * make it computationally expensive for attackers to replicate the computation, enhancing
 * security against attacks.
 *
 * The class is utility-based and cannot be instantiated.
 */
public class HashingAlgorithm {
    private static final int ITERATIONS = 10000;
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 64;
    private HashingAlgorithm() {
        // Private constructor to avoid instantiation
    }

    /**
     * Generates a cryptographically secure random salt encoded in Base64 format.
     * The salt is used in cryptographic operations to ensure data uniqueness
     * and protect against common attacks like precomputed hash attacks.
     *
     * @return a randomly generated salt as a Base64-encoded string
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes the given password using the SHA-512 algorithm and a provided salt,
     * performing a configurable number of iterations to enhance security.
     *
     * @param password the plaintext password to be hashed
     * @param salt the salt to be applied to the hash function to ensure uniqueness
     * @return the hashed password as a Base64-encoded string
     * @throws RuntimeException if the hashing algorithm is not available
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(Base64.getDecoder().decode(salt));
            byte[] hash = digest.digest(password.getBytes());
            for (int i = 0; i < ITERATIONS; i++) {
                digest.reset();
                hash = digest.digest(hash);
            }
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies whether the given input password matches the stored hashed password using the specified salt.
     *
     * @param inputPassword the password input provided by the user for verification
     * @param storedHash the stored hashed password to compare against
     * @param salt the salt used during the hashing process to ensure uniqueness
     * @return true if the input password matches the stored hash after applying the salt; false otherwise
     */
    public static boolean verifyPassword(String inputPassword, String storedHash, String salt) {
        if (inputPassword == null || storedHash == null || salt == null) {
            return false;
        }
        String newHash = hashPassword(inputPassword, salt);
        return MessageDigest.isEqual(
                Base64.getDecoder().decode(newHash),
                Base64.getDecoder().decode(storedHash)
        );
    }
}