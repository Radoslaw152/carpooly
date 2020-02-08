package bg.fmi.spring.course.project.configuration;

import bg.fmi.spring.course.project.auth.AuthProvider;
import bg.fmi.spring.course.project.auth.JwtAuthenticationFilter;
import bg.fmi.spring.course.project.auth.JwtAuthorizationFilter;
import bg.fmi.spring.course.project.auth.LogoutHandler;
import bg.fmi.spring.course.project.auth.SecurityConstants;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class ApplicationConfig extends WebSecurityConfigurerAdapter {
    @Autowired private AuthProvider authProvider;
    @Autowired private AuthenticationEventPublisher authEventPublisher;
    @Autowired private AccountService accountService;
    @Autowired private LogoutHandler logoutHandler;
    private static final String[] PUBLIC_APIS =
            new String[] {"/", "/api/login", "/api/logout", "/api/register"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(PUBLIC_APIS)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .deleteCookies(SecurityConstants.ACCESS_TOKEN, "JSESSIONID")
                .invalidateHttpSession(true)
                .logoutSuccessHandler(logoutHandler)
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        new AuthenticationEntryPoint() {
                            @Override
                            public void commence(
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    AuthenticationException authException)
                                    throws IOException, ServletException {
                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            }
                        });
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
        auth.authenticationEventPublisher(authEventPublisher);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
