package com.cheering.global.constant;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    //User
    INVALID_EMAIL_FORMAT(BAD_REQUEST, "invalid email"),
    DUPLICATED_EMAIL(CONFLICT, "duplicated"),
    FAIL_SIGN_UP(BAD_REQUEST, "fail signup"),
    MISMATCH_PASSWORD(CONFLICT, "mismatch password");

    private final HttpStatus httpStatus;
    private final String message;
}
