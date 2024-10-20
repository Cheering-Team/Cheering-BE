package com.cheering.comment.reComment;

import com.cheering.community.relation.FanResponse;
import com.cheering.post.PostResponse;
import java.time.LocalDateTime;
import java.util.List;

public class ReCommentResponse {
    public record ReCommentIdDTO (Long id) { }

    public record ReCommentListDTO (List<ReCommentResponse.ReCommentDTO> reComments) { }

    public record ReCommentDTO (Long id, String content, LocalDateTime createdAt, FanResponse.FanDTO to, FanResponse.FanDTO writer, boolean isWriter) {
        public ReCommentDTO(ReComment reComment, boolean isWriter) {
            this(reComment.getId(), reComment.getContent(), reComment.getCreatedAt(), new FanResponse.FanDTO(reComment.getTo()), new FanResponse.FanDTO(reComment.getWriter()), isWriter);
        }
    }
}
