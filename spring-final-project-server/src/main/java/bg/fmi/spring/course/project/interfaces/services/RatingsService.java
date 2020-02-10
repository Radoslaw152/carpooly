package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Ratings;
import java.util.List;

public interface RatingsService {
    Ratings getRatingsOfUser(String idUser);

    List<Ratings> getAllRatings();

    Ratings addRatingToUser(String idUser, Account from, Integer ratings);

    Ratings deleteRatingOfUser(String idUser, String idFrom);
}
