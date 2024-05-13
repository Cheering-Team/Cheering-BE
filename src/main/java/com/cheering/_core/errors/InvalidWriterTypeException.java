package com.cheering._core.errors;

import com.cheering._core.errors.BaseException;
import com.cheering._core.errors.ExceptionMessage;

public class InvalidWriterTypeException extends BaseException {
    public InvalidWriterTypeException(ExceptionMessage status) {
        super(status);
    }
}
