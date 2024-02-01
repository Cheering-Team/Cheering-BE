package com.cheering.domain.post.dto;

import com.cheering.domain.post.domain.Post;
import com.cheering.domain.user.dto.response.PostOwnerResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostResponse(Long id,
                           String content,
                           Long commentCount,
                           Long likeCount,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt,
                           PostOwnerResponse owner
) {

    public static List<PostResponse> ofList(List<Post> posts, PostOwnerResponse owner) {
        return posts.stream().map(post -> PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .commentCount(100L)
                .likeCount(200L)
                .owner(owner)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build()
        ).toList();
    }
}
