package bg.fmi.spring.course.project.constants;

public class Constants {
    public static final String AUTH_LOGIN_URL = "/api/login";
    public static final String JWT_SECRET = "n2r5u8x/A%D*G-KaPdSgVkYp3s6v9y$B&E(H+MbQeThWmZq4t7w!z%C*F-J@NcRf";

    public static final String TOKEN_HEADER = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "hospital-assistant-api";
    public static final String TOKEN_AUDIENCE = "hospital-assistant-user";

    public static final String BASIC_AUTH_TOKEN_PREFIX = "Basic ";
    public static final String EMAIL_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    public static final String ROLE_KEY = "role";
}
