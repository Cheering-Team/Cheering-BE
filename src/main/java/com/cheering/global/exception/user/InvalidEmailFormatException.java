package com.cheering.global.exception.user;

import com.cheering.global.constant.ExceptionStatus;
import com.cheering.global.exception.common.BaseException;

public class InvalidEmailFormatException extends BaseException {

    public InvalidEmailFormatException() {
        super(ExceptionStatus.INVALID_EMAIL_FORMAT);
    }
}
