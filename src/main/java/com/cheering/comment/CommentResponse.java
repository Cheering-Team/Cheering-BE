package com.cheering.comment;

import com.cheering.community.UserCommunityInfo;
import com.cheering.post.PostResponse;
import com.cheering.user.WriterResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public class CommentResponse {
    public record CommentIdDTO (Long id) { }

    public record CommentListDTO (List<CommentDTO> comments) { }

    public record CommentDTO (Long id, String content, LocalDateTime createdAt, PostResponse.WriterDTO writer) {
        public CommentDTO(Comment comment, PostResponse.WriterDTO writer) {
            this(comment.getId(), comment.getContent(), comment.getCreatedAt(), writer);
        }
    }
}