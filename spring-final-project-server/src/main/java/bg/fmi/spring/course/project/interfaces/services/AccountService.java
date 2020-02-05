package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.dao.Account;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> getAccounts();

    Optional<Account> getAccountByEmail(String email);

    Account getAccountById(Long id);

    Account addAccount(Account account);

    Account updateAccount(Long id, Account account);

    Account deleteAccount(Long id);
}
