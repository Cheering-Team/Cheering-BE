package com.cheering.global.exception.community;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class NotFoundCommunityException extends BaseException {

    public NotFoundCommunityException(ExceptionMessage status) {
        super(status);
    }
}
