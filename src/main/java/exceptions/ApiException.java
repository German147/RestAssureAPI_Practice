package exceptions;

import io.restassured.RestAssured;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
