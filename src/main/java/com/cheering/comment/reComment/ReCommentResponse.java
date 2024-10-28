package com.cheering.comment.reComment;

import com.cheering.fan.FanResponse;

import java.time.LocalDateTime;

public class ReCommentResponse {
    public record ReCommentIdDTO (Long id) { }

    public record ReCommentDTO (Long id, String content, LocalDateTime createdAt, FanResponse.FanDTO to, FanResponse.FanDTO writer, boolean isWriter) {
        public ReCommentDTO(ReComment reComment, boolean isWriter) {
            this(reComment.getId(), reComment.getContent(), reComment.getCreatedAt(), new FanResponse.FanDTO(reComment.getTo()), new FanResponse.FanDTO(reComment.getWriter()), isWriter);
        }
    }
}
