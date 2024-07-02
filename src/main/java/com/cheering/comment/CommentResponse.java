package com.cheering.comment;

import com.cheering.community.UserCommunityInfo;
import com.cheering.user.WriterResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public class CommentResponse {
    public record CommentIdDTO (Long id) { }
}