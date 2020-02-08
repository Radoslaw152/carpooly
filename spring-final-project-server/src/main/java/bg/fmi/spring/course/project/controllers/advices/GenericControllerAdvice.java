package bg.fmi.spring.course.project.controllers.advices;

import bg.fmi.spring.course.project.controllers.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(99)
public class GenericControllerAdvice extends AbstractGenericControllerAdvice {
    public GenericControllerAdvice() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @Override
    public ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        return super.handleException(ex);
    }
}
