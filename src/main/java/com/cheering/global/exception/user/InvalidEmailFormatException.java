package com.cheering.global.exception.user;

import com.cheering.global.constant.ExceptionMessage;
import com.cheering.global.exception.common.BaseException;

public class InvalidEmailFormatException extends BaseException {

    public InvalidEmailFormatException() {
        super(ExceptionMessage.INVALID_EMAIL_FORMAT);
    }
}
