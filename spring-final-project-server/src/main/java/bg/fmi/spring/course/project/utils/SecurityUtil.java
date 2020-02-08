package bg.fmi.spring.course.project.utils;

import bg.fmi.spring.course.project.auth.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.util.Base64;

public class SecurityUtil {
    public static Jws<Claims> decryptJwsToken(String token) {
        byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

        Jws<Claims> parsedToken =
                Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token.replace(SecurityConstants.BEARER_PREFIX, ""));

        return parsedToken;
    }

    public static String decryptBasicAuthToken(String basicAuthToken) {
        return new String(
                Base64.getDecoder()
                        .decode(
                                basicAuthToken.replace(
                                        SecurityConstants.BASIC_AUTH_TOKEN_PREFIX, "")));
    }
}
