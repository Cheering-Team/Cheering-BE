package com.cheering._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    // USER
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 사용자를 찾을 수 없습니다."),
    CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "인증코드가 일치하지 않습니다."),
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증코드가 만료되었습니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),

    // TEAM
    TEAM_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 팀을 찾을 수 없습니다."),

    // S3
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "유효하지 않는 파일입니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "확장자가 유효하지 않습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "이미지 업로드에 실패하였습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "이미지 업로드에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
