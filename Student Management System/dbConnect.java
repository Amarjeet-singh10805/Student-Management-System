import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles MySQL database connection.
 * Update DB_URL, DB_USER, and DB_PASSWORD to match your MySQL setup.
 */
public class dbConnect {

    // ── ▼ CHANGE THESE TO MATCH YOUR MYSQL SETUP ▼ ──────────────────────────
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/studata?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER     = "root";    
    private static final String DB_PASSWORD = "Jeet12";  
    // ── ▲ ────────────────────────────────────────────────────────────────────

    private static Connection connection = null;

    /** Returns a singleton Connection, creating it if necessary. */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connected successfully.");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found. Add mysql-connector-j.jar to classpath.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    /** Closes the connection (call on application exit). */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
