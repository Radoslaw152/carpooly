package bg.fmi.spring.course.project.dao;

import org.hibernate.validator.constraints.Length;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import bg.fmi.spring.course.project.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue
    private Long id;
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
    private String passwordHash;
    @NonNull
    private Role role;
    @Builder.Default
    private boolean isInRide = false;
    @Builder.Default
    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RATINGS_ID")
    @Valid
    private Ratings rating = new Ratings();
}
