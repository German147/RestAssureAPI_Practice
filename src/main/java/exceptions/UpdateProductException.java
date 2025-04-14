package exceptions;

public class UpdateProductException extends RuntimeException{
    public UpdateProductException(String message) {
        super(message);
    }
}
