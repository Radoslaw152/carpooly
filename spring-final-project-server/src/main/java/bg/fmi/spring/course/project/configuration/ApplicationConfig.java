package bg.fmi.spring.course.project.configuration;

import bg.fmi.spring.course.project.auth.AuthProvider;
import bg.fmi.spring.course.project.auth.JwtAuthenticationFilter;
import bg.fmi.spring.course.project.auth.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class ApplicationConfig extends WebSecurityConfigurerAdapter {
    @Autowired private AuthProvider authProvider;
    @Autowired private AuthenticationEventPublisher authEventPublisher;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                // @TODO ADD ANTMACTCHERS
                .antMatchers("/api/register")
                .anonymous()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(new BasicAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
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
