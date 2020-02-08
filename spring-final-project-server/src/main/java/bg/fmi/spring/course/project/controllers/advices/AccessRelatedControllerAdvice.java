package bg.fmi.spring.course.project.controllers.advices;

import bg.fmi.spring.course.project.controllers.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(1)
public class AccessRelatedControllerAdvice extends AbstractGenericControllerAdvice {
    public AccessRelatedControllerAdvice() {
        super(HttpStatus.FORBIDDEN);
    }

    @Override
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        return super.handleException(ex);
    }
}
