package chess;

public class IllegalPositionException extends RuntimeException {
    public IllegalPositionException(String errorMsg) {
        super(errorMsg);
    }
}
