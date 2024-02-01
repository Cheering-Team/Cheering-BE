package com.cheering.global.exception.community;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class NotFoundUserCommunityInfoException extends BaseException {
    public NotFoundUserCommunityInfoException(ExceptionMessage status) {
        super(status);
    }
}
