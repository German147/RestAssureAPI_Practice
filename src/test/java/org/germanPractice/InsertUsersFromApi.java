package org.germanPractice;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.Random;

public class InsertUsersFromApi {

    public static void main(String[] args) throws SQLException {
        // Step 1: Fetch users from the API
        Response response = RestAssured
                .given()
                .baseUri("https://jsonplaceholder.typicode.com")
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JSONArray usersArray = new JSONArray(response.getBody().asString());

        // Step 2: DB connection
        String url = "jdbc:postgresql://localhost:5432/test_db";
        String user = "postgres";
        String password = "postgres123";
        Connection conn = DriverManager.getConnection(url, user, password);

        String insertQuery = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(insertQuery);

        Random random = new Random();

        // Step 3: Loop through API users and insert into DB
        for (int i = 0; i < usersArray.length(); i++) {
            JSONObject userObj = usersArray.getJSONObject(i);
            String name = userObj.getString("name");
            String email = userObj.getString("email");
            int age = random.nextInt(30) + 20; // random age between 20 and 50

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, age);
            stmt.executeUpdate();
            System.out.println("Inserted: " + name + " | " + email + " | " + age);
        }

        stmt.close();
        conn.close();
        System.out.println("✅ All users inserted from API!");
    }
}

