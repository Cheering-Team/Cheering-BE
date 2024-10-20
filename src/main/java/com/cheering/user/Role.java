package com.cheering.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    USER("사용자"),
    ADMIN("관리자"),
    PLAYER("선수"),
    TEAM("팀");

    private final String value;
}
