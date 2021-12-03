package view_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import models.Appointment;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerReportController implements Initializable {

    @FXML
    private ComboBox<?> contactComboBox;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, ?> appointmentIdCol;

    @FXML
    private TableColumn<Appointment, ?> customerIdCol;

    @FXML
    private TableColumn<Appointment, ?> contactCol;

    @FXML
    private TableColumn<Appointment, ?> titleCol;

    @FXML
    private TableColumn<Appointment, ?> typeCol;

    @FXML
    private TableColumn<Appointment, ?> descriptionCol;

    @FXML
    private TableColumn<Appointment, ?> startCol;

    @FXML
    private TableColumn<Appointment, ?> endCol;

    @FXML
    private Button backButton;

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
    void onActionComboBox(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
