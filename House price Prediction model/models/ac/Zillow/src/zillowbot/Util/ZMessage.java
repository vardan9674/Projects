package zillowbot.Util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ZMessage {
    public static void ShowMessage(Alert.AlertType alertType, String title, String message)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void Error(String message)
    {
        ShowMessage(Alert.AlertType.ERROR,"Error",message);
    }

    public static void Information(String message)
    {
        ShowMessage(Alert.AlertType.INFORMATION,"Information",message);
    }

    public static boolean Confirm(String message)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.YES, ButtonType.NO);
        alert.setTitle("Question");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        return (alert.getResult() == ButtonType.YES);
    }
}
