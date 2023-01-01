package com.michael.blog.constants;

public class SecurityConstant {
   public static final long EXPIRATION_TIME_FOR_ACCESS_TOKEN = 432_000_000; // 5 days expressed in milliseconds
   // public static final long EXPIRATION_TIME_FOR_ACCESS_TOKEN = 180_000; // 3 min
    public static final long EXPIRATION_TIME_FOR_REFRESH_TOKEN = 3_600_000; //

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_ACCESS_TOKEN_HEADER = "Jwt-Access-Token";
    public static final String JWT_REFRESH_TOKEN_HEADER = "Jwt-Refresh-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String MICHAEL_ROYF_LLC = "Michael Royf, LLC";
    public static final String MICHAEL_ROYF_ADMINISTRATION = "User Management Blog";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token was expired. Please make a new signin request";
    public static final String VERIFICATION_TOKEN_NOT_FOUND = "Verification Token not found";
    public static final String VERIFICATION_TOKEN_EXPIRED = "Verification token expired";

    public static final String[] PUBLIC_URLS = {"/api/v1/registration/confirm/**",
            "/api/v1/login",
            "/api/v1/register",
            "/api/v1/refresh-token/**",
            "/api/v1/forgot-password/**"};

    // public static final String[] PUBLIC_URLS = { "**" };
}
