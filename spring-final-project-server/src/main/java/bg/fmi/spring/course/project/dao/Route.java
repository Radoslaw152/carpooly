package bg.fmi.spring.course.project.dao;

import bg.fmi.spring.course.project.constants.RouteType;
import bg.fmi.spring.course.project.constants.TimeInterval;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Route {

    @Id @GeneratedValue private Long id;

    @Size(min = 2)
    @Convert(converter = RideCoordinatesConverter.class)
    private List<Coordinates> pathCoordinates;

    @NotNull private RouteType routeType;

    private TimeInterval timeInterval;

    @OneToMany @JsonManagedReference List<Account> subscribedUsers;
}
