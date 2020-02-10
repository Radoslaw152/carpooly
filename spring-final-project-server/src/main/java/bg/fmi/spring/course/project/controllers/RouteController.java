package bg.fmi.spring.course.project.controllers;

import bg.fmi.spring.course.project.constants.RouteType;
import bg.fmi.spring.course.project.constants.TimeInterval;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Route;
import bg.fmi.spring.course.project.interfaces.services.RouteService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/routes")
public class RouteController {
    public RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAll());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/all-start-finish",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Route>> getRoutesByCoordinates(
            @RequestParam("start") String start, @RequestParam("finish") String finish) {
        return ResponseEntity.ok(routeService.getAll(start, finish));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/all-type",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Route>> getAllByType(@RequestParam("type") String type) {
        return ResponseEntity.ok(routeService.getAll(RouteType.valueOf(type)));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/all-type-time",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Route>> getAllByTypeAndTime(
            @RequestParam("type") String type, @RequestParam("time") String time) {
        return ResponseEntity.ok(
                routeService.getAll(RouteType.valueOf(type), TimeInterval.valueOf(time)));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/near-start",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Route>> getAllNearStart(
            @RequestParam("start") String start, @RequestParam("r") double radius) {
        return ResponseEntity.ok(routeService.getAllCloseToStart(start, radius));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/near-finish",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Route>> getAllNearFinish(
            @RequestParam("finish") String finish, @RequestParam("r") double radius) {
        return ResponseEntity.ok(routeService.getAllCloseToFinish(finish, radius));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/near",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Route>> getAllNearFinish(
            @RequestParam("start") String start,
            @RequestParam("finish") String finish,
            @RequestParam("r") double radius) {
        return ResponseEntity.ok(routeService.getAllCloseToStartAndFinish(start, finish, radius));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/near-list",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Route>> getAllNearStartAndFinish(
            @RequestParam("start") String start,
            @RequestParam("finish") String finish,
            @RequestParam("r") double radius,
            @RequestParam("type") String type,
            @RequestParam("time") String time) {
        return ResponseEntity.ok(
                routeService.getAllClose(
                        start,
                        finish,
                        radius,
                        radius,
                        RouteType.valueOf(type),
                        TimeInterval.valueOf(time)));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/route",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Route> getRoute(
            @RequestParam("start") String start,
            @RequestParam("finish") String finish,
            @RequestParam("type") String type,
            @RequestParam("time") String time) {
        return ResponseEntity.ok(
                routeService
                        .getRoute(
                                start, finish, RouteType.valueOf(type), TimeInterval.valueOf(time))
                        .get());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/route-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Route> getRoute(@RequestBody @Valid Route route) {
        return ResponseEntity.ok(routeService.getRoute(route).get());
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add-json-full",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Route> addRoute(@RequestBody @Valid Route route) {
        return ResponseEntity.ok(routeService.addRoute(route));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Route> addRoute(
            @RequestParam("start") String start,
            @RequestParam("finish") String finish,
            @RequestParam("type") String type,
            @RequestParam("time") String time,
            Authentication authentication) {
        Account user = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(
                routeService.addRoute(
                        start,
                        finish,
                        user.getEmail(),
                        RouteType.valueOf(type),
                        TimeInterval.valueOf(time)));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/subscribe",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Route> subscribeForRoute(
            @RequestParam("start") String start,
            @RequestParam("finish") String finish,
            @RequestParam("type") String type,
            @RequestParam("time") String time,
            Authentication authentication) {
        Account user = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(
                routeService.subscribeToRoute(
                        start,
                        finish,
                        RouteType.valueOf(type),
                        TimeInterval.valueOf(time),
                        user.getEmail()));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/subscribe-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Route> subscribeForRoute(
            @RequestBody @Valid Route route, @RequestBody @Valid Account account) {
        return ResponseEntity.ok(routeService.subscribeToRoute(route, account));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/subscribers-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Account>> getSubscribers(@RequestBody @Valid Route route) {
        return ResponseEntity.ok(routeService.getSubcribers(route));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/subscribers",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Account>> getSubscribers(
            @RequestParam("start") String start,
            @RequestParam("finish") String finish,
            @RequestParam("type") String type,
            @RequestParam("time") String time) {
        return ResponseEntity.ok(
                routeService.getSubcribers(
                        start, finish, RouteType.valueOf(type), TimeInterval.valueOf(time)));
    }
}
