package com.cheering.global.exception.common;

import com.cheering.global.exception.constant.ExceptionMessage;

public class BaseException extends RuntimeException {
    private final ExceptionMessage status;

    public BaseException(ExceptionMessage status) {
        super(status.getMessage());
        this.status = status;
    }

}
