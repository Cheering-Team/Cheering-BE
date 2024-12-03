package com.cheering.match;

import com.cheering.community.CommunityResponse;

import java.time.LocalDateTime;
import java.util.List;

public class MatchRequest {
    public record EditMatchDTO (
            LocalDateTime time,
            String location,
            String status,
            Long homeScore,
            Long awayScore,
            List<CommunityResponse.CommunityDTO> homePlayers,
            List<CommunityResponse.CommunityDTO> awayPlayers
    ) { }
}
