package bg.fmi.spring.course.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.interfaces.services.RideService;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private RideService rideService;

    @Autowired
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ride>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> getRideByDriver(@PathVariable String email) {
        Ride ride = rideService.getRideByDriver(email)
                .orElseThrow(() -> new RuntimeException(
                        String.format("There is no ride with driver email=%s", email)));
        return ResponseEntity.ok(ride);
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> addRide(@RequestBody @Valid Ride ride,
            Authentication authentication) {
        if (ride.getDriver() == null) {
            ride.setDriver((Account) authentication.getPrincipal());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.addRide(ride));
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    @PreAuthorize("(#ride.getDriver().getId() == authentication.principal.getId() "
//            + "and #id == ride.getId()) or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Ride> updateRide(@PathVariable Long id, @RequestBody @Valid Ride ride) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.updateRide(ride));
    }

    @RequestMapping(method = RequestMethod.DELETE,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> deleteRide(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.deleteRide(id));
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "startRide/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> startRide(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rideService.startRide(id));
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "stopRide/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> stopRide(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rideService.stopRide(id));
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "joinRide/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> joinRide(@PathVariable Long id, @RequestBody Payment payment,
            Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        payment.setOwner(account);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rideService.joinRide(id, payment));
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "leaveRide/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> leaveRide(@PathVariable Long id, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rideService.leaveRide(id, account));
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "filter",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ride>> getAllRidesByStartingAndFinalDestination(@RequestParam String startingDestination, @RequestParam String finalDestination) {
        return ResponseEntity.ok(rideService.getAllRidesByDestination(startingDestination, finalDestination));
    }
}
