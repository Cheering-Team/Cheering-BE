package com.cheering._core.errors;

import com.cheering._core.errors.BaseException;
import com.cheering._core.errors.ExceptionMessage;

public class DuplicatedEmailException extends BaseException {

    public DuplicatedEmailException(ExceptionMessage status) {
        super(status);
    }
}
