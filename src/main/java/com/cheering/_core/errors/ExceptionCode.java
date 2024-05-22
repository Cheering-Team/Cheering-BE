package com.cheering._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),

    // Token
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
