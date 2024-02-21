package com.cheering.domain.post.dto;

import com.cheering.domain.community.constant.BooleanType;
import com.cheering.domain.post.domain.Interesting;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.user.dto.response.WriterResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;

@Builder
public record PostResponse(Long id,
                           String content,
                           Long commentCount,
                           Long likeCount,
                           BooleanType likeStatus,
                           List<ImageFileInfo> image,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt,
                           WriterResponse writer
) {
    public static PostResponse of(Post post, BooleanType likeStatus, WriterResponse writerResponse,
                                  List<ImageFileInfo> files) {

        int commentCount = post.getComments().size();
        Long likeCount = post.getLikes().stream().filter(like -> like.getStatus().equals(BooleanType.TRUE)).count();

        return PostResponse.builder()
                .writer(writerResponse)
                .id(post.getId())
                .content(post.getContent())
                .image(files)
                .likeStatus(likeStatus)
                .commentCount((long) commentCount)
                .likeCount(likeCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static List<PostResponse> ofList(List<Post> posts, List<Interesting> interestings,
                                            WriterResponse writerResponse) {
        List<PostResponse> result = new ArrayList<>();

        for (Post post : posts) {
            Optional<Interesting> likeStatus = interestings.stream()
                    .filter(interesting -> interesting.getPost().equals(post))
                    .findFirst();

            int commentCount = post.getComments().size();
            Long likeCount = post.getLikes().stream().filter(like -> like.getStatus().equals(BooleanType.TRUE)).count();

            List<ImageFileInfo> imageFileInfos = post.getFiles().stream().map(imageFile -> ImageFileInfo.builder()
                    .url(imageFile.getPath())
                    .width(imageFile.getWidth())
                    .height(imageFile.getHeight())
                    .build()).toList();

            PostResponse postResponse;
            if (likeStatus.isPresent()) {
                postResponse = PostResponse.builder()
                        .writer(writerResponse)
                        .id(post.getId())
                        .content(post.getContent())
                        .image(imageFileInfos)
                        .likeStatus(likeStatus.get().getStatus())
                        .commentCount((long) commentCount)
                        .likeCount(likeCount)
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .build();
            } else {
                postResponse = PostResponse.builder()
                        .writer(writerResponse)
                        .id(post.getId())
                        .content(post.getContent())
                        .image(imageFileInfos)
                        .likeStatus(BooleanType.FALSE)
                        .commentCount((long) commentCount)
                        .likeCount(likeCount)
                        .createdAt(post.getCreatedAt())
                        .updatedAt(post.getUpdatedAt())
                        .build();
            }
            result.add(postResponse);
        }

        return result;
    }
}
