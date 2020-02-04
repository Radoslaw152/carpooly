package bg.fmi.spring.course.project.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.Length;

import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;
    private Role role;
    @Builder.Default
    private boolean isInRide = false;
}
