package com.cheering.domain.user.dto.response;

import java.net.URL;
import lombok.Builder;

@Builder
public record WriterResponse(Long id,
                             String name,
                             URL profileImage) {
    public static WriterResponse of(Long id, String name, URL profileImage) {
        return builder()
                .id(id)
                .name(name)
                .profileImage(profileImage)
                .build();
    }
}
