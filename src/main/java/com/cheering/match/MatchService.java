package com.cheering.match;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.league.League;
import com.cheering.team.league.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;
    private final MatchRepository matchRepository;
    public Map<String, List<MatchResponse.MatchDTO>> getMatchSchedule(Long communityId) {
        Optional<Team> team = teamRepository.findById(communityId);
        Optional<Player> player = playerRepository.findById(communityId);

        if(team.isPresent()) {
            List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(team.get());

            return matches.stream()
                    .collect(Collectors.groupingBy(
                            match -> match.getTime().toLocalDate().toString(),
                            Collectors.mapping(
                                    match -> new MatchResponse.MatchDTO(match, team.get()),
                                    Collectors.toList()
                            )
                    ));
        }
        if(player.isPresent()) {
            List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(player.get().getFirstTeam());

            return matches.stream()
                    .collect(Collectors.groupingBy(
                            match -> match.getTime().toLocalDate().toString(),
                            Collectors.mapping(
                                    match -> new MatchResponse.MatchDTO(match, player.get().getFirstTeam()),
                                    Collectors.toList()
                            )
                    ));
        }
        return null;
    }

    public MatchResponse.MatchDetailDTO getMatch(Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(()-> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        return new MatchResponse.MatchDetailDTO(match);
    }

    public void addMatches(Long leagueId, List<MatchRequest.MatchDTO> matches) {
        int currentYear = LocalDate.now().getYear();
        League league = leagueRepository.findById(leagueId).orElseThrow(()-> new CustomException(ExceptionCode.LEAGUE_NOT_FOUND));

        for (MatchRequest.MatchDTO request : matches) {
            LocalDate date = LocalDate.of(currentYear, Integer.parseInt(request.month()), Integer.parseInt(request.day()));
            LocalTime time = LocalTime.parse(request.time());

            Team homeTeam = teamRepository.findByKoreanNameStartingWithAndLeague(request.home(), league);
            Team awayTeam = teamRepository.findByKoreanNameStartingWithAndLeague(request.away(), league);

            Match match = Match.builder()
                    .time(LocalDateTime.of(date, time))
                    .homeTeam(homeTeam)
                    .awayTeam(awayTeam)
                    .location(request.location())
                    .build();

            matchRepository.save(match);
        }
    }
}
