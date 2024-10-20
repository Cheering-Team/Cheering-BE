package com.cheering.comment;

import com.cheering.community.relation.FanResponse;
import com.cheering.post.PostResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

public class CommentResponse {
    public record CommentIdDTO (Long id) { }

    public record CommentListDTO (List<CommentDTO> comments, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public CommentListDTO(Page<?> page, List<CommentDTO> comments) {
            this(comments, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }
    }

    public record CommentDTO (Long id, String content, LocalDateTime createdAt, Long reCount, FanResponse.FanDTO writer, Boolean isWriter) {
        public CommentDTO(Comment comment, Long reCount, Boolean isWriter) {
            this(comment.getId(), comment.getContent(), comment.getCreatedAt(), reCount, new FanResponse.FanDTO(comment.getWriter()), isWriter);
        }
    }
}