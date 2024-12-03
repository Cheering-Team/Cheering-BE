package com.cheering.match;

import com.cheering.post.PostResponse;
import com.cheering.team.Team;
import com.cheering.team.TeamResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

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

    public record VoteMatchDTO (Long id, String opponentImage, String shortName, LocalDateTime time) { }

    public record MatchListDTO(List<MatchDetailDTO> matches, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public MatchListDTO(Page<?> page, List<MatchDetailDTO> matches) {
            this(matches, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }

    }
}
