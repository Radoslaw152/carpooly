package bg.fmi.spring.course.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import javax.validation.constraints.NotNull;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.interfaces.services.AccountService;

@RestController("/api/accounts")
public class UsersController {
    private AccountService accountService;

    public UsersController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Account>> getAccounts(HttpServletRequest request) {
        List<Account> accounts = accountService.getAccounts();
        accounts.forEach(account -> account.setPasswordHash(null));
        return ResponseEntity.ok(accounts);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> addAccount(@NotNull @Valid @RequestBody Account account) {
        Account createdAccount = accountService.addAccount(account);
        createdAccount.setPasswordHash(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @Valid @RequestBody Account account) {
        Account updatedAccount = accountService.updateAccount(id, account);
        updatedAccount.setPasswordHash(null);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedAccount);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            value = "{id}")
    public ResponseEntity deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
