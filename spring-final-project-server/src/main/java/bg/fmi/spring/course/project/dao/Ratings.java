package bg.fmi.spring.course.project.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
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
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rating {
        private String emailFrom;
        private Integer rating;
    }

    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RATINGS_ID")
    private Account account;
    @Builder.Default
    @NonNull
    @ElementCollection(targetClass = HashSet.class)
    private Set<Rating> ratings = new HashSet<>();


    public void addRating(String from, Integer rating) {
        if (rating < 0 || rating > 5) {
            //@TODO add exception
        }
        if (account.getEmail().equals(from)) {
            //@TODO add exception
        }
        ratings.add(new Rating(from, rating));
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0;
        }
        int score = 0;
        for (Rating rating : ratings) {
            score += rating.getRating();
        }
        return score / (double) ratings.size();
    }
}
