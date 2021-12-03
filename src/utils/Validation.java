package utils;

import javafx.scene.control.Alert;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Scanner;

/**
 * Public validation class to handle user validation logic.
 */
public class Validation {
    private static String userName;
    private static int userId;

    /**
     * Retrieves string from userName attribute.
     * @return Returns userName string.
     */
    public static String getCurrentUser() {
        return userName;
    }

    /**
     * Retrieves string from userId attribute.
     * @return Returns userId string.
     */
    public static int getCurrentUserId() {
        return userId;
    }

    /**
     * Writes login attempt data into login_activity.txt.
     * @param username Username inputted by user at login.
     * @param password Password inputted by user at login.
     * @param success Boolean of whether or not login attempt was a success.
     * @throws IOException
     */
    public static void loginAttempt(String username, String password, boolean success) throws IOException {
        String filename = "login_activity.txt";
        System.out.println("Login attempted...\nUsername: " + username + "\nPassword: " + password + "\nAttempt success: " + success);
        // Writes data into login_activity. Uses buildAttemptString to format the data before writing to file
        FileWriter fileWrite = new FileWriter(filename, true);
        PrintWriter outputFile = new PrintWriter(fileWrite);
        outputFile.println(buildAttemptString(username, password, success));
        // Closes file at end of method
        outputFile.close();
    }

    /**
     * Takes data and builds a line of text to insert into login_activity.
     * @param username Username inputted by user at login.
     * @param password Password inputted by user at login.
     * @param success Boolean of whether or not login attempt was a success.
     * @return Returns a formatted string of login time, username, password, and login success.
     */
    private static String buildAttemptString(String username, String password, boolean success) {
        Timestamp timeStamp = Timestamp.from(Instant.now());
        String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(timeStamp);
        return  "|" + time + align(20-time.length()) +
                "|" + username + align(12-username.length()) +
                "|" + password + align(12-password.length()) +
                "|" + success + align(7-String.valueOf(success).length()) + "|";
    }

    /**
     * Creates whitespace to align data into login_activity table.
     * @param space Amount of whitespace needed.
     * @return Returns a concatenated string of whitespace.
     */
    private static String align(int space) {
        String spaces = "";
        for(int i = 0; i < space; i++) {
            spaces += " ";
        }
        return spaces;
    }

    /**
     * Sets userName and userId attributes based on the current user.
     * @param user String of current username.
     * @throws SQLException
     */
    public static void setUserLogged(String user) throws SQLException {
        userName = user;
        userId = getUserId(userName);
    }

    /**
     * Retrieves ID of user related to username in database..
     * @param user String of username to search database for.
     * @return Returns ID of user record related to username.
     */
    private static int getUserId(String user) {
        String getUserLoggedQuery = "SELECT User_ID FROM users WHERE User_Name='" + user + "'";
        try{
            ResultSet rs = DBQuery.selectQuery(getUserLoggedQuery);
            while(rs.next()) {
                return rs.getInt("User_ID");
            }
        } catch(SQLException e) {System.out.println("Exception caught calling getUserLogged in LoginController: " + e.getMessage());}
        return 0;
    }
}
