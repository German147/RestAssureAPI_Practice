package exceptions;

public class GetUserException extends RuntimeException{
    public GetUserException(String message) {
        super(message);
    }
}
