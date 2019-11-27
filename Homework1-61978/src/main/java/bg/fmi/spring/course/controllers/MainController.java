package bg.fmi.spring.course.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bg.fmi.spring.course.models.User;
import bg.fmi.spring.course.services.UserService;

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

}
