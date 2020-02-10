package bg.fmi.spring.course.project.controllers;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Rating;
import bg.fmi.spring.course.project.interfaces.services.RatingService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/ratings")
public class RatingController {

    public RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Rating>> getAllRatings() {
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/by-username",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Rating>> getRatingsFromUser(@RequestParam("usr") String username) {
        return ResponseEntity.ok(ratingService.getAllRatingsByUser(username));
    }
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/by",
           consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
           produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Rating>> getRatingsFromUser(@RequestBody @Valid Account user) {
        return ResponseEntity.ok(ratingService.getAllRatingsByUser(user));
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/for-username",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Rating>> getRatingsForUser(@RequestParam("usr") String username ) {
        return ResponseEntity.ok(ratingService.getAllRatingsForUser(username));
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/for",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Rating>> getRatingsForUser(@RequestBody @Valid Account account ) {
        return ResponseEntity.ok(ratingService.getAllRatingsForUser(account));
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/rating-usernames",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> getRatingsForUserByUser(
            @RequestParam("forUser") String forUser, @RequestParam("fromUser") String fromUser) {
        return ResponseEntity.ok(ratingService.getRatingForUserByUser(forUser, fromUser));
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/rating",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> getRatingsForUserByUser(
            @RequestBody @Valid Account forUser, @RequestBody @Valid Account fromUser) {
        return ResponseEntity.ok(ratingService.getRatingForUserByUser(forUser, fromUser));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/avg-username",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Double> getAvgRating(@RequestParam("usr") String user) {
        return ResponseEntity.ok(ratingService.getAvgRatingForUser(user));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/avg",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Double> getAvgRating(@RequestBody Account user) {
        return ResponseEntity.ok(ratingService.getAvgRatingForUser(user));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> addRating(@RequestParam("from") String fromUser,
                                            @RequestParam("for") String forUser,
                                            @RequestParam("msg") String message,
                                            @RequestParam("score") int score,
                                            Authentication authentication) {

        //TODO: Throw exception if authentication ins't provided  and remove fromUser
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ratingService.addRating(forUser, fromUser, message, score));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add-json",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> addRating(
            @RequestBody @Valid Rating rating, Authentication authentication) {

        if (rating.getFromUserID() == null) {
            Account from = (Account) authentication.getPrincipal();
            rating.setFromUserID(from.getEmail());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ratingService.addRating(rating));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/edit",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> editRating(@RequestParam("forUser") String forUser,
                                             @RequestParam("byUser") String byUser,
                                             @RequestParam("msg") String message,
                                             @RequestParam("score") int score,
                                             Authentication authentication) {
        if (((Account) authentication.getPrincipal()).getEmail().equals(byUser)) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ratingService.editRating(forUser, byUser, message, score));
        } else {
            throw new RuntimeException("You do not have access to this user's reviews!");
        }
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/edit-json",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> editRating(
            @RequestBody @Valid Rating rating, Authentication authentication) {
        if (((Account) authentication.getPrincipal()).getEmail().equals(rating.getFromUserID())) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ratingService.editRating(rating));
        } else {
            throw new RuntimeException("You do not have access to this user's reviews!");
        }
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/delete/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> deleteRating(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.deleteRating(id));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/delete",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> deleteRating(@RequestParam("from") String fromUser,
                                               @RequestParam("for") String forUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.deleteRating(forUser, fromUser));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/delete-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> deleteRating(@RequestBody @Valid Rating rating, Authentication authentication) {
        Account loggedUser = (Account) authentication.getPrincipal();
        if(!loggedUser.getEmail().equals(rating.getFromUserID())){
            throw new RuntimeException("Logged username and the owner of the event do not match");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.deleteRating(rating));
    }


}
