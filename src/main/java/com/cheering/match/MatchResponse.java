package com.cheering.match;

import com.cheering.team.Team;
import com.cheering.team.TeamResponse;

import java.time.LocalDateTime;

public class MatchResponse {
    public record MatchDTO (Long id, Boolean isHome, String opponentImage) {
        public MatchDTO(Match match, Team curTeam) {
            this(match.getId(), match.getHomeTeam().equals(curTeam), match.getHomeTeam().equals(curTeam) ? match.getAwayTeam().getImage() : match.getHomeTeam().getImage());
        }
    }

    public record MatchDetailDTO (Long id, LocalDateTime time, String location, TeamResponse.TeamDTO homeTeam, TeamResponse.TeamDTO awayTeam) {
        public MatchDetailDTO(Match match) {
            this(match.getId(), match.getTime(), match.getLocation(), new TeamResponse.TeamDTO(match.getHomeTeam()), new TeamResponse.TeamDTO(match.getAwayTeam()));
        }
    }
}
