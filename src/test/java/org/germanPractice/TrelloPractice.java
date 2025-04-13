package org.germanPractice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

public class TrelloPractice {

    @Test
    public void createBoard() {

        RestAssured.baseURI = "https://api.trello.com";
        String boardName = "My Board Form API" + (int) (Math.random() * 100);

        System.out.println("This is the created Board Name " + boardName);

        given().queryParam("key", "66230352ae879578682cf7c7f2cf090a")
                .queryParam("token", "ATTAfea56e650a3293694d5d8e55991c689732b65d012181c824e19fda5b16d10849E6D52209")
                .queryParam("name", boardName)
                .header("Content-Type", "application/json").
                when()
                .post("/1/boards").
                then().assertThat()
                .statusCode(200)
                .and()
                .contentType(ContentType.JSON)
                .and()
                .body("name", equalTo(boardName));

    }

    @Test
    public void getTrelloHomePage() {

        Response rs = given()
                .queryParam("key", "66230352ae879578682cf7c7f2cf090a")
                .queryParam("token", "ATTAfea56e650a3293694d5d8e55991c689732b65d012181c824e19fda5b16d10849E6D52209")
                .when()
                .get("https://trello.com/b/zVAdLs0J/my-board-from-api-intellij")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .contentType(ContentType.HTML)
                .extract()
                .response();

        String jString = rs.asString();
        JsonPath jsonPath = new JsonPath(jString);
        System.out.println(jString);
    }

    @Test
    public void getBoardStars() {
        Response response = given()
                .queryParam("key", "66230352ae879578682cf7c7f2cf090a")
                .queryParam("token", "ATTAfea56e650a3293694d5d8e55991c689732b65d012181c824e19fda5b16d10849E6D52209")
                .when()
                .get("https://api.trello.com/1/boards/zVAdLs0J/boardStars")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .contentType(ContentType.JSON)
                .and()
//                 .body("idBoard", equalTo("[664cbe718139faef4c89fd08]"))
                .extract()
                .response();

        System.out.println();

//        System.out.println(idBoard);
//        String jString = response.asString();
//        JsonPath rs = new JsonPath(JsonResponse);
//        System.out.println((String) rs.get("idBoard"));

//        JsonPath jsonPath = new JsonPath(jString);
//        System.out.println("This is the body of the query " + jString.);
//           System.out.println((String) jsonPath.get("idBoar"));

    }

    @Test
    public void extractBoardInformationByBoardId() {

        Response response = given()
                .queryParam("key", "66230352ae879578682cf7c7f2cf090a")
                .queryParam("token", "ATTAfea56e650a3293694d5d8e55991c689732b65d012181c824e19fda5b16d10849E6D52209").
                when()
                .get("https://api.trello.com/1/boards/DWVfITuQ").
                then()
                .assertThat()
                .statusCode(200)
                .and()
                .contentType(ContentType.JSON)
                .extract()
                .response();

        String stringResponse = response.asString();
        JsonPath jsonPath = new JsonPath(stringResponse);

        assertEquals(jsonPath.get("id"),
                "66f66f196b5201f95ff2fb43");
        assertEquals(jsonPath.get("name"),
                "MORNING Board From API IntelliJ.V4");
        assertEquals(jsonPath.get("url"),
                "https://trello.com/b/DWVfITuQ/morning-board-from-api-intellijv4");
        assertEquals(jsonPath.get("shortUrl"),
                "https://trello.com/b/DWVfITuQ");

    }

    @DataProvider(name = "queryParameters")
    public static Object[][] responseParameters() {
        return new Object[][] {
                { "66230352ae879578682cf7c7f2cf090a", "ATTAfea56e650a3293694d5d8e55991c689732b65d012181c824e19fda5b16d10849E6D52209", "https://api.trello.com/1/boards/DWVfITuQ","66f66f196b5201f95ff2fb43","MORNING Board From API IntelliJ.V4","https://trello.com/b/DWVfITuQ/morning-board-from-api-intellijv4","https://trello.com/b/DWVfITuQ"},
                { "66230352ae879578682cf7c7f2cf090a", "ATTAfea56e650a3293694d5d8e55991c689732b65d012181c824e19fda5b16d10849E6D52209", "https://api.trello.com/1/boards/frTkjOtd","66a1a176f7355f9157445b34","GermanNewBoard","https://trello.com/b/frTkjOtd/germannewboard","https://trello.com/b/frTkjOtd" },
                { "66230352ae879578682cf7c7f2cf090a", "ATTAfea56e650a3293694d5d8e55991c689732b65d012181c824e19fda5b16d10849E6D52209", "https://api.trello.com/1/boards/4LznlHiT","6626b2abcf705da6061f9dfe"," Automation Sprint 52","https://trello.com/b/4LznlHiT/automation-sprint-52","https://trello.com/b/4LznlHiT"},
                { "66230352ae879578682cf7c7f2cf090a", "ATTAfea56e650a3293694d5d8e55991c689732b65d012181c824e19fda5b16d10849E6D52209", "https://api.trello.com/1/boards/zVAdLs0J","664cbe718139faef4c89fd08","My Board From API IntelliJ","https://trello.com/b/zVAdLs0J/my-board-from-api-intellij","https://trello.com/b/zVAdLs0J"}
        };
    }
    @Test(dataProvider = "queryParameters")
    public void validateParameterInformationByBoardId(
            String key,
            String token,
            String http,

            String expectedId,
            String expectedName,
            String expectedUrl,
            String expectedShortUrl
    ) {

        Response response = given()
                .queryParam("key", key)
                .queryParam("token", token).
                when()
                .get(http).
                then()
                .assertThat()
                .statusCode(200)
                .and()
                .contentType(ContentType.JSON)
                .extract()
                .response();

        String stringResponse = response.asString();
        JsonPath jsonPath = new JsonPath(stringResponse);

        assertEquals(jsonPath.get("id"),
                expectedId);
        assertEquals(jsonPath.get("name"),
                expectedName);
        assertEquals(jsonPath.get("url"),
                expectedUrl);
        assertEquals(jsonPath.get("shortUrl"),
                expectedShortUrl);

    }



}
