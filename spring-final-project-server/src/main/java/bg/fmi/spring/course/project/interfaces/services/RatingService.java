package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Rating;
import java.util.List;

public interface RatingService {
    List<Rating> getAllRatings();

    List<Rating> getAllRatingsByUser(String userId);
    List<Rating> getAllRatingsByUser(Account user);

    List<Rating> getAllRatingsForUser(String userId);
    List<Rating> getAllRatingsForUser(Account user);

    Rating addRating(Rating rating);
    Rating addRating(String forUser, String byUser, String message, Integer score);

    Rating deleteRating(Long id);
    Rating deleteRating(String forUser, String fromUser);
    Rating deleteRating(Rating rating);

    double getAvgRatingForUser(String userId);
    double getAvgRatingForUser(Account user);

    Rating editRating(String forUserId, String fromUserId, String message, Integer score);
    Rating editRating(Rating rating);

    Rating getRatingForUserByUser(String forUserId, String fromUserId);
    Rating getRatingForUserByUser(Account forUser, Account byUser);
}
