package exceptions;

public class InsertProductException extends RuntimeException{
    public InsertProductException(String message) {
        super(message);
    }
}
