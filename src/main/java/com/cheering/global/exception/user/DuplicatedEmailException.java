package com.cheering.global.exception.user;

import com.cheering.global.constant.ExceptionMessage;
import com.cheering.global.exception.common.BaseException;

public class DuplicatedEmailException extends BaseException {

    public DuplicatedEmailException(ExceptionMessage status) {
        super(status);
    }
}
