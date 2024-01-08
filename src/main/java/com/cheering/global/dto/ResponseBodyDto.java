package com.cheering.global.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ResponseBodyDto<T> {
    private final HttpStatus code;
    private final String message;
    private final T data;

    public static <T> ResponseBodyDto<?> of(HttpStatus code, String message, T data) {
        return builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

}
