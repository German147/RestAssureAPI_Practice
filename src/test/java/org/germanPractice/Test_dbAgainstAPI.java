package org.germanPractice;

import database.DBConnection;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import service.UserService;

import java.sql.*;

import static io.restassured.RestAssured.given;

public class Test_dbAgainstAPI {

    private final UserService userService = new UserService();

    @Test
    public void firstUserInfoTest() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/users/1";
        Response response = given().
                when()
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response();

        int userId = response.jsonPath().getInt("id");
        String userName = response.jsonPath().getString("name");
        String userEmail = response.jsonPath().getString("email");

        try (Connection conn = DBConnection.getInstance()) {

            String getFirstUser =
                    "SELECT * FROM users " +
                            "WHERE id = 1";


            Statement stmt = conn.createStatement();
            ResultSet rst = stmt.executeQuery(getFirstUser);
            rst.next();
            Assert.assertEquals(rst.getString("name"), userName, "The username is different");
            Assert.assertEquals(rst.getString("email"), userEmail, "The userEmail is different");
            Assert.assertEquals(rst.getInt("id"), userId, "User Id id different");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void getUserUsingLayers() {
        int userId = 1;
        User userAPI = userService.getUserFromApi(userId);
        User dbUser = userService.getUserFromDb(1);

        Assert.assertEquals(userAPI.getId(), dbUser.getId(), "User ID mismatch");
        Assert.assertEquals(userAPI.getName(), dbUser.getName(), "User name mismatch");
        Assert.assertEquals(userAPI.getEmail(), dbUser.getEmail(), "User Email mismatch");
    }

    @Test
    public void testUserAddressMatchesApiAndDb() {
        int userId = 1;

        //  1. Obtener usuario desde la API
        User apiUser = userService.getUserFromApi(userId);

        //  2. Obtener usuario desde la DB (ahora incluye Address)
        User dbUser = userService.getUserFromDb(userId);

        //  3. Asserts de los datos de address
        Assert.assertEquals(apiUser.getAddress().getStreet(), dbUser.getAddress().getStreet(), "Street does not match");
        Assert.assertEquals(apiUser.getAddress().getSuite(), dbUser.getAddress().getSuite(), "Suite does not match");
        Assert.assertEquals(apiUser.getAddress().getCity(), dbUser.getAddress().getCity(), "City does not match");
        Assert.assertEquals(apiUser.getAddress().getZipcode(), dbUser.getAddress().getZipcode(), "Zipcode does not match");
    }

    public static void main(String[] args) {


        try {
            Connection conn = DBConnection.getInstance();
            System.out.println("DataBase connected!!");

            String getFourUsers = "SELECT * from users";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getFourUsers);

            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("username") + " | " +
                                rs.getString("email")

                );
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
