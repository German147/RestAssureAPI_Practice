package org.germanPractice;

import database.DBConnection;
import exceptions.GetUserException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class InsertUsersFromApi {

  @Test
  public void insertAllUsersFromApi() {
      RestAssured.baseURI = "https://jsonplaceholder.typicode.com/users";

      Response response = given()
              .when()
              .get()
              .then()
              .statusCode(200)
              .extract()
              .response();

      List<Map<String, Object>> users = response.jsonPath().getList("");

      try (Connection conn = DBConnection.getInstance()) {
          conn.setAutoCommit(false); // Start transaction

          for (Map<String, Object> userMap : users) {
              // Extract user fields
              int userId = (int) userMap.get("id");
              String name = (String) userMap.get("name");
              String username = (String) userMap.get("username");
              String email = (String) userMap.get("email");

              // Address nested map
              Map<String, Object> addressMap = (Map<String, Object>) userMap.get("address");
              String street = (String) addressMap.get("street");
              String suite = (String) addressMap.get("suite");
              String city = (String) addressMap.get("city");
              String zipcode = (String) addressMap.get("zipcode");

              Map<String, String> geoMap = (Map<String, String>) addressMap.get("geo");
              String lat = geoMap.get("lat");
              String lng = geoMap.get("lng");

              // 1. Insert into geo
              String geoSql = "INSERT INTO geo (lat, lng) VALUES (?, ?) RETURNING id";
              PreparedStatement geoStmt = conn.prepareStatement(geoSql);
              geoStmt.setString(1, lat);
              geoStmt.setString(2, lng);
              ResultSet geoRs = geoStmt.executeQuery();
              geoRs.next();
              int geoId = geoRs.getInt("id");

              // 2. Insert into addresses
              String addrSql = "INSERT INTO addresses (street, suite, city, zipcode, geo_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
              PreparedStatement addrStmt = conn.prepareStatement(addrSql);
              addrStmt.setString(1, street);
              addrStmt.setString(2, suite);
              addrStmt.setString(3, city);
              addrStmt.setString(4, zipcode);
              addrStmt.setInt(5, geoId);
              ResultSet addrRs = addrStmt.executeQuery();
              addrRs.next();
              int addressId = addrRs.getInt("id");

              // 3. Insert into users
              String userSql = "INSERT INTO users (id, name, username, email, address_id) VALUES (?, ?, ?, ?, ?)";
              PreparedStatement userStmt = conn.prepareStatement(userSql);
              userStmt.setInt(1, userId); // keeps API's ID
              userStmt.setString(2, name);
              userStmt.setString(3, username);
              userStmt.setString(4, email);
              userStmt.setInt(5, addressId);
              userStmt.executeUpdate();
          }

          conn.commit(); // Commit transaction

      } catch (Exception e) {
          e.printStackTrace();
          throw new GetUserException("Failed to insert users from API");
      }
  }

}

