package bg.fmi.piss.course.config;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;

public class TokenService extends TokenBasedRememberMeServices {

    public final static String TOKEN_STRING = "auth";

    public TokenService(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected String extractRememberMeCookie(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_STRING);
        if ((token == null) || (token.length() == 0)) {
            return "";
        }
        return token;
    }
}