package bg.fmi.piss.course.models;


import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class RideRequest {
    @Id
    private String id;
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd HH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();
    private User passenger;
    @NonNull
    private Integer passengers;
    @NonNull
    private String startPoint;
    @NonNull
    private String endPoint;
}
