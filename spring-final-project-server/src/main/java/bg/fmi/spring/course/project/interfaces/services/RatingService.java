package bg.fmi.spring.course.project.interfaces.services;

import java.util.List;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Rating;

public interface RatingService {
    List<Rating> getAllRatings();
    List<Rating> getAllRatingsByUser(String userId);
    List<Rating> getAllRatingsForUser(String userId);
    Rating addRatingToUser(String forUserId, String fromUserId, Integer rating, String message);
    Rating deleteRatingById(Long  id);
     Rating deleteRatingOfUser(String forUser, String fromUser);
    double getAvgRatingForUser(String userId);
    Rating editRating(String forUserId, String fromUserId, Integer rating, String message);
    Rating getRatingForUserByUser(String forUserId, String fromUserId);
}
