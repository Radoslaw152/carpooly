package bg.fmi.spring.course.project.dao;

import bg.fmi.spring.course.project.constants.RequestState;
import bg.fmi.spring.course.project.constants.Role;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {
    @Id @GeneratedValue private String id;

    @NonNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "REQUEST_ACCOUNT_ID")
    private Account requestAccount;

    @NonNull private Role wantedRole;
    private RequestState requestState = RequestState.PENDING;
}
