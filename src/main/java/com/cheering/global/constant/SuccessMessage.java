package com.cheering.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessMessage {
    //user
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "signup success"),
    VALIDATE_EMAIL_SUCCESS(HttpStatus.OK, "not duplicated"),
    SIGN_IN_SUCCESS(HttpStatus.OK, "login success"),
    SIGN_OUT_SUCCESS(HttpStatus.OK, "logout success"),
    REISSUE_ACCESS_TOKEN_SUCCESS(HttpStatus.OK, "reIssue Access-Token"),

    //community
    SEARCH_COMMUNITY_SUCCESS(HttpStatus.OK, "search success"),
    JOIN_COMMUNITY_SUCCESS(HttpStatus.CREATED, "join community success"),
    GET_COMMUNITY_SUCCESS(HttpStatus.OK, "get community success"),

    //post
    GET_POSTS_SUCCESS(HttpStatus.OK, "get posts success"),
    CREATE_POST_SUCCESS(HttpStatus.CREATED, "create post success"),
    DETAIL_POST_SUCCESS(HttpStatus.OK, "detail post success"),

    //like
    LIKE_SUCCESS(HttpStatus.OK, "like success"),
    LIKE_CANCEL_SUCCESS(HttpStatus.OK, "like cancel success"),

    //comment
    CREATE_COMMENT_SUCCESS(HttpStatus.CREATED, "create comment success"),
    GET_COMMENT_SUCCESS(HttpStatus.OK, "get comment success"),

    //re_comment
    CREATE_RE_COMMENT_SUCCESS(HttpStatus.CREATED, "create re-comment success"),
    GET_RE_COMMENT_SUCCESS(HttpStatus.OK, "get re-comments-success");

    private final HttpStatus httpStatus;
    private final String message;

}
