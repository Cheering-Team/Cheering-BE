package com.cheering._core.errors;

import com.cheering._core.util.ApiUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{
    private ExceptionCode exceptionCode;
    private String message;

    public CustomException(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
        this.message = exceptionCode.getMessage();
    }

    public ApiUtils.ApiResult<?> body() {
        return ApiUtils.error(message, exceptionCode.getHttpStatus());
    }

    public HttpStatus status() {
        return exceptionCode.getHttpStatus();
    }
}
