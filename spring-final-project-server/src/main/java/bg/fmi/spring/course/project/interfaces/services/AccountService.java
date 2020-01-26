package bg.fmi.spring.course.project.interfaces.services;

import java.util.List;
import java.util.Optional;

import bg.fmi.spring.course.project.dao.Account;

public interface AccountService {
    List<Account> getAccounts();
    Optional<Account> getAccountByEmail(String email);
    Account getAccountById(Long id);
    Account addAccount(Account account);
    Account updateAccount(Long id, Account account);
    Account deleteAccount(Long id);
}
