package com.cheering._core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class ApiUtils {

    public static <T> ApiResult<T> success(HttpStatus httpStatus,String message,  T result) {
        return new ApiResult<>(httpStatus.value(), message, result);
    }

    public static <T> ApiResult<T> error(String message, HttpStatus httpStatus) {
        return new ApiResult<>(httpStatus.value(), message, null);
    }

    @Getter @Setter @AllArgsConstructor
    public static class ApiResult<T> {
        private final int code;
        private final String message;
        private final T result;
    }
}
