package bg.fmi.spring.course.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.interfaces.services.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class UsersController {
    private AccountService accountService;

    public UsersController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAccounts(HttpServletRequest request) {
        List<Account> accounts = accountService.getAccounts();
        return ResponseEntity.ok(accounts);
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> getAccountById(@PathVariable Long id, Authentication authentication) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/email/{email}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("(#email == authentication.principal.getEmail()) or hasRole('ADMIN')")
    public ResponseEntity<Account> getAccountByEmail(@PathVariable String email) {
        //TODO add exception
        Account account = accountService.getAccountByEmail(email).orElseThrow(() -> new RuntimeException("There is no account with such email."));
        return ResponseEntity.ok(account);
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> addAccount(@Valid @RequestBody Account account) {
        Account result = accountService.addAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @RequestMapping(method = RequestMethod.POST,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("(#id == authentication.principal.getId()) or hasRole('ADMIN') or"
            + " (hasRole('MODERATOR') and #account.getRole().toString() != 'ADMIN')")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @Valid @RequestBody Account account) {
        Account updatedAccount = accountService.updateAccount(id, account);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedAccount);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            value = "{id}")
    @PreAuthorize("(#id == authentication.principal.getId()) or hasRole('ADMIN') or"
     + " (hasRole('MODERATOR') and #account.getRole().toString() != 'ADMIN')")
    public ResponseEntity deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
