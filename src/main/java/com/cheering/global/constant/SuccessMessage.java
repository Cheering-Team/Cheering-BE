package com.cheering.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessMessage {

    SIGN_UP_SUCCESS(201, "signup success", HttpStatus.CREATED),
    VALIDATE_EMAIL_SUCCESS(200, "not duplicated", HttpStatus.OK);


    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
