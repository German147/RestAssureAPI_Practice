package exceptions;

public class GetProductException extends RuntimeException{
    public GetProductException(String message) {
        super(message);
    }
}
