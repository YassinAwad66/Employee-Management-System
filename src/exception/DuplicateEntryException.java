package exception;

public class DuplicateEntryException extends ValidationException {

    public DuplicateEntryException(String message) {
        super(message);
    }
}