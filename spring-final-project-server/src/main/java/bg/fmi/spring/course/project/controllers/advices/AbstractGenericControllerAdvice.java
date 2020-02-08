package bg.fmi.spring.course.project.controllers.advices;

import bg.fmi.spring.course.project.controllers.ErrorResponse;
import java.time.Instant;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@AllArgsConstructor
public abstract class AbstractGenericControllerAdvice {
    private final HttpStatus defaultStatus;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        HttpStatus httpStatus =
                Optional.ofNullable(ex.getClass().getAnnotation(ResponseStatus.class))
                        .map(ResponseStatus::code)
                        .orElse(defaultStatus);
        return handleException(ex, httpStatus);
    }

    private ResponseEntity<ErrorResponse> handleException(
            Exception exception, HttpStatus httpStatus) {
        log.error("Error {}: {}", exception.getClass(), exception.getLocalizedMessage());
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .time(Instant.now())
                        .message(exception.getLocalizedMessage())
                        .build(),
                httpStatus);
    }
}
