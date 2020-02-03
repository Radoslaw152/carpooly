package bg.fmi.spring.course.project.dao;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import bg.fmi.spring.course.project.constants.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RIDE_ID")
    private Ride ride;
    @NonNull
    private PaymentType paymentType;
    private boolean isPaid = false;
}
