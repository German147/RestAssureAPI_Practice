package model;


import database.DBConnection;
import exceptions.GetUserException;

import java.sql.*;

public class UserDAO {

    public User getUserById(int id) {
        User user = null;

        try (Connection conn = DBConnection.getInstance()) {
            String query = "SELECT u.id AS user_id, u.name, u.username, u.email, " +
                    "a.street, a.suite, a.city, a.zipcode, a.lat, a.lng " +
                    "FROM users u " +
                    "JOIN addresses a ON u.address_id = a.id " +
                    "WHERE u.id = " + id;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                Geo geo = new Geo(
                        rs.getString("lat"),
                        rs.getString("lng")
                );

                Address address = new Address(
                        rs.getString("street"),
                        rs.getString("suite"),
                        rs.getString("city"),
                        rs.getString("zipcode"),
                        geo
                );

                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        address
                );
            }

        } catch (Exception e) {
            throw new GetUserException("Failed to get user by id");
        }

        return user;
    }


}
