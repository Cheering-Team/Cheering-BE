package com.cheering.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ResponseBodyDto<T> {
    private final String message;
    private final T data;

    public static <T> ResponseBodyDto<?> of(String message, T data) {
        return builder()
                .message(message)
                .data(data)
                .build();
    }

}
