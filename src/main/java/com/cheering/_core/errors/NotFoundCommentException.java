package com.cheering._core.errors;

public class NotFoundCommentException extends BaseException {

    public NotFoundCommentException(ExceptionMessage status) {
        super(status);
    }
}
