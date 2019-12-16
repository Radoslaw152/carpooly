package bg.fmi.piss.course.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import bg.fmi.piss.course.models.User;
import bg.fmi.piss.course.services.RideRequestService;
import bg.fmi.piss.course.dao.RideRequestRepository;
import bg.fmi.piss.course.exceptions.EntityNotFoundException;
import bg.fmi.piss.course.models.RideRequest;

@Service
public class RideRequestServiceImpl implements RideRequestService {
    private RideRequestRepository mRideRequestRepository;

    @Autowired
    public RideRequestServiceImpl(RideRequestRepository RideRequestRepository) {
        this.mRideRequestRepository = RideRequestRepository;
    }

    @Override
    public List<RideRequest> getAllRideRequests() {
        return mRideRequestRepository.findAll();
    }

    @Override
    public RideRequest getAllRideRequestByUser(User user) {
        return mRideRequestRepository.findAll()
                .stream()
                .filter(rideRequest -> rideRequest.getPassenger().equals(user))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There are no rides from %s user", user.getEmail())));
    }

    @Override
    public RideRequest addRideRequest(RideRequest rideRequest) {
        return mRideRequestRepository.insert(rideRequest);
    }

    @Override
    public RideRequest updateRideRequest(RideRequest rideRequest) {
        return mRideRequestRepository.save(rideRequest);
    }

    @Override
    public RideRequest getRideRequest(String id) {
        return mRideRequestRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There is no entity with %s ID", id)));
    }

    @Override
    public RideRequest deleteRideRequest(String id) {
        RideRequest RideRequest = getRideRequest(id);
        mRideRequestRepository.deleteById(id);
        return RideRequest;
    }
}
