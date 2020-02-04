package bg.fmi.spring.course.project.impl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.interfaces.repositories.RideRepository;
import bg.fmi.spring.course.project.interfaces.services.RideService;

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
        return rideRepository.findAll()
                .stream()
                .filter(ride -> ride.getDriver().getEmail().equals(email))
                .findAny();
    }

    @Override
    public Optional<Ride> getRideById(Long id) {
        return rideRepository.findById(id);
    }

    @Override
    public Ride getRideByIdUnsecure(Long id) {
        return getRideById(id).orElseThrow(()->new RuntimeException(String.format("Ride with id=%s does not exist.",id)));
    }

    @Override
    public Ride addRide(Ride ride) {
        Optional<Ride> existingRide = getRideByDriver(ride.getDriver().getEmail());
        if(existingRide.isPresent()) {
            throw new RuntimeException(String.format("Ride with driver email=%s already exist", ride.getDriver().getEmail()));
        }
        return rideRepository.save(ride);
    }

    @Override
    public Ride updateRide(Ride ride) {
        return rideRepository.save(ride);
    }

    @Override
    public Ride deleteRide(Long id) {
        Ride ride = getRideByIdUnsecure(id);
        rideRepository.delete(ride);
        return ride;
    }

    @Override
    public Ride startRide(Long id) {
        Ride ride = getRideByIdUnsecure(id);
        ride.setIsStarted(true);
        return rideRepository.save(ride);
    }

    @Override
    public Ride joinRide(Long idRide, Account account, Payment payment) {
        Ride ride = getRideByIdUnsecure(idRide);
        ride.getPassengers().put(account, payment);
        return rideRepository.save(ride);
    }

    @Override
    public Ride leaveRide(Long idRide, Account account) {
        Ride ride = getRideByIdUnsecure(idRide);
        ride.getPassengers().remove(account);
        return rideRepository.save(ride);
    }

    @Override
    public Ride stopRide(Long id) {
        Ride ride = getRideByIdUnsecure(id);
        ride.setIsStarted(false);
        return rideRepository.save(ride);
    }

    @Override
    public List<Ride> getAllRidesByDestination(String startingDestination,
            String finalDestination) {
        return rideRepository.findAll()
                .stream()
                .filter(ride -> ride.getStartingDestination().equals(startingDestination)
                        && ride.getFinalDestination().equals(finalDestination))
                .collect(Collectors.toList());
    }
}
