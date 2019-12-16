package bg.fmi.piss.course.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import bg.fmi.piss.course.exceptions.WrongAccess;
import bg.fmi.piss.course.models.User;
import bg.fmi.piss.course.exceptions.InvalidEntityIdException;
import bg.fmi.piss.course.models.RideRequest;
import bg.fmi.piss.course.services.RideRequestService;

@RestController
@RequestMapping("/api/ride-requests")
public class RideRequestController {
    private RideRequestService mRideRequestService;

    @Autowired
    public RideRequestController(RideRequestService rideRequestService) {
        this.mRideRequestService = rideRequestService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<RideRequest>> getAllRideRequests(UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (currentUser.getRole().equals("passenger")) {
            List<RideRequest> rideRequests = new ArrayList<>();
            rideRequests.add(mRideRequestService.getAllRideRequestByUser(currentUser));
            return ResponseEntity.ok(rideRequests);
        }
        return ResponseEntity.ok(mRideRequestService.getAllRideRequests());
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RideRequest> addRideRequest(@RequestBody RideRequest rideRequest,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        rideRequest.setPassenger(currentUser);
        return ResponseEntity.ok(mRideRequestService.addRideRequest(rideRequest));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RideRequest> getRideRequest(@PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        RideRequest rideRequest = mRideRequestService.getRideRequest(id);
        if (!rideRequest.getPassenger().equals(currentUser) && currentUser.getRole().equals("passenger")) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",currentUser.getEmail(),id));
        }
        return ResponseEntity.ok(rideRequest);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RideRequest> updatePost(@RequestBody RideRequest rideRequest, @PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (!rideRequest.getId().equals(id)) {
            throw new InvalidEntityIdException(
                    String.format("Article ID=%s from path is different from Entity ID=%s", id,
                            rideRequest.getId()));
        }
        if (!rideRequest.getPassenger().equals(currentUser) && currentUser.getRole().equals(
                "passenger")) {
            throw new WrongAccess(String.format("User with email=%s cannot update RideRequest with ID=%s",currentUser.getEmail(),id));
        }
        return ResponseEntity.ok(mRideRequestService.updateRideRequest(rideRequest));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RideRequest> deletePost(@PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        RideRequest RideRequest = mRideRequestService.getRideRequest(id);
        if (!RideRequest.getId().equals(id)) {
            throw new InvalidEntityIdException(
                    String.format("Article ID=%s from path is different from Entity ID=%s", id,
                            RideRequest.getId()));
        }
        if (!RideRequest.getPassenger().equals(currentUser) && currentUser.getRole().equals(
                "passenger")) {
            throw new WrongAccess(String.format("User with email=%s cannot delete RideRequest with ID=%s",currentUser.getEmail(),id));
        }
        return ResponseEntity.ok(mRideRequestService.deleteRideRequest(id));
    }
}
