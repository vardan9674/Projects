package zillowbot.Entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import zillowbot.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class School {
    private SimpleStringProperty Name;
    private SimpleStringProperty Grades;
    private SimpleDoubleProperty Distance;


    public School(String name, String grades,Double distance) {
        Name = new SimpleStringProperty(name);
        Grades = new SimpleStringProperty(grades);
        Distance = new SimpleDoubleProperty(distance);
    }

    private int findSchool() {
        int result = 0;
        try {
            Connection conn = Global.getConnection();
            String sql = "select * from [School] where [Name]=? and [Grades]=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,getName());
            preparedStatement.setString(2,getGrades());
            ResultSet rs=preparedStatement.executeQuery();
            if (rs.next()) {
                result = rs.getInt("SchoolId");
            }
            conn.close();
        }catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    private void saveSchool() {
        try {
            Connection conn = Global.getConnection();
            String sql = "insert into [School]([Name],[Grades]) values(?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, getName());
            preparedStatement.setString(2, getGrades());
            preparedStatement.executeUpdate();
            conn.close();
        }catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public void save(int estateId) {
        int schoolId = findSchool();
        if (schoolId == 0) {
            saveSchool();
            schoolId = findSchool();
        }
        try {
            Connection conn = Global.getConnection();
            String sql = "insert into [EstateSchool]([EstateId],[SchoolId],[Distance]) values(?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, estateId);
            preparedStatement.setInt(2, schoolId);
            preparedStatement.setDouble(3, getDistance());
            preparedStatement.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public String getName() {
        return Name.get();
    }

    public SimpleStringProperty NameProperty() {
        return Name;
    }

    public String getGrades() {
        return Grades.get();
    }

    public SimpleStringProperty GradesProperty() {
        return Grades;
    }

    public Double getDistance() {
        return Distance.get();
    }

    public SimpleDoubleProperty DistanceProperty() {
        return Distance;
    }
}
