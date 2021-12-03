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
import utils.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Public class for Scheduling Controller.
 */
public class SchedulingController implements Initializable {
    private ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
    private ObservableList<Customer> customersList = FXCollections.observableArrayList();
    private final ZoneId utcZone = ZoneId.of("UTC");
    private final ZoneId localZone = ZoneId.systemDefault();
    Stage stage;
    Parent root;
    Parent scene;

    @FXML
    private TableView<Appointment> appointmentsTable;

    @FXML
    private TableColumn<Appointment, Integer> appointmentsIdCol;

    @FXML
    private TableColumn<Appointment, Integer> appointmentsUserIdCol;

    @FXML
    private TableColumn<Appointment, Integer> appointmentsCustomerIdCol;

    @FXML
    private TableColumn<Appointment, String> appointmentsTitleCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentsStartCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentsEndCol;

    @FXML
    private Button appointmentsCreate;

    @FXML
    private Button appointmentsModify;

    @FXML
    private Button appointmentsDelete;

    @FXML
    private Button viewAppointmentsButton;

    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, Integer> customerIdCol;

    @FXML
    private TableColumn<Customer, String> customerNameCol;

    @FXML
    private TableColumn<Customer, String> customerAddressCol;

    @FXML
    private TableColumn<Customer, String> customerPhoneCol;

    @FXML
    private TableColumn<Customer, String> customerDivisionCol;

    @FXML
    private TableColumn<Customer, String> customerCountryCol;

    @FXML
    private Button customersCreate;

    @FXML
    private Button customersModify;

    @FXML
    private Button customersDelete;

    @FXML
    private Button logOutButton;

    @FXML
    private Label userNameLabel;

    @FXML
    private Button customerReportButton;

    @FXML
    private Button detailedReportButton;

    @FXML
    private Button userReportButton;

    @FXML
    void onActionLogOut(ActionEvent event) throws SQLException {
        Validation.setUserLogged("");

        Button b = (Button)event.getSource();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/Login.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(IOException e) {System.out.println(e.getMessage());}
    }

    @FXML
    void onActionAppointmentsCreate(ActionEvent event) throws IOException {
        Button b = (Button)event.getSource();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/CreateAppointment.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(IOException e) {System.out.println(e);}
    }

