package bg.fmi.spring.course.project.dao;

import bg.fmi.spring.course.project.constants.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(of = "email")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Id @GeneratedValue private String id;

    @NonNull
    @Length(min = 3, max = 16)
    private String firstName;

    @NonNull
    @Length(min = 3, max = 16)
    private String surname;

    @NonNull
    @Length(min = 3, max = 16)
    private String email;

    @NonNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;

    private Role role;
    @Builder.Default private boolean isInRide = false;
}
