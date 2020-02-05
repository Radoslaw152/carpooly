package bg.fmi.spring.course.project.controllers;

import bg.fmi.spring.course.project.auth.types.IsAdmin;
import bg.fmi.spring.course.project.auth.types.IsSelfOrAdmin;
import bg.fmi.spring.course.project.auth.types.IsSelfOrAdminOrModeratorChangingNonAdmin;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class UsersController {
    private AccountService accountService;

    public UsersController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @IsAdmin
    public ResponseEntity<List<Account>> getAccounts(HttpServletRequest request) {
        List<Account> accounts = accountService.getAccounts();
        return ResponseEntity.ok(accounts);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> getAccountById(
            @PathVariable Long id, Authentication authentication) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/email/{email}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @IsSelfOrAdmin
    public ResponseEntity<Account> getAccountByEmail(@PathVariable String email) {
        // TODO add exception
        Account account =
                accountService
                        .getAccountByEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException("There is no account with such email."));
        return ResponseEntity.ok(account);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @IsAdmin
    public ResponseEntity<Account> addAccount(@Valid @RequestBody Account account) {
        Account result = accountService.addAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> updateAccount(
            @PathVariable Long id, @Valid @RequestBody Account account) {
        Account updatedAccount = accountService.updateAccount(id, account);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedAccount);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "{id}")
    @IsSelfOrAdminOrModeratorChangingNonAdmin
    public ResponseEntity deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
