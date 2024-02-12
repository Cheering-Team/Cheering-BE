package com.cheering.global.exception.auth;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class ExpiredRefreshTokenException extends BaseException {
    public ExpiredRefreshTokenException(ExceptionMessage status) {
        super(status);
    }
}
