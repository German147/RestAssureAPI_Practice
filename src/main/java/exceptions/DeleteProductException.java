package exceptions;

public class DeleteProductException extends RuntimeException{
    public DeleteProductException(String message) {
        super(message);
    }
}
