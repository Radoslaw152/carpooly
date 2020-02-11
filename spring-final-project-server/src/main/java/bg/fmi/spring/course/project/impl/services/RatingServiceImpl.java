package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Rating;
import bg.fmi.spring.course.project.interfaces.repositories.RatingRepository;
import bg.fmi.spring.course.project.interfaces.services.RatingService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private RatingRepository ratingRepository;
    // TODO: ADD EXCEPTIONS WHERE NEEDED
    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    /* Returns all ratings made by user*/
    @Override
    public List<Rating> getAllRatingsByUser(String userId) {
        Stream<Rating> ratings =
                ratingRepository.findAll().stream()
                        .filter(rating -> rating.getFromUserID().equals(userId));
        return ratings.collect(Collectors.toList());
    }

    @Override
    public List<Rating> getAllRatingsByUser(Account user) {
        return getAllRatingsByUser(user.getEmail());
    }

    /* Returns all ratings made for user by other users*/
    @Override
    public List<Rating> getAllRatingsForUser(String userId) {
        Stream<Rating> ratings =
                ratingRepository.findAll().stream()
                        .filter(rating -> rating.getForUserID().equals(userId));
        return ratings.collect(Collectors.toList());
    }

    @Override
    public List<Rating> getAllRatingsForUser(Account user) {
        return getAllRatingsForUser(user.getEmail());
    }

    @Override
    public Rating addRating(Rating rating) {
        Rating review = new Rating();
        if (rating.getScore() < 0 || rating.getScore() > 5) {
            throw new RuntimeException("Invalid rating score exception");
        }
        Optional<Rating> existingRating =
                ratingRepository.findAll().stream()
                        .filter(
                                r ->
                                        r.getForUserID().equals(rating.getForUserID())
                                                && r.getFromUserID().equals(rating.getFromUserID()))
                        .findAny();
        if (existingRating.isPresent())
            throw new RuntimeException(
                    String.format(
                            "Rating for user %s by user %s already exists",
                            rating.getForUserID(), rating.getFromUserID()));
        ratingRepository.save(review);
        return review;
    }

    @Override
    public Rating addRating(String forUser, String byUser, String message, Integer score) {
        Rating rating = new Rating();
        rating.setFromUserID(byUser);
        rating.setForUserID(forUser);
        rating.setMessage(message);
        rating.setScore(score);
        return addRating(rating);
    }

    @Override
    public Rating deleteRating(String forUser, String fromUser) {
        Rating rating = getRatingForUserByUser(forUser, fromUser);
        ratingRepository.delete(rating);
        return rating;
    }

    @Override
    public Rating deleteRating(Rating rating) {
        return deleteRating(rating.getForUserID(), rating.getFromUserID());
    }

    public Rating deleteRating(Long id) {
        Optional<Rating> rating = ratingRepository.findById(id);
        if (!rating.isPresent())
            throw new RuntimeException(String.format("No rating with id = %s found", id));
        ratingRepository.delete(rating.get());
        return rating.get();
    }

    @Override
    public double getAvgRatingForUser(String userId) {
        List<Rating> ratings = getAllRatingsForUser(userId);
        double total = ratings.stream().mapToInt(Rating::getScore).sum();
        return total / ratings.size();
    }

    @Override
    public double getAvgRatingForUser(Account user) {
        return getAvgRatingForUser(user.getEmail());
    }

    @Override
    public Rating editRating(String forUserId, String fromUserId, String message, Integer rating) {
        deleteRating(forUserId, fromUserId);
        return addRating(forUserId, fromUserId, message, rating);
    }

    @Override
    public Rating editRating(Rating rating) {
        return editRating(
                rating.getForUserID(),
                rating.getFromUserID(),
                rating.getMessage(),
                rating.getScore());
    }

    @Override
    public Rating getRatingForUserByUser(String forUserId, String fromUserId) {
        Optional<Rating> rating =
                ratingRepository.findAll().stream()
                        .filter(
                                r ->
                                        r.getFromUserID().equals(fromUserId)
                                                && r.getForUserID().equals(forUserId))
                        .findAny();
        if (!rating.isPresent())
            throw new RuntimeException(
                    String.format("No review from %s for %s found", fromUserId, forUserId));
        return rating.orElseGet(rating::get);
    }

    @Override
    public Rating getRatingForUserByUser(Account forUser, Account byUser) {
        return getRatingForUserByUser(forUser.getEmail(), byUser.getEmail());
    }
}
