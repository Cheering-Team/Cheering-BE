package com.cheering.global.dto;

import com.cheering.global.exception.common.ExceptionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ResponseGenerator {

    static <T> ResponseEntity<ResponseBodyDto<?>> success(Integer code, String accessToken, String message, T data) {
        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken", accessToken)
                .body(ResponseBodyDto.of(code, message, data));
    }

    static <T> ResponseEntity<ResponseBodyDto<?>> success(Integer code, String message, T data) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseBodyDto.of(code, message, data));
    }

    static <T> ResponseEntity<ResponseBodyDto<?>> fail(ExceptionStatus exceptionStatus, T data) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseBodyDto.of(
                        exceptionStatus.getCode(),
                        exceptionStatus.getMessage(),
                        data));
    }
}
