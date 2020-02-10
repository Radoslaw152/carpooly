package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.constants.RouteType;
import bg.fmi.spring.course.project.constants.TimeInterval;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Route;
import java.util.List;
import java.util.Optional;

public interface RouteService {
    List<Route> getAll();

    List<Route> getAll(String start, String finish);

    List<Route> getAll(RouteType routeType);

    List<Route> getAll(TimeInterval timeInterval);

    List<Route> getAll(RouteType routeType, TimeInterval timeInterval);

    List<Route> getAllCloseToStart(String start, Double radiusKm);

    List<Route> getAllCloseToFinish(String finish, Double radiusKm);

    List<Route> getAllCloseToStartAndFinish(String start, String finish, Double radiusKm);

    List<Route> getAllClose(
            String start, String finish, Double radiusKmStart, Double radiusKmFinish);

    List<Route> getAllClose(
            String start,
            String finish,
            Double radiusKmStart,
            Double radiusKmFinish,
            RouteType routeType);

    List<Route> getAllClose(
            String start,
            String finish,
            Double radiusKmStart,
            Double radiusKmFinish,
            RouteType routeType,
            TimeInterval timeInterval);

    Optional<Route> getRoute(Route route);

    Optional<Route> getRoute(String start, String finish, RouteType routeType);

    Route addRoute(Route route);

    Route addRoute(String start, String finish, String creator);

    Route addRoute(
            String start,
            String finish,
            String creator,
            RouteType routeType,
            TimeInterval timeInterval);

    Route addRoute(
            String start,
            String finish,
            Account creator,
            RouteType routeType,
            TimeInterval timeInterval);

    Route addTimeInterval(Route route, TimeInterval timeInterval);

    Route subscribeToRoute(Route route, Account user);

    Route subscribeToRoute(Route route, TimeInterval timeInterval, Account user);

    Route subscribeToRoute(
            String start,
            String finish,
            RouteType routeType,
            TimeInterval timeInterval,
            String user);

    List<Account> getSubcribers(Route route);

    List<Account> getSubcribers(Route route, TimeInterval timeInterval);

    List<Account> getSubcribers(
            String start, String finish, RouteType routeType, TimeInterval timeInterval);
}
