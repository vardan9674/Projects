package zillowbot.DB;

import zillowbot.Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
    public static Connection connect(String dbPath) throws SQLException {
        String url="jdbc:sqlite:"+dbPath;
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }

    public static Connection connect() throws SQLException {
        return connect(Global.DbPath);
    }
}
