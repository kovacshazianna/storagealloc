package hu.kovacshazianna.service.exception;

/**
 * Exception for the case when storage has not enough place.
 *
 * @author Anna_Kovacshazi
 */
public class StorageFullException extends RuntimeException {

    private static final String message = "Not enough storage! Required %d bytes, actual %d bytes";
    private final int required;
    private final int actual;

    public StorageFullException(int required, int actual) {
        this.required = required;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return String.format(message, required, actual);
    }
}
