package bg.fmi.piss.course.services;

import java.util.List;

import bg.fmi.piss.course.models.User;
import bg.fmi.piss.course.models.Drive;

public interface DriveService {
    List<Drive> getAllDrives();

    List<Drive> getCurrentDrive(User user);

    List<Drive> getAllDrivesByRideRequest(String id);
    Drive chooseDrive(String requestRideId, String id);

    Drive addDrive(Drive drive);
    Drive updateDrive(Drive drive);
    Drive getDrive(String id);
    Drive deleteDrive(String id);
}
