package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Coordinates;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.dto.CoordinatesFilterReq;
import bg.fmi.spring.course.project.dto.RideFilterReq;
import bg.fmi.spring.course.project.interfaces.repositories.RideRepository;
import bg.fmi.spring.course.project.interfaces.services.RideService;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RideServiceImpl implements RideService {
    private RideRepository rideRepository;

    private static Predicate<Coordinates> getPredicate(double maxDistanceInKm, Coordinates from) {
        return coordinates ->
                maxDistanceInKm >= getDistanceBetweenTwoCoordinatesInKm(from, coordinates);
    }

    @Autowired
    public RideServiceImpl(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @Override
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    @Override
    public List<Ride> getRidesByDriver(String email) {
        return rideRepository.findAll().stream()
                .filter(ride -> ride.getDriver().getEmail().equals(email))
                .collect(Collectors.toList());
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
                            "Passenger with id=%s cannot join the same ride twice",
                            payment.getOwnerAccountId()));
        }
        if (ride.getPassengers().size() == ride.getMaxPassengers()) {
            throw new RuntimeException(
                    String.format(
                            "Ride has exceeded maximum passengers - %s", ride.getMaxPassengers()));
        }
        if (ride.getDriver().getId().equals(payment.getOwnerAccountId())) {
            throw new RuntimeException(
                    String.format("cannot join your own ride with id %s", ride.getId()));
        }
        ride.getPassengers().add(payment);
        return rideRepository.save(ride);
    }

    @Override
    public Ride leaveRide(Long idRide, Account account) {
        Ride ride = getRideByIdSecure(idRide);
        Optional<Payment> passenger =
                ride.getPassengers().stream()
                        .filter(payment -> payment.getOwnerAccountId().equals(account.getId()))
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

    @Override
    public List<Ride> getJoinableFiltered(
            final RideFilterReq rideFilterReq, Long requestingAccountId) {
        List<Ride> allRides =
                getAllRides().stream()
                        .filter(
                                ride ->
                                        ride.getPassengers().stream()
                                                        .noneMatch(
                                                                passenger ->
                                                                        passenger
                                                                                .getOwnerAccountId()
                                                                                .equals(
                                                                                        requestingAccountId))
                                                && !ride.getDriver()
                                                        .getId()
                                                        .equals(requestingAccountId))
                        .collect(Collectors.toList());
        if (rideFilterReq == null) {
            return allRides;
        }
        CoordinatesFilterReq startingCoord = rideFilterReq.getStartFilter();
        if (startingCoord != null) {
            allRides =
                    allRides.stream()
                            .filter(
                                    coord ->
                                            getPredicate(
                                                            startingCoord.getRadiusInKm(),
                                                            startingCoord.getCoordinates())
                                                    .test(
                                                            coord.getPathCoordinates()
                                                                    .iterator()
                                                                    .next()))
                            .collect(Collectors.toList());
        }
        CoordinatesFilterReq endCoord = rideFilterReq.getEndFilter();
        if (endCoord != null) {
            allRides =
                    allRides.stream()
                            .filter(
                                    coord ->
                                            coord.getPathCoordinates().stream()
                                                    .skip(1)
                                                    .anyMatch(
                                                            getPredicate(
                                                                    endCoord.getRadiusInKm(),
                                                                    endCoord.getCoordinates())))
                            .collect(Collectors.toList());
        }
        return allRides;
    }

    private static double getDistanceBetweenTwoCoordinatesInKm(
            Coordinates cord1, Coordinates cord2) {
        double lat1 = cord1.getLatitude();
        double lon1 = cord1.getLongitude();
        double lat2 = cord2.getLatitude();
        double lon2 = cord2.getLongitude();
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist =
                    Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                            + Math.cos(Math.toRadians(lat1))
                                    * Math.cos(Math.toRadians(lat2))
                                    * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            return (dist);
        }
    }
}
