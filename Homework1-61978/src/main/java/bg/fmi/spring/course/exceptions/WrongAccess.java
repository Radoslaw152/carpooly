package bg.fmi.spring.course.exceptions;

public class WrongAccess extends RuntimeException {
    public WrongAccess() {
    }

    public WrongAccess(String message) {
        super(message);
    }

    public WrongAccess(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongAccess(Throwable cause) {
        super(cause);
    }
}
