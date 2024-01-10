package com.cheering.global.exception.user;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.constant.ExceptionMessage;

public class DuplicatedEmailException extends BaseException {

    public DuplicatedEmailException(ExceptionMessage status) {
        super(status);
    }
}
