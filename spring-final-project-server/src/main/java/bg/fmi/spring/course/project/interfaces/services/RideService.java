package bg.fmi.spring.course.project.interfaces.services;

import java.util.List;
import java.util.Optional;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;

public interface RideService {
    List<Ride> getAllRides();
    Optional<Ride> getRideByDriver(String email);
    Optional<Ride> getRideById(Long id);
    Ride getRideByIdUnsecure(Long id);
    Ride addRide(Ride ride);
    Ride updateRide(Ride ride);
    Ride deleteRide(Long id);
    Ride startRide(Long id);
    Ride joinRide(Long idRide, Account account, Payment payment);
    Ride leaveRide(Long idRide, Account account);
    Ride stopRide(Long id);
    List<Ride> getAllRidesByDestination(String startingDestination, String finalDestination);
}
