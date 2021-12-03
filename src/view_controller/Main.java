package view_controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.sql.*;
import java.time.*;
import java.util.Scanner;

/**
 * @author Jesse Knoblauch
 * Initiates application database, loads stage, and loads login.fxml view into stage.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {
        DBConnection.startConnection();
        Connection conn = DBConnection.getConnection();
        launch(args);
        DBConnection.CloseConnection();
    }
}