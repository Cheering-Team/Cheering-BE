package com.cheering._core.errors;

import com.cheering._core.errors.BaseException;
import com.cheering._core.errors.ExceptionMessage;

public class NotFoundUserException extends BaseException {

    public NotFoundUserException(ExceptionMessage status) {
        super(status);
    }
}
