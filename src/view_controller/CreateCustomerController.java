package view_controller;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;
import utils.Popups;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Public class for CreateCustomer Controller.
 */
public class CreateCustomerController implements Initializable {
    @FXML
    private TextField customerName;

    @FXML
    private TextField customerAddress;

    @FXML
    private TextField customerPostal;

    @FXML
    private TextField customerPhone;

    @FXML
    private ComboBox<String> customerState;

    @FXML
    private ComboBox<String> customerCountry;

    @FXML
    private Button createAppointmentSave;

    @FXML
    private Button createAppointmentBack;

    @FXML
    private Label feedbackLabel;

    @FXML
    void onActionBack(ActionEvent event) {
        try {
            Button b = (Button)event.getSource();
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view_controller/Scheduling.fxml"));

            stage.setScene(new Scene(scene));
            stage.show();
        } catch (Exception e) {System.out.println("Exception when calling onActionBack in CreateCustomerController: " + e.getMessage());}
    }

    @FXML
    void onActionSave(ActionEvent event) {
        if(isValidCustomer()) {
            saveCustomer();
        }
    }

    /**
     * Adds customer to database using form fields.
     */
    private void saveCustomer() {
        try {
            String name = customerName.getText();
            String address = customerAddress.getText();
            String postal = customerPostal.getText();
            String phone = customerPhone.getText();
            int division = Integer.parseInt(getDivision());
            Connection conn = DBConnection.getConnection();
            DBQuery.setPreparedStatement(conn, "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES(?, ?, ?, ?, ?)");
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, postal);
            ps.setString(4, phone);
            ps.setInt(5, division);
            if(ps.executeUpdate() > 0) {
                System.out.println("Customer " + name + " added successfully");
                Popups.informationalPopup("Customer " + name + " added successfully", "Alert");
            }
        } catch (Exception e) {System.out.println("Exception caught while calling saveAppointment in CreateAppointmentController: " + e.getMessage());}
    }

    /**
     * Checks for possible exceptions and displays alert box if customer input does not meet requirements.
     * @return Returns boolean representing if customer is valid or not.
     */
    private boolean isValidCustomer() {
        String errorMsg = "";
        String state = customerState.getValue();
        String country = customerCountry.getValue();
        String address = customerAddress.getText();
        String name = customerName.getText();
        String phone = customerPhone.getText();
        String postal = customerPostal.getText();

        // Checks for empty fields
        if(state == null || state.length() == 0) errorMsg += "Please select a state\n";
        if(country == null || country.length() == 0) errorMsg += "Please select a country\n";
        if(address == null || address.length() == 0) errorMsg += "Please input an address\n";
        if(name == null || name.length() == 0) errorMsg += "Please input a name\n";
        if(phone == null || phone.length() == 0) errorMsg += "Please input a phone number\n";
        if(postal == null || postal.length() == 0) errorMsg += "Please input a postal code\n";

        // Checks if an error message has been set, calls any error messages, and returns false if an error has been called
        if(errorMsg.length() > 0) {
            Popups.errorPopup(errorMsg);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Retrieves all countries from database to fill country combobox.
     * @return Returns ObservableList of strings representing all countries in database
     */
    private ObservableList<String> getCountries() {
        ObservableList<String> countriesList = FXCollections.observableArrayList();
        try {
            ResultSet results = DBQuery.selectQuery("SELECT Country FROM countries");
            while (results.next()) {
                String state = results.getString("Country");
                countriesList.add(state);
            }
        } catch(Exception e) {System.out.println("Exception caught while calling getCountries in CreateCustomerController" + e.getMessage());}
        return countriesList;
    }

    /**
     * Retrieves country ID related to user's selected country.
     * @return Returns string representing country ID.
     */
    private String getCountryId() {
        try {
            Connection conn = DBConnection.getConnection();
            DBQuery.setPreparedStatement(conn, "SELECT Country_ID FROM countries WHERE Country = ?");
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, customerCountry.getValue());
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                return results.getString("Country_ID");
            }
        } catch(Exception e) {System.out.println("Exception caught while calling getCountryId in CreateCustomerController" + e.getMessage());}
        return "";
    }

    /**
     * Retrieves division ID related to division name and country ID.
     * @return Returns string of related division ID.
     */
    private String getDivision() {
        try {
            Connection conn = DBConnection.getConnection();
            DBQuery.setPreparedStatement(conn, "SELECT Division_ID FROM first_level_divisions WHERE COUNTRY_ID = ? AND Division = ?");
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, getCountryId());
            ps.setString(2, customerState.getValue());
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                return results.getString("Division_ID");
            }
        } catch(Exception e) {System.out.println("Exception caught while calling getDivision in CreateCustomerController" + e.getMessage());}
        return "";
    }

    /**
     * Retrieves division/state names related to selected country. This method is called by onActionCountryPicked after a country is selected by a user.
     * @param country String of country name selected by user.
     * @return Returns ObservableList of strings representing the country selected by the user.
     */
    private ObservableList<String> getStates(String country) {
        ObservableList<String> statesList = FXCollections.observableArrayList();
        try {
            Connection conn = DBConnection.getConnection();
            DBQuery.setPreparedStatement(conn, "SELECT Division FROM first_level_divisions WHERE COUNTRY_ID = ?");
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, getCountryId());
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String state = results.getString("Division");
                statesList.add(state);
            }
        } catch(Exception e) {System.out.println("Exception caught while calling getStates in CreateCustomerController" + e.getMessage());}
        return statesList;
    }

    /**
     * Called when a country is selected by user. Enables state/division combobox and fills it with data from getStates method.
     * @param actionEvent Action event trigged by selected a country.
     */
    public void onActionCountryPicked(ActionEvent actionEvent) {
        customerState.setDisable(false);
        String country = customerCountry.getValue();
        customerState.setItems(getStates(country));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerCountry.setItems(getCountries());
    }
}
