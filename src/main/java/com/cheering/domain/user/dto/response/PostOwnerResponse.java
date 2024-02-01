package com.cheering.domain.user.dto.response;

import lombok.Builder;

@Builder
public record PostOwnerResponse(Long id,
                                String name) {

}
