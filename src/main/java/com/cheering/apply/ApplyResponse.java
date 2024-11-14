package com.cheering.apply;

import java.time.LocalDateTime;

public class ApplyResponse {
    public record ApplyDTO (Long id, String content, LocalDateTime createdAt, ApplyStatus status) {
        public ApplyDTO(Apply apply) {
            this(apply.getId(), apply.getContent(), apply.getCreatedAt(), apply.getStatus());
        }
    }
}
