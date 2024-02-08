package com.cheering.global.exception.post;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class NotFoundPostException extends BaseException {
    public NotFoundPostException(ExceptionMessage status) {
        super(status);
    }
}
