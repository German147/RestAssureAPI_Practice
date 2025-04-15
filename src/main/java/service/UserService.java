package service;

import database.DBConnection;
import exceptions.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Address;
import model.Geo;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

import static io.restassured.RestAssured.given;

public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    public User getUserFromApi(int userId) {
        try {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com/users/" + userId;
            Response response = given()
                    .when()
                    .get()
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .response();

            Geo geo = new Geo(
                    response.jsonPath().getString("address.geo.lat"),
                    response.jsonPath().getString("address.geo.lng")
            );

            Address address = new Address(
                    response.jsonPath().getString("address.street"),
                    response.jsonPath().getString("address.suite"),
                    response.jsonPath().getString("address.city"),
                    response.jsonPath().getString("address.zipcode"),
                    geo
            );

            return new User(
                    response.jsonPath().getInt("id"),
                    response.jsonPath().getString("name"),
                    response.jsonPath().getString("username"),
                    response.jsonPath().getString("email"),
                    address
            );

        } catch (ApiException e) {
            throw new ApiException("User not found due to Server is down or URL is wrong");
        }
    }

    public User getUserFromDb(int id) {
        User user = null;

        try (Connection conn = DBConnection.getInstance()) {
            String query = """
            SELECT u.id AS user_id, u.name, u.username, u.email,
                   a.street, a.suite, a.city, a.zipcode,
                   g.lat, g.lng
            FROM users u
            JOIN addresses a ON u.address_id = a.id
            JOIN geo g ON a.geo_id = g.id
            WHERE u.id = """ + id;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                // Create Geo object
                Geo geo = new Geo(
                        rs.getString("lat"),
                        rs.getString("lng")
                );

                // Create Address object
                Address address = new Address(
                        rs.getString("street"),
                        rs.getString("suite"),
                        rs.getString("city"),
                        rs.getString("zipcode"),
                        geo
                );

                // Create User object (make sure constructor includes the ID as first argument)
                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("email"),
                        address
                );
            }

        } catch (SQLException e) {
            throw new GetUserException("Failed to get user by id"); // Now passing the cause for better debugging
        }

        return user;
    }

    public void fetchAndInsertUsers() {
        try {
            truncateTables();
            URL url = new URL("https://jsonplaceholder.typicode.com/users");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();

            JSONArray users = new JSONArray(response.toString());

            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                JSONObject address = user.getJSONObject("address");
                JSONObject geo = address.getJSONObject("geo");

                int geoId = insertGeo(geo);
                int addressId = insertAddress(address, geoId);
                insertUser(user, addressId);

                System.out.println("✅ Inserted user: " + user.getString("name"));
            }

            System.out.println("🎉 All users inserted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void truncateTables() {
        try (Connection conn = DBConnection.getInstance()) {
            Statement stmt = conn.createStatement();
            stmt.execute("TRUNCATE TABLE users, addresses, geo RESTART IDENTITY CASCADE");
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertGeo(JSONObject geo) {
        try (Connection conn = DBConnection.getInstance()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO geo (lat, lng) VALUES (?, ?) RETURNING id");
            stmt.setString(1, geo.getString("lat"));
            stmt.setString(2, geo.getString("lng"));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("id");
        } catch (SQLException e) {
            logger.error("The geo was not inserted");
            throw new InsertGeoException("The geo was not inserted");
        }
    }

    public int insertAddress(JSONObject address, int geoId) {

        try (Connection conn = DBConnection.getInstance()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO addresses (street, suite, city, zipcode, geo_id) VALUES (?, ?, ?, ?, ?) RETURNING id"
            );
            stmt.setString(1, address.getString("street"));
            stmt.setString(2, address.getString("suite"));
            stmt.setString(3, address.getString("city"));
            stmt.setString(4, address.getString("zipcode"));
            stmt.setInt(5, geoId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt("id");
        } catch (SQLException e) {
            throw new AddressException("The address was not performed correctly");
        }
    }

    public void insertUser(JSONObject user, int addressId) {
        try (Connection conn = DBConnection.getInstance()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users (id, name, username, email, address_id) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, user.getInt("id"));
            stmt.setString(2, user.getString("name"));
            stmt.setString(3, user.getString("username"));
            stmt.setString(4, user.getString("email"));
            stmt.setInt(5, addressId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new InsertUserException("The user was not created");
        }
    }
}
