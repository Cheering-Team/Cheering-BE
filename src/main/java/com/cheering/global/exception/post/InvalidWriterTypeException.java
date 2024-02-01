package com.cheering.global.exception.post;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class InvalidWriterTypeException extends BaseException {
    public InvalidWriterTypeException(ExceptionMessage status) {
        super(status);
    }
}
