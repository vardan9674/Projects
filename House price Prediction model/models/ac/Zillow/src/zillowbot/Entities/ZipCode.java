package zillowbot.Entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import zillowbot.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ZipCode {
    private final SimpleIntegerProperty ZipCode;
    private final SimpleStringProperty PrimaryCity;
    private final SimpleStringProperty Type;
    private final SimpleStringProperty Status;
    public int RequestZipId;
    public int Sys;
    public int PrevSys;

    public ZipCode(int zipCode,String primaryCity,String type,String status) {
        ZipCode = new SimpleIntegerProperty(zipCode);
        Status = new SimpleStringProperty(status);
        PrimaryCity = new SimpleStringProperty(primaryCity);
        Type = new SimpleStringProperty(type);
        Sys = 0;
        RequestZipId = 0;
    }

    public void update()
    {
        try {
            Connection conn = Global.getConnection();
            String sql = "update [RequestZip] set Status=? , Sys=? where Id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,getStatus());
            preparedStatement.setInt(2,Sys);
            preparedStatement.setInt(3,RequestZipId);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public ZipCode(int zipCode)
    {
        this(zipCode,"","","");
    }

    public int getZipCode() {
        return ZipCode.get();
    }

    public SimpleIntegerProperty zipCodeProperty() {
        return ZipCode;
    }

    public String getStatus() {
        return Status.get();
    }

    public void setStatus(String status) { Status.set(status); }

    public SimpleStringProperty statusProperty() {
        return Status;
    }

    public String getPrimaryCity() {
        return PrimaryCity.get();
    }

    public void setPrimaryCity(String primaryCity) { PrimaryCity.set(primaryCity); }

    public SimpleStringProperty primaryCityProperty() {
        return PrimaryCity;
    }

    public String getType() {
        return Type.get();
    }

    public void setType(String type) { Type.set(type); }

    public SimpleStringProperty typeProperty() {
        return Type;
    }
}
