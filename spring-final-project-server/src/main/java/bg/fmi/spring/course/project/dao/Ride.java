package bg.fmi.spring.course.project.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
public class Ride {
    @Id @GeneratedValue private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DRIVER_ID")
    private Account driver;

    @OneToMany @Builder.Default @JsonManagedReference
    private List<Payment> passengers = new ArrayList<>();

    @Size(min = 2)
    @Convert(converter = RideCoordinatesConverter.class)
    private List<Coordinates> pathCoordinates;

    @NotNull private Double price;

    @NonNull
    @Min(1)
    private Integer maxPassengers;

    @Builder.Default private Boolean isStarted = false;
}
