package com.cheering.match;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.relation.TeamPlayerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final MatchRepository matchRepository;
    public Map<String, List<MatchResponse.MatchDTO>> getMatchSchedule(Long communityId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        YearMonth previousMonth = yearMonth.minusMonths(2);
        YearMonth nextMonth = yearMonth.plusMonths(2);

        LocalDateTime startDateTime = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = nextMonth.atEndOfMonth().atTime(23, 59, 59);

        Optional<Team> team = teamRepository.findById(communityId);
        Optional<Player> player = playerRepository.findById(communityId);

        if(team.isPresent()) {
            List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(team.get(), startDateTime, endDateTime);

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
            List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(player.get().getFirstTeam(), startDateTime, endDateTime);

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


    public MatchResponse.MatchDetailDTO getNextMatch(Long communityId) {
        Pageable pageable = PageRequest.of(0, 1);

        Optional<Team> team = teamRepository.findById(communityId);
        Optional<Player> player = playerRepository.findById(communityId);

        Team curTeam;

        curTeam = team.orElseGet(() -> player.get().getFirstTeam());

        List<MatchStatus> statuses = Arrays.asList(MatchStatus.not_started, MatchStatus.live, MatchStatus.delayed, MatchStatus.interrupted, MatchStatus.started, MatchStatus.match_about_to_start);


        List<Match> nextMatch = matchRepository.findNextMatch(curTeam, statuses, pageable);
        if(!nextMatch.isEmpty()) {
            return new MatchResponse.MatchDetailDTO(nextMatch.get(0));
        }

        return null;
    }

    public List<MatchResponse.MatchDetailDTO> getNearMatches(Long communityId) {
        LocalDate today = LocalDate.now();
        LocalDateTime oneWeekAgo = today.minusWeeks(1).atStartOfDay();
        LocalDateTime oneWeekLater = today.plusWeeks(1).atTime(23, 59, 59);

        Optional<Team> team = teamRepository.findById(communityId);
        Optional<Player> player = playerRepository.findById(communityId);

        if(team.isPresent()) {
            List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(team.get(), oneWeekAgo, oneWeekLater);
            return matches.stream().map(MatchResponse.MatchDetailDTO::new).toList();
        }
        if(player.isPresent()) {
            List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(player.get().getFirstTeam(), oneWeekAgo, oneWeekLater);
            return matches.stream().map(MatchResponse.MatchDetailDTO::new).toList();
        }
        return null;
    }

    @Transactional
    public void addMatches(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);

            JsonNode summariesNode = rootNode.path("summaries");
            if(summariesNode.isArray()) {
                for (JsonNode summary: summariesNode) {
                    JsonNode sportEventNode = summary.path("sport_event");
                    String radarId = sportEventNode.path("id").asText();

                    String startTime = sportEventNode.path("start_time").asText();
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(startTime);
                    ZonedDateTime koreanTime = offsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Seoul"));
                    LocalDateTime time = koreanTime.toLocalDateTime();

                    Team homeTeam = null;
                    Team awayTeam = null;
                    String location = null;
                    JsonNode competitorsNode = sportEventNode.path("competitors");
                    if (competitorsNode.isArray()) {
                        for (JsonNode competitor : competitorsNode) {
                            String teamRadarId = competitor.path("id").asText();
                            String homeAway = competitor.path("qualifier").asText();

                            if(homeAway.equals("home")) {
                                homeTeam = teamRepository.findByRadarId(teamRadarId);
                                location = homeTeam.getLocation();
                            } else {
                                awayTeam = teamRepository.findByRadarId(teamRadarId);
                            }
                        }
                    }

                    JsonNode statusNode = summary.path("sport_event_status");
                    String statusText = statusNode.path("status").asText();
                    MatchStatus status = MatchStatus.valueOf(statusText);

                    Long homeScore = null;
                    Long awayScore = null;

                    if(statusText.equals("closed")) {
                        homeScore = statusNode.path("home_score").asLong();
                        awayScore = statusNode.path("away_score").asLong();
                    }

                    Match match = Match.builder()
                            .status(status)
                            .time(time)
                            .location(location)
                            .radarId(radarId)
                            .homeScore(homeScore)
                            .awayScore(awayScore)
                            .homeTeam(homeTeam)
                            .awayTeam(awayTeam)
                            .build();

                    matchRepository.save(match);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
