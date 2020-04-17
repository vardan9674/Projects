package zillowbot.DB;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.*;

public class TableUtil {


    public static void populate(TableView tableView,ResultSet resultSet) throws Exception {
        tableView.getColumns().clear();
        final ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();
        String[] columnName = new String[columnCount];
        int i;
        for(i=0;i<columnCount;i++) {
            columnName[i] = meta.getColumnName(i+1);
            TableColumn<HashMap<String, String>, String> column = new TableColumn<>(columnName[i]);
            column.setText(columnName[i]);
            int finalI = i;
            column.setCellValueFactory(p-> {
                HashMap<String, String> hashMap = p.getValue();
                return new SimpleStringProperty(hashMap.get(columnName[finalI]));
            });
            int columnType = meta.getColumnType(i+1);
            if (Arrays.asList(Types.DOUBLE, Types.FLOAT, Types.INTEGER).contains(columnType)) {
                column.setPrefWidth(75);
            } else {
                column.setPrefWidth(120);
            }
            tableView.getColumns().add(column);
        }
        ObservableList<HashMap<String,String>> data = FXCollections.observableArrayList();
        tableView.setItems(data);
        while (resultSet.next()) {
            HashMap<String ,String> hashMap = new HashMap<>();
            for (i = 0; i < columnCount; i++) {
                hashMap.put(columnName[i],String.valueOf(resultSet.getObject(i+1)));
            }
            data.add(hashMap);
        }
    }
}
