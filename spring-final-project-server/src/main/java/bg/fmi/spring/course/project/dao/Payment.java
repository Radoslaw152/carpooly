package bg.fmi.spring.course.project.dao;

import bg.fmi.spring.course.project.constants.PaymentType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "owner")
public class Payment {

    @Id @GeneratedValue private Long id;

    @OneToOne
    @JoinColumn(name = "OWNER_ID")
    private Account owner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RIDE_ID")
    @JsonBackReference
    private Ride ride;

    @NonNull private PaymentType paymentType;

    private Double amount;

    private Date dateCompleted;

    private boolean isPaid = false;
}
