package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.DBConnection;
import utils.DBQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Public class for Appointment objects used by appointments table.
 */
public class Appointment {
    private static final ZoneId utcZone = ZoneId.of("UTC");
    private static final ZoneId localZone = ZoneId.systemDefault();
    private int appointmentId;
    private int userId;
    private int customerId;
    private int contactId;
    private String appointmentTitle;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;

    public Appointment(int appointmentId, int userId, int customerId, int contactId, String appointmentTitle, String description, String location, String type, LocalDateTime start, LocalDateTime end) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.customerId = customerId;
        this.contactId = contactId;
        this.appointmentTitle = appointmentTitle;
        this.start = start;
        this.end = end;
        this.description = description;
        this.location = location;
        this.type = type;
    }

    /**
     * Deletes appointment from database.
     * @param selectedAppointment Appointment object to be deleted.
     * @throws SQLException
     */
    public static void deleteAppointment(Appointment selectedAppointment) throws SQLException {
        int selectedId = selectedAppointment.appointmentId;
        DBQuery.deleteStatement("DELETE FROM appointments WHERE Appointment_ID = ?", selectedId);
    }

    /**
     * Gets appointment ID.
     * @return Returns appointment attribute.
     */
    public int getAppointmentId() {
        return appointmentId;
    }

    /**
     * Retrieves contact name related to contact ID of Appointment object.
     * @return Returns string of contact name.
     */
    public String getContactName() {
        try {
            String contactName = "";
            ResultSet result = DBQuery.selectQuery("SELECT Contact_Name FROM contacts WHERE Contact_ID = " + this.contactId);
            while(result.next()) {
                contactName = result.getString("Contact_Name");
            }
            return contactName;
        } catch(SQLException e) {System.out.println("Exception caught while calling getContactName in Appointment: " + e.getMessage());}
        return "";
    }

    /**
     * Sets appointment ID to integer of param.
     * @param appointmentId Appointment ID to set.
     */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * Retrieves start LocalDateTime.
     * @return Returns LocalDateTime attribute start.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Sets end attribute.
     * @param end LocalDateTime to set end attribute to.
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * Sets start attribute.
     * @param start LocalDateTime to set start attribute to.
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Retrieves formatted string from getStart.
     * @return Returns string formatted by DateTimeFormatter.
     */
    public String getStartString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
        return start.format(formatter);
    }

    /**
     * Retrieves end LocalDateTime.
     * @return Returns end LocaldateTime.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Retrieves formatted string from getEnd.
     * @return Returns string formatted by DateTimeFormatter.
     */
    public String getEndString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");
        return end.format(formatter);
    }


    /**
     * Retrieves description.
     * @return Returns string description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     * @param description String to set description to.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Retrieves location.
     * @return Returns string location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets location.
     * @param location String to set location to.
     */
    public void setLocation(String location) {
        this.location = location;
    }


    /**
     * Retrieves type.
     * @return Returns string type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     * @param type String to set type to.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retrieves customerId.
     * @return Returns integer customerId.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets customerId.
     * @param customerId Integer to set customerId to.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Retrieves appointmentTitle.
     * @return Returns string appointmentTitle.
     */
    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    /**
     * Sets appointmentTitle.
     * @param appointmentTitle String to set appointmentTitle to.
     */
    public void setAppointmentTitle(String appointmentTitle) {
        this.appointmentTitle = appointmentTitle;
    }

    /**
     * Retrieves userId.
     * @return Returns string userId.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets userId.
     * @param userId String to set userId to.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Determines if conflicting appointments exists.
     * @param appointmentStart LocalDateTime of appointment start time.
     * @param appointmentEnd LocalDateTime of appointment end time.
     * @param customerId String customerId to filter by.
     * @param appointmentId int appointmentId to compare to appointmentsList.
     * @return Returns boolean true if conflict exists, false otherwise.
     * @throws SQLException
     */
    public static Boolean conflictingAppointment(LocalDateTime appointmentStart, LocalDateTime appointmentEnd, String customerId, int appointmentId) throws SQLException {
        ObservableList<Appointment> appointmentsList = Appointment.getAppointmentsFilteredById(customerId);

        for(Appointment appointment : appointmentsList) {
            // Skips appointment that matches appointment to compare to
            if(appointment.getAppointmentId() == appointmentId) continue;
            LocalDateTime customerStart = appointment.getStart();
            LocalDateTime customerEnd = appointment.getEnd();
            if(appointmentStart.isAfter(customerStart) && appointmentStart.isBefore(customerEnd)) {
                return true;
            } else if (appointmentEnd.isAfter(customerStart) && appointmentEnd.isBefore(customerEnd)) {
                return true;
            } else if (customerStart.isAfter(appointmentStart) && customerStart.isBefore(appointmentEnd)) {
                return true;
            } else if (customerEnd.isAfter(appointmentStart) && customerEnd.isBefore(appointmentEnd)) {
                return true;
            } else if (appointmentStart.isEqual(customerStart) || appointmentEnd.isEqual(customerEnd)) {
                return true;
            };
        }
        return false;
    }

    /**
     * Retrieves a list of Appointment objects filtered by customer ID.
     * @param customerFilterId ID to filter appointments by.
     * @return Returns ObservableList of Appointment objects.
     */
    public static ObservableList<Appointment> getAppointmentsFilteredById(String customerFilterId) {
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
        try {
            // Retrieves data from appointments table using method in DBQuery
            Connection conn = DBConnection.getConnection();
            DBQuery.setPreparedStatement(conn, "SELECT * FROM appointments WHERE Customer_ID = ?");
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setInt(1, Integer.parseInt(customerFilterId));
            ResultSet results = ps.executeQuery();
            appointmentsList.clear();
            // Sets up a DateTimeFormatter to turn results into a LocalDateTime format
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // Loops through each Appointment query result and adds Appointment objects to the appointmentsList
            while(results.next()) {
                // Sets variables for Appointment object fields
                int appointmentId = results.getInt("appointment_id");
                int userID = results.getInt("user_id");
                int customerId = results.getInt("customer_id");
                int contactId = results.getInt("contact_id");
                String appointmentTitle = results.getString("title");
                String appointmentDescription = results.getString("description");
                String appointmentLocation = results.getString("location");
                String appointmentType = results.getString("Type");
                // Saves UTC Appointment start and end times to strings
                String utcStartString = results.getString("Start");
                String utcEndString = results.getString("End");
                // Converts UTC Appointment start and end times to to LocalDateTime Objects
                LocalDateTime utcStart = LocalDateTime.parse(utcStartString, format);
                LocalDateTime utcEnd = LocalDateTime.parse(utcEndString, format);
                // Converts UTC LocalDateTime objects to user's time zone
                LocalDateTime localStart = utcStart.atZone(utcZone).withZoneSameInstant(localZone).toLocalDateTime();
                LocalDateTime localEnd = utcEnd.atZone(utcZone).withZoneSameInstant(localZone).toLocalDateTime();
                // Creates new Appointment object using above variables
                appointmentsList.add(new Appointment(appointmentId, userID, customerId, contactId, appointmentTitle, appointmentDescription, appointmentLocation, appointmentType, localStart, localEnd));
            }
        } catch(Exception e) {System.out.println("Exception caught in getAppointmentsFilteredById in Appointment: " + e.getMessage());}
        return appointmentsList;
    }
}
