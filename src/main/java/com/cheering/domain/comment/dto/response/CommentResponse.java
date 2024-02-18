package com.cheering.domain.comment.dto.response;

import com.cheering.domain.comment.domain.Comment;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.user.dto.response.WriterResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record CommentResponse(Long id,
                              String content,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              Long reCommentCount,
                              WriterResponse writer
) {

    public static List<CommentResponse> ofList(List<Comment> comments) {
        List<CommentResponse> result = new ArrayList<>();

        for (Comment comment : comments) {
            UserCommunityInfo writerInfo = comment.getWriterInfo();

            WriterResponse writerResponse = WriterResponse.of(writerInfo.getUser().getId(), writerInfo.getNickname(),
                    writerInfo.getProfileImage());

            Long reCommentCount = (long) comment.getReComments().size();

            CommentResponse commentResponse = builder().id(comment.getId())
                    .reCommentCount(reCommentCount)
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .writer(writerResponse)
                    .build();

            result.add(commentResponse);
        }

        return result;
    }
}
