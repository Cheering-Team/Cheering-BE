package com.cheering.domain.user.dto.response;

import lombok.Builder;

@Builder
public record OwnerResponse(Long id,
                            String name) {

}
