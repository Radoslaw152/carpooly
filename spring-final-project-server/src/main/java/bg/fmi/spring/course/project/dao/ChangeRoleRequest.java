package bg.fmi.spring.course.project.dao;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import bg.fmi.spring.course.project.constants.RequestState;
import bg.fmi.spring.course.project.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private Account requestAccount;
    @NonNull
    private Role wantedRole;
    private RequestState requestState = RequestState.PENDING;
}
