package com.cheering.global.dto;

import com.cheering.auth.constant.JwtConstant;
import com.cheering.auth.jwt.JWToken;
import com.cheering.global.constant.ExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ResponseGenerator {

    static <T> ResponseEntity<ResponseBodyDto<?>> signUpSuccess(JWToken token, String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Access-Token", JwtConstant.GRANT_TYPE + " " + token.accessToken())
                .header("Refresh-Token", JwtConstant.GRANT_TYPE + " " + token.refreshToken())
                .body(ResponseBodyDto.of(message, data));
    }

    static <T> ResponseEntity<ResponseBodyDto<?>> success(String accessToken, String message, T data) {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Access-Token", JwtConstant.GRANT_TYPE + " " + accessToken)
                .body(ResponseBodyDto.of(message, data));
    }

    static <T> ResponseEntity<ResponseBodyDto<?>> success(String message, T data) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseBodyDto.of(message, data));
    }

    static <T> ResponseEntity<ResponseBodyDto<?>> fail(ExceptionMessage exceptionStatus, T data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseBodyDto.of(
                        exceptionStatus.getMessage(),
                        data));
    }
}
