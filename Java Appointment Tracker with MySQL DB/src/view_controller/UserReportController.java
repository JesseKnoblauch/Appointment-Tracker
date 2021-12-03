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
import models.Customer;
import utils.DBQuery;
import utils.Validation;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;

public class UserReportController implements Initializable {
    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
    private final ZoneId utcZone = ZoneId.of("UTC");
    private final ZoneId localZone = ZoneId.systemDefault();
    Stage stage;
    Parent root;
    Parent scene;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, Integer> appointmentIdCol;

    @FXML
    private TableColumn<Appointment, Integer> userIdCol;

    @FXML
    private TableColumn<Appointment, Integer> customerIdCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private TableColumn<Appointment, String> locationCol;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private TableColumn<Appointment, String> descriptionCol;

    @FXML
    private TableColumn<Appointment, String> startCol;

    @FXML
    private TableColumn<Appointment, String> endCol;

    @FXML
    private Button backButton;

    @FXML
    private Label userReportLabel;

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
            setAllAppointments();
        } catch (Exception e) {System.out.println("Exception while calling onActionAll in DetailedAppointmentsController: " + e.getMessage());}
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userReportLabel.setText("User Reports for User: " + Validation.getCurrentUser());
        // Populates appointmentsList with sql data and prints exception message to console if exception is caught
        try{
            setAllAppointments();
        } catch (Exception e) {System.out.println("Exception while initializing tables in UserReportController: " + e.getMessage());}

        // Populates appointments table with data from appointmentsList
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentId"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("UserId"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("CustomerId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentTitle"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("StartString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("EndString"));

        appointmentTable.setItems(appointmentsList);
    }

    private void setAllAppointments() throws SQLException {
        // Retrieves data from appointments table using method in DBQuery
        ResultSet results = DBQuery.selectQuery("SELECT * FROM appointments WHERE User_ID = " + Validation.getCurrentUserId());
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
