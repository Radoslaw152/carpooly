package bg.fmi.spring.course.project.dao;

import java.util.HashMap;
import java.util.Map;

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
    @NonNull
    @Valid
    @OneToOne
    @JoinColumn(name = "DRIVER_ID")
    private Account driver;
    @NonNull
    @Valid
    @MapKeyJoinColumn(name = "PASSENGERS_ID")
    @CollectionTable(name = "PASSENGERS_PAYMENT")
    @ElementCollection(targetClass = HashMap.class)
    private Map<Account, Payment> passengers;
    @NonNull
    private String startingDestination;
    @NonNull
    private String finalDestination;
    @NonNull
    private Double price;
    @NonNull
    private Boolean isStarted;

    public boolean checkPaid() {
        for(Payment payment : passengers.values()) {
            if(!payment.isPaid()) {
                return false;
            }
        }
        return true;
    }
}
