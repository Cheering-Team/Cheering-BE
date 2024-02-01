package com.cheering.domain.user.dto.response;

import lombok.Builder;

@Builder
public record PostOwnerResponse(Long id,
                                String name) {
    public static PostOwnerResponse of(Long id, String name) {
        return builder().id(id).name(name).build();
    }
}
