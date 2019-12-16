package bg.fmi.piss.course.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bg.fmi.piss.course.exceptions.WrongAccess;
import bg.fmi.piss.course.models.User;
import bg.fmi.piss.course.services.UserService;

@RestController
@RequestMapping("/api")
public class MainController {

    private UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            value = "/login",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User logIn(UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        return currentUser;
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> register(@RequestBody User user) {
        if (user.getRole().equals("administrator")) {
            throw new WrongAccess(String.format("You cannot register as administrator"));
        }
        user.setRole("passenger");
        User result = userService.addUser(user);
        return ResponseEntity.ok(user);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            value = "/become-driver",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> becomeDriver(UsernamePasswordAuthenticationToken token) {
        User currentUser = (User) token.getPrincipal();
        currentUser.setRole("driver");
        userService.updateUser(currentUser);
        return ResponseEntity.ok(currentUser);
    }
}
