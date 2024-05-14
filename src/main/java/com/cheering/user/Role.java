package com.cheering.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_USER("사용자"),
    ROLE_ADMIN("관리자");

    private final String value;


}
