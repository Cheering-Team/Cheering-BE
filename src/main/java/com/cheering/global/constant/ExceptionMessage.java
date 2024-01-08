package com.cheering.global.constant;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    //User
    INVALID_EMAIL_FORMAT(BAD_REQUEST, "invalid email", BAD_REQUEST),
    DUPLICATED_EMAIL(BAD_REQUEST, "duplicated", BAD_REQUEST);


    private final HttpStatus code;
    private final String message;
    private final HttpStatus httpStatus;

}
