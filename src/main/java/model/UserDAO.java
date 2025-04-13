package model;


import database.DBConnection;
import exceptions.GetUserException;

import java.sql.*;

public class UserDAO {

    public User getUserById(int id) {
        User user = null;

        try (Connection conn = DBConnection.getInstance()){
            String query = "SELECT * FROM users WHERE id = " + id;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email")
                );
            }

        } catch (Exception e) {
            throw new GetUserException("Failed to get user by id");
        }

        return user;
    }

}
