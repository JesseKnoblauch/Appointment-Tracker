package view_controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Appointment;
import utils.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ResourceBundle;

/**
 * Public class for ModifyAppointment Controller.
 */
public class ModifyAppointmentController implements Initializable {
    private Appointment selectedAppointment;

    @FXML
    private DatePicker startDate;

    @FXML
    private ComboBox<String> startHour;

    @FXML
    private ComboBox<String> startMin;

    @FXML
    private ComboBox<String> endHour;

    @FXML
    private ComboBox<String> endMin;

    @FXML
    private ComboBox<String> customerComboBox;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TextField createTitleField;

    @FXML
    private TextField createDescriptionField;

    @FXML
    private TextField createLocationField;

    @FXML
    private TextField createTypeField;

    @FXML
    private TextField createUserField;

    @FXML
    private TextField createCustomerField;

    @FXML
    private Button updateAppointmentButton;

    @FXML
    private Button createAppointmentBack;

    @FXML
    private Label feedbackLabel;

    @FXML
    private ComboBox<String> startPeriod;

    @FXML
    private ComboBox<String> endPeriod;

    @FXML
    void onActionBack(ActionEvent event) throws IOException {
        try {
            Button b = (Button)event.getSource();
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/Scheduling.fxml"));

            stage.setScene(new Scene(scene));
            stage.show();
        } catch (Exception e) {System.out.println("Exception when calling onActionBack in ModifyAppointmentController: " + e.getMessage());}
    }

    @FXML
    void onActionUpdate(ActionEvent event) throws SQLException {
        if(isValidAppointment()) {
            updateAppointment();
        }
    }

    /**
     * Contains logic for updating Appointment object in database.
     * @throws SQLException
     */
    private void updateAppointment() throws SQLException {
        // Initializes variables to hold time data, and retrieves data from ComboBoxes and DatePicker
        String startH = Time.timePeriodConverter.convertHour(startHour.getValue(), startPeriod.getValue());
        String startM = startMin.getValue();
        String endH = Time.timePeriodConverter.convertHour(endHour.getValue(), startPeriod.getValue());
        String endM = endMin.getValue();
        LocalDate appointmentDate = startDate.getValue();
        int contactId = Integer.parseInt(getContactId(contactComboBox.getValue()));
        // Uses method from utils.Time to build LocalDateTime objects for both start and end time
        LocalDateTime appointmentStartTime = Time.buildLocalDateTime(appointmentDate, startH, startM);
        LocalDateTime appointmentEndTime = Time.buildLocalDateTime(appointmentDate, endH, endM);
        // Sets variables for User's time zone, and UTC zone
        final ZoneId utcZone = ZoneId.of("UTC");
        final ZoneId localZone = ZoneId.systemDefault();
        // Uses ZoneId time zones to convert LocalDateTime objects to UTC time, from the User's default time zone
        LocalDateTime utcStart = appointmentStartTime.atZone(localZone).withZoneSameInstant(utcZone).toLocalDateTime();
        LocalDateTime utcEnd = appointmentEndTime.atZone(localZone).withZoneSameInstant(utcZone).toLocalDateTime();
        // Prepared Statement
        try {
            Connection conn = DBConnection.getConnection();
            DBQuery.setPreparedStatement(conn, "UPDATE appointments SET Customer_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Contact_ID = ? WHERE Appointment_ID = ?");
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setInt(1, Integer.parseInt(getCustomerId(customerComboBox.getValue())));
            ps.setString(2, createTitleField.getText());
            ps.setString(3, createDescriptionField.getText());
            ps.setString(4, createLocationField.getText());
            ps.setString(5, createTypeField.getText());
            ps.setObject(6, utcStart);
            ps.setObject(7, utcEnd);
            ps.setInt(8, contactId);
            ps.setInt(9, selectedAppointment.getAppointmentId());
            if(ps.executeUpdate() > 0) {
                System.out.println("Appointment for " + customerComboBox.getValue() + " updated successfully");
                Popups.informationalPopup("Appointment for " + customerComboBox.getValue() + " updated successfully", "Alert");
            }
        } catch (SQLException e) {System.out.println("Exception caught while calling updateAppointment in modifyAppointmentController: " + e.getMessage());}
    }

