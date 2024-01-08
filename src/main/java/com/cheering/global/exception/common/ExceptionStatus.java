package com.cheering.global.exception.common;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionStatus {
    //User
    INVALID_EMAIL_FORMAT(400, "invalid email", BAD_REQUEST),
    DUPLICATED_EMAIL(400, "duplicated", BAD_REQUEST);
    

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

}
