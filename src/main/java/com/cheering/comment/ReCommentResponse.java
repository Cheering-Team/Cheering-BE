package com.cheering.comment;

import com.cheering.community.UserCommunityInfo;
import com.cheering.user.WriterResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record ReCommentResponse(Long id,
                                String content,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt,
                                WriterResponse writer) {

    public static List<ReCommentResponse> ofList(List<ReComment> reComments) {
        List<ReCommentResponse> result = new ArrayList<>();

        for (ReComment reComment : reComments) {
            UserCommunityInfo writerInfo = reComment.getWriterInfo();

            WriterResponse writerResponse = WriterResponse.of(writerInfo.getUser().getId(), writerInfo.getNickname(),
                    writerInfo.getProfileImage());

            ReCommentResponse commentResponse = builder().id(reComment.getId())
                    .content(reComment.getContent())
                    .createdAt(reComment.getCreatedAt())
                    .updatedAt(reComment.getUpdatedAt())
                    .writer(writerResponse)
                    .build();

            result.add(commentResponse);
        }

        return result;
    }
}
