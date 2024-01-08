package com.cheering.global.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ResponseGenerator {

    static <T> ResponseEntity<ResponseBodyDto<?>> success(Integer code, String accessToken, String message, T data) {
        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken", accessToken)
                .body(ResponseBodyDto.of(code, message, data));
    }
}
