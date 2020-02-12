package bg.fmi.spring.course.project.dto;

import bg.fmi.spring.course.project.dao.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesFilterReq {
    private Coordinates coordinates;
    private int maxRadius;
}
