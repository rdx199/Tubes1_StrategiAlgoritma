
public class IDMismatchException extends RuntimeException {

    private static final long serialVersionUID = 1_00_00L;

    public IDMismatchException() {
    }

    public IDMismatchException(String message) {
        super(message);
    }

    public IDMismatchException(Throwable cause) {
        super(cause);
    }

    public IDMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

}
