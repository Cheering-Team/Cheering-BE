package com.cheering.notice;

import java.time.LocalDateTime;

public class NoticeResponse {
    public record NoticeDTO (Long id, String title, LocalDateTime createdAt) {
        public NoticeDTO(Notice notice) {
            this(notice.getId(), notice.getTitle(), notice.getCreatedAt());
        }
    }
}
