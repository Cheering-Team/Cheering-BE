package com.cheering.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessMessage {

    SIGN_UP_SUCCESS(HttpStatus.CREATED, "signup success"),
    VALIDATE_EMAIL_SUCCESS(HttpStatus.OK, "not duplicated"),
    SIGN_IN_SUCCESS(HttpStatus.OK, "login success");

    private final HttpStatus httpStatus;
    private final String message;

}
