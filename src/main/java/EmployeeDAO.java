import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for managing database operations related to the Employee entity.
 * Provides methods to create, read, update, and delete employee records in the SQLite database.
 */
public class EmployeeDAO {
    private static final String TABLE_NAME = "employees";


    // Method to create the table
    public static void createTable() throws SQLException {
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "id INTEGER PRIMARY KEY, " +
                        "name TEXT NOT NULL, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "password TEXT NOT NULL, " +
                        "salt TEXT NOT NULL, " +
                        "role TEXT NOT NULL CHECK(role IN ('MANAGER', 'WAITER', 'CHEF'))" +
                        ")", TABLE_NAME);

        try (Connection conn = SQLiteConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    //  Save a new employee
    public static void save(Employee employee) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, name, email, password, salt, role) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = SQLiteConfig.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employee.getId());
            pstmt.setString(2, employee.getName());
            pstmt.setString(3, employee.getEmail());
            pstmt.setString(4, employee.getPasswordHash());
            pstmt.setString(5, employee.getSalt());
            pstmt.setString(6, employee.getRole().toString());

            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }


    public static List<Employee> findAll() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = SQLiteConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String salt = rs.getString("salt");
                EmployeeRole role = EmployeeRole.valueOf(rs.getString("role"));

                Employee emp = new Employee(id, name, email, password, role);
                emp.setSalt(salt);
                employees.add(emp);
            }
        }
        return employees;
    }

    // Search by ID
    public static Employee findById(int id) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE id = ?", TABLE_NAME);
        try (Connection conn = SQLiteConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                EmployeeRole role = EmployeeRole.valueOf(rs.getString("role"));
                Employee emp = new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        role
                );
                emp.setSalt(rs.getString("salt"));
                return emp;
            }
        }
        return null;
    }

    // Update an Employee
    public static void update(Employee employee) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET name = ?, email = ?, password = ?, salt = ?, role = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = SQLiteConfig.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getPasswordHash());
            pstmt.setString(4, employee.getSalt());
            pstmt.setString(5, employee.getRole().toString());
            pstmt.setInt(6, employee.getId());
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }


    // Delete an employee
    public static void delete(int id) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE id = ?", TABLE_NAME);
        try (Connection conn = SQLiteConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public static void recreateTable() throws SQLException {
        try (Connection conn = SQLiteConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + TABLE_NAME);
            createTable();
        }
    }
}