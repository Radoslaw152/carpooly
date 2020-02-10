package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Coordinates;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.interfaces.repositories.RideRepository;
import bg.fmi.spring.course.project.interfaces.services.RideService;
import com.google.gson.Gson;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RideServiceImpl implements RideService {
    private RideRepository rideRepository;

    @Autowired
    public RideServiceImpl(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @Override
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    @Override
    public Optional<Ride> getRideByDriver(String email) {
        return rideRepository.findAll().stream()
                .filter(ride -> ride.getDriver().getEmail().equals(email))
                .findAny();
    }

    @Override
    public Optional<Ride> getRideById(Long id) {
        return rideRepository.findById(id);
    }

    @Override
    public Ride getRideByIdSecure(Long id) {
        return getRideById(id)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        String.format("Ride with id=%s does not exist.", id)));
    }

    @Override
    public Ride addRide(Ride ride) {
        Optional<Ride> existingRide = getRideByDriver(ride.getDriver().getEmail());
        if (existingRide.isPresent()) {
            throw new RuntimeException(
                    String.format(
                            "Ride with driver email=%s already exist",
                            ride.getDriver().getEmail()));
        }
        return rideRepository.save(ride);
    }

    @Override
    public Ride updateRide(Ride ride) {
        return rideRepository.save(ride);
    }

    @Override
    public Ride deleteRide(Long id) {
        Ride ride = getRideByIdSecure(id);
        rideRepository.delete(ride);
        return ride;
    }

    @Override
    public Ride startRide(Long id) {
        Ride ride = getRideByIdSecure(id);
        if (ride.getIsStarted()) {
            throw new RuntimeException(
                    String.format(
                            "Ride with driver email=%s cannot be started, because it has been already started",
                            ride.getDriver().getEmail()));
        }
        ride.setIsStarted(true);
        return rideRepository.save(ride);
    }

    @Override
    public Ride joinRide(Long idRide, Payment payment) {
        Ride ride = getRideByIdSecure(idRide);
        if (ride.getPassengers().contains(payment)) {
            throw new RuntimeException(
                    String.format(
                            "Passenger with email=%s cannot join the same ride twice",
                            payment.getOwner().getEmail()));
        }
        if (ride.getPassengers().size() == ride.getMaxPassengers()) {
            throw new RuntimeException(
                    String.format(
                            "Ride has exceeded maximum passengers - %s", ride.getMaxPassengers()));
        }
        ride.getPassengers().add(payment);
        return rideRepository.save(ride);
    }

    @Override
    public Ride leaveRide(Long idRide, Account account) {
        Ride ride = getRideByIdSecure(idRide);
        Optional<Payment> passenger =
                ride.getPassengers().stream()
                        .filter(payment -> payment.getOwner().equals(account))
                        .findAny();
        if (!passenger.isPresent()) {
            throw new RuntimeException(
                    String.format(
                            "Passenger with email=%s isn't in the ride to join",
                            account.getEmail()));
        }
        ride.getPassengers().remove(passenger.get());
        return rideRepository.save(ride);
    }

    @Override
    public Ride stopRide(Long id) {
        Ride ride = getRideByIdSecure(id);
        if (!ride.getIsStarted()) {
            throw new RuntimeException(
                    String.format(
                            "Ride with driver email=%s cannot be stopped, because it hasn't started",
                            ride.getDriver().getEmail()));
        }
        ride.setIsStarted(false);
        return rideRepository.save(ride);
    }

    @Override
    public List<Ride> getAllRidesByDestination(
            Coordinates startingDestination, Coordinates finalDestination) {
        return rideRepository.findAll().stream()
                .filter(
                        ride ->
                                ride.getPathCoordinates()
                                                .iterator()
                                                .next()
                                                .equals(startingDestination)
                                        && ride.getPathCoordinates()
                                                .get(ride.getPathCoordinates().size() - 1)
                                                .equals(finalDestination)
                                        && !ride.getIsStarted())
                .collect(Collectors.toList());
    }
}
