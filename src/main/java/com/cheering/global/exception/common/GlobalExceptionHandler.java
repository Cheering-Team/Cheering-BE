package com.cheering.global.exception.common;

import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.DuplicatedEmailException;
import com.cheering.global.exception.user.InvalidEmailFormatException;
import com.cheering.global.exception.user.MisMatchPasswordException;
import com.cheering.global.exception.user.NotFoundUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //이메일 형식 유효성 예외
    @ExceptionHandler(InvalidEmailFormatException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleInvalidEmailFormatException(
            InvalidEmailFormatException e
    ) {
        log.error("handle InvalidEmailFormatException: ", e);
        return ResponseGenerator.fail(ExceptionMessage.INVALID_EMAIL_FORMAT, null);
    }

    //이메일 중복 예외
    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleDuplicatedEmailException(
            DuplicatedEmailException e
    ) {
        log.error("handle DuplicatedEmailException: ", e);

        return ResponseGenerator.fail(ExceptionMessage.DUPLICATED_EMAIL, null);
    }

    //회원 가입 시 패스워드 확인 문자열 불일치 예외
    @ExceptionHandler(MisMatchPasswordException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleMisMatchPasswordException(
            MisMatchPasswordException e
    ) {
        log.error("handle handleMisMatchPasswordException", e);
        return ResponseGenerator.fail(ExceptionMessage.MISMATCH_PASSWORD, null);
    }

    //회원 가입 DTO 형식 유효성 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleSignUpRequestFormatException(
            MethodArgumentNotValidException e
    ) {
        log.error("handle SignUpRequestFormatException", e);
        return ResponseGenerator.fail(ExceptionMessage.FAIL_SIGN_UP, null);
    }

    //로그인 실패 예외
    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleNotFoundUserException(
            NotFoundUserException e
    ) {
        log.error("handle NotFoundUserException", e);
        return ResponseGenerator.fail(ExceptionMessage.NOT_FOUND_USER, null);
    }
}
