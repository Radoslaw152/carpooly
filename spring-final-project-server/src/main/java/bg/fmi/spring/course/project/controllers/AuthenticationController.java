package bg.fmi.spring.course.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import bg.fmi.spring.course.project.constants.Role;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.interfaces.services.AccountService;

@RestController
@RequestMapping(value = "/api/register")
public class AuthenticationController {
    private AccountService accountService;

    @Autowired
    public AuthenticationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> register(@Valid @RequestBody Account account, HttpServletRequest request) {
        if(!request.isUserInRole("ADMIN") || account.getRole() != null) {
            account.setRole(Role.PASSENGER);
        }
        Account result = accountService.addAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
