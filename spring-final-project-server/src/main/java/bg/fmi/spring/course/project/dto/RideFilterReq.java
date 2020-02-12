package bg.fmi.spring.course.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideFilterReq {
    private CoordinatesFilterReq startFilter;
    private CoordinatesFilterReq endFilter;
}
