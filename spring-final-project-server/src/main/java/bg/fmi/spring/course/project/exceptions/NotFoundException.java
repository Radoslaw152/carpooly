package bg.fmi.spring.course.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    private static final String TEMPLATE = "Cannot find %s with %s %s";

    public NotFoundException(final String message) {
        super(message);
    }

    public static NotFoundException generateForTypeAndIdTypeAndId(
            String type, String idType, String id) {
        return new NotFoundException(String.format(TEMPLATE, type, idType, id));
    }
}
