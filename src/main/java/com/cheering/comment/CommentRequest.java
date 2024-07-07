package com.cheering.comment;

public class CommentRequest {
    public record WriteCommentDTO(
            String content
    ) { }
}