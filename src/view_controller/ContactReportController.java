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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Appointment;
import utils.DBConnection;
import utils.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ContactReportController implements Initializable {
    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
    private final ZoneId utcZone = ZoneId.of("UTC");
    private final ZoneId localZone = ZoneId.systemDefault();
    Stage stage;
    Parent root;
    Parent scene;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, Integer> appointmentIdCol;

    @FXML
    private TableColumn<Appointment, Integer> customerIdCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private TableColumn<Appointment, String> descriptionCol;

    @FXML
    private TableColumn<Appointment, String> startCol;

    @FXML
    private TableColumn<Appointment, String> endCol;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    void onActionBack(ActionEvent event) {
        Button b = (Button)event.getSource();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/Scheduling.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(IOException e) {System.out.println(e);}
    }

    @FXML
    void onActionAll(ActionEvent event) {
        try{
            setAllAppointmentsList();
        } catch (Exception e) {System.out.println("Exception while calling onActionAll in DetailedAppointmentsController: " + e.getMessage());}
    }

    @FXML
    void onActionComboBox(ActionEvent event) {
        try{
            setAppointmentsByContact(contactComboBox.getValue());
        } catch (Exception e) {
            System.out.println("Exception while calling onActionComboBox in DetailedAppointmentsController: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactComboBox.setItems(getContacts());
        try{
            setAllAppointmentsList();
        } catch (Exception e) {System.out.println("Exception while initializing tables in SchedulingController: " + e.getMessage());}

        // Populates appointments table with data from appointmentsList
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentId"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("CustomerId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentTitle"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("StartString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("EndString"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("ContactName"));

        appointmentTable.setItems(appointmentsList);
    }
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

    private int getContactId(String contact) throws SQLException {
        Connection conn = DBConnection.getConnection();
        DBQuery.setPreparedStatement(conn, "SELECT * FROM contacts WHERE Contact_Name = ?");
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setString(1, contact);
        ResultSet results = ps.executeQuery();
        while(results.next()) {
            return results.getInt("Contact_ID");
        }
        return 0;
    }

    private void setAppointmentsByContact(String contact) throws SQLException {
        int selectedContactId = getContactId(contact);
        // Retrieves data from appointments table using method in DBQuery
        Connection conn = DBConnection.getConnection();
        DBQuery.setPreparedStatement(conn, "SELECT * FROM appointments WHERE Contact_ID = ?");
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.setInt(1, selectedContactId);
        ResultSet results = ps.executeQuery();
        appointmentsList.clear();
        // Sets up a DateTimeFormatter to turn results into a LocalDateTime format
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Loops through each Appointment query result and adds Appointment objects to the appointmentsList
        while(results.next()) {
            // Sets variables for Appointment object fields
            int appointmentId = results.getInt("appointment_id");
            int userId = results.getInt("user_id");
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
            appointmentsList.add(new Appointment(appointmentId, userId, customerId, contactId, appointmentTitle, appointmentDescription, appointmentLocation, appointmentType, localStart, localEnd));
        }
    }

    private void setAllAppointmentsList() throws SQLException {
        // Retrieves data from appointments table using method in DBQuery
        ResultSet results = DBQuery.selectQuery("SELECT * FROM appointments");
        appointmentsList.clear();
        // Sets up a DateTimeFormatter to turn results into a LocalDateTime format
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Loops through each Appointment query result and adds Appointment objects to the appointmentsList
        while(results.next()) {
            // Sets variables for Appointment object fields
            int appointmentId = results.getInt("appointment_id");
            int userId = results.getInt("user_id");
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
            appointmentsList.add(new Appointment(appointmentId, userId, customerId, contactId, appointmentTitle, appointmentDescription, appointmentLocation, appointmentType, localStart, localEnd));
        }
    }
}
