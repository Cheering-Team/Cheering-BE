package com.cheering.team;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Category {
    SOCCER("축구"),
    BASEBALL("야구"),
    BASKETBALL("농구");

    private final String korean;
}
