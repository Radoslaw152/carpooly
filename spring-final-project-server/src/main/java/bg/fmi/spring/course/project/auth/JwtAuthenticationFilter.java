package bg.fmi.spring.course.project.auth;

import bg.fmi.spring.course.project.utils.JsonUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Gson gson = new Gson();
    private static final Duration TOKEN_TTL = Duration.of(3, ChronoUnit.HOURS);
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) {
        log.info("Attempting auth {}", request.getRequestURI());
        Credentials credentials = gson.fromJson(request.getReader(), Credentials.class);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        credentials.getEmail(), credentials.getPassword(), new ArrayList<>());
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            Authentication authentication) {
        UsernamePasswordAuthenticationToken user =
                ((UsernamePasswordAuthenticationToken) authentication);

        List<String> roles =
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
        byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

        String token =
                Jwts.builder()
                        .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                        .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
                        .setIssuer(SecurityConstants.TOKEN_ISSUER)
                        .setAudience(SecurityConstants.TOKEN_AUDIENCE)
                        .setSubject(JsonUtil.toStringObject(user.getPrincipal()))
                        .setExpiration(new Date(System.currentTimeMillis() + TOKEN_TTL.toMillis()))
                        .claim("role", roles)
                        .compact();

        response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.BEARER_PREFIX + token);
        Cookie cookie = new Cookie(SecurityConstants.ACCESS_TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(((int) TOKEN_TTL.getSeconds()));

        response.addCookie(cookie);
    }
}
