package bg.fmi.spring.course.project.dao;

import bg.fmi.spring.course.project.constants.PaymentType;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"ownerAccountId", "rideId"})
public class Payment {

    @Id @GeneratedValue private Long id;

    @NotNull private Long ownerAccountId;
    @NotNull private Long rideId;

    private PaymentType paymentType;

    private Double amount;

    private Date dateCompleted;

    private boolean isPaid = false;
}
