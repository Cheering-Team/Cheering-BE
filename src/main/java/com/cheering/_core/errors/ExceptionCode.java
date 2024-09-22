package com.cheering._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    // USER
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 사용자를 찾을 수 없습니다."),
    CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증번호가 만료되었습니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),

    // TEAM
    TEAM_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 팀을 찾을 수 없습니다."),

    // PLAYER
    PLAYER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 선수를 찾을 수 없습니다."),
    PLAYER_USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 커뮤니티에 가입되지 않은 유저입니다."),
    CUR_PLAYER_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 커뮤니티 유저를 찾을 수 없습니다."),

    // POST
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    NOT_WRITER(HttpStatus.BAD_REQUEST, "해당 글의 작성자가 아닙니다."),

    // TAG
    TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 태그를 찾을 수 없습니다."),

    // COMMENT
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    COMMENT_WRITER_NOT_FOUND(HttpStatus.BAD_REQUEST, "댓글 작성자를 찾을 수 없습니다."),

    // CHAT
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),

    // REPORT
    ALREADY_REPORT(HttpStatus.BAD_REQUEST, "이미 신고하였습니다."),
    REPORTED_POST(HttpStatus.BAD_REQUEST, "신고 누적된 게시글이므로 수정 및 삭제가 불가합니다."),

    // NOTIFICATION
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다."),

    // S3
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "유효하지 않는 파일입니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "확장자가 유효하지 않습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "이미지 업로드에 실패하였습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "이미지 삭제에 실패하였습니다."),

    // FCM
    FAILED_TO_SEND(HttpStatus.BAD_REQUEST, "FCM 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
