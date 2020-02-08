package bg.fmi.spring.course.project.controllers;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ErrorResponse {
    private Instant time;
    private String message;
}
