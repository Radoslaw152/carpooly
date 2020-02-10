package bg.fmi.spring.course.project.dao;

import bg.fmi.spring.course.project.constants.RouteType;
import bg.fmi.spring.course.project.constants.TimeInterval;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Route {

    @Id @GeneratedValue private Long id;

    @NonNull private String startingDestination;

    @NonNull private String finalDestination;

    @NotNull private RouteType routeType;

    private Map<TimeInterval, List<Account>> subscribedUsers;
}
