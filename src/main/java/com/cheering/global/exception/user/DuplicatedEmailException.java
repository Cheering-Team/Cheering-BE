package com.cheering.global.exception.user;

import com.cheering.global.constant.ExceptionStatus;
import com.cheering.global.exception.common.BaseException;

public class DuplicatedEmailException extends BaseException {

    public DuplicatedEmailException() {
        super(ExceptionStatus.DUPLICATED_EMAIL);
    }
}
