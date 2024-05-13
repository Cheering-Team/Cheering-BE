package com.cheering._core.errors;

import com.cheering._core.errors.BaseException;
import com.cheering._core.errors.ExceptionMessage;

public class MisMatchPasswordException extends BaseException {
    public MisMatchPasswordException(ExceptionMessage status) {
        super(status);
    }
}
