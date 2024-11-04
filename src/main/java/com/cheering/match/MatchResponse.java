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

    public record MatchDetailDTO (Long id, MatchStatus status, LocalDateTime time, String location, Long homeScore, Long awayScore, TeamResponse.TeamDTO homeTeam, TeamResponse.TeamDTO awayTeam, String sportName) {
        public MatchDetailDTO(Match match) {
            this(match.getId(), match.getStatus(), match.getTime(), match.getLocation(), match.getHomeScore(), match.getAwayScore(), new TeamResponse.TeamDTO(match.getHomeTeam()), new TeamResponse.TeamDTO(match.getAwayTeam()), match.getHomeTeam().getLeague().getSport().getName());
        }
    }
}
