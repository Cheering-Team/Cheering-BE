package com.cheering._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    // User
    CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "g");

    private final HttpStatus httpStatus;
    private final String message;
}
