package com.cheering.domain.post.dto;

import com.cheering.domain.post.domain.ImageFile;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.user.dto.response.WriterResponse;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record PostResponse(Long id,
                           String content,
                           Long commentCount,
                           Long likeCount,
                           Boolean likeStatus,
                           List<URL> image,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt,
                           WriterResponse writer
) {
    public static PostResponse of(Post post, WriterResponse writerResponse, List<URL> files) {
        return PostResponse.builder()
                .writer(writerResponse)
                .id(post.getId())
                .content(post.getContent())
                .image(files)
                .commentCount(20L)
                .likeCount(100L)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static List<PostResponse> ofList(List<Post> posts, WriterResponse writerResponse) {
        return posts.stream().map(post -> PostResponse.builder()
                .writer(writerResponse)
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
