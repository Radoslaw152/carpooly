package bg.fmi.piss.course.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import bg.fmi.piss.course.dao.DriveRepository;
import bg.fmi.piss.course.models.User;
import bg.fmi.piss.course.exceptions.EntityNotFoundException;
import bg.fmi.piss.course.models.Drive;
import bg.fmi.piss.course.services.DriveService;

@Service
public class DriveServiceImpl implements DriveService {

    private DriveRepository mDriveRepository;

    @Autowired
    public DriveServiceImpl(DriveRepository driveRepository) {
        this.mDriveRepository = driveRepository;
    }

    @Override
    public List<Drive> getAllDrives() {
        return mDriveRepository.findAll();
    }

    @Override
    public List<Drive> getCurrentDrive(User user) {
        return mDriveRepository.findAll()
                .stream()
                .filter(drive -> drive.getRide().getPassenger().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Drive> getAllDrivesByRideRequest(String id) {
        return mDriveRepository.findAll()
                .stream()
                .filter(drive -> drive.getRide().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public Drive chooseDrive(String requestRideId, String id) {
        return mDriveRepository.findAll()
                .stream()
                .filter(drive -> drive.getRide().getId().equals(requestRideId))
                .filter(drive -> drive.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There is no entity with %s user", id)));
    }

    @Override
    public Drive addDrive(Drive drive) {
        return mDriveRepository.insert(drive);
    }

    @Override
    public Drive updateDrive(Drive drive) {
        return mDriveRepository.save(drive);
    }

    @Override
    public Drive getDrive(String id) {
        return mDriveRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There is no entity with %s user", id)));
    }

    @Override
    public Drive deleteDrive(String id) {
        Drive drive = getDrive(id);
        mDriveRepository.deleteById(id);
        return drive;
    }
}
