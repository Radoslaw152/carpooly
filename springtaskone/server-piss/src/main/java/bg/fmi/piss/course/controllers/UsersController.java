package bg.fmi.piss.course.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import bg.fmi.piss.course.exceptions.InvalidEntityIdException;
import bg.fmi.piss.course.exceptions.WrongAccess;
import bg.fmi.piss.course.models.User;
import bg.fmi.piss.course.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.addUser(user));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> getUser(@PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (!currentUser.getId().equals(id) && currentUser.getRole().equals("blogger")) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",
                    currentUser.getEmail(), id));
        }
        return ResponseEntity.ok(userService.getUser(id));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (!currentUser.getId().equals(id) && currentUser.getRole().equals("blogger")) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",
                    currentUser.getEmail(), id));
        }
        if (!user.getId().equals(id)) {
            throw new InvalidEntityIdException(
                    String.format("Article ID=%s from path is different from Entity ID=%s", id,
                            user.getId()));
        }
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> deleteUser(@PathVariable String id,
            UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        if (!currentUser.getId().equals(id) && currentUser.getRole().equals("blogger")) {
            throw new WrongAccess(String.format("User with email=%s cannot read user with ID=%s",
                    currentUser.getEmail(), id));
        }
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            value = "/{id}/ratings",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> addRating(@PathVariable String id, @RequestParam int rating) {
        if(rating < 2 || rating > 6) {
            throw new WrongAccess(String.format("You cannot give that rating"));
        }
        User user = userService.getUser(id);
        user.addRating(rating);
        return ResponseEntity.ok(user);
    }
}
