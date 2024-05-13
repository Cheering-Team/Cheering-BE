package com.cheering._core.errors;

public class ExpiredRefreshTokenException extends BaseException {
    public ExpiredRefreshTokenException(ExceptionMessage status) {
        super(status);
    }
}
