package org.germanPractice;

import java.sql.*;

public class DBTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/test_db";
        String username = "postgres";
        String password = "postgres123";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to the database!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM geo");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                                rs.getString("lat") + " | " +
                                rs.getString("lng")
//                                rs.getString("username") + " | " +
//                                rs.getString("email")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
