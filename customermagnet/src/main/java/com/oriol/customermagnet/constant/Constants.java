package com.oriol.customermagnet.constant;

import com.oriol.customermagnet.provider.TokenProvider;

public class Constants {

    public final static String[] PUBLIC_URLS = { "/api/v1/user/login/**", "/api/v1/user/register/**", "/api/v1/user/image/**", //"/api/v1/user/**", "/api/v1/user/update/password"
            "/api/v1/user/verify/code/**", "/api/v1/user/reset/password/**", "/api/v1/user/resetpassword/**", "/api/v1/user/verify/password/**", "/api/v1/user/verify/account/**",
            "/api/v1/user/error", "/api/v1/user/verify/password/**", "/api/v1/user/verify/account/**", "/api/v1/user/refresh/token/**" };

    public static final String TOKEN_PREFIX = "Bearer ";
    public final static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static String[] HEADERS = { "ID", "NAME", "EMAIL", "TYPE", "STATUS", "ADDRESS", "PHONE", "CREATED_AT" };

    public static final String[] PUBLIC_ROUTES = {"/api/v1/user/login","/api/v1/user/register", "/api/v1/user", "/api/v1/user/verify/code/" //"/api/v1/user/update/password",
            , "/api/v1/user/refresh/token","/api/v1/user/image", "/api/v1/user/reset/password/**","/api/v1/user/resetpassword/**",  "/api/v1/user/verify/password/**", "/api/v1/user/verify/account/**",};
    public static final String EMAIL_KEY = "email";
    public static final String TOKEN_KEY = "token";

}