    /**
     * Checks for exceptions and input errors to ensure Appointment object meets constraints.
     * @return Returns Boolean representing whether Appointment object meets qualifications to be updated in database.
     */
    private Boolean isValidAppointment () {
        String errorMsg = "";
        String customerId = getCustomerId(customerComboBox.getValue());
        Boolean isInt = false;
        try{
            Integer.parseInt(customerId);
            isInt = true;
        } catch(NumberFormatException e) {isInt = false;}

        // Sets variables for text inserted into fields
        String title = createTitleField.getText();
        String description = createDescriptionField.getText();
        String location = createLocationField.getText();
        String type = createTypeField.getText();
        String startH = Time.timePeriodConverter.convertHour(startHour.getValue(), startPeriod.getValue());
        String startM = startMin.getValue();
        String endH = Time.timePeriodConverter.convertHour(endHour.getValue(), endPeriod.getValue());
        String endM = endMin.getValue();
        LocalDate appointmentDate = startDate.getValue();
        String contact = contactComboBox.getValue();

        // Builds LocalDateTime objects for time validation, verifies that appointment start time is before end time, and that date selected is not prior to current date
        LocalDateTime appointmentStartTime = Time.buildLocalDateTime(appointmentDate, startH, startM);
        LocalDateTime appointmentEndTime = Time.buildLocalDateTime(appointmentDate, endH, endM);

        // If statements checking for exceptions
        try {
            if(appointmentStartTime.isBefore(LocalDateTime.now())) errorMsg += "Appointment start time must be after current time\n";
            if(!Time.startBeforeEnd(appointmentStartTime, appointmentEndTime)) errorMsg += "End time must be after start time\n";
        } catch(Exception e) {System.out.println("Exception caught while calling isValidAppointment in modifyAppointmentController: " + e.getMessage());}

        try {
            if(Appointment.conflictingAppointment(appointmentStartTime, appointmentEndTime, customerId, selectedAppointment.getAppointmentId()))
                errorMsg += "Appointment cannot overlap with existing appointment for customer\n";
        } catch(Exception e) {System.out.println("Exception caught while calling conflictingAppointment in CreateAppointmentController: " + e.getMessage());}

        try {
            if(!Time.isOpenHours(appointmentStartTime, appointmentEndTime)) errorMsg += "Appointment must be set between the hours of 8:00AM and 10:00PM EST\n";
        } catch(Exception e) {System.out.println("Exception caught while calling isOpenHours in CreateAppointmentController: " + e.getMessage());}

        if(customerId == null || customerId.length() == 0) errorMsg += "Please select a customer\n";
        if(customerId.length() > 0 && !isInt) errorMsg += "Customer ID must be a number\n";
        if(title == null || title.length() == 0) errorMsg += "Please enter a title\n";
        if(description == null || description.length() == 0) errorMsg += "Please enter a description\n";
        if(location == null || location.length() == 0) errorMsg += "Please enter a location\n";
        if(type == null || type.length() == 0) errorMsg += "Please enter a type\n";
        if(contact == null || contact.length() == 0) errorMsg += "Please select a contact\n";
        if(startHour.getValue() == null) errorMsg += "Please enter an hour for appointment start\n";
        if(endHour.getValue() == null) errorMsg += "Please enter an hour for appointment end\n";
        if(startMin.getValue() == null) errorMsg += "Please enter a minute for appointment start\n";
        if(endMin.getValue() == null) errorMsg += "Please enter a minute for appointment end\n";
        if(startDate.getValue() == null) errorMsg += "Please select a date for appointment\n";

        // Checks if an error message has been set, calls any error messages, and returns false if an error has been called
        if(errorMsg.length() > 0) {
            Popups.errorPopup(errorMsg);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Method called while loading modifyAppointment. Fills appointment fields with data from selectedAppointment selected on main menu.
     */
    public void setAppointmentFields() {
        customerComboBox.setValue(getCustomerName(selectedAppointment.getCustomerId()));
        createTitleField.setText(selectedAppointment.getAppointmentTitle());
        createDescriptionField.setText(selectedAppointment.getDescription());
        createLocationField.setText(selectedAppointment.getLocation());
        createTypeField.setText(selectedAppointment.getType());
        startDate.setValue(selectedAppointment.getStart().toLocalDate());
        contactComboBox.setValue(selectedAppointment.getContactName());
        String startHourString = String.valueOf(selectedAppointment.getStart().getHour());
        String endHourString = String.valueOf(selectedAppointment.getEnd().getHour());
        String startMinString = String.valueOf(selectedAppointment.getStart().getMinute());
        String endMinString = String.valueOf(selectedAppointment.getEnd().getMinute());
        if(Integer.parseInt(startHourString) > 11) startPeriod.setValue("PM"); else startPeriod.setValue("AM");
        if(Integer.parseInt(endHourString) > 11) endPeriod.setValue("PM"); else endPeriod.setValue("AM");
        startHourString = Time.periodToMilitary.convertBack(startHourString);
        endHourString = Time.periodToMilitary.convertBack(endHourString);
        if(startMinString.length() < 2) startMinString = "0" + startMinString;
        if(endMinString.length() < 2) endMinString = "0" + endMinString;
        if(startHourString.length() < 2) startHourString = "0" + startHourString;
        if(endHourString.length() < 2) endHourString = "0" + endHourString;
        startHour.setValue(startHourString);
        endHour.setValue(endHourString);
        startMin.setValue(startMinString);
        endMin.setValue(endMinString);
    }

    /**
     * Creates a list of options to fill minute selection combo boxes.
     * @return Returns ObservableList containing strings to represent minute selection options.
     */
    private ObservableList<String> minuteGenerator() {
        ObservableList<String> minutes = FXCollections.observableArrayList();
        int i;
        minutes.addAll("00", "15", "30", "45");
        return minutes;
    }

    /**
     * Creates a list of options to fill hour selection combo boxes.
     * @return Returns ObservableList containing strings to represent hour selection options.
     */
    private ObservableList<String> hourGenerator() {
        ObservableList<String> hours = FXCollections.observableArrayList();
        int i;
        for (i=1; i < 13; i++) {
            if(i < 10) {hours.add("0" + i) ;}
            else {hours.add(String.valueOf(i));}
        }
        return hours;
    }

    /**
     * Retrieves customer names to fill customer selection combo box.
     * @return Returns ObservableList of strings to represent customer name options.
     */
    private ObservableList<String> getCustomers() {
        Connection conn = DBConnection.getConnection();
        ObservableList<String> customers = FXCollections.observableArrayList();
        DBQuery.setPreparedStatement(conn, "SELECT * FROM customers");
        PreparedStatement ps = DBQuery.getPreparedStatement();
        try {
            ResultSet results = ps.executeQuery();
            while(results.next()) {
                String id = results.getString("Customer_Name");
                customers.add(id);
            }
        } catch(SQLException e) {System.out.println("Exception caught while calling getCustomers in modifyAppointmentController: " + e.getMessage());}

        return customers;
    }

    /**
     * Gets customername related to customer ID. Called in setAppointmentFields.
     * @param customerId ID integer of selectedCustomer.
     * @return Returns string of customer name related to customer ID.
     */
    private String getCustomerName(int customerId) {
        Connection conn = DBConnection.getConnection();
        DBQuery.setPreparedStatement(conn, "SELECT * FROM customers WHERE Customer_ID = ?");
        PreparedStatement ps = DBQuery.getPreparedStatement();
        String customerName = "";
        try {
            ps.setInt(1, customerId);
            ResultSet results = ps.executeQuery();
            while(results.next()) {
                customerName = results.getString("Customer_Name");
            }
        } catch(SQLException e) {System.out.println("Exception caught while calling getCustomerName in modifyAppointmentController: " + e.getMessage());}
        return customerName;
    }

    /**
     * Retrieves list of contact names to fill in contact combo box.
     * @return Returns ObservableList of strings representing possible contact names for user to select.
     */
    private ObservableList<String> getContacts() {
        Connection conn = DBConnection.getConnection();
        ObservableList<String> contacts = FXCollections.observableArrayList();
        DBQuery.setPreparedStatement(conn, "SELECT * from contacts");
        PreparedStatement ps = DBQuery.getPreparedStatement();
        try {
            ResultSet results = ps.executeQuery();
            while(results.next()) {
                String contactName = results.getString("Contact_Name");
                contacts.add(contactName);
            }
        } catch(SQLException e) {System.out.println("Exception caught while calling getContacts in createAppointmentController" + e.getMessage());}
        return contacts;
    }

    /**
     * Retrieves contact ID relating to contact name selected in contact name combo box.
     * @param contactName String representing contact name selected by user.
     * @return Returns string representing the contact ID of selected contact.
     */
    private String getContactId(String contactName) {
        String contactId = "";
        Connection conn = DBConnection.getConnection();
        DBQuery.setPreparedStatement(conn, "SELECT Contact_ID from contacts where Contact_Name = " + "'" + contactName + "'");
        PreparedStatement ps = DBQuery.getPreparedStatement();
        try {
            ResultSet results = ps.executeQuery();
            while(results.next()) {
                String id = results.getString("Contact_ID");
                contactId = id;
            }
        } catch(SQLException e) {System.out.println("Exception caught while calling getContacts in createAppointmentController" + e.getMessage());}
        return contactId;
    }

    /**
     * Retrieves customer ID relating to customer name selected in customer name combo box.
     * @param customerName String representing customer name selected by user.
     * @return Returns string representing the customer ID of selected customer.
     */
    private String getCustomerId(String customerName) {
        Connection conn = DBConnection.getConnection();
        DBQuery.setPreparedStatement(conn, "SELECT * FROM customers WHERE Customer_Name = ?");
        PreparedStatement ps = DBQuery.getPreparedStatement();
        String customerId = "";
        try {
            ps.setString(1, customerName);
            ResultSet results = ps.executeQuery();
            while(results.next()) {
                customerId = results.getString("Customer_ID");
            }
        } catch(SQLException e) {System.out.println("Exception caught while calling getCustomerId in modifyAppointmentController: " + e.getMessage());}
        return customerId;
    }

    /**
     * Creates options for AM/PM combobox.
     * @return Returns short ObservableList of strings for both AM and PM options to fill AM/PM combobox.
     */
    private ObservableList<String> getPeriods() {
        ObservableList<String> periods = FXCollections.observableArrayList();
        periods.add("AM");
        periods.add("PM");
        return periods;
    }

    /**
     * Sets selectedAppointment variable.
     * @param appointment Appointment object to set selectedAppointment to.
     */
    public void setSelectedAppointment(Appointment appointment) {
        selectedAppointment = appointment;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createUserField.setText(String.valueOf(Validation.getCurrentUserId()));
        startHour.setItems(hourGenerator());
        endHour.setItems(hourGenerator());
        customerComboBox.setItems(getCustomers());
        contactComboBox.setItems(getContacts());
        startPeriod.setItems(getPeriods());
        endPeriod.setItems(getPeriods());

        startMin.setItems(minuteGenerator());
        endMin.setItems(minuteGenerator());
    }
}
