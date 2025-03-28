package last.project.lms.exceptions;

public class InvalidAuthException extends Exception{
    public InvalidAuthException() {
    }

    public InvalidAuthException(String message) {
        super(message);
    }
}
