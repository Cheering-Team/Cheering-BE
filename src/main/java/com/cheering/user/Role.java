package com.cheering.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    ROLE_USER("USER"),
    ROLE_PLAYER("PLAYER"),
    ROLE_TEAM("TEAM"),
    ROLE_ADMIN("ADMIN");

    private final String type;


}
