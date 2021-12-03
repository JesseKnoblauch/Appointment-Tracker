package view_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.*;
import utils.Time;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Public class for Login Controller.
 */
public class LoginController implements Initializable {
    private Stage stage;
    private Parent scene;
    private Parent root;
    private String zone = String.valueOf(ZoneId.systemDefault());
    private ResourceBundle rb;
    private String date = "";
    private String time = "";
    private String notFound = "";
    private String upcoming = "";
    private String enterUsername = "";
    private String enterPassword = "";
    private String usernameShort = "";
    private String passwordShort = "";
    private String invalidCredentials = "";
    private String alert = "";

    @FXML
    private Label loginLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    @FXML
    private Label zoneLabel;

    @FXML
    void onActionExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onActionLogin(ActionEvent event) throws IOException, SQLException {
        if(isValidLogin()) {
            if(loginAttempt()) {
                Button b = (Button)event.getSource();
                stage = (Stage)((Button)event.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/view_controller/Scheduling.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
        }
    }

    private boolean loginAttempt() throws SQLException {
        Boolean validated = false;
        String username = usernameField.getText();
        String password = passwordField.getText();
        ResultSet result = DBQuery.selectQuery("SELECT EXISTS(SELECT * FROM users WHERE User_Name='" + username + "' and Password='" + password + "') as truth");
        while(result.next()) {
            if (result.getBoolean("truth") == true) {
                validated = true;
                Validation.setUserLogged(username);
            } else {
                Popups.errorPopup(invalidCredentials);
            }
        }
        try{
            if(validated) {
                int userId = Validation.getCurrentUserId();
                isUpcomingAppointment(userId);
                Validation.loginAttempt(username, password, true);
            } else {
                Validation.loginAttempt(username, password, false);
            }
        } catch(Exception e) {System.out.println("Exception when calling onActionLogin in LoginController: " + e.getMessage());}
        return validated;
    }

    private boolean isValidLogin() {
        String errorMessage = "";
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(username == null || username.length() < 1) errorMessage += enterUsername + "\n";
        if(password == null || password.length() < 1) errorMessage += enterPassword + "\n";
        if(username.length() > 12) errorMessage += usernameShort + "\n";
        if(password.length() > 12) errorMessage += passwordShort + "\n";
        if(errorMessage.length() > 0) {
            Popups.errorPopup(errorMessage);
            return false;
        }
        else return true;
    }

    /**
     * Creates a popup displaying any upcoming appointment within 15 minutes.
     * @param userId User ID of user matching login credentials.
     */
    public void isUpcomingAppointment(int userId){
        boolean foundUpcoming = false;
        try {
            ResultSet rs = DBQuery.selectQuery("SELECT * FROM appointments WHERE User_ID="+userId);
            while(rs.next()){
                String utcStartString = rs.getString("Start");
                LocalDateTime utcStartTime = Time.convertUTCToLocal(utcStartString);
                if(Time.isUpcoming(utcStartString)) {
                    LocalTime localTime = utcStartTime.toLocalTime();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
                    String formattedString = localTime.format(formatter);
                    foundUpcoming = true;
                    Popups.informationalPopup(
                            upcoming+"\n\nID: "+rs.getInt("Appointment_ID") +
                                    "\n"+date+": "+utcStartTime.toLocalDate() +
                                    "\n"+time+": "+formattedString, alert
                    );
                }
            } if(!foundUpcoming) Popups.informationalPopup(notFound, alert);
        } catch(NullPointerException e) {e.printStackTrace();}
        catch(SQLException e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            rb = ResourceBundle.getBundle("properties.login", Locale.getDefault());
            if(Locale.getDefault().getLanguage().equals("en") || Locale.getDefault().getLanguage().equals("fr"))
            {
                loginLabel.setText(rb.getString("Login_Screen"));
                zoneLabel.setText(rb.getString("Current_Time_Zone")+zone);
                usernameField.setPromptText(rb.getString("Username"));
                passwordField.setPromptText(rb.getString("Password"));
                loginButton.setText(rb.getString("Login"));
                exitButton.setText(rb.getString("Exit"));
                upcoming = rb.getString("Upcoming_Appointment");
                date = rb.getString("Date");
                time = rb.getString("Time");
                notFound = rb.getString("No_Appointments_Popup");
                usernameShort = rb.getString("usernameShort");
                passwordShort = rb.getString("passwordShort");
                enterUsername = rb.getString("enterUsername");
                enterPassword = rb.getString("enterPassword");
                invalidCredentials = rb.getString("invalidCredentials");
                alert = rb.getString("alert");
            }
        } catch(Exception e) {
            System.out.println("Exception caught while initializing in LoginController: " + e.getMessage());
        }
    }
}
