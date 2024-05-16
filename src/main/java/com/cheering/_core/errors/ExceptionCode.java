package com.cheering._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    // User
    CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
