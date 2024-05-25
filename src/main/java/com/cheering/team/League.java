package com.cheering.team;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum League {
    KBO("KBO",Category.BASEBALL),
    MLB("MLB",Category.BASEBALL);

    private final String korean;
    private final Category category;
}
