package com.cheering.global.exception.user;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.common.ExceptionStatus;

public class InvalidEmailFormatException extends BaseException {

    public InvalidEmailFormatException() {
        super(ExceptionStatus.INVALID_EMAIL_FORMAT);
    }
}
