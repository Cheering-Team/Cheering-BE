package com.cheering.core.community.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum League {
    KBO("한국 야구"),
    EPL("프리미어 리그"),
    FRENCH_LEAGUE1("프랑스 리그1");

    private final String korean;
}
