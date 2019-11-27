package bg.fmi.spring.course.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import bg.fmi.spring.course.exceptions.EntityNotFoundException;
import bg.fmi.spring.course.exceptions.InvalidEntityIdException;
import bg.fmi.spring.course.exceptions.WrongAccess;

@ControllerAdvice("bg.fmi.spring.course")
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<String> handle(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(InvalidEntityIdException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(WrongAccess e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
