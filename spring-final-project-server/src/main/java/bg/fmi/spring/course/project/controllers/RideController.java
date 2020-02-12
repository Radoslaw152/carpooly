package bg.fmi.spring.course.project.controllers;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.dto.RideFilterReq;
import bg.fmi.spring.course.project.exceptions.NotFoundException;
import bg.fmi.spring.course.project.interfaces.services.RideService;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private RideService rideService;

    @Autowired
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ride>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ride>> getRideByDriver(@PathVariable String email) {
        return ResponseEntity.ok(rideService.getRidesByDriver(email));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> addRide(
            @RequestBody @Valid Ride ride, Authentication authentication) {
        if (ride.getDriver() == null) {
            ride.setDriver((Account) authentication.getPrincipal());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.addRide(ride));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    //    @PreAuthorize("(#ride.getDriver().getId() == authentication.principal.getId() "
    //            + "and #id == ride.getId()) or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Ride> updateRide(@PathVariable Long id, @RequestBody @Valid Ride ride) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.updateRide(ride));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> deleteRide(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.deleteRide(id));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "startRide/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> startRide(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rideService.startRide(id));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "stopRide/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> stopRide(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rideService.stopRide(id));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "joinRide/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> joinRide(@PathVariable Long id, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Ride rideById =
                rideService
                        .getRideById(id)
                        .orElseThrow(
                                () ->
                                        NotFoundException.generateForTypeAndIdTypeAndId(
                                                Ride.class.getSimpleName(), "id", id.toString()));
        Payment payment =
                Payment.builder()
                        .rideId(rideById.getId())
                        .ownerAccountId(account.getId())
                        .isPaid(false)
                        .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rideService.joinRide(id, payment));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "leaveRide/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ride> leaveRide(@PathVariable Long id, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(rideService.leaveRide(id, account));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "filter",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ride>> getAllRidesByStartingAndFinalDestination(
            @RequestBody RideFilterReq rideFilterReq) {
        return ResponseEntity.ok(rideService.getByFilter(rideFilterReq));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "joined",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Ride> getJoinedRides(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        return rideService.getAllRides().stream()
                .filter(
                        ride ->
                                ride.getPassengers().stream()
                                                .map(Payment::getOwnerAccountId)
                                                .anyMatch(account.getId()::equals)
                                        || ride.getDriver().getId().equals(account.getId()))
                .collect(Collectors.toList());
    }
}
