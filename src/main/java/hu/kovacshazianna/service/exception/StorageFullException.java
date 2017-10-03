package hu.kovacshazianna.service.exception;

/**
 * Exception thrown when storage has not enough place.
 *
 * @author Anna_Kovacshazi
 */
public class StorageFullException extends RuntimeException {

    private static final String message = "Not enough storage! Required %d, actual %d";
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
