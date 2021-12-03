package utils;

import javax.xml.transform.Result;
import java.sql.*;

/**
 * Public DBQuery class used to handle database queries and prepared statements.
 */
public class DBQuery {
    private static PreparedStatement statement;
    private static Connection conn = DBConnection.getConnection();

    /**
     * Creates a prepared statement, executes it, and returns a resultset.
     * @param query String to turn into a PreparedStatement object.
     * @return Returns a ResultSet object of an executed PreparedStatement.
     * @throws SQLException
     */
    public static ResultSet selectQuery(String query) throws SQLException {
        setPreparedStatement(conn, query);
        PreparedStatement ps = getPreparedStatement();
        ResultSet rs = ps.executeQuery();
        return rs;
    }

    /**
     * Creates a prepared statement for purpose of deleting a database record.
     * @param statement String to turn into a PreparedStatement object.
     * @param recordId ID of record to be deleted.
     * @return Returns the amount of deleted records.
     * @throws SQLException
     */
    public static int deleteStatement(String statement, int recordId) throws SQLException {
        setPreparedStatement(conn, statement);
        PreparedStatement ps = getPreparedStatement();

        // Sets ? in DELETE statement to int recordId
        ps.setInt(1, recordId);
        int updatedCount = 0;
        try {
            updatedCount = ps.executeUpdate();
        } catch(Exception e) {
            System.out.println("Exception caught while calling deleteStatement in DBQuery: " + e.getMessage());
        };
        return updatedCount;
    }

    /**
     * Executes a prepared statement and returns boolean if a record was successfully inserted.
     * @param statement String to turn into a PreparedStatement object.
     * @return Returns a boolean if executed insert query successfully inserts record.
     * @throws SQLException
     */
    public static Boolean insertStatement(String statement) throws SQLException {
        setPreparedStatement(conn, statement);
        PreparedStatement ps = getPreparedStatement();
        try{return ps.execute();} catch(SQLException e) {System.out.println("Exception caught while calling insertStatement in DBQuery: " + e.getMessage());}
        return null;
    }

    /**
     * Sets prepared statement.
     * @param conn Connection object to prepare query on.
     * @param sqlStatement String representing statement to set statement to.
     */
    public static void setPreparedStatement(Connection conn, String sqlStatement) {
        try {
            statement = conn.prepareStatement(sqlStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Gets the prepared statement.
     * @return Returns PreparedStatement object, statement.
     */
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }
}
