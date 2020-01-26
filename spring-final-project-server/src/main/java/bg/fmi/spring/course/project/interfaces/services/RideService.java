package bg.fmi.spring.course.project.interfaces.services;

import java.util.List;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Ride;

public interface RideService {
    List<Ride> getAllRides();
    Ride getRideByDriver(Account account);
    Ride addRide(Ride ride);
    Ride updateRide(String id, Ride ride);
    Ride deleteRide(String id);
    Ride startRide(String id);
    Ride joinRide(String idRide, Account account);
    Ride leaveRide(String idRide, Account account);
    Ride stopRide(String id);
    List<Ride> getAllRidesByDestination(String startingDestination, String finalDestination);
}
