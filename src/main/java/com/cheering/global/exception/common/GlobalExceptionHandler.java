package com.cheering.global.exception.common;

import com.cheering.global.constant.ExceptionMessage;
import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import com.cheering.global.exception.user.DuplicatedEmailException;
import com.cheering.global.exception.user.InvalidEmailFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidEmailFormatException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleInvalidEmailFormatException(
            InvalidEmailFormatException e
    ) {
        log.error("handle InvalidEmailFormatException: ", e);
        return ResponseGenerator.fail(ExceptionMessage.INVALID_EMAIL_FORMAT, null);
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleDuplicatedEmailException(
            DuplicatedEmailException e
    ) {
        log.error("handle DuplicatedEmailException: ", e);
        return ResponseGenerator.fail(ExceptionMessage.DUPLICATED_EMAIL, null);
    }

}
