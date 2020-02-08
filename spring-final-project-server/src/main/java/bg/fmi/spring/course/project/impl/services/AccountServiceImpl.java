package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.constants.Role;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.interfaces.repositories.AccountRepository;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Value("${first.name.admin:admin}")
    private String firstNameAdmin;

    @Value("${surname.admin:admin}")
    private String surnameAdmin;

    @Value("${role.admin:ADMIN}")
    private Role roleAdmin;

    @Value("${email.admin:admin}")
    private String emailAdmin;

    @Value("${password.admin:admin}")
    private String passwordAdmin;

    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void setUp() {
        Optional<Account> optional = getAccountByEmail(emailAdmin);
        if (!optional.isPresent()) {
            passwordAdmin = passwordEncoder.encode(passwordAdmin);
            Account admin =
                    Account.builder()
                            .email(emailAdmin)
                            .firstName(firstNameAdmin)
                            .surname(surnameAdmin)
                            .role(roleAdmin)
                            .passwordHash(passwordAdmin)
                            .build();
            accountRepository.save(admin);
        }
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        log.debug("Searching account by Email - {}", email);
        return accountRepository.findAll().stream()
                .filter(account -> account.getEmail().equals(email))
                .findAny();
    }

    @Override
    //    @PreAuthorize("(#id == authentication.principal.id) or hasRole('ADMIN')")
    public Account getAccountById(Long id) {
        log.debug("Searching account by id - {}", id);
        // @TODO ADD EXCEPTION
        return accountRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        String.format("There is no account with ID=%s", id)));
    }

    @Override
    public Account addAccount(Account account) {
        log.debug("Adding a new account with email - {}", account.getEmail());
        Optional<Account> optional = getAccountByEmail(account.getEmail());
        if (optional.isPresent()) {
            log.error("Account with email {} does exist!", account.getEmail());
            throw new RuntimeException(
                    String.format("Account with email=%s exist!", account.getEmail()));
        }
        String passwordHash = passwordEncoder.encode(account.getPasswordHash());
        account.setPasswordHash(passwordHash);
        return accountRepository.save(account);
    }

    @Override
    //    @PreAuthorize("(#id == authentication.principal.id) or hasRole('ADMIN')")
    public Account updateAccount(Long id, Account account) {
        log.debug("Updating a new account with email - {}", account.getEmail());
        if (!account.getId().equals(id)) {
            throw new RuntimeException(
                    String.format(
                            "Account with EMAIL=%s does not match with ID=%s",
                            account.getEmail(), id));
        }
        return accountRepository.save(account);
    }

    @Override
    //    @PreAuthorize("(#id == authentication.principal.id) or hasRole('ADMIN')")
    public Account deleteAccount(Long id) {
        log.debug("Deleting a new account with id - {}", id);
        Account account = getAccountById(id);
        accountRepository.delete(account);
        return account;
    }
}
