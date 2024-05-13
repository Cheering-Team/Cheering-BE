package com.cheering._core.errors;

import com.cheering._core.errors.BaseException;
import com.cheering._core.errors.ExceptionMessage;

public class NotFoundTeamException extends BaseException {
    public NotFoundTeamException(ExceptionMessage status) {
        super(status);
    }
}
