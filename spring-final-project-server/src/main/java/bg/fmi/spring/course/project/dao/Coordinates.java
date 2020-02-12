package bg.fmi.spring.course.project.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Coordinates {
    private Double longitude;
    private Double latitude;
}
