package com.cheering.meet;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.Chat;
import com.cheering.chat.ChatRepository;
import com.cheering.chat.ChatResponse;
import com.cheering.chat.ChatType;
import com.cheering.chat.chatRoom.*;
import com.cheering.chat.session.ChatSession;
import com.cheering.chat.session.ChatSessionRepository;
import com.cheering.fan.CommunityType;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.match.Match;
import com.cheering.match.MatchRepository;
import com.cheering.match.MatchResponse;
import com.cheering.matchRestriction.MatchRestriction;
import com.cheering.matchRestriction.MatchRestrictionRepository;
import com.cheering.meetfan.MeetFan;
import com.cheering.meetfan.MeetFanRepository;
import com.cheering.meetfan.MeetFanRole;
import com.cheering.notification.Fcm.FcmServiceImpl;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.Gender;
import com.cheering.user.User;
import com.cheering.user.deviceToken.DeviceToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.cheering.meet.MeetRequest.TicketOption.HAS;
import static com.cheering.meet.MeetRequest.TicketOption.NOT;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final MatchRepository matchRepository;
    private final MatchRestrictionRepository matchRestrictionRepository;
    private final FanRepository fanRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MeetFanRepository meetFanRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatRepository chatRepository;
    private final FcmServiceImpl fcmService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public MeetResponse.MeetIdDTO createMeet(Long communityId, MeetRequest.CreateMeetDTO requestDto, User user) {
        validateParticipation(requestDto.matchId(), user);

        Fan fan = fanRepository.findByCommunityIdAndUser(communityId, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Match match = matchRepository.findById(requestDto.matchId())
                .orElseThrow(() -> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        if (!isMatchRelatedToCommunity(match, communityId)) {
            throw new CustomException(ExceptionCode.MATCH_NOT_RELATED_TO_COMMUNITY);
        }

        boolean MatchDuplicatedMeet = checkExistingMeet(match.getId(), user);
        if(MatchDuplicatedMeet) {
            throw new CustomException(ExceptionCode.DUPLICATE_MEET);
        }

        Boolean hasTicket = null;

        if (requestDto.type() == MeetType.LIVE) {
            if (requestDto.hasTicket() == null) {
                throw new CustomException(ExceptionCode.HAS_TICKET_REQUIRED_FOR_LIVE);
            }
            hasTicket = requestDto.hasTicket();
        } else if (requestDto.type() == MeetType.BOOKING) {
            hasTicket = null;
        }

        // Meet 생성
        Meet meet = Meet.builder()
                .communityId(communityId)
                .communityType(requestDto.communityType())
                .title(requestDto.title())
                .manager(fan)
                .description(requestDto.description())
                .max(requestDto.max())
                .gender(requestDto.gender())
                .ageMin(requestDto.ageMin())
                .ageMax(requestDto.ageMax())
                .place(requestDto.place())
                .type(requestDto.type())
                .hasTicket(hasTicket)
                .match(match)
                .build();

        meetRepository.save(meet);

        chatRoomService.createConfirmedChatRoom(communityId, meet, requestDto.max(), user);
        MeetFan meetFan = MeetFan.builder()
                .role(MeetFanRole.MANAGER)
                .meet(meet)
                .fan(fan)
                .build();

        meetFanRepository.save(meetFan);

        return new MeetResponse.MeetIdDTO(meet.getId());
    }

    @Transactional(readOnly = true)
    public boolean checkExistingMeet(Long matchId, User user) {
        // 동일한 경기 ID와 사용자의 모임이 있는지 확인
        return meetRepository.existsByMatchAndMeetFansFanUser(matchId, user);
    }

    @Transactional(readOnly = true)
    public MeetResponse.MeetDetailDTO getMeetDetail(Long meetId, User user) {

        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        boolean isMember = meetFanRepository.existsByMeetAndFanUserAndRole(meet, user, MeetFanRole.MEMBER) || meetFanRepository.existsByMeetAndFanUserAndRole(meet, user, MeetFanRole.MANAGER);

        MeetFan managerFan = meetFanRepository.findByMeetAndRole(meet, MeetFanRole.MANAGER)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        int currentCount = meetFanRepository.countByMeet(meet);

        Fan writer = managerFan.getFan();

        boolean isManager = managerFan.getFan().getUser().getId().equals(user.getId());

        // 사용자의 참여 여부 확인
        boolean isUserParticipating = meetFanRepository.existsByMeetAndFanUser(meet, user);

        ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meetId, ChatRoomType.CONFIRM).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        ChatRoomResponse.ChatRoomDTO chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                confirmChatRoom,
                currentCount,
                isUserParticipating
        );

        MatchResponse.MatchDetailDTO matchDetailDTO = null;
        if (meet.getMatch() != null) {
            Match match = meet.getMatch();

            MeetStatus status;
            Team team;
            if (meet.getManager().getUser().equals(user)) {
                status = MeetStatus.MANAGER; // 내가 만든 모임
            } else if (meetFanRepository.existsByMeetAndFanUser(meet, user)) {
                status = MeetStatus.CONFIRMED; // 확정된 모임
            } else {
                status = MeetStatus.APPLIED; // 1:1 대화 중인 상태
            }
            Optional<Team> optionalTeam = teamRepository.findById(meet.getCommunityId());
            Optional<Player> optionalPlayer = playerRepository.findById(meet.getCommunityId());

            if (optionalTeam.isPresent()) {
                team = optionalTeam.get();
            } else {
                Player player = optionalPlayer.get();
                team = player.getFirstTeam();
            }

            MeetResponse.MeetInfoDTO meetInfoDTO = new MeetResponse.MeetInfoDTO(
                    meet,
                    currentCount,
                    chatRoomDTO,
                    team,
                    status
            );
            matchDetailDTO = new MatchResponse.MatchDetailDTO(match, meetInfoDTO);
        }

        int currentYear = java.time.Year.now().getValue();
        Long privateChatRoomId = null;

        if(!isManager) {
            privateChatRoomId = chatRoomRepository.findPrivateChatRoomByMeetIdAndUser(meetId, user)
                    .orElseThrow(()-> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
        }

        return new MeetResponse.MeetDetailDTO(
                meetId,
                meet.getTitle(),
                meet.getDescription(),
                meet.getType(),
                chatRoomDTO,
                currentCount,
                meet.getMax(),
                meet.getHasTicket(),
                meet.getGender(),
                meet.getAgeMin(),
                meet.getAgeMax(),
                new MeetResponse.MeetDetailDTO.MeetWriterDTO(
                        writer.getId(),
                        currentYear - writer.getUser().getAge() + 1,
                        writer.getUser().getGender()
                ),
                matchDetailDTO,
                meet.getPlace(),
                isManager,
                isMember,
                privateChatRoomId
        );
    }

    @Transactional(readOnly = true)
    public MeetResponse.MeetListDTO findAllMeetsByCommunity(MeetRequest.MeetSearchRequest request, Long communityId, User user) {

        Optional<Team> optionalTeam = teamRepository.findById(communityId);
        Optional<Player> optionalPlayer = playerRepository.findById(communityId);

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Boolean hasTicket = null;
        if (request.getTicketOption() == HAS) {
            hasTicket = true;
        } else if (request.getTicketOption() == NOT) {
            hasTicket = false;
        }

        List<MeetGender> genders = new ArrayList<>();
        if (request.getGender() == null || request.getGender() == MeetGender.ANY) {
            genders.add(MeetGender.MALE);
            genders.add(MeetGender.FEMALE);
            genders.add(MeetGender.ANY);
        } else {
            genders.add(request.getGender());
            genders.add(MeetGender.ANY);
        }

        String keyword = request.getKeyword();
        Page<Meet> meetPage = meetRepository.findByFilters(
                communityId,
                keyword,
                request.getType(),
                genders,
                request.getMinAge(),
                request.getMaxAge(),
                request.getMatchId(),
                hasTicket,
                pageRequest
        );

        List<MeetResponse.MeetInfoDTO> meetInfoDTOs = meetPage.getContent().stream()
                .map(meet -> {
                    int currentCount = calculateCurrentCount(meet.getId());

                    // 사용자의 참여 여부 확인
                    boolean isUserParticipating = meetFanRepository.existsByMeetAndFanUser(meet, user);

                    MeetStatus status;
                    if (meet.getManager().getUser().equals(user)) {
                        status = MeetStatus.MANAGER; // 내가 만든 모임
                    } else if (meetFanRepository.existsByMeetAndFanUser(meet, user)) {
                        status = MeetStatus.CONFIRMED; // 확정된 모임
                    } else {
                        status = MeetStatus.APPLIED; // 1:1 대화 중인 상태
                    }
                    ChatRoomResponse.ChatRoomDTO chatRoomDTO = null;
                    ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
                    chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                            confirmChatRoom,
                            currentCount,
                            isUserParticipating
                    );
                    if (optionalTeam.isPresent()) {
                        Team curTeam = optionalTeam.get();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, curTeam, status);
                    } else {
                        Player player = optionalPlayer.get();
                        Team firstTeam = player.getFirstTeam();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, firstTeam, status);
                    }
                })
                .collect(Collectors.toList());

        return new MeetResponse.MeetListDTO(meetPage, meetInfoDTOs);
    }


    private boolean isMatchRelatedToCommunity(Match match, Long communityId) {
        return match.getHomeTeam().getId().equals(communityId) ||
                match.getAwayTeam().getId().equals(communityId);
    }

    public int calculateCurrentCount(Long meetId) {
        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        // 현재 참가자 수 계산
        int currentParticipants = meetFanRepository.countByMeet(meet);

        return currentParticipants;
    }

    @Transactional
    public void deleteMeet(Long meetId, User user) {

        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        MeetFan managerFan = meetFanRepository.findByMeetAndRole(meet, MeetFanRole.MANAGER)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_FORBIDDEN));

        if (!managerFan.getFan().getUser().getId().equals(user.getId())) {
            throw new CustomException(ExceptionCode.USER_FORBIDDEN);
        }

        Match match = meet.getMatch();
        // 현재 날짜와 경기를 비교
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime matchTime = match.getTime();
        boolean isMatchWithinTwoDays = now.isAfter(matchTime.minusDays(2));

        // 이틀보다 가까우면 제한 추가
        if (isMatchWithinTwoDays) {
            // 이미 제한 있는지 확인
            validateParticipation(match.getId(), user);
            MatchRestriction restriction = MatchRestriction.builder()
                    .user(user)
                    .match(match)
                    .build();
            matchRestrictionRepository.save(restriction); // 새로운 제한 저장
        }

        // 알림 전송
        List<MeetFan> meetFans = meetFanRepository.findByFanUserOrderByRoleExcludingLeft(user, MeetFanRole.LEFT);
        for (MeetFan meetFan : meetFans) {
            User fanUser = meetFan.getFan().getUser();
            for (DeviceToken deviceToken : fanUser.getDeviceTokens()) {
                fcmService.sendMeetDeleteMessageTo(
                        deviceToken.getToken(),
                        meet.getTitle(),
                        meet.getTitle() + " 모임이 삭제되었습니다.",
                        meetId,
                        meet.getCommunityId()
                );
            }
        }

        meetRepository.delete(meet);
    }

    // 모임 탈퇴
    @Transactional
    public void leaveMeet(Long meetId, User user) {
        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        MeetFan meetFan = meetFanRepository.findByMeetAndUserAndRole(meet, user, MeetFanRole.MEMBER)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        // 관리자는 탈퇴 X -> 삭제만
        if (meetFan.getRole() == MeetFanRole.MANAGER) {
            throw new CustomException(ExceptionCode.USER_FORBIDDEN);
        }

        Match match = meet.getMatch();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime matchTime = match.getTime();
        boolean isMatchWithinTwoDays = now.isAfter(matchTime.minusDays(2));

        // 이틀보다 가까우면 제한 추가
        if (isMatchWithinTwoDays) {
            // 이미 제한 있는지 확인
            validateParticipation(match.getId(), user);
            MatchRestriction restriction = MatchRestriction.builder()
                    .user(user)
                    .match(match)
                    .build();
            matchRestrictionRepository.save(restriction); // 새로운 제한 저장
        }

        // 참가 정보 삭제
        meetFan.setRole(MeetFanRole.LEFT);
    }

    @Transactional
    public void updateMeet(Long meetId, MeetRequest.UpdateMeetDTO updateMeetDTO, User user) {

        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        MeetFan managerFan = meetFanRepository.findByMeetAndRole(meet, MeetFanRole.MANAGER)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_FORBIDDEN));

        if (!managerFan.getFan().getUser().getId().equals(user.getId())) {
            throw new CustomException(ExceptionCode.USER_FORBIDDEN);
        }

        meet.setTitle(updateMeetDTO.title());
        meet.setDescription(updateMeetDTO.description());

        meetRepository.save(meet);
    }

    // 경기 제한 검증
    public void validateParticipation(Long matchId, User user) {
        // 경기 제한 여부 확인
        boolean isRestricted = matchRestrictionRepository.existsByMatchIdAndUser(matchId, user);
        if (isRestricted) {
            throw new CustomException(ExceptionCode.USER_RESTRICTED_FOR_MATCH);
        }
    }

    // 참여 중인 모든 모임 조회하기
    @Transactional(readOnly = true)
    public MeetResponse.MeetListDTO findAllMyMeets(MeetRequest.MeetSearchRequest request, User user) {

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

        // 요청한 사용자가 참여 중인 모든 모임 조회 (탈퇴 제외)
        Page<MeetFan> meetFanPage = meetFanRepository.findByFanUserOrderByMatchTimeExcludingLeft(user, MeetFanRole.LEFT, pageRequest);

        List<MeetResponse.MeetInfoDTO> meetInfoDTOs = meetFanPage.getContent().stream()
                .map(meetFan -> {
                    Meet meet = meetFan.getMeet();

                    int currentCount = calculateCurrentCount(meet.getId());

                    MeetStatus status;
                    if (meet.getManager().getUser().equals(user)) {
                        status = MeetStatus.MANAGER; // 내가 만든 모임
                    } else if (meetFanRepository.existsByMeetAndFanUser(meet, user)) {
                        status = MeetStatus.CONFIRMED; // 확정된 모임
                    } else {
                        status = MeetStatus.APPLIED; // 1:1 대화 중인 상태
                    }

                    ChatRoomResponse.ChatRoomDTO chatRoomDTO = null;
                    ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM)
                            .orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
                    chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                            confirmChatRoom,
                            currentCount,
                            true
                    );

                    if (meet.getCommunityType() == CommunityType.TEAM) {
                        Team curTeam = teamRepository.findById(meet.getCommunityId())
                                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, curTeam, status);
                    } else {
                        Player player = playerRepository.findById(meet.getCommunityId())
                                .orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
                        Team firstTeam = player.getFirstTeam();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, firstTeam, status);
                    }
                })
                .collect(Collectors.toList());

        return new MeetResponse.MeetListDTO(meetFanPage, meetInfoDTOs);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAllMembersByMeet(Long meetId, User user) {
        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        MeetFan managerFan = meetFanRepository.findByMeetAndRole(meet, MeetFanRole.MANAGER)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        boolean isManager = managerFan.getFan().getUser().getId().equals(user.getId());

        // Retrieve all members
        List<MeetFan> members = meetFanRepository.findByMeetAndRoleIsManagerOrMember(meet)
                .stream()
                .sorted(Comparator.comparing(MeetFan::getCreatedAt))
                .collect(Collectors.toList());

        List<MeetFan> leavedMembers = isManager ?
                meetFanRepository.findLeavedMeetFansByMeet(meet)
                        .stream()
                        .sorted(Comparator.comparing(MeetFan::getCreatedAt))
                        .collect(Collectors.toList())
                : Collections.emptyList();

        // Group the results
        List<Map<String, Object>> response = new ArrayList<>();

        // Add MEMBERS section
        Map<String, Object> membersSection = new HashMap<>();
        membersSection.put("title", "MEMBERS");
        membersSection.put("data", members.stream().map(MeetResponse.MeetMemberDTO::new).collect(Collectors.toList()));
        response.add(membersSection);

        // Add LEAVED section
        Map<String, Object> leavedSection = new HashMap<>();
        leavedSection.put("title", "LEAVED");
        leavedSection.put("data", leavedMembers.stream().map(MeetResponse.MeetMemberDTO::new).collect(Collectors.toList()));
        response.add(leavedSection);

        return response;
    }


    @Transactional(readOnly = true)
    public MeetResponse.MeetListDTO findMyConfirmedMeetsInCommunity(MeetRequest.MeetSearchRequest request, Long communityId, User user) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

        // 요청한 커뮤니티에서 사용자의 참여 확정된 모임 조회
        Page<Meet> meetPage = meetRepository.findConfirmedMeetsByCommunityIdAndRole(communityId, user, pageRequest);

        Optional<Team> optionalTeam = teamRepository.findById(communityId);
        Optional<Player> optionalPlayer = playerRepository.findById(communityId);

        List<MeetResponse.MeetInfoDTO> meetInfoDTOs = meetPage.getContent().stream()
                .map(meet -> {
                    int currentCount = calculateCurrentCount(meet.getId());

                    MeetStatus status;
                    if (meet.getManager().getUser().equals(user)) {
                        status = MeetStatus.MANAGER; // 내가 만든 모임
                    } else if (meetFanRepository.existsByMeetAndFanUser(meet, user)) {
                        status = MeetStatus.CONFIRMED; // 확정된 모임
                    } else {
                        status = MeetStatus.APPLIED; // 1:1 대화 중인 상태
                    }

                    // 사용자의 참여 여부 확인
                    boolean isUserParticipating = meetFanRepository.existsByMeetAndFanUser(meet, user);

                    ChatRoomResponse.ChatRoomDTO chatRoomDTO = null;
                    ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
                    chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                            confirmChatRoom,
                            currentCount,
                            isUserParticipating
                    );
                    if (optionalTeam.isPresent()) {
                        Team curTeam = optionalTeam.get();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, curTeam, status);
                    } else {
                        Player player = optionalPlayer.get();
                        Team firstTeam = player.getFirstTeam();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, firstTeam, status);
                    }
                })
                .collect(Collectors.toList());

        return new MeetResponse.MeetListDTO(meetPage, meetInfoDTOs);
    }


    @Transactional(readOnly = true)
    public MeetResponse.MeetSectionResponse findAllMyMeetsWithPrivateChats(MeetRequest.MyMeetListRequest request, Long communityId, User user) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

        // 커뮤니티의 모임 조회
        Page<Meet> meets;

        if (request.getPastFiltering()) {
            // 과거 모임만 필터링
            meets = meetRepository.findPastMeetsByCommunityAndUser(communityId, user, pageRequest);
        } else {
            List<MeetFanRole> roles = List.of(MeetFanRole.MANAGER, MeetFanRole.MEMBER, MeetFanRole.APPLIER);
            meets = meetRepository.findFutureMeetsByCommunityAndUser(communityId, user, roles, pageRequest);
        }

        Fan fan = fanRepository.findByCommunityIdAndUser(communityId, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        // 커뮤니티 타입 확인 (Team 또는 Player)
        Optional<Team> optionalTeam = teamRepository.findById(communityId);
        Optional<Player> optionalPlayer = playerRepository.findById(communityId);

        // Meet -> MeetInfoDTO 변환
        List<MeetResponse.MeetInfoDTO> meetInfoDTOs = meets.getContent().stream()
                .map(meet -> {
                    int currentCount = calculateCurrentCount(meet.getId());

                    // 모임 상태 결정
                    MeetStatus status;
                    if (meet.getManager().getUser().getId().equals(user.getId())) {
                        status = MeetStatus.MANAGER; // 내가 만든 모임
                    } else if (meetFanRepository.existsByMeetAndFanUserAndRole(meet, user, MeetFanRole.MEMBER)) {
                        status = MeetStatus.CONFIRMED; // 확정된 모임
                    } else {
                        status = MeetStatus.APPLIED; // 1:1 대화 중인 상태
                    }

                    // 채팅방 정보 생성
                    ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM)
                            .orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
                    ChatRoomResponse.ChatRoomDTO chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                            confirmChatRoom,
                            currentCount,
                            true
                    );

                    if (optionalTeam.isPresent()) {
                        Team curTeam = optionalTeam.get();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, curTeam, status);
                    } else {
                        Player player = optionalPlayer.orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
                        Team firstTeam = player.getFirstTeam();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, firstTeam, status);
                    }
                })
                .collect(Collectors.toList());

        // 날짜별 그룹화 (LinkedHashMap으로 정렬 유지)
        Map<String, List<MeetResponse.MeetInfoDTO>> groupedByDate = meetInfoDTOs.stream()
                .collect(Collectors.groupingBy(
                        dto -> dto.match().time().toLocalDate().format(
                                DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREA)
                        ),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<MeetResponse.MeetSectionDTO> sections = groupedByDate.entrySet().stream()
                .map(entry -> new MeetResponse.MeetSectionDTO(entry.getKey(), entry.getValue()))
                .toList();

        return new MeetResponse.MeetSectionResponse(
                sections,
                meets.getNumber(),
                meets.getSize(),
                meets.getTotalElements(),
                meets.getTotalPages(),
                meets.isLast(),
                meets.hasNext()
        );
    }

    @Transactional
    public void joinAsApplier(Long chatRoomId, Long fanId) {

        Fan fan = fanRepository.findById(fanId)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        Match match = chatRoom.getMeet().getMatch();
        validateParticipation(match.getId(), fan.getUser());
        boolean alreadyApplier = meetFanRepository.existsByMeetAndFanUser(chatRoom.getMeet(), fan.getUser());

        Meet meet = chatRoom.getMeet();
        Fan manager = meet.getManager();

        if (alreadyApplier) {
            throw new CustomException(ExceptionCode.ALREADY_APPLIED);
        }

        MeetFan meetFan = MeetFan.builder()
                .meet(chatRoom.getMeet())
                .fan(fan)
                .role(MeetFanRole.APPLIER)
                .build();

        meetFanRepository.save(meetFan);

        User managerUser = manager.getUser();
        for (DeviceToken deviceToken : managerUser.getDeviceTokens()) {
            fcmService.sendMeetNewAppliedMessageTo(
                    deviceToken.getToken(),
                    meet.getTitle(),
                    meet.getTitle() + " 모임에 새로운 신청이 있습니다.",
                    meet.getId(),
                    meet.getCommunityId()
            );
        }

    }

    public FanResponse.FanDTO getMeetProfile(Long communityId, User user) {
        Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        return new FanResponse.FanDTO(curFan.getId(), curFan.getType(), curFan.getMeetName(), curFan.getMeetImage());
    }

    public MeetGender genderMapper(Gender gender) {
        return MeetGender.valueOf(gender.name());
    }

    @Transactional(readOnly = true)
    public List<MeetResponse.MeetInfoDTO> getRandomMeetsByConditions(Long communityId, User user) {
        // 커뮤니티 타입 확인 (Team 또는 Player)
        Optional<Team> optionalTeam = teamRepository.findById(communityId);
        Optional<Player> optionalPlayer = playerRepository.findById(communityId);
        List<Meet> meets;

        if (user.getAge() == null || user.getGender() == null) {
            meets = meetRepository.findMeetsByConditionsWithoutProfile(communityId, PageRequest.of(0,50));
        } else{
            // 현재 나이 계산
            int currentYear = java.time.Year.now().getValue();
            int currentAge = currentYear - user.getAge() + 1;
            MeetGender meetGender = genderMapper(user.getGender());

            meets = meetRepository.findMeetsByConditions(
                    communityId,
                    currentAge,
                    meetGender,
                    user,
                    PageRequest.of(0, 50)
            );
        }

        if (meets.isEmpty()) {
            return Collections.emptyList();
        }

        // 랜덤으로 5개 선택
        Collections.shuffle(meets);
        List<Meet> randomMeets = meets.stream().limit(5).collect(Collectors.toList());

        List<MeetResponse.MeetInfoDTO> meetInfoDTOs = randomMeets.stream()
                .map(meet -> {
                    int currentCount = calculateCurrentCount(meet.getId());

                    // 모임 상태 결정
                    MeetStatus status = null;

                    // 채팅방 정보 생성
                    ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM)
                            .orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
                    ChatRoomResponse.ChatRoomDTO chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                            confirmChatRoom,
                            currentCount,
                            null
                    );

                    if (optionalTeam.isPresent()) {
                        Team curTeam = optionalTeam.get();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, curTeam, status);
                    } else {
                        Player player = optionalPlayer.orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
                        Team firstTeam = player.getFirstTeam();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, firstTeam, status);
                    }
                })
                .collect(Collectors.toList());
        return meetInfoDTOs;
    }

    @Transactional(readOnly = true)
    public List<MeetResponse.MeetInfoDTO> findClosestMeets(Long communityId, User user) {

        List<MeetFanRole> roles = List.of(MeetFanRole.MANAGER, MeetFanRole.MEMBER, MeetFanRole.APPLIER);
        List<Meet> meets = meetRepository.findClosestMeetsByUserAndRoles(communityId, user, roles);

        meets.stream().limit(5).collect(Collectors.toList());

        return meets.stream()
                .map(meet -> {
                    int currentCount = calculateCurrentCount(meet.getId());

                    MeetStatus status;
                    if (meet.getManager().getUser().equals(user)) {
                        status = MeetStatus.MANAGER; // 내가 만든 모임
                    } else if (meetFanRepository.existsByMeetAndFanUser(meet, user)) {
                        status = MeetStatus.CONFIRMED; // 확정된 모임
                    } else {
                        status = MeetStatus.APPLIED; // 1:1 대화 중인 상태
                    }

                    Optional<Team> optionalTeam = teamRepository.findById(meet.getCommunityId());
                    Optional<Player> optionalPlayer = playerRepository.findById(meet.getCommunityId());

                    ChatRoomResponse.ChatRoomDTO chatRoomDTO = null;
                    ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
                    chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                            confirmChatRoom,
                            currentCount,
                            true
                    );
                    if (optionalTeam.isPresent()) {
                        Team curTeam = optionalTeam.get();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, curTeam, status);
                    } else {
                        Player player = optionalPlayer.get();
                        Team firstTeam = player.getFirstTeam();
                        return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, firstTeam, status);
                    }
                })
                .toList();
    }

}
