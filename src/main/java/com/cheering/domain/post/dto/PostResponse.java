package com.cheering.domain.post.dto;

import com.cheering.domain.post.domain.Post;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostResponse(Long id,
                           String content,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt,
                           UserResponse owner
) {

    public static List<PostResponse> ofList(List<Post> posts) {
        return posts.stream().map(post -> PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .build()).toList();
    }
}
