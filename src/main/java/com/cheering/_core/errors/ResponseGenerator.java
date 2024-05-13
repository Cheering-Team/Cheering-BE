package com.cheering._core.errors;

import com.cheering._core.security.JWToken;
import com.cheering._core.security.JwtConstant;
import org.springframework.http.ResponseEntity;

public interface ResponseGenerator {

    static <T> ResponseEntity<ResponseBodyDto<?>> signSuccess(JWToken token, SuccessMessage status, T data) {
        return ResponseEntity.status(status.getHttpStatus())
                .header("Access-Token", JwtConstant.GRANT_TYPE + " " + token.accessToken())
                .header("Refresh-Token", JwtConstant.GRANT_TYPE + " " + token.refreshToken())
                .body(ResponseBodyDto.of(status.getMessage(), data));
    }

    static <T> ResponseEntity<ResponseBodyDto<?>> success(String accessToken, SuccessMessage status, T data) {
        return ResponseEntity.status(status.getHttpStatus())
                .header("Access-Token", JwtConstant.GRANT_TYPE + " " + accessToken)
                .body(ResponseBodyDto.of(status.getMessage(), data));
    }

    static <T> ResponseEntity<ResponseBodyDto<?>> success(SuccessMessage status, T data) {
        return ResponseEntity.status(status.getHttpStatus())
                .body(ResponseBodyDto.of(status.getMessage(), data));
    }

    static <T> ResponseEntity<ResponseBodyDto<?>> fail(ExceptionMessage status, T data) {
        return ResponseEntity.status(status.getHttpStatus())
                .body(ResponseBodyDto.of(
                        status.getMessage(),
                        data));
    }
}
