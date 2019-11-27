package bg.fmi.spring.course.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import bg.fmi.spring.course.impl.UserServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityUser extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServiceImpl mUserService;
    @Autowired
    private TokenService tokenSvc;
    @Autowired
    private RememberMeAuthenticationProvider rememberMeProvider;
    @Autowired
    private PasswordEncoder mPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/users").hasRole("administrator")
                .antMatchers("/api/**").hasAnyRole("administrator","blogger")
                .and()
                .httpBasic()
                .and()
                .formLogin()
//                .successForwardUrl("/api/login")
                .permitAll()
//        .and()
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .clearAuthentication(true)
                .deleteCookies(TokenService.TOKEN_STRING)
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .rememberMe().rememberMeServices(tokenSvc);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
            throws Exception {
        authenticationManagerBuilder
                .userDetailsService(mUserService)
                .passwordEncoder(mPasswordEncoder);
        authenticationManagerBuilder.authenticationProvider(rememberMeProvider);
    }

    @Bean
    public TokenService tokenService() {
        TokenService tokenService = new TokenService(TokenService.TOKEN_STRING, mUserService);
        tokenService.setAlwaysRemember(true);
        tokenService.setCookieName(TokenService.TOKEN_STRING);
        return tokenService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    @Bean
    public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception {
        return new RememberMeAuthenticationFilter(authenticationManager(), tokenService());
    }

    @Bean
    public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        return new RememberMeAuthenticationProvider(TokenService.TOKEN_STRING);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
