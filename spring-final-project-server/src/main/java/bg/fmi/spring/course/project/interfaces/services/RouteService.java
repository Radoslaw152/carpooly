package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.constants.RouteType;
import bg.fmi.spring.course.project.constants.TimeInterval;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Coordinates;
import bg.fmi.spring.course.project.dao.Route;
import java.util.List;
import java.util.Optional;

public interface RouteService {
    List<Route> getAll();

    List<Route> getAll(Coordinates start, Coordinates finish);

    List<Route> getAll(RouteType routeType);

    List<Route> getAll(TimeInterval timeInterval);

    List<Route> getAll(RouteType routeType, TimeInterval timeInterval);

    List<Route> getAllCloseToStart(Coordinates start, Double radiusKm);

    List<Route> getAllCloseToFinish(Coordinates finish, Double radiusKm);

    List<Route> getAllCloseToStartAndFinish(Coordinates start, Coordinates finish, Double radiusKm);

    List<Route> getAllClose(
            Coordinates start, Coordinates finish, Double radiusKmStart, Double radiusKmFinish);

    List<Route> getAllClose(
            Coordinates start,
            Coordinates finish,
            Double radiusKmStart,
            Double radiusKmFinish,
            RouteType routeType);

    List<Route> getAllClose(
            Coordinates start,
            Coordinates finish,
            Double radiusKmStart,
            Double radiusKmFinish,
            RouteType routeType,
            TimeInterval timeInterval);

    Optional<Route> getRoute(Route route);

    Optional<Route> getRoute(
            Coordinates start, Coordinates finish, RouteType routeType, TimeInterval timeInterval);

    Route addRoute(Route route);

    Route addRoute(Coordinates start, Coordinates finish, String creator);

    Route addRoute(
            Coordinates start,
            Coordinates finish,
            String creator,
            RouteType routeType,
            TimeInterval timeInterval);

    Route addRoute(
            Coordinates start,
            Coordinates finish,
            Account creator,
            RouteType routeType,
            TimeInterval timeInterval);

    Route subscribeToRoute(Route route, Account user);

    Route subscribeToRoute(
            Coordinates start,
            Coordinates finish,
            RouteType routeType,
            TimeInterval timeInterval,
            String user);

    List<Account> getSubcribers(Route route);

    List<Account> getSubcribers(Route route, TimeInterval timeInterval);

    List<Account> getSubcribers(
            Coordinates start, Coordinates finish, RouteType routeType, TimeInterval timeInterval);
}
