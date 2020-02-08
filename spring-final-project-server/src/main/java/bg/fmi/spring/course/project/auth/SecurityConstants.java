package bg.fmi.spring.course.project.auth;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityConstants {
    public static final String JWT_SECRET =
            "n2r5u8x/A%D*G-KaPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq4t7w!z%C*F-J@NcRf";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "carpooly-api";
    public static final String TOKEN_AUDIENCE = "carpooly-user";
    public static final String AUTH_LOGIN_URL = "/api/login";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BASIC_AUTH_TOKEN_PREFIX = "Basic ";
    public static final String ROLE_KEY = "role";
}
