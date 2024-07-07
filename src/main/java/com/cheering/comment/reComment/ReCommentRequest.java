package com.cheering.comment.reComment;

public class ReCommentRequest {

    public record WriteReCommentDTO(
            String content,
            Long toId
    ) { }
}
