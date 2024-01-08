package com.cheering.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessMessage {

    SIGN_UP_SUCCESS(HttpStatus.CREATED, "signup success", HttpStatus.CREATED),
    VALIDATE_EMAIL_SUCCESS(HttpStatus.OK, "not duplicated", HttpStatus.OK);


    private final HttpStatus code;
    private final String message;
    private final HttpStatus httpStatus;
}
