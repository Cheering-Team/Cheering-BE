package com.cheering.global.constant;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    //User
    INVALID_EMAIL_FORMAT(BAD_REQUEST, "invalid email"),
    DUPLICATED_EMAIL(BAD_REQUEST, "duplicated"),
    FAIL_SIGN_UP(BAD_REQUEST, "fail signup");

    private final HttpStatus httpStatus;
    private final String message;
}
