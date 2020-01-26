package bg.fmi.spring.course.project.dao;

import org.springframework.data.annotation.Id;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
    private Ride ride;
    @NonNull
    private PaymentType paymentType;
    private boolean isPaid = false;
}
