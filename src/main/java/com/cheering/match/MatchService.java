package com.cheering.match;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.chatRoom.ChatRoomResponse;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.meet.*;
import com.cheering.meetfan.MeetFanRepository;
import com.cheering.meetfan.MeetFanRole;
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
import com.cheering.vote.Vote;
import com.cheering.vote.VoteRepository;
import com.cheering.vote.voteOption.VoteOption;
import com.cheering.vote.voteOption.VoteOptionRepository;
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
    private final MeetService meetService;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRepository voteRepository;
    private final MeetRepository meetRepository;
    private final MeetFanRepository meetFanRepository;

    public Map<String, List<MatchResponse.MatchDTO>> getMatchSchedule(Long communityId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

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

    public MatchResponse.MatchDetailDTO getMatch(Long matchId, User user) {

        Match match = matchRepository.findById(matchId).orElseThrow(()-> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        List<MeetFanRole> roles = Arrays.asList(MeetFanRole.MANAGER, MeetFanRole.MEMBER, MeetFanRole.APPLIER);
        Meet meet = meetRepository.findByMatchAndUserWithRoles(matchId, user, roles);


        MeetStatus status;
        Integer currentCount = null;
        Team team = null;
        if (meet != null) {
            Optional<Team> optionalTeam = teamRepository.findById(meet.getCommunityId());
            Optional<Player> optionalPlayer = playerRepository.findById(meet.getCommunityId());

            currentCount = meetService.calculateCurrentCount(meet.getId());
            if (optionalTeam.isPresent()) {
                team = optionalTeam.get();
            }else {
                Player player = optionalPlayer.get();
                team = player.getFirstTeam();
            }
            if (meet.getManager().getUser().equals(user)) {
                status = MeetStatus.MANAGER; // 내가 만든 모임
            } else if (meetFanRepository.existsByMeetAndFanUser(meet, user)) {
                status = MeetStatus.CONFIRMED; // 확정된 모임
            } else {
                status = MeetStatus.APPLIED; // 1:1 대화 중인 상태
            }
        } else {
            status = null;
        }


        return new MatchResponse.MatchDetailDTO(
                match,
                meet != null ? new MeetResponse.MeetInfoDTO(meet, currentCount, null, team, status) : null,
                null
        );
    }


    public MatchResponse.MatchDetailDTO getNextMatch(Long communityId, User user) {
        Pageable pageable = PageRequest.of(0, 1);

        Optional<Team> team = teamRepository.findById(communityId);
        Optional<Player> player = playerRepository.findById(communityId);

        Team curTeam;

        curTeam = team.orElseGet(() -> player.get().getFirstTeam());

        List<MatchStatus> statuses = Arrays.asList(MatchStatus.not_started, MatchStatus.live, MatchStatus.delayed, MatchStatus.interrupted, MatchStatus.started, MatchStatus.match_about_to_start);


        MeetStatus status;
        List<Match> nextMatches = matchRepository.findNextMatch(curTeam, statuses, pageable);
        if(!nextMatches.isEmpty()) {
            Match nextMatch = nextMatches.get(0);

            List<MeetFanRole> roles = Arrays.asList(MeetFanRole.MANAGER, MeetFanRole.MEMBER, MeetFanRole.APPLIER);
            Meet meet = meetRepository.findByMatchAndUserWithRoles(nextMatch.getId(), user, roles);
            MeetResponse.MeetInfoDTO meetInfoDTO = null;
            if (meet != null) {
                Integer currentCount = meetService.calculateCurrentCount(meet.getId());
                if (meet.getManager().getUser().equals(user)) {
                    status = MeetStatus.MANAGER; // 내가 만든 모임
                } else if (meetFanRepository.existsByMeetAndFanUser(meet, user)) {
                    status = MeetStatus.CONFIRMED; // 확정된 모임
                } else {
                    status = MeetStatus.APPLIED; // 1:1 대화 중인 상태
                }
                meetInfoDTO = new MeetResponse.MeetInfoDTO(meet, currentCount, null, curTeam, status);
            }
            return new MatchResponse.MatchDetailDTO(nextMatches.get(0), meetInfoDTO, null);
        }

        return null;
    }

    public List<MatchResponse.MatchDetailDTO> getNearMatches(Long communityId, User user) {
        LocalDate today = LocalDate.now();
        LocalDateTime oneWeekAgo = today.minusWeeks(1).atStartOfDay();
        LocalDateTime oneWeekLater = today.plusWeeks(1).atTime(23, 59, 59);

        Optional<Team> optionalTeam = teamRepository.findById(communityId);
        Optional<Player> optionalPlayer = playerRepository.findById(communityId);
        Team team;

        if (optionalTeam.isPresent()) {
            team = optionalTeam.get();
        } else {
            team = optionalPlayer.get().getFirstTeam();
        }

        List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(team, oneWeekAgo, oneWeekLater);

        return matches.stream().map(match -> {
            // Manager 또는 Member 상태의 모임 우선 확인
            Meet meet = meetRepository.findByMatchAndUserWithRoles(match.getId(), user, Arrays.asList(MeetFanRole.MANAGER, MeetFanRole.MEMBER));

            MeetResponse.MeetInfoDTO meetInfoDTO;

            if (meet != null) {
                // Manager 또는 Member 상태의 모임이 있는 경우
                Integer currentCount = meetFanRepository.countByMeet(meet);
                MeetStatus status = meet.getManager().getUser().equals(user) ? MeetStatus.MANAGER : MeetStatus.CONFIRMED;
                meetInfoDTO = new MeetResponse.MeetInfoDTO(meet, currentCount, null, team, status);
            } else {
                List<Meet> meets;
                if (user.getAge() == null || user.getGender() == null) {
                    meets = meetRepository.findMeetsByMatch(communityId, match.getId(), PageRequest.of(0, 50));
                } else {
                    int currentYear = java.time.Year.now().getValue();
                    int currentAge = currentYear - user.getAge() + 1;
                    MeetGender meetGender = meetService.genderMapper(user.getGender());

                    meets = meetRepository.findMeetsByConditionsWithMatch(
                            communityId,
                            currentAge,
                            meetGender,
                            user,
                            match.getId(),
                            PageRequest.of(0, 50)
                    );
                }

                // 모임 1개 랜덤으로 고르기
                if (!meets.isEmpty()) {
                    Collections.shuffle(meets);
                    Meet selectedMeet = meets.get(0);
                    Integer currentCount = meetFanRepository.countByMeet(selectedMeet);

                    meetInfoDTO = new MeetResponse.MeetInfoDTO(
                            selectedMeet,
                            currentCount,
                            null,
                            team,
                            null
                    );
                } else {
                    meetInfoDTO = null;
                }

            }

            return new MatchResponse.MatchDetailDTO(match, meetInfoDTO, null);
        }).toList();
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

    // 안끝난 경기 조회
    @Transactional
    public MatchResponse.MatchListDTO getUnfinishedMatches(Pageable pageable) {
        Page<Match> matchList = matchRepository.findAllUnfinishedMatch(MatchStatus.closed, pageable);

        List<MatchResponse.MatchDetailDTO> matchDetailDTOS = matchList.getContent().stream()
                .map(match -> new MatchResponse.MatchDetailDTO(match, null, null))
                .toList();

        return new MatchResponse.MatchListDTO(matchList, matchDetailDTOS);
    }

    // 경기 수정
    @Transactional
    public void editMatch(Long matchId, MatchRequest.EditMatchDTO requestDTO) {
        Match match = matchRepository.findById(matchId).orElseThrow(()-> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        match.setTime(requestDTO.time());
        match.setLocation(requestDTO.location());
        match.setStatus(MatchStatus.valueOf(requestDTO.status()));
        match.setHomeScore(requestDTO.homeScore());
        match.setAwayScore(requestDTO.awayScore());

        matchRepository.save(match);

        if(requestDTO.status().equals(MatchStatus.closed.toString())){
            Fan admin = fanRepository.findByAdminFan();

            // HOME
            Post homePost = Post.builder()
                    .content(match.getHomeTeam().getShortName() + " " + requestDTO.homeScore() + " : " + requestDTO.awayScore() + " " + match.getAwayTeam().getShortName())
                    .writer(admin)
                    .communityId(match.getHomeTeam().getId())
                    .build();

            postRepository.save(homePost);

            Vote homeVote = Vote.builder()
                    .title("직접 뽑는 오늘의 MVP")
                    .endTime(LocalDateTime.now().plusHours(2))
                    .match(match)
                    .post(homePost)
                    .build();

            voteRepository.save(homeVote);

            requestDTO.homePlayers().forEach(player -> {
                VoteOption voteOption = VoteOption.builder()
                        .name(player.koreanName())
                        .communityId(player.id())
                        .image(player.image())
                        .backgroundImage(player.backgroundImage())
                        .vote(homeVote)
                        .build();

                voteOptionRepository.save(voteOption);
            });

            // AWAY
            Post awayPost = Post.builder()
                    .content(match.getHomeTeam().getShortName() + " " + requestDTO.homeScore() + " : " + requestDTO.awayScore() + " " + match.getAwayTeam().getShortName())
                    .writer(admin)
                    .communityId(match.getAwayTeam().getId())
                    .build();

            postRepository.save(awayPost);

            Vote awayVote = Vote.builder()
                    .title("직접 뽑는 오늘의 MVP")
                    .endTime(LocalDateTime.now().plusHours(2))
                    .match(match)
                    .post(awayPost)
                    .build();

            voteRepository.save(awayVote);

            requestDTO.awayPlayers().forEach(player -> {
                VoteOption voteOption = VoteOption.builder()
                        .name(player.koreanName())
                        .communityId(player.id())
                        .image(player.image())
                        .backgroundImage(player.backgroundImage())
                        .vote(awayVote)
                        .build();

                voteOptionRepository.save(voteOption);
            });

            Team homeTeam = match.getHomeTeam();
            Team awayTeam = match.getAwayTeam();

            List<User> homeFans = userRepository.findByTeamId(homeTeam.getId());
            for(User user : homeFans) {
                for(DeviceToken deviceToken: user.getDeviceTokens()){
                    Boolean isTeamFan = fanRepository.existsByCommunityIdAndUser(homeTeam.getId(), user);
                    if(isTeamFan) {
                        fcmService.sendMatchEndPostMessageTo(deviceToken.getToken(), homeTeam.getKoreanName(), "[경기 종료]\n" + homeTeam.getShortName() + " " + requestDTO.homeScore() + " : " + requestDTO.awayScore()  + " " + awayTeam.getShortName() + "\n지금 바로 MVP를 뽑아보세요!", homePost.getId());
                    } else {
                        fcmService.sendMatchEndCommunityMessageTo(deviceToken.getToken(), homeTeam.getKoreanName(), "[경기 종료]\n" + homeTeam.getShortName() + " " + requestDTO.homeScore() + " : " + requestDTO.awayScore()  + " " + awayTeam.getShortName() + "\n지금 바로 MVP를 뽑아보세요!", homeTeam.getId());
                    }

                }
            }
            List<User> awayFans = userRepository.findByTeamId(awayTeam.getId());
            for(User user : awayFans) {
                for(DeviceToken deviceToken: user.getDeviceTokens()){
                    Boolean isTeamFan = fanRepository.existsByCommunityIdAndUser(awayTeam.getId(), user);
                    if(isTeamFan){
                        fcmService.sendMatchEndPostMessageTo(deviceToken.getToken(), awayTeam.getKoreanName(), "[경기 종료]\n" + homeTeam.getShortName() + " " + requestDTO.homeScore() + " : " + requestDTO.awayScore()  + " " + awayTeam.getShortName() + "\n지금 바로 MVP를 뽑아보세요!", awayPost.getId());
                    } else {
                        fcmService.sendMatchEndCommunityMessageTo(deviceToken.getToken(), awayTeam.getKoreanName(), "[경기 종료]\n" + homeTeam.getShortName() + " " + requestDTO.homeScore() + " : " + requestDTO.awayScore()  + " " + awayTeam.getShortName() + "\n지금 바로 MVP를 뽑아보세요!", awayTeam.getId());
                    }
                }
            }
        }
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
                        fcmService.sendMatchStartMessageTo(deviceToken.getToken(), homeTeam.getKoreanName(), "[30분 후] vs " + awayTeam.getShortName() + "\n응원의 메세지를 남겨보세요!", match.getId(), homeTeam.getId());
                    } else {
                        Pageable pageable = PageRequest.of(0, 1);
                        Page<Fan> firstFan = fanRepository.findByFirstTeamIdAndUser(homeTeam.getId(), user, pageable);
                        fcmService.sendMatchStartMessageTo(deviceToken.getToken(), homeTeam.getKoreanName(), "[30분 후] vs " + awayTeam.getShortName() + "\n응원의 메세지를 남겨보세요!", match.getId(), firstFan.getContent().get(0).getCommunityId());
                    }

                }
            }
            List<User> awayFans = userRepository.findByTeamId(awayTeam.getId());
            for(User user : awayFans) {
                for(DeviceToken deviceToken: user.getDeviceTokens()){
                    Boolean isTeamFan = fanRepository.existsByCommunityIdAndUser(awayTeam.getId(), user);
                    if(isTeamFan){
                        fcmService.sendMatchStartMessageTo(deviceToken.getToken(), awayTeam.getKoreanName(), "[30분 후] vs " + homeTeam.getShortName() + "\n응원의 메세지를 남겨보세요!", match.getId(), awayTeam.getId());
                    } else {
                        Pageable pageable = PageRequest.of(0, 1);
                        Page<Fan> firstFan = fanRepository.findByFirstTeamIdAndUser(awayTeam.getId(), user, pageable);
                        fcmService.sendMatchStartMessageTo(deviceToken.getToken(), awayTeam.getKoreanName(), "[30분 후] vs " + homeTeam.getShortName() + "\n응원의 메세지를 남겨보세요!", match.getId(), firstFan.getContent().get(0).getCommunityId());
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

    // 다음 2주 내 경기 목록
    public List<MatchResponse.MatchDetailDTO> getTwoWeeksMatches(Long communityId, User user) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoWeeksLater = now.plusWeeks(2);

        Optional<Team> team = teamRepository.findById(communityId);
        Optional<Player> player = playerRepository.findById(communityId);

        if (team.isPresent()) {
            List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(team.get(), now, twoWeeksLater);
            return matches.stream()
                    .map(match -> new MatchResponse.MatchDetailDTO(match, null, null))
                    .toList();
        }
        if (player.isPresent()) {
            List<Match> matches = matchRepository.findByHomeTeamOrAwayTeam(player.get().getFirstTeam(), now, twoWeeksLater);
            return matches.stream()
                    .map(match -> new MatchResponse.MatchDetailDTO(match, null, null))
                    .toList();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<MatchResponse.MatchDetailDTO> getMatchesByDate(User user, Integer year, Integer month, Integer day) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);

        List<Fan> fans = fanRepository.findFansByUser(user);

        List<Long> teamCommunityIds = fans.stream()
                .map(Fan::getCommunityId)
                .filter(teamRepository::existsById)
                .toList();

        List<Long> playerCommunityIds = fans.stream()
                .map(Fan::getCommunityId)
                .filter(playerRepository::existsById)
                .toList();

        Map<Long, Long> playerToTeamMap = playerRepository.findAllById(playerCommunityIds).stream()
                .collect(Collectors.toMap(Player::getId, player -> player.getFirstTeam().getId()));

        List<Long> allCommunityIds = new ArrayList<>(teamCommunityIds);
        allCommunityIds.addAll(playerToTeamMap.values());

        List<Match> matches = matchRepository.findMatchesByCommunityIdsAndTimeRange(allCommunityIds, startOfDay, endOfDay);

        List<MeetFanRole> roles = Arrays.asList(MeetFanRole.MANAGER, MeetFanRole.MEMBER);

        Map<Long, List<Long>> matchToCommunityMap = new HashMap<>();

        matches.forEach(match -> {
            Long matchId = match.getId();

            List<Long> relatedCommunityIds = new ArrayList<>();

            if (teamCommunityIds.contains(match.getHomeTeam().getId())) {
                relatedCommunityIds.add(match.getHomeTeam().getId());
            }
            if (teamCommunityIds.contains(match.getAwayTeam().getId())) {
                relatedCommunityIds.add(match.getAwayTeam().getId());
            }

            playerToTeamMap.forEach((playerId, firstTeamId) -> {
                if (firstTeamId.equals(match.getHomeTeam().getId()) || firstTeamId.equals(match.getAwayTeam().getId())) {
                    relatedCommunityIds.add(playerId);
                }
            });

            matchToCommunityMap.put(matchId, relatedCommunityIds);
        });
        return matches.stream()
                .map(match -> {
                    Meet meet = meetRepository.findByMatchAndUserWithRoles(match.getId(), user, roles);
                    MeetResponse.MeetInfoDTO meetInfoDTO = null;

                    if (meet != null) {
                        int currentCount = meetFanRepository.countByMeet(meet);
                        meetInfoDTO = new MeetResponse.MeetInfoDTO(meet, currentCount, null, null, null);
                    }

                    List<Long> relatedCommunityIds = matchToCommunityMap.get(match.getId());

                    return new MatchResponse.MatchDetailDTO(match, meetInfoDTO, relatedCommunityIds);
                })
                .toList();
    }
}
