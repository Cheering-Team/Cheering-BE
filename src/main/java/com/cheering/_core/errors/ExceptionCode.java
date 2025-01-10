package com.cheering._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    INVALID_PHONE(2001, "잘못된 휴대폰 번호 형식"),
    CODE_EXPIRED(2002, "인증번호 만료"),
    CODE_NOT_EQUAL(2003, "인증번호 불일치"),
    BADWORD_INCLUDED(2004, "부적절한 단어 포함"),
    NEED_SIGNUP(2005, "회원가입 필요"),
    NOT_FOUND_MANAGER_ACCOUNT(2006, "존재하지 않는 매니저 계정"),
    DUPLICATE_NAME(2007, "중복된 이름"),
    ALREADY_HANDLED_APPLY(2008, "이미 처리된 신청"),
    VOTE_IS_CLOSED(2009, "이미 마감된 투표"),

    // 404
    FAN_NOT_FOUND(404, "존재하지 않는 팬"),
    CUR_FAN_NOT_FOUND(404, "커뮤니티로부터 제재"),
    POST_NOT_FOUND(404, "존재하지 않는 게시글"),
    COMMENT_NOT_FOUND(404, "존재하지 않는 댓글"),
    NOTIFICATION_NOT_FOUND(404, "존재하지 않는 알림"),
    CHATROOM_NOT_FOUND(404, "존재하지 않는 채팅방"),
    NOTICE_NOT_FOUND(404, "존재하지 않는 공지사항"),
    MATCH_NOT_FOUND(404, "존재하지 않는 경기"),
    CHEER_NOT_FOUND(404, "존재하지 않는 응원"),
    CHAT_SESSION_NOT_FOUND(404, "존재하지 않는 세션"),
    APPLY_NOT_FOUND(404, "존재하지 않는 신청"),
    MEET_NOT_FOUND(404, "존재하지 않는 모임"),
    MEET_FAN_NOT_FOUND(404, "존재하지 않는 모임 참가자"),
    MANAGER_NOT_FOUND(404, "모임장이 존재하지 않습니다."),

    // USER
    USER_NOT_FOUND(200, "해당 사용자를 찾을 수 없습니다."),
    USER_FORBIDDEN(403, "유저 권한 없음"),
    USER_UNAUTHORIZED(401, "인증되지 않았습니다."),
    INVALID_TOKEN(401, "토큰이 유효하지 않습니다."),
    ALREADY_MANAGER_ACCOUNT(200, "이미 존재하는 매니저 계정"),
    USER_AGE_NOT_SET(400, "나이가 설정되지 않았습니다."),
    USER_GENDER_NOT_SET(400, "성별이 설정되지 않았습니다."),

    // TEAM
    LEAGUE_NOT_FOUND(404, "존재하지 않는 리그"),
    TEAM_NOT_FOUND(200, "해당 팀을 찾을 수 없습니다."),

    // PLAYER
    PLAYER_NOT_FOUND(404, "존재하지 않는 선수"),

    NOT_OWNER(200, "선수 본인이 아닙니다."),

    // POST
    NOT_WRITER(200, "해당 글의 작성자가 아닙니다."),

    // TAG
    TAG_NOT_FOUND(404, "존재하지 않는 태그"),

    // COMMENT
    COMMENT_WRITER_NOT_FOUND(200, "존재하지 않는 댓글 작성자"),

    // CHAT
    NOT_CHATROOM_MEMBER(200, "더 이상 채팅방에 속해있지 않습니다."),

    // REPORT
    ALREADY_REPORT(200, "이미 신고하였습니다."),
    REPORTED_POST(200, "신고 누적된 게시글이므로 수정 및 삭제가 불가합니다."),

    // S3
    EMPTY_FILE(200, "유효하지 않는 파일입니다."),
    INVALID_FILE_EXTENSION(200, "유효하지 않은 확장자"),
    IMAGE_UPLOAD_FAILED(200, "이미지 업로드 실패"),
    IMAGE_DELETE_FAILED(200, "이미지 삭제에 실패하였습니다."),

    // FCM
    FAILED_TO_SEND(200, "FCM 전송에 실패했습니다."),

    // MEET
    DUPLICATE_MEET(2010, "이미 해당 경기에 대해 생성된 모임이 있습니다."),
    DUPLICATE_CHAT_ROOM(2011, "이미 채팅방이 있습니다."),
    HAS_TICKET_REQUIRED_FOR_LIVE(2012, "직관 모임은 티켓 여부 입력이 필수입니다."),
    USER_RESTRICTED_FOR_MATCH(2013, "해당 경기에 대한 모임 참여가 제한되었습니다."),

    //MATCH
    MATCH_NOT_RELATED_TO_COMMUNITY(403, "커뮤니티와 관련 없는 경기입니다.");



    private final int code;
    private final String message;
}
