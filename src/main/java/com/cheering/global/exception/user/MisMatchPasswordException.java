package com.cheering.global.exception.user;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class MisMatchPasswordException extends BaseException {
    public MisMatchPasswordException(ExceptionMessage status) {
        super(status);
    }
}
