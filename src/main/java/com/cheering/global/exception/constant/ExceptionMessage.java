package com.cheering.global.exception.constant;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    //user
    INVALID_EMAIL_FORMAT(BAD_REQUEST, "invalid email"),
    DUPLICATED_EMAIL(CONFLICT, "duplicated"),
    FAIL_SIGN_UP(BAD_REQUEST, "fail signup"),
    MISMATCH_PASSWORD(CONFLICT, "mismatch password"),
    FAIL_SIGN_OUT(BAD_REQUEST, "fail signout"),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "expired Refreh-Token"),
    NOT_FOUND_USER(UNAUTHORIZED, "not found user"),
    NOT_FOUND_TEAM(UNAUTHORIZED, "not found team"),
    
    //community
    NOT_FOUND_COMMUNITY(NOT_FOUND, "not found community"),
    DUPLICATED_JOIN_COMMUNITY(BAD_REQUEST, "duplicated join community");

    private final HttpStatus httpStatus;
    private final String message;
}
