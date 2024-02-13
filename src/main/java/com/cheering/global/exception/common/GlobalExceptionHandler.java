package com.cheering.global.exception.common;

import com.cheering.global.dto.ResponseBodyDto;
import com.cheering.global.dto.ResponseGenerator;
import com.cheering.global.exception.auth.ExpiredRefreshTokenException;
import com.cheering.global.exception.community.DuplicatedCommunityJoinException;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.community.NotFoundUserCommunityInfoException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.post.InvalidWriterTypeException;
import com.cheering.global.exception.post.NotFoundPostException;
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

    //존재하지 않는 커뮤니티 조회 예외
    @ExceptionHandler(NotFoundCommunityException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleNotFoundCommunityException(
            NotFoundCommunityException e
    ) {
        log.error("handle NotFoundCommunityException", e);
        return ResponseGenerator.fail(ExceptionMessage.NOT_FOUND_COMMUNITY, null);
    }

    //중복 커뮤니티 가입 예외
    @ExceptionHandler(DuplicatedCommunityJoinException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleDuplicatedCommunityJoinException(
            DuplicatedCommunityJoinException e
    ) {
        log.error("handle DuplicatedCommunityJoinException", e);
        return ResponseGenerator.fail(ExceptionMessage.DUPLICATED_JOIN_COMMUNITY, null);
    }

    //존재하지 않는 유저 커뮤니티 정보 조회 예외
    @ExceptionHandler(NotFoundUserCommunityInfoException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleNotFoundUserCommunityInfoException(
            NotFoundUserCommunityInfoException e
    ) {
        log.error("handle NotFoundUserCommunityInfoException", e);
        return ResponseGenerator.fail(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO, null);
    }

    //작성자 타입 미스매치 예외
    @ExceptionHandler(InvalidWriterTypeException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleInvalidWriterTypeException(
            InvalidWriterTypeException e
    ) {
        log.error("handle InvalidWriterTypeException", e);
        return ResponseGenerator.fail(ExceptionMessage.INVALID_WRITER_TYPE, null);
    }

    //존재하지 않는 게시글 조회 예외
    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleNotFoundPostException(
            NotFoundPostException e
    ) {
        log.error("handle NotFoundPostException", e);
        return ResponseGenerator.fail(ExceptionMessage.NOT_FOUND_POST, null);
    }

    //리프레시 토큰 만료 예외
    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<ResponseBodyDto<?>> handleExpiredRefreshTokenException(
            ExpiredRefreshTokenException e
    ) {
        log.error("handle ExpiredRefreshTokenException", e);
        return ResponseGenerator.fail(ExceptionMessage.EXPIRED_REFRESH_TOKEN, null);
    }
}
