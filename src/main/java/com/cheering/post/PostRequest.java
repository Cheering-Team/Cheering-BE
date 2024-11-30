package com.cheering.post;

import java.time.LocalDateTime;
import java.util.List;

public class PostRequest {
    public record VoteDTO (String title, LocalDateTime endTime, Long matchId, List<VoteOptionDTO> options) { }
    public record VoteOptionDTO (String name, String image, String backgroundImage, Long communityId) { }
}