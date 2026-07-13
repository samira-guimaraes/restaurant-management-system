import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SQLiteConfig} class.
 * This class verifies that a valid connection can be established
 * and that the SQLite driver is correctly loaded.
 */
public class SQLiteConfigTest {

    /**
     * Tests whether a connection can be successfully established
     * using the {@code SQLiteConfig.getConnection()} method.
     */
    @Test
    public void getConnection_ShouldReturnValidConnection() {
        try (Connection connection = SQLiteConfig.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("Exception thrown while trying to get connection: " + e.getMessage());
        }
    }

    /**
     * Tests whether multiple calls to {@code getConnection()} return different connection instances.
     */
    @Test
    public void getConnection_ShouldReturnNewConnectionEachTime() throws SQLException {
        Connection conn1 = SQLiteConfig.getConnection();
        Connection conn2 = SQLiteConfig.getConnection();
        assertNotSame(conn1, conn2, "Each call to getConnection should return a new instance");
        conn1.close();
        conn2.close();
    }

    /**
     * Tests whether an SQLException is thrown when the driver is unavailable (manually simulated).
     * This test is for documentation purposes and cannot be implemented directly without mocking the class loader.
     */
    @Test
    public void driver_ShouldBeLoadedSuccessfully() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            fail("SQLite JDBC driver should be loaded without error");
        }
    }
}
