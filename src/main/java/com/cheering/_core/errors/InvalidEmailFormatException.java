package com.cheering._core.errors;

import com.cheering._core.errors.BaseException;
import com.cheering._core.errors.ExceptionMessage;

public class InvalidEmailFormatException extends BaseException {

    public InvalidEmailFormatException(ExceptionMessage status) {
        super(status);
    }
}
