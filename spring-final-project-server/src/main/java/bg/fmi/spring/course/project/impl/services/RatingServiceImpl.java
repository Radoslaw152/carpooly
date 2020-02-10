package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.dao.Rating;
import bg.fmi.spring.course.project.interfaces.repositories.RatingRepository;
import bg.fmi.spring.course.project.interfaces.services.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RatingServiceImpl implements RatingService {
    private RatingRepository ratingRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository= ratingRepository;
    }

    @Override
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    /* Returns all ratings made by user*/
    @Override
    public List<Rating> getAllRatingsByUser(String userId) {
        Stream<Rating> ratings = ratingRepository.findAll().stream().filter(rating -> rating.getFromUserID().equals(userId));
        return ratings.collect(Collectors.toList());
    }

    /* Returns all ratings made for user by other users*/
    @Override
    public List<Rating> getAllRatingsForUser(String userId) {
        Stream<Rating> ratings = ratingRepository.findAll().stream().filter(rating -> rating.getForUserID().equals(userId));
        return ratings.collect(Collectors.toList());
    }

    @Override
    public Rating addRatingToUser(String forUserId, String fromUserId, Integer rating, String message) {
        Rating review = new Rating();
            if (rating >= 0 && rating <= 5) {
                review.setForUserID(forUserId);
                review.setFromUserID(fromUserId);
                review.setMessage(message);
                review.setRating(rating);
            } else {
                throw new RuntimeException("Invalid rating score exception");
            }
            Optional<Rating> existingRating = ratingRepository.
                    findAll().stream().
                    filter(r -> r.getForUserID().equals(forUserId) &&
                            r.getFromUserID().equals(fromUserId)).findAny();
            if (existingRating.isPresent())
                throw new RuntimeException(String.format("Rating for user %s by user %s already exists" , forUserId, fromUserId));
            ratingRepository.save(review);
        return review;
    }

    @Override
    public Rating deleteRatingOfUser(String forUser, String fromUser) {
        Rating rating = getRatingForUserByUser(forUser, fromUser);
        ratingRepository.delete(rating);
        return rating;
    }

    public Rating deleteRatingById(Long id){
        Optional<Rating> rating = ratingRepository.findById(id);
        if(!rating.isPresent())
            throw new RuntimeException(String.format("No rating with id = %s found", id));
        ratingRepository.delete(rating.get());
        return rating.get();
    }

    @Override
    public double getAvgRatingForUser(String userId) {
        List<Rating> ratings = getAllRatingsForUser(userId);
        double total = ratings.stream().mapToInt(Rating::getRating).sum();
        return total/ratings.size();
    }

    @Override
    public Rating editRating(String forUserId, String fromUserId, Integer rating, String message){
        deleteRatingOfUser(forUserId, fromUserId);
        return addRatingToUser(forUserId, fromUserId, rating, message);
    }

    @Override
    public Rating getRatingForUserByUser(String forUserId, String fromUserId) {
        Optional<Rating> rating = ratingRepository.findAll().stream().
                filter(r -> r.getFromUserID().equals(fromUserId) && r.getForUserID().equals(forUserId)).findAny();
        if (!rating.isPresent())
            throw new RuntimeException(String.format("No review from %s for %s found", fromUserId, forUserId));
        return rating.orElseGet(rating::get);
    }
}
