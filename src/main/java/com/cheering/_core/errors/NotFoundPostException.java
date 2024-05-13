package com.cheering._core.errors;

import com.cheering._core.errors.BaseException;
import com.cheering._core.errors.ExceptionMessage;

public class NotFoundPostException extends BaseException {
    public NotFoundPostException(ExceptionMessage status) {
        super(status);
    }
}
