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
import bg.fmi.piss.course.models.Drive;
import bg.fmi.piss.course.services.DriveService;

@RestController
@RequestMapping("/api/drive")
public class DriveController {
    private DriveService mDriveService;

    @Autowired
    public DriveController(DriveService driveService) {
        this.mDriveService = driveService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Drive>> getAllDrives(
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (!currentUser.getRole().equals("administrator")) {
            return ResponseEntity.ok(mDriveService.getCurrentDrive(currentUser));
        }
        return ResponseEntity.ok(mDriveService.getAllDrives());
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Drive> addDrive(@RequestBody Drive drive,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (currentUser.getRole().equals("passenger")) {
            throw new WrongAccess("You cannot add a drive. Only a driver and administrator can");
        }
        drive.setDriver(currentUser);
        return ResponseEntity.ok(mDriveService.addDrive(drive));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Drive> getDrive(@PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        Drive drive = mDriveService.getDrive(id);
        if ((!drive.getDriver().equals(currentUser) || !drive.getRide().getPassenger().equals(
                currentUser))
                && !currentUser.getRole().equals("administrator")) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",
                    currentUser.getEmail(), id));
        }
        return ResponseEntity.ok(drive);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Drive> deleteDrive(@PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        Drive drive = mDriveService.getDrive(id);
        if (!drive.getId().equals(id)) {
            throw new InvalidEntityIdException(
                    String.format("Article ID=%s from path is different from Entity ID=%s", id,
                            drive.getId()));
        }
        if ((!drive.getDriver().equals(currentUser) || !drive.getRide().getPassenger().equals(
                currentUser))
                && !currentUser.getRole().equals("administrator")) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",
                    currentUser.getEmail(), id));
        }
        return ResponseEntity.ok(mDriveService.deleteDrive(id));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            value = "/{id}/{requestId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Drive> chooseDrive(@PathVariable String id,
            @PathVariable String requestId,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        Drive drive = mDriveService.chooseDrive(requestId, id);
        if(!drive.getRide().getPassenger().equals(currentUser)) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",
                    currentUser.getEmail(), id));
        }
        return ResponseEntity.ok(drive);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            value = "/{requestId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Drive>> getAllDrives(@PathVariable String requestId,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        List<Drive> drives = mDriveService.getAllDrivesByRideRequest(requestId);
        if(!drives.isEmpty() && !drives.get(0).getRide().getId().equals(requestId)
                && !currentUser.getRole().equals("administrator")) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",
                    currentUser.getEmail(), requestId));
        }
        return ResponseEntity.ok(drives);
    }
}
