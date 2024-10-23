package com.cheering._core.errors;

import com.cheering._core.util.ApiUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public int getCode() {
        return exceptionCode.getCode();
    }

    public ApiUtils.ApiResult<?> body() {
        return ApiUtils.error(exceptionCode.getMessage(), exceptionCode.getCode());
    }
}
