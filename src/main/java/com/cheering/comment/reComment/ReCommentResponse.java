package com.cheering.comment.reComment;

import com.cheering.post.PostResponse;
import java.time.LocalDateTime;
import java.util.List;

public class ReCommentResponse {
    public record ReCommentIdDTO (Long id) { }

    public record ReCommentListDTO (List<ReCommentResponse.ReCommentDTO> reComments) { }

    public record ReCommentDTO (Long id, String content, LocalDateTime createdAt, PostResponse.WriterDTO to, PostResponse.WriterDTO writer, boolean isWriter) {
        public ReCommentDTO(ReComment reComment, PostResponse.WriterDTO to, PostResponse.WriterDTO writer, boolean isWriter) {
            this(reComment.getId(), reComment.getContent(), reComment.getCreatedAt(), to, writer, isWriter);
        }
    }
}
