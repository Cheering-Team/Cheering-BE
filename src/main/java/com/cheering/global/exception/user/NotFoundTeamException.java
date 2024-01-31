package com.cheering.global.exception.user;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class NotFoundTeamException extends BaseException {
    public NotFoundTeamException(ExceptionMessage status) {
        super(status);
    }
}
