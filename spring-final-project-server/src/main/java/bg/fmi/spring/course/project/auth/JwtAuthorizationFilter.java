package bg.fmi.spring.course.project.auth;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.utils.JsonUtil;
import bg.fmi.spring.course.project.utils.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (!extractToken(request).isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        Optional<String> token = extractToken(request);
        if (token.isPresent()) {
            try {
                Jws<Claims> parsedToken = SecurityUtil.decryptJwsToken(token.get());

                String accountRaw = parsedToken.getBody().getSubject();

                List<SimpleGrantedAuthority> authorities =
                        ((List<?>) parsedToken.getBody().get(SecurityConstants.ROLE_KEY))
                                .stream()
                                        .map(
                                                authority ->
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_" + authority))
                                        .collect(Collectors.toList());

                if (!StringUtils.isEmpty(accountRaw)) {
                    Account account = JsonUtil.mapObject(accountRaw, Account.class);
                    return new UsernamePasswordAuthenticationToken(account, null, authorities);
                }
            } catch (ExpiredJwtException exception) {
                log.warn(
                        "Request to parse expired JWT : {} failed : {}",
                        token,
                        exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                log.warn(
                        "Request to parse unsupported JWT : {} failed : {}",
                        token,
                        exception.getMessage());
            } catch (MalformedJwtException exception) {
                log.warn(
                        "Request to parse invalid JWT : {} failed : {}",
                        token,
                        exception.getMessage());
            } catch (SignatureException exception) {
                log.warn(
                        "Request to parse JWT with invalid signature : {} failed : {}",
                        token,
                        exception.getMessage());
            } catch (IllegalArgumentException exception) {
                log.warn(
                        "Request to parse empty or null JWT : {} failed : {}",
                        token,
                        exception.getMessage());
            }
        }

        return null;
    }

    private static Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(
                        extractCookieVal(request, SecurityConstants.ACCESS_TOKEN)
                                .orElse(request.getHeader(SecurityConstants.TOKEN_HEADER)))
                .map(header -> header.replace(SecurityConstants.BEARER_PREFIX, ""));
    }

    private static Optional<String> extractCookieVal(
            HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue);
    }
}
