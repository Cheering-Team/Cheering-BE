package com.cheering.global.exception.user;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class InvalidEmailFormatException extends BaseException {

    public InvalidEmailFormatException(ExceptionMessage status) {
        super(status);
    }
}
