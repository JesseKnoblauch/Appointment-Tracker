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
import models.Type;
import utils.DBConnection;
import utils.DBQuery;
import utils.Popups;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.ResourceBundle;

public class TypeReportController implements Initializable {
    private ObservableList<Type> typeList = FXCollections.observableArrayList();
    private final ZoneId utcZone = ZoneId.of("UTC");
    private final ZoneId localZone = ZoneId.systemDefault();
    Stage stage;
    Parent root;
    Parent scene;

    @FXML
    private ToggleGroup monthToggleGroup;

    @FXML
    private RadioButton janRadio;

    @FXML
    private RadioButton febRadio;

    @FXML
    private RadioButton marchRadio;

    @FXML
    private RadioButton aprilRadio;

    @FXML
    private RadioButton mayRadio;

    @FXML
    private RadioButton juneRadio;

    @FXML
    private RadioButton julyRadio;

    @FXML
    private RadioButton augRadio;

    @FXML
    private RadioButton septRadio;

    @FXML
    private RadioButton octRadio;

    @FXML
    private RadioButton novRadio;

    @FXML
    private RadioButton decRadio;

    @FXML
    private TableView<Type> appointmentTable;

    @FXML
    private TableColumn<Type, Integer> countCol;

    @FXML
    private TableColumn<Type, String> typeCol;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    void onActionJan(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            janRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 1);
        }
    }

    @FXML
    void onActionFeb(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            febRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 2);
        }
    }

    @FXML
    void onActionMarch(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            marchRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 3);
        }
    }

    @FXML
    void onActionApril(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            aprilRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 4);
        }
    }

    @FXML
    void onActionMay(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            mayRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 5);
        }
    }

    @FXML
    void onActionJune(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            juneRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 6);
        }
    }

    @FXML
    void onActionJuly(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            julyRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 7);
        }
    }

    @FXML
    void onActionAug(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            augRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 8);
        }
    }

    @FXML
    void onActionSept(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            septRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 9);
        }
    }

    @FXML
    void onActionOct(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            octRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 10);
        }
    }

    @FXML
    void onActionNov(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            novRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 11);
        }
    }

    @FXML
    void onActionDec(ActionEvent event) {
        if(yearComboBox.getValue() == null) {
            decRadio.setSelected(false);
            Popups.errorPopup("Pick year before selecting a month");
        } else {
            setAppointmentsByMonth(yearComboBox.getValue(), 12);
        }
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        yearComboBox.setItems(getYears());

        // Populates appointments table with data from appointmentsList
        countCol.setCellValueFactory(new PropertyValueFactory<>("Count"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));

        appointmentTable.setItems(typeList);
    }

    private ObservableList<Integer> getYears() {
        ObservableList<Integer> years = FXCollections.observableArrayList();
        int year = 2020;
        for(int i = year; i < year+5; i++) {
            years.add(i);
        }
        return years;
    }

    private void setAppointmentsByMonth(int year, int month) {
        try {
            // Get LocalDate objects for first and last day of current month
            YearMonth selectedYearMonth = YearMonth.of(year, month);
            LocalDate firstDayOfMonth = selectedYearMonth.atDay(1);
            LocalDate lastDayOfMonth = selectedYearMonth.atEndOfMonth();
            // Retrieves data from appointments table using method in DBQuery
            Connection conn = DBConnection.getConnection();
            DBQuery.setPreparedStatement(conn, "SELECT Type, COUNT(*) AS 'count' FROM appointments WHERE Start >= ? AND Start < ? GROUP BY Type");
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, String.valueOf(firstDayOfMonth));
            ps.setString(2, String.valueOf(lastDayOfMonth));
            ResultSet results = ps.executeQuery();
            typeList.clear();
            // Loops through each Appointment query result and adds Appointment objects to the appointmentsList
            while(results.next()) {
                int count = results.getInt("count");
                String type = results.getString("Type");
                typeList.add(new Type(count, type));
            }
        } catch(Exception e) {
            System.out.println("Exception caught in setAppointmentsByMonth in TypeReportController: " + e.getMessage());
        }
    }

    private void setAllAppointmentsList() throws SQLException {
        // Retrieves data from appointments table using method in DBQuery
        ResultSet results = DBQuery.selectQuery("SELECT Type, COUNT(*) AS 'count' FROM appointments GROUP BY Type");
        typeList.clear();
        // Loops through each Appointment query result and adds Appointment objects to the appointmentsList
        while(results.next()) {
            int count = results.getInt("count");
            String type = results.getString("Type");
            typeList.add(new Type(count, type));
        }
    }
}
