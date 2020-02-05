package bg.fmi.spring.course.project.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.Min;

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
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DRIVER_ID")
    private Account driver;
    @OneToMany
    @Builder.Default
    @JsonManagedReference
    private List<Payment> passengers = new ArrayList<>();
    @NonNull
    private String startingDestination;
    @NonNull
    private String finalDestination;
    @NonNull
    private Double price;
    @NonNull
    @Min(1)
    private Integer maxPassengers;
    @Builder.Default
    private Boolean isStarted = false;
}
