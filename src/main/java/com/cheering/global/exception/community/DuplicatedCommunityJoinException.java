package com.cheering.global.exception.community;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class DuplicatedCommunityJoinException extends BaseException {
    public DuplicatedCommunityJoinException(ExceptionMessage status) {
        super(status);
    }
}
