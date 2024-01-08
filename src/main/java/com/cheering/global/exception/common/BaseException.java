package com.cheering.global.exception.common;

import com.cheering.global.constant.ExceptionStatus;

public class BaseException extends RuntimeException {
    private final ExceptionStatus status;

    public BaseException(ExceptionStatus status) {
        super(status.getMessage());
        this.status = status;
    }

}
