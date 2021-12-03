package utils;

import javafx.scene.control.Alert;

/**
 * Public popups class used to handle alerts.
 */
public class Popups {
    /**
     * Creates error alert and presents to user.
     * @param message Message to be shown in error alert.
     */
    public static void errorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Creates informational alert and presents to user.
     * @param message Message to be shown in informational alert.
     */
    public static void informationalPopup(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
