package com.cheering.domain.comment.dto;

import com.cheering.domain.user.dto.response.WriterResponse;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentResponse(Long id,
                              String content,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              Long reCommentCount,
                              WriterResponse writer
) {
}
