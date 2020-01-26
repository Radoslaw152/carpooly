package bg.fmi.spring.course.project.dao;

import org.springframework.data.annotation.Id;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToOne;

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
public class Ratings {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
    @Builder.Default
    @NonNull
    private Map<String,Integer> ratings = new HashMap<>();

    public void addRating(String from, Integer rating) {
        if(rating < 0 || rating > 5) {
            //@TODO add exception
        }
        if(account.getEmail().equals(from)) {
            //@TODO add exception
        }
        ratings.put(from,rating);
    }

    public double getAverageRating() {
        if(ratings.isEmpty()) {
            return 0;
        }
        int score = 0;
        for(Integer rating : ratings.values()) {
            score += rating;
        }
        return score / (double) ratings.size();
    }
}
