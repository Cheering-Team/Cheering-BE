package com.cheering.global.exception.comment;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class NotFoundCommentException extends BaseException {

    public NotFoundCommentException(ExceptionMessage status) {
        super(status);
    }
}
