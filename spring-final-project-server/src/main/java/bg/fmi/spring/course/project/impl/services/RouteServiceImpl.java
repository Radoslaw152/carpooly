package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.constants.RouteType;
import bg.fmi.spring.course.project.constants.TimeInterval;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Route;
import bg.fmi.spring.course.project.interfaces.repositories.RouteRepository;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import bg.fmi.spring.course.project.interfaces.services.RouteService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteServiceImpl implements RouteService {

    @Autowired private RouteRepository routeRepository;

    @Autowired private AccountService accountService;

    @Override
    public List<Route> getAll() {
        return routeRepository.findAll();
    }

    @Override
    public List<Route> getAll(String start, String finish) {
        return getAll().stream()
                .filter(
                        route ->
                                route.getStartingDestination().equals(start)
                                        && route.getFinalDestination().equals(finish))
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> getAll(RouteType routeType) {
        List<RouteType> types = groupTypes(routeType);
        return getAll().stream()
                .filter(route -> types.contains(route.getRouteType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> getAll(TimeInterval timeInterval) {
        List<TimeInterval> times = groupTimeIntervals(timeInterval);
        List<Route> routes = getAll();
        List<Route> result = new ArrayList<>();
        for (Route r : routes) {
            for (TimeInterval t : times) {
                if (r.getTimeInterval().equals(t)) {
                    result.add(r);
                }
            }
        }
        return result;
    }

    @Override
    public List<Route> getAll(RouteType routeType, TimeInterval timeInterval) {
        List<RouteType> types = groupTypes(routeType);
        return getAll(timeInterval).stream()
                .filter(route -> types.contains(route.getRouteType()))
                .collect(Collectors.toList());
    }

    public double calculateDistance(String point1, String point2) {
        String[] coordinates1 = point1.split(", ");
        double x1 = Double.parseDouble(coordinates1[0]);
        double y1 = Double.parseDouble(coordinates1[1]);
        String[] coordinates2 = point2.split(", ");
        double x2 = Double.parseDouble(coordinates2[0]);
        double y2 = Double.parseDouble(coordinates2[1]);

        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    @Override
    public List<Route> getAllCloseToStart(String start, Double radiusKm) {
        return getAll().stream()
                .filter(
                        route ->
                                calculateDistance(start, route.getStartingDestination())
                                        <= radiusKm)
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> getAllCloseToFinish(String finish, Double radiusKm) {
        return getAll().stream()
                .filter(route -> calculateDistance(finish, route.getFinalDestination()) <= radiusKm)
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> getAllCloseToStartAndFinish(String start, String finish, Double radiusKm) {
        return getAllClose(start, finish, radiusKm, radiusKm);
    }

    @Override
    public List<Route> getAllClose(
            String start, String finish, Double radiusKmStart, Double radiusKmFinish) {
        return getAll().stream()
                .filter(
                        route ->
                                calculateDistance(finish, route.getFinalDestination())
                                                <= radiusKmStart
                                        && calculateDistance(start, route.getStartingDestination())
                                                <= radiusKmFinish)
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> getAllClose(
            String start,
            String finish,
            Double radiusKmStart,
            Double radiusKmFinish,
            RouteType routeType) {

        return getAllClose(start, finish, radiusKmStart, radiusKmFinish).stream()
                .filter(route -> route.getRouteType().equals(routeType))
                .collect(Collectors.toList());
    }

    @Override
    public List<Route> getAllClose(
            String start,
            String finish,
            Double radiusKmStart,
            Double radiusKmFinish,
            RouteType routeType,
            TimeInterval timeInterval) {
        return getAllClose(start, finish, radiusKmStart, radiusKmFinish, routeType).stream()
                .filter(route -> route.getTimeInterval().equals(timeInterval))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Route> getRoute(Route route) {
        return getAll(route.getStartingDestination(), route.getFinalDestination()).stream()
                .filter(route1 -> route1.getRouteType().equals(route.getRouteType()))
                .findAny();
    }

    @Override
    public Optional<Route> getRoute(
            String start, String finish, RouteType routeType, TimeInterval timeInterval) {
        return getAll(start, finish).stream()
                .filter(route -> route.getRouteType().equals(routeType))
                .findFirst();
    }

    @Override
    public Route addRoute(String start, String finish, String creator) {
        return addRoute(start, finish, creator, RouteType.ANY, TimeInterval.ANY);
    }

    @Override
    public Route addRoute(
            String start,
            String finish,
            String creator,
            RouteType routeType,
            TimeInterval timeInterval) {
        Optional<Account> account = accountService.getAccountByEmail(creator);
        if (account.isPresent()) {
            return addRoute(start, finish, account.get(), routeType, timeInterval);
        }
        throw new RuntimeException(String.format("No user with email = %s found", creator));
    }

    @Override
    public Route addRoute(
            String start,
            String finish,
            Account creator,
            RouteType routeType,
            TimeInterval timeInterval) {
        Route route = new Route();
        route.setFinalDestination(finish);
        route.setStartingDestination(finish);
        route.setRouteType(routeType);
        route.setTimeInterval(timeInterval);
        if (getRoute(route).isPresent()) {
            throw new RuntimeException("This route already exists");
        }
        List<Account> accounts = new ArrayList<>();
        accounts.add(creator);
        route.setSubscribedUsers(accounts);
        return routeRepository.save(route);
    }

    @Override
    public Route addRoute(Route route) {
        if (getRoute(route).isPresent()) {
            throw new RuntimeException("This route already exists");
        }
        return routeRepository.save(route);
    }

    @Override
    public Route subscribeToRoute(Route route, Account user) {
        Optional<Route> routeOptional = getRoute(route);
        if (!routeOptional.isPresent())
            throw new RuntimeException(
                    String.format(
                            "Route with start = %s and finish = %s does not exist",
                            route.getStartingDestination(), route.getFinalDestination()));
        route = routeOptional.get();
        List<Account> accounts = route.getSubscribedUsers();
        if (accounts.contains(user)) {
            throw new RuntimeException(
                    String.format(
                            "User %s is already subscribed for route from %s to %s for time interval %s",
                            user.getEmail(),
                            route.getStartingDestination(),
                            route.getFinalDestination(),
                            route.getTimeInterval()));
        }
        accounts.add(user);
        routeRepository.deleteById(route.getId());
        route.setSubscribedUsers(accounts);
        return routeRepository.save(route);
    }

    @Override
    public Route subscribeToRoute(
            String start,
            String finish,
            RouteType routeType,
            TimeInterval timeInterval,
            String user) {
        Optional<Route> route = getRoute(start, finish, routeType, timeInterval);
        Optional<Account> account = accountService.getAccountByEmail(user);
        if (route.isPresent() && account.isPresent())
            return subscribeToRoute(route.get(), account.get());
        throw new RuntimeException("No route or user found");
    }

    @Override
    public List<Account> getSubcribers(Route route) {
        List<Account> users = new ArrayList<>();
        List<Route> routes = getAll(route.getStartingDestination(), route.getFinalDestination());
        for (Route r : routes) users.addAll(r.getSubscribedUsers());
        return users;
    }

    @Override
    public List<Account> getSubcribers(Route route, TimeInterval timeInterval) {
        List<TimeInterval> times = groupTimeIntervals(timeInterval);
        List<Account> users = new ArrayList<>();
        for (TimeInterval t : times) {
            route.setTimeInterval(t);
            Optional<Route> routes = getRoute(route);
            routes.ifPresent(value -> users.addAll(value.getSubscribedUsers()));
        }
        return users;
    }

    @Override
    public List<Account> getSubcribers(
            String start, String finish, RouteType routeType, TimeInterval timeInterval) {
        Optional<Route> route = getRoute(start, finish, routeType, timeInterval);
        if (route.isPresent()) return route.get().getSubscribedUsers();
        throw new RuntimeException("Route not found");
    }

    // There are methods that should use these lists however they do not.
    private List<TimeInterval> groupTimeIntervals(TimeInterval timeInterval) {
        List<TimeInterval> times = new ArrayList<>();
        if (TimeInterval.ANY.equals(timeInterval)) {
            times = Arrays.asList(TimeInterval.values());
        } else {
            times.add(timeInterval);
            if (TimeInterval.MORNING.equals(timeInterval)) {
                times.add(TimeInterval.AM_5);
                times.add(TimeInterval.AM_6);
                times.add(TimeInterval.AM_7);
                times.add(TimeInterval.AM_8);
                times.add(TimeInterval.AM_9);
                times.add(TimeInterval.AM_10);
                times.add(TimeInterval.AM_11);
            }
            if (TimeInterval.LUNCH.equals(timeInterval)) {
                times.add(TimeInterval.AM_12);
                times.add(TimeInterval.PM_1);
                times.add(TimeInterval.PM_2);
                times.add(TimeInterval.PM_3);
                times.add(TimeInterval.PM_4);
            }
            if (TimeInterval.EVENING.equals(timeInterval)) {
                times.add(TimeInterval.PM_5);
                times.add(TimeInterval.PM_6);
                times.add(TimeInterval.PM_7);
                times.add(TimeInterval.PM_8);
                times.add(TimeInterval.PM_9);
                times.add(TimeInterval.PM_10);
            }
            if (TimeInterval.NIGHT.equals(timeInterval)) {
                times.add(TimeInterval.PM_11);
                times.add(TimeInterval.PM_12);
                times.add(TimeInterval.AM_1);
                times.add(TimeInterval.AM_2);
                times.add(TimeInterval.AM_3);
                times.add(TimeInterval.AM_4);
            }
        }
        return times;
    }

    private List<RouteType> groupTypes(RouteType routeType) {
        List<RouteType> routeTypes = new ArrayList<>();
        if (RouteType.ANY.equals(routeType)) routeTypes = Arrays.asList(RouteType.values());
        else routeTypes.add(routeType);
        return routeTypes;
    }
}
