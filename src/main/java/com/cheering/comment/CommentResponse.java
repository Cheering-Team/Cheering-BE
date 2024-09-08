package com.cheering.comment;

import com.cheering.community.UserCommunityInfo;
import com.cheering.post.PostResponse;
import com.cheering.user.WriterResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

public class CommentResponse {
    public record CommentIdDTO (Long id) { }

    public record CommentListDTO (List<CommentDTO> comments, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public CommentListDTO(Page<?> page, List<CommentDTO> comments) {
            this(comments, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }
    }

    public record CommentDTO (Long id, String content, LocalDateTime createdAt, Long reCount, PostResponse.WriterDTO writer, boolean isWriter) {
        public CommentDTO(Comment comment, Long reCount, PostResponse.WriterDTO writer, boolean isWriter) {
            this(comment.getId(), comment.getContent(), comment.getCreatedAt(), reCount, writer, isWriter);
        }
    }
}