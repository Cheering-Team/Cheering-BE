package com.cheering.comment.reComment;

import com.cheering.comment.Comment;
import com.cheering.comment.CommentResponse;
import com.cheering.community.UserCommunityInfo;
import com.cheering.post.PostResponse;
import com.cheering.user.WriterResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

public class ReCommentResponse {
    public record ReCommentIdDTO (Long id) { }

    public record ReCommentListDTO (List<ReCommentResponse.ReCommentDTO> reComments) { }

    public record ReCommentDTO (Long id, String content, LocalDateTime createdAt, PostResponse.WriterDTO to, PostResponse.WriterDTO writer) {
        public ReCommentDTO(ReComment reComment, PostResponse.WriterDTO to, PostResponse.WriterDTO writer) {
            this(reComment.getId(), reComment.getContent(), reComment.getCreatedAt(), to, writer);
        }
    }
}
