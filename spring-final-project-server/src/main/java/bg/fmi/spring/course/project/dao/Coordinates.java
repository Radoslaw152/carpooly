package bg.fmi.spring.course.project.dao;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Coordinates {
    @NotNull private Double longitude;
    @NotNull private Double latitude;
}
