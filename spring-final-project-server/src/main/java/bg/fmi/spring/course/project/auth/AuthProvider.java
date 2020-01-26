package bg.fmi.spring.course.project.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
@Component
public class AuthProvider implements AuthenticationProvider {
    private PasswordEncoder passwordEncoder;
    private AccountService accountService;

    @Autowired
    public AuthProvider(PasswordEncoder passwordEncoder, AccountService accountService) {
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.getAccountByEmail(email);
        if(!optionalAccount.isPresent()) {
            return null;
        }

        Account account = optionalAccount.get();
        String credentials = String.valueOf(authentication.getCredentials());
        if(!passwordEncoder.matches(credentials, account.getPasswordHash())) {
            return null;
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(authentication.getName(),
                authentication.getCredentials(),
                authentication.getAuthorities());
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
