package zillowbot.Entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import zillowbot.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Option {
    private SimpleStringProperty DealType;
    private SimpleStringProperty EstateType;
    private SimpleIntegerProperty Bedrooms;
    private SimpleDoubleProperty Bathrooms;
    private SimpleIntegerProperty AreaSpace;
    private SimpleIntegerProperty Price;
    private SimpleStringProperty UnitNumber;
    public Integer Id = null;

    public Option(String dealType, String estateType, int bedrooms, double bathrooms, int areaSpace, int price, String unitNumber) {
        DealType = new SimpleStringProperty(dealType);
        EstateType = new SimpleStringProperty(estateType);
        Bedrooms = new SimpleIntegerProperty(bedrooms);
        Bathrooms = new SimpleDoubleProperty(bathrooms);
        AreaSpace = new SimpleIntegerProperty(areaSpace);
        Price = new SimpleIntegerProperty(price);
        UnitNumber = new SimpleStringProperty(unitNumber);
    }

    public void save(int estateId) {
        try {
            Connection conn = Global.getConnection();
            String sql = "insert into [Option]([EstateId],[DealType],[EstateType],[Bedrooms],[Bathrooms],[AreaSpace],[Price],[UnitNumber])"+
                    " values(?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, estateId);
            preparedStatement.setString(2, getDealType());
            preparedStatement.setString(3, getEstateType());
            preparedStatement.setInt(4, getBedrooms());
            preparedStatement.setDouble(5, getBathrooms());
            preparedStatement.setInt(6, getAreaSpace());
            preparedStatement.setInt(7, getPrice());
            preparedStatement.setString(8, getUnitNumber());
            preparedStatement.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setDealType(String dealType) {
        DealType.set(dealType);
    }

    public String getDealType() {
        return DealType.get();
    }

    public SimpleStringProperty StatusProperty() {
        return DealType;
    }

    public String getEstateType() {
        return EstateType.get();
    }

    public SimpleStringProperty TypeProperty() {
        return EstateType;
    }

    public int getBedrooms() {
        return Bedrooms.get();
    }

    public SimpleIntegerProperty BedroomsProperty() {
        return Bedrooms;
    }

    public double getBathrooms() {
        return Bathrooms.get();
    }

    public SimpleDoubleProperty BathroomsProperty() {
        return Bathrooms;
    }

    public int getAreaSpace() {
        return AreaSpace.get();
    }

    public SimpleIntegerProperty AreaSpaceProperty() {
        return AreaSpace;
    }

    public int getPrice() {
        return Price.get();
    }

    public SimpleIntegerProperty PriceProperty() {
        return Price;
    }

    public String getUnitNumber() {
        return UnitNumber.get();
    }

    public SimpleStringProperty UnitNumberProperty() {
        return UnitNumber;
    }
}
