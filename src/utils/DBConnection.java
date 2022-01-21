package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Public DBConnection class used to handle Connection objects.
 */
public class DBConnection {
    // URL Parts
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//database-1.ctui2dnqckp8.us-east-2.rds.amazonaws.com:3306/";
    private static final String databaseName = "client_schedule";
    // JDBC URL

    // OLD CONNECTION
    // private static final String jdbcURL = protocol + vendorName + ipAddress;

    // NEW CONNECTION
    private static final String jdbcURL = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER";

    // Driver and Connection Interface Reference
    private static final String MYSQLJDBCDriver = "java.sql.Driver";
    private static Connection conn = null;
    // Credentials
    private static final String username = "admin";
    private static final String password = "password";

    /**
     * Initiates connection to database.
     */
    public static void startConnection() {
        try{
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connection Successful");
        }
        catch(ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retrieves connection object for use in queries.
     * @return Returns connection object.
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Ends connection to database.
     */
    public static void CloseConnection() {
        try {
            conn.close();
            System.out.println("Connection Closed");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
