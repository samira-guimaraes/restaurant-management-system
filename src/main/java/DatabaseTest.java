import java.sql.Connection;
import java.sql.SQLException;

/**
 * The DatabaseTest class is a utility that demonstrates the process of establishing
 * a connection to an SQLite database and performing basic operations such as table creation.
 *
 * The class utilizes the SQLiteConfig utility to obtain a database connection. It creates a
 * table named "teste" if it does not already exist. Any issues during the connection process
 * or SQL operations are handled and logged appropriately.
 *
 * Responsibilities include:
 * - Establishing a connection to the SQLite database.
 * - Creating a sample table in the database.
 */
public class DatabaseTest {
    public static void main(String[] args) {
        try (Connection conn = SQLiteConfig.getConnection()) {
            System.out.println("✅ SQLite connection established!");

            // Criar uma tabela teste
            conn.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS teste (id INTEGER PRIMARY KEY, nome TEXT)");

            System.out.println("✅ Table created successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Erro na conexão: " + e.getMessage());
        }
    }
}
