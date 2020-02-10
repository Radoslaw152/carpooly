package bg.fmi.spring.course.project.controllers;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Rating;
import bg.fmi.spring.course.project.interfaces.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    public RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {this.ratingService=ratingService;}

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Rating>> getAllRatings() {
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Rating>> getRatingsFromUser(@PathVariable String email) {
        return ResponseEntity.ok(ratingService.getAllRatingsByUser(email));
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Rating>> getRatingsForUser(@PathVariable String email) {
        return ResponseEntity.ok(ratingService.getAllRatingsForUser(email));
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> getRatingsForUserByUser(@PathVariable String emailFor,
                                                                @PathVariable String emailFrom) {
        return ResponseEntity.ok(ratingService.getRatingForUserByUser(emailFor, emailFrom));
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Double> getAvgRating(@PathVariable String user) {
        return ResponseEntity.ok(ratingService.getAvgRatingForUser(user));
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> addRating(@RequestBody @Valid Rating rating,
                                          Authentication authentication) {

        if(rating.getFromUserID() == null){
            Account from = (Account) authentication.getPrincipal();
            rating.setFromUserID(from.getEmail());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ratingService.addRatingToUser(rating.getForUserID(), rating.getFromUserID(),
                        rating.getRating(), rating.getMessage()));
    }

    @RequestMapping(method = RequestMethod.POST,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> editRating(@RequestBody @Valid Rating rating,
                                            Authentication authentication) {
        if(((Account) authentication.getPrincipal()).getEmail().equals(rating.getFromUserID())){
            return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.editRating(
                    rating.getForUserID(), rating.getFromUserID(), rating.getRating(), rating.getMessage()));
        } else {
            throw new RuntimeException("You do not have access to this user's reviews!");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Rating> deleteRating( @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.deleteRatingById(id));
    }

}
