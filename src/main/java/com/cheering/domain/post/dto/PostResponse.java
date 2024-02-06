package com.cheering.domain.post.dto;

import com.cheering.domain.post.domain.ImageFile;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.user.dto.response.PostOwnerResponse;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostResponse(Long id,
                           String content,
                           Long commentCount,
                           Long likeCount,
                           List<URL> image,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt,
                           PostOwnerResponse owner
) {
    public static PostResponse of(Post post, PostOwnerResponse postOwnerResponse, List<URL> files) {
        return PostResponse.builder()
                .owner(postOwnerResponse)
                .id(post.getId())
                .content(post.getContent())
                .image(files)
                .commentCount(20L)
                .likeCount(100L)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static List<PostResponse> ofList(List<Post> posts, PostOwnerResponse postOwnerResponse) {
        return posts.stream().map(post -> PostResponse.builder()
                .owner(postOwnerResponse)
                .id(post.getId())
                .content(post.getContent())
                .image(post.getFiles().stream().map(ImageFile::getPath).toList())
                .commentCount(20L)
                .likeCount(100L)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build()
        ).toList();
    }
}
