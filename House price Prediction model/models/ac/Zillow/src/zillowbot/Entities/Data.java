package zillowbot.Entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import zillowbot.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Data {
    private SimpleStringProperty Key;
    private SimpleStringProperty Value;
    private SimpleDoubleProperty Value2;
    private SimpleStringProperty Category;
    private SimpleStringProperty Title;
    private SimpleStringProperty ValueType;


    public Data(String category,String title, String key, String value, String valueType) {
        Key = new SimpleStringProperty(key);
        Value = new SimpleStringProperty(value);
        Value2 = null;
        Category = new SimpleStringProperty(category);
        Title = new SimpleStringProperty(title);
        ValueType = new SimpleStringProperty(valueType);
    }

    public void save(int estateId) {
        try {
            Connection conn = Global.getConnection();
            String sql = "insert into [Data]([EstateId],[Category],[Title],[Key],[Value],[ValueType])"+
                    " values(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,estateId);
            preparedStatement.setString(2,getCategory());
            preparedStatement.setString(3,getTitle());
            preparedStatement.setString(4,getKey());
            preparedStatement.setString(5,getValue());
            preparedStatement.setString(6,getValueType());
            preparedStatement.executeUpdate();
            conn.close();
        }catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public String getKey() {
        return Key.get();
    }

    public SimpleStringProperty keyProperty() {
        return Key;
    }

    public String getValue() {
        return Value.get();
    }

    public SimpleStringProperty ValueProperty() {
        return Value;
    }

    public Double getValue2() {
        return Value2.get();
    }

    public SimpleDoubleProperty Value2Property() {
        return Value2;
    }

    public String getCategory() {
        return Category.get();
    }

    public SimpleStringProperty CategoryProperty() {
        return Category;
    }

    public String getTitle() {
        return Title.get();
    }

    public SimpleStringProperty TitleProperty() {
        return Title;
    }

    public String getValueType() {
        return ValueType.get();
    }

    public SimpleStringProperty ValueTypeProperty() {
        return ValueType;
    }
}
