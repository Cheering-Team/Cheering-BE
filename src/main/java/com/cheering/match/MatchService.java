package com.cheering.match;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.notification.Fcm.FcmServiceImpl;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
import com.cheering.post.PostResponse;
import com.cheering.post.PostService;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import com.cheering.user.UserRepository;
import com.cheering.user.deviceToken.DeviceToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final PostRepository postRepository;
    private final MatchRepository matchRepository;
    private final FanRepository fanRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final FcmServiceImpl fcmService;

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

    // 특정 경기투표 포함 게시글 조회
    @Transactional
    public PostResponse.PostListDTO getVotes(Long matchId, Long communityId, String orderBy, Pageable pageable, User user) {
        Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
        Page<Post> postList;
        if(orderBy.equals("latest")){
            postList = postRepository.findByMatchIdAndCommunityIdOrderByLatest(communityId, matchId, curFan, pageable);
        } else {
            postList = postRepository.findByMatchIdAndCommunityIdOrderByVotes(communityId, matchId, curFan, pageable);
        }

        List<PostResponse.PostInfoWithCommunityDTO> postInfoDTOS = postList.getContent().stream().map((post -> postService.getPostInfo(post, curFan))).toList();

        return new PostResponse.PostListDTO(postList, postInfoDTOS);
    }

    // 경기 시작 30분 알림
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void notifyMatch() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusMinutes(30);

        List<Match> matches = matchRepository.findMatchesForReminder(now, targetTime);

        for(Match match : matches) {
            Team homeTeam = match.getHomeTeam();
            Team awayTeam = match.getAwayTeam();

            List<User> homeFans = userRepository.findByTeamId(homeTeam.getId());
            for(User user : homeFans) {
                for(DeviceToken deviceToken: user.getDeviceTokens()){
                    Boolean isTeamFan = fanRepository.existsByCommunityIdAndUser(homeTeam.getId(), user);
                    if(isTeamFan) {
                        fcmService.sendMatchMessageTo(deviceToken.getToken(), homeTeam.getKoreanName(), "30분 후 vs " + awayTeam.getKoreanName() + "시작!\n응원의 메세지를 남겨보세요!", match.getId(), homeTeam.getId());
                    } else {
                        Pageable pageable = PageRequest.of(0, 1);
                        Page<Fan> firstFan = fanRepository.findByFirstTeamIdAndUser(homeTeam.getId(), user, pageable);
                        fcmService.sendMatchMessageTo(deviceToken.getToken(), homeTeam.getKoreanName(), "30분 후 vs " + awayTeam.getKoreanName() + "시작!\n응원의 메세지를 남겨보세요!", match.getId(), firstFan.getContent().get(0).getCommunityId());
                    }

                }
            }
            List<User> awayFans = userRepository.findByTeamId(awayTeam.getId());
            for(User user : awayFans) {
                for(DeviceToken deviceToken: user.getDeviceTokens()){
                    Boolean isTeamFan = fanRepository.existsByCommunityIdAndUser(awayTeam.getId(), user);
                    if(isTeamFan){
                        fcmService.sendMatchMessageTo(deviceToken.getToken(), awayTeam.getKoreanName(), "30분 후 vs " + homeTeam.getKoreanName() + "시작!\n응원의 메세지를 남겨보세요!", match.getId(), awayTeam.getId());
                    } else {
                        Pageable pageable = PageRequest.of(0, 1);
                        Page<Fan> firstFan = fanRepository.findByFirstTeamIdAndUser(awayTeam.getId(), user, pageable);
                        fcmService.sendMatchMessageTo(deviceToken.getToken(), awayTeam.getKoreanName(), "30분 후 vs " + homeTeam.getKoreanName() + "시작!\n응원의 메세지를 남겨보세요!", match.getId(), firstFan.getContent().get(0).getCommunityId());
                    }
                }
            }
            match.setIsMatchNotified(true);
            matchRepository.save(match);
        }
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
