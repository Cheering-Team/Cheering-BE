package com.cheering.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessMessage {
    //user
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "signup success"),
    VALIDATE_EMAIL_SUCCESS(HttpStatus.OK, "not duplicated"),
    SIGN_IN_SUCCESS(HttpStatus.OK, "login success"),
    SIGN_OUT_SUCCESS(HttpStatus.OK, "logout success"),

    //community
    SEARCH_COMMUNITY_SUCCESS(HttpStatus.OK, "search complete");


    private final HttpStatus httpStatus;
    private final String message;

}
