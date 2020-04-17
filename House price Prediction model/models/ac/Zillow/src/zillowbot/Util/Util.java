package zillowbot.Util;

import javafx.application.Platform;
import javafx.scene.control.TableView;

import java.util.Objects;

public class Util {
    public static String getDigits(String data,boolean returnZero) {
        String result = data.replaceAll("\\D+", "");
        if ((returnZero) && (Objects.equals(result, ""))) {
            result = "0";
        }
        return result;
    }

    public static Integer getDigits(String data)
    {
        String d = getDigits(data, true);
        return Integer.parseInt(d);
    }

    public static String getDouble(String data, boolean returnZero)
    {
        String result = data.replaceAll("[^\\.0123456789]","");
        if ((returnZero) && (Objects.equals(result, ""))) {
            result = "0";
        }
        return result;
    }

    public static Double getDouble(String data)
    {
        String d = getDouble(data,true);
        return Double.parseDouble(d);
    }

    public static void refreshGrid(TableView grid, int index) {
        if (grid == null) return;
        Platform.runLater(() -> {
            grid.refresh();
            if (index >= 0) {
                grid.getSelectionModel().select(index);
                grid.scrollTo(index);
            } else if (grid.getItems() != null) {
                int size = grid.getItems().size();
                grid.getSelectionModel().select(size - 1);
                grid.scrollTo(size - 1);
            }
        });
    }


}
