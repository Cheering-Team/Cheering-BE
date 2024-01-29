package com.cheering.auth.jwt;

public abstract class JwtConstant {
    // * 60 * 60 * 24
    public static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60;
    public static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24;
    public static final String GRANT_TYPE = "Bearer";
}
