package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String HOST = "sql12.freesqldatabase.com";
    private static final String DB_NAME = "sql12824557";
    private static final String USERNAME = "sql12824557";
    private static final String PASSWORD = "VsqhVf5PWD";
    private static final String PORT = "3306";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found!");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validateLogin(String username, String password, String role) {
        String query = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            java.sql.ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}