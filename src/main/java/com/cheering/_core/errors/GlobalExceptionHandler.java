package com.cheering._core.errors;

import com.cheering._core.util.ApiUtils;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice

public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customError(CustomException e) {
        if(e.getCode() == 404) {
            Sentry.captureException(e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiUtils.error(e.getMessage(), e.getCode()));
        }
        if(e.getCode() == 401) {
            Sentry.captureException(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiUtils.error(e.getMessage(), e.getCode()));
        }
        return ResponseEntity.badRequest().body(ApiUtils.error(e.getMessage(), e.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknownServerError(Exception e){
        log.error("서버에서 알 수 없는 오류 발생: {}", e.getMessage(), e);
        Sentry.captureException(e);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.error(e.getMessage(), 500);
        return new ResponseEntity<>(apiResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

