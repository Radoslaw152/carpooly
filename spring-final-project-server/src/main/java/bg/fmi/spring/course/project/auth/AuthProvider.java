package bg.fmi.spring.course.project.auth;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider implements AuthenticationProvider {
    private PasswordEncoder passwordEncoder;
    private AccountService accountService;

    @Autowired
    public AuthProvider(AccountService accountService) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.accountService = accountService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.getAccountByEmail(email);
        if (!optionalAccount.isPresent()) {
            return null;
        }

        Account account = optionalAccount.get();
        String credentials = String.valueOf(authentication.getCredentials());
        if (!passwordEncoder.matches(credentials, account.getSecret())) {
            return null;
        }

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        account,
                        authentication.getCredentials(),
                        Collections.singletonList(
                                () -> account.getRole().toString().toUpperCase()));
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
