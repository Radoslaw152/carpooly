package bg.fmi.spring.course.project.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Voucher {

    @Id @GeneratedValue private Long id;

    @OneToMany @JsonManagedReference List<Account> passengerDriverPair;

    @NotNull
    @Min(1)
    private Integer amount;
}
