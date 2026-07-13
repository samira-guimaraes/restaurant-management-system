import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The SQLiteConfig class serves as a utility for managing the SQLite database configuration
 * and establishing database connections. It provides a static method to retrieve a database
 * connection object for interacting with the SQLite database.
 *
 * This class ensures that the SQLite JDBC driver is loaded at runtime,
 * facilitating seamless communication with the SQLite database.
 *
 * The database file specified in the configuration will be created in the project root directory
 * if it does not already exist.
 */
public class SQLiteConfig {

    /**
     * The database URL used to establish a connection to the SQLite database.
     *
     * This constant specifies the JDBC connection string for the SQLite database file
     * located in the project root directory. It is utilized by the application to interact
     * with the database for storing and retrieving data.
     *
     * The file name for the SQLite database is "tastybit.db".
     *
     * Note: Ensure that the SQLite JDBC driver is properly included and loaded
     * for successful database connectivity.
     */
    private static final String DB_URL = "jdbc:sqlite:tastybit.db";
    static {
        try {
            // Essa linha registra o driver manualmente
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the SQLite database using the configured database URL.
     *
     * @return a {@code Connection} object representing the connection to the SQLite database
     * @throws SQLException if a database access error occurs or the URL is invalid
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}