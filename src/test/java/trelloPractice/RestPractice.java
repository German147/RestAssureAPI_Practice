package trelloPractice;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class RestPractice {
    String user = "https://jsonplaceholder.typicode.com/users/1";

    @Test
    public void getUser() {
        String endpoint = "https://jsonplaceholder.typicode.com/posts/1";
        var response = given().when().get(endpoint).then();
        response.log().all();
    }

    @Test
    public void getUserId() {
        Response rs = given()
                .when()
                .get(user)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response();
        int userId = rs.jsonPath().getInt("id");
        System.out.println("The id is " + userId);
        Assert.assertEquals(1, userId, "The user id is not correct");
    }

    @Test
    public void validateUserId() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com/users";
        Response rs = given()
                .when()
                .get("/1")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response();

        int userId = rs.jsonPath().getInt("id");
        Assert.assertEquals(userId, 1, "The id is not correct");
    }

    @Test
    public void getUserName() {
        Response rs = given()
                .when()
                .get(user)
                .then()
                .assertThat()
                .statusCode(200)
                .log()
                .all()
                .extract()
                .response();
        String name = rs.jsonPath().getString("name");


        Assert.assertEquals("Leanne Graham", name, "The name is not correct");
    }

    @Test
    public void getUserList() {
        String path = "https://reqres.in/api/users?page=2";
        Response rs = given()
                .when()
                .get(path)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response();


        long time = rs.getTime();
        System.out.println("The time of the call is " + time);

    }


}
