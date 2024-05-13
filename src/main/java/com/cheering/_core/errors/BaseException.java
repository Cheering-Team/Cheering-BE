package com.cheering._core.errors;

public class BaseException extends RuntimeException {
    private final ExceptionMessage status;

    public BaseException(ExceptionMessage status) {
        super(status.getMessage());
        this.status = status;
    }

}
