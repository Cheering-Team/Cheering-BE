package com.cheering.global.exception.user;

import com.cheering.global.exception.common.BaseException;
import com.cheering.global.exception.common.ExceptionStatus;

public class DuplicatedEmailException extends BaseException {

    public DuplicatedEmailException() {
        super(ExceptionStatus.DUPLICATED_EMAIL);
    }
}
