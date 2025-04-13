package typicodeTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class InsertTypiCodeUser {
    public static void main(String[] args) {
        String apiUrl = "https://jsonplaceholder.typicode.com/users";

        try {
            // 1. Connect to DB
            Connection conn = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/test_db",
                            "postgres",
                            "postgres123");
            Statement cleanStmt = conn.createStatement();
            cleanStmt.execute("TRUNCATE TABLE users, addresses, geo RESTART IDENTITY CASCADE");
            cleanStmt.close();


            // 2. Fetch JSON data
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder json = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();

            // 3. Parse JSON
            JSONArray users = new JSONArray(json.toString());

            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);

                // 3.1 Insert into geo
                JSONObject address = user.getJSONObject("address");
                JSONObject geo = address.getJSONObject("geo");
                PreparedStatement geoStmt = conn.prepareStatement(
                        "INSERT INTO geo (lat, lng) VALUES (?, ?) RETURNING id"
                );
                geoStmt.setString(1, geo.getString("lat"));
                geoStmt.setString(2, geo.getString("lng"));
                ResultSet geoRs = geoStmt.executeQuery();
                geoRs.next();
                int geoId = geoRs.getInt("id");

                // 3.2 Insert into addresses
                PreparedStatement addressStmt = conn.prepareStatement(
                        "INSERT INTO addresses (street, suite, city, zipcode, geo_id) VALUES (?, ?, ?, ?, ?) RETURNING id"
                );
                addressStmt.setString(1, address.getString("street"));
                addressStmt.setString(2, address.getString("suite"));
                addressStmt.setString(3, address.getString("city"));
                addressStmt.setString(4, address.getString("zipcode"));
                addressStmt.setInt(5, geoId);
                ResultSet addressRs = addressStmt.executeQuery();
                addressRs.next();
                int addressId = addressRs.getInt("id");

                // 3.3 Insert into users
                PreparedStatement userStmt = conn.prepareStatement(
                        "INSERT INTO users (id, name, username, email, address_id) VALUES (?, ?, ?, ?, ?)"
                );
                userStmt.setInt(1, user.getInt("id"));
                userStmt.setString(2, user.getString("name"));
                userStmt.setString(3, user.getString("username"));
                userStmt.setString(4, user.getString("email"));
                userStmt.setInt(5, addressId);
                userStmt.executeUpdate();

                System.out.println("✅ Inserted user: " + user.getString("name"));
            }

            System.out.println("🎉 All users inserted!");
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

