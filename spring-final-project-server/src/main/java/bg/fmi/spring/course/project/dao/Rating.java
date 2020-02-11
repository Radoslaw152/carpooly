package bg.fmi.spring.course.project.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class Rating {

    @NotNull private String fromUserID;

    @NotNull private Integer score;

    private String message;

    @NotNull private String forUserID;

    @Id @GeneratedValue private Long id;
}
