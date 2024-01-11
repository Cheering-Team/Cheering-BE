package com.cheering.global.exception.user;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class NotFoundUserException extends BaseException {

    public NotFoundUserException(ExceptionMessage status) {
        super(status);
    }
}
