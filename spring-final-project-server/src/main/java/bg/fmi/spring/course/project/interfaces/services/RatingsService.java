package bg.fmi.spring.course.project.interfaces.services;

import java.util.List;
import java.util.Map;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Ratings;

public interface RatingsService {
    Ratings getRatingsOfUser(String idUser);
    List<Ratings> getAllRatings();
    Ratings addRatingToUser(String idUser, Account from, Integer ratings);
    Ratings deleteRatingOfUser(String idUser, String idFrom);
}
