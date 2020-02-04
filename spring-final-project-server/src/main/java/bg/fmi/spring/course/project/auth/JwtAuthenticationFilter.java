package bg.fmi.spring.course.project.auth;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bg.fmi.spring.course.project.constants.Constants;
import bg.fmi.spring.course.project.constants.Role;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import bg.fmi.spring.course.project.utils.JsonUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private AccountService accountService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AccountService accountService) {
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
        setFilterProcessesUrl(Constants.AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws
            AuthenticationException {
        log.info("Attempting auth {}", request.getRequestURI());
        String email;
        String password;
        Role role;

        try {
            String body = request.getReader().lines().collect(Collectors.joining("\n"));
            JsonNode node = JsonUtil.toJsonNode(body);
            if (node == null) {
                return null;
            }

            email = node.get(Constants.EMAIL_KEY).asText();
            password = node.get(Constants.PASSWORD_KEY).asText();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email,
                        password,
                        new ArrayList<>());
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain, Authentication authentication) {
        UsernamePasswordAuthenticationToken user = ((UsernamePasswordAuthenticationToken) authentication);

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        byte[] signingKey = Constants.JWT_SECRET.getBytes();

        String token = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", Constants.TOKEN_TYPE)
                .setIssuer(Constants.TOKEN_ISSUER)
                .setAudience(Constants.TOKEN_AUDIENCE)
                .setSubject(JsonUtil.toStringObject(user.getPrincipal()))
                .setExpiration(new Date(System.currentTimeMillis() + 864000000))
                .claim("role", roles)
                .compact();

        response.addHeader(Constants.TOKEN_HEADER,
                Constants.JWT_TOKEN_PREFIX + token);
    }
}
