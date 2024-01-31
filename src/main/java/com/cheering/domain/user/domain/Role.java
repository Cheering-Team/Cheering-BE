package com.cheering.domain.user.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ROLE_USER("USER"),
    ROLE_PLAYER("PLAYER"),
    ROLE_TEAM("TEAM"),
    ROLE_ADMIN("ADMIN");

    private final String type;


}