    @FXML
    void onActionAppointmentsModify(ActionEvent event) throws IOException {
        if(!appointmentsTable.getSelectionModel().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyAppointment.fxml"));
                root = loader.load();

                ModifyAppointmentController modifyAppointment = loader.getController();
                Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
                modifyAppointment.setSelectedAppointment(selectedAppointment);
                modifyAppointment.setAppointmentFields();

                stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                scene = root;
                stage.setScene(new Scene(scene));
                stage.show();
            } catch (IOException e) {
                System.out.println("Exception caught while calling onActionAppointmentsModify in SchedulingController" + e.getMessage());
            }
        } else {
            Popups.errorPopup("Please select an appointment to modify");
        }
    }

    @FXML
    void onActionAppointmentsDelete(ActionEvent event) throws SQLException {
        if(!appointmentsTable.getSelectionModel().isEmpty()) {
            Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("You are attempting to delete an appointment");
            alert.setContentText("Are you sure?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                appointmentsList.remove(selectedAppointment);
                Appointment.deleteAppointment(selectedAppointment);
                Popups.informationalPopup("Appointment of ID " + selectedAppointment.getAppointmentId() + " and type " + selectedAppointment.getType() + " successfully deleted", "Alert");
            }
        } else {
            Popups.errorPopup("Please select an appointment to delete");
        }
    }

    @FXML
    void onActionCustomersCreate(ActionEvent event) {
        Button b = (Button)event.getSource();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/CreateCustomer.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(IOException e) {System.out.println("Exception caught while calling onActionCustomerCreate in SchedulingController" + e.getMessage());}
    }

    @FXML
    void onActionCustomersModify(ActionEvent event) {
        if(!customersTable.getSelectionModel().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyCustomer.fxml"));
                root = loader.load();

                ModifyCustomerController modifyCustomer = loader.getController();
                Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
                modifyCustomer.setSelectedCustomer(selectedCustomer);
                modifyCustomer.setCustomerFields();

                stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                scene = root;
                stage.setScene(new Scene(scene));
                stage.show();
            } catch (IOException | SQLException e) {
                System.out.println("Exception caught while calling onActionAppointmentsModify in SchedulingController" + e.getMessage());
            }
        } else {
            Popups.errorPopup("Please select a customer to modify");
        }
    }

    @FXML
    void onActionCustomersDelete(ActionEvent actionEvent) throws SQLException {
        if(!customersTable.getSelectionModel().isEmpty()) {
            Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("You are attempting to delete a customer");
            alert.setContentText("Are you sure?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK){
                if (Customer.deleteCustomer(selectedCustomer) > 0) {
                    customersList.remove(selectedCustomer);
                    Popups.informationalPopup("Customer " + selectedCustomer.getCustomerName() + " successfully deleted", "Alert");
                }
                else Popups.errorPopup("Cannot delete customer who still has appointment set. Delete corresponding appointment if you wish to delete customer from system.");
            }
        } else {
            Popups.errorPopup("Please select a customer to delete");
        }
    }

    @FXML
    void onActionViewAppointments(ActionEvent event) {
        Button b = (Button)event.getSource();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/ContactReport.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(IOException e) {System.out.println(e);}
    }

    @FXML
    void onActionCustomerReport(ActionEvent event) {
        Button b = (Button)event.getSource();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/TypeReport.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(IOException e) {System.out.println(e);}
    }

    @FXML
    void onActionDetailed(ActionEvent event) {
        Button b = (Button)event.getSource();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/AppointmentReport.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(IOException e) {System.out.println(e);}
    }

    @FXML
    void onActionUserReport(ActionEvent event) {
        Button b = (Button)event.getSource();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        try{
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/UserReport.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(IOException e) {System.out.println(e);}
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populates appointmentsList with sql data and prints exception message to console if exception is caught
        try{
            setAppointmentsList();
            setCustomersList();
        } catch (Exception e) {System.out.println("Exception while initializing tables in SchedulingController: " + e.getMessage());}

        // Sets username label to display current username to user
        userNameLabel.setText("Logged in as: " + Validation.getCurrentUser());

        // Populates appointments table with data from appointmentsList
        appointmentsIdCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentId"));
        appointmentsUserIdCol.setCellValueFactory(new PropertyValueFactory<>("UserId"));
        appointmentsCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("CustomerId"));
        appointmentsTitleCol.setCellValueFactory(new PropertyValueFactory<>("AppointmentTitle"));
        appointmentsStartCol.setCellValueFactory(new PropertyValueFactory<>("StartString"));
        appointmentsEndCol.setCellValueFactory(new PropertyValueFactory<>("EndString"));

        appointmentsTable.setItems(appointmentsList);

        // Populates customers table with data from customersList
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerDivisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        customerCountryCol.setCellValueFactory(new PropertyValueFactory<>("countryName"));

        customersTable.setItems(customersList);
    }

    /**
     * Fills appointments table with Appointment objects from database.
     * @throws SQLException
     */
    private void setAppointmentsList() throws SQLException {
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

    /**
     * Fills customers table with Customer objects from database.
     * @throws SQLException
     */
    private void setCustomersList() throws SQLException {
        ResultSet results = DBQuery.selectQuery("SELECT * FROM customers");
        customersList.clear();
        while(results.next()) {
            int id = results.getInt("Customer_ID");
            String customerName = results.getString("Customer_Name");
            String address = results.getString("Address");
            String postalCode = results.getString("Postal_Code");
            String phone = results.getString("Phone");
            int division = results.getInt("Division_ID");
            customersList.add(new Customer(id, customerName, address, postalCode, phone, division));
        }
    }
}