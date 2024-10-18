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
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "유저 권한 없음"),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않았습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    INVALID_PHONE(HttpStatus.BAD_REQUEST, "올바르지 않은 휴대폰 번호입니다."),
    DUPLICATE_NAME(HttpStatus.BAD_REQUEST, "중복된 이름"),
    ALREADY_MANAGER_ACCOUNT(HttpStatus.BAD_REQUEST, "이미 존재하는 매니저 계정"),
    NOT_FOUND_MANAGER_ACCOUNT(HttpStatus.BAD_REQUEST, "존재하지 않는 매니저 계정"),

    // TEAM
    LEAGUE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리그"),
    TEAM_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 팀을 찾을 수 없습니다."),

    // PLAYER
    COMMUNITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 커뮤니티"),
    FAN_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 팬"),
    CUR_FAN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 현재 접속 팬"),
    NOT_OWNER(HttpStatus.BAD_REQUEST, "선수 본인이 아닙니다."),

    // POST
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    NOT_WRITER(HttpStatus.BAD_REQUEST, "해당 글의 작성자가 아닙니다."),

    // TAG
    TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 태그"),

    // COMMENT
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    COMMENT_WRITER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 댓글 작성자"),

    // CHAT
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방"),
    NOT_CHATROOM_MEMBER(HttpStatus.BAD_REQUEST, "더 이상 채팅방에 속해있지 않습니다."),

    // REPORT
    ALREADY_REPORT(HttpStatus.BAD_REQUEST, "이미 신고하였습니다."),
    REPORTED_POST(HttpStatus.BAD_REQUEST, "신고 누적된 게시글이므로 수정 및 삭제가 불가합니다."),

    // NOTIFICATION
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다."),

    // NOTICE
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공지사항을 찾을 수 없습니다."),

    // BADWORD
    BADWORD_INCLUDED(HttpStatus.BAD_REQUEST, "부적절한 단어가 포함되어 있습니다."),

    // S3
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "유효하지 않는 파일입니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "유효하지 않은 확장자"),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "이미지 업로드 실패"),
    IMAGE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "이미지 삭제에 실패하였습니다."),

    // FCM
    FAILED_TO_SEND(HttpStatus.BAD_REQUEST, "FCM 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
