package bg.fmi.piss.course.services;

import java.util.List;

import bg.fmi.piss.course.models.User;
import bg.fmi.piss.course.models.RideRequest;

public interface RideRequestService {
    List<RideRequest> getAllRideRequests();

    RideRequest getAllRideRequestByUser(User user);

    RideRequest addRideRequest(RideRequest rideRequest);

    RideRequest updateRideRequest(RideRequest rideRequest);

    RideRequest getRideRequest(String id);

    RideRequest deleteRideRequest(String id);
}
