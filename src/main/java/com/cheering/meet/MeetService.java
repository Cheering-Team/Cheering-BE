package com.cheering.meet;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.chatRoom.*;
import com.cheering.chat.session.ChatSession;
import com.cheering.chat.session.ChatSessionRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.match.Match;
import com.cheering.match.MatchRepository;
import com.cheering.match.MatchResponse;
import com.cheering.matchRestriction.MatchRestriction;
import com.cheering.matchRestriction.MatchRestrictionRepository;
import com.cheering.meetfan.MeetFan;
import com.cheering.meetfan.MeetFanRepository;
import com.cheering.meetfan.MeetFanRole;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    private final MeetFanRepository meetFanRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;
    private final ChatSessionRepository chatSessionRepository;

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

        //boolean alreadyExists = meetRepository.existsByMatchIdAndUser(requestDto.matchId(), user);
        //if (alreadyExists) {
        //    throw new CustomException(ExceptionCode.DUPLICATE_MEET);
        //}

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
        boolean isMember = fanRepository.existsByCommunityIdAndUser(meet.getCommunityId(), user);
        if (!isMember) {
            throw new CustomException(ExceptionCode.FAN_NOT_FOUND);
        }

        MeetFan managerFan = meetFanRepository.findByMeetAndRole(meet, MeetFanRole.MANAGER)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));
        Team curTeam = teamRepository.findById(meet.getCommunityId())
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        int currentCount = meetFanRepository.countByMeet(meet);

        Fan writer = managerFan.getFan();

        // 사용자의 참여 여부 확인
        boolean isUserParticipating = meetFanRepository.existsByMeetAndFanUser(meet, user);

        ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meetId, ChatRoomType.CONFIRM).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        ChatRoomResponse.ChatRoomDTO chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                confirmChatRoom,
                currentCount,
                isUserParticipating
        );

        MatchResponse.MatchDetailDTO matchDetailDTO = meet.getMatch() != null
                ? new MatchResponse.MatchDetailDTO(meet.getMatch())
                : null;

        return new MeetResponse.MeetDetailDTO(
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
                        writer.getId()
                        // writer.getAge(),
                        // writer.getGender().toString()
                ),
                matchDetailDTO,
                meet.getPlace()
        );
    }

    @Transactional(readOnly = true)
    public MeetResponse.MeetListDTO findAllMeetsByCommunity(MeetRequest.MeetSearchRequest request, Long communityId, User user) {
        boolean isMember = fanRepository.existsByCommunityIdAndUser(communityId, user);
        if (!isMember) {
            throw new CustomException(ExceptionCode.FAN_NOT_FOUND);
        }

        Team curTeam = teamRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

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

                    ChatRoomResponse.ChatRoomDTO chatRoomDTO = null;
                    ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));
                    chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                            confirmChatRoom,
                            currentCount,
                            isUserParticipating
                    );

                    return new MeetResponse.MeetInfoDTO(meet, currentCount, chatRoomDTO, curTeam);
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
        boolean isWithinTwoDays = now.isAfter(matchTime.minusDays(2));

        // 이틀 전이 아니면 제한 추가
        if (isWithinTwoDays) {
            MatchRestriction restriction = MatchRestriction.builder()
                    .user(user)
                    .match(match)
                    .build();
            matchRestrictionRepository.save(restriction); // 새로운 제한 저장
        }

        meetRepository.delete(meet);
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
        meet.setMax(updateMeetDTO.max());
        meet.setHasTicket(updateMeetDTO.hasTicket());
        meet.setGender(updateMeetDTO.gender());
        meet.setAgeMin(updateMeetDTO.ageMin());
        meet.setAgeMax(updateMeetDTO.ageMax());
        meet.setPlace(updateMeetDTO.place());

        meetRepository.save(meet);
    }

    // 확정 질문 수락 -> 모임 가입
    @Transactional
    public void acceptJoinRequest(Long chatRoomId, User user) {

        ChatRoom privateChatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        Meet meet = privateChatRoom.getMeet();
        if (meet == null) {
            throw new CustomException(ExceptionCode.MEET_NOT_FOUND);
        }

        validateParticipation(meet.getMatch().getId(), user);

        Fan userFan = fanRepository.findByCommunityIdAndUser(privateChatRoom.getCommunityId(), user)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        MeetFan meetFan = MeetFan.builder()
                .meet(meet)
                .fan(userFan)
                .role(MeetFanRole.MEMBER)
                .build();

        meetFanRepository.save(meetFan);

        ChatRoom confirmChatRoom = chatRoomRepository.findConfirmedChatRoomByMeetId(meet.getId(), ChatRoomType.CONFIRM).orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        String sessionId = UUID.randomUUID().toString();

        ChatSession chatSession = ChatSession.builder()
                .chatRoom(confirmChatRoom)
                .fan(userFan)
                .sessionId(sessionId)
                .build();

        chatSessionRepository.save(chatSession);
    }

    // 모임 참여 취소
    @Transactional
    public void cancelMeetParticipation(Long meetId, User user) {

        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEET_NOT_FOUND));

        Match match = meet.getMatch();
        if (match == null) {
            throw new CustomException(ExceptionCode.MATCH_NOT_FOUND);
        }

        MeetFan meetFan = meetFanRepository.findByMeetAndFanUser(meet, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        // 현재 날짜와 경기를 비교
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime matchTime = match.getTime();
        boolean isWithinTwoDays = now.isAfter(matchTime.minusDays(2));

        // 사용자가 이미 해당 경기에서 제한된 상태인지 확인
        boolean isRestricted = matchRestrictionRepository.existsByMatchIdAndUser(match.getId(), user);
        if (isRestricted) {
            throw new CustomException(ExceptionCode.USER_RESTRICTED_FOR_MATCH);
        }

        meetFanRepository.delete(meetFan);

        // 이틀 전이 아니면 제한 추가
        if (isWithinTwoDays) {
            MatchRestriction restriction = MatchRestriction.builder()
                    .user(user)
                    .match(match)
                    .build();
            matchRestrictionRepository.save(restriction); // 새로운 제한 저장
        }
    }

    // 경기 제한 검증
    @Transactional
    public void validateParticipation(Long matchId, User user) {
        // 경기 제한 여부 확인
        boolean isRestricted = matchRestrictionRepository.existsByMatchIdAndUser(matchId, user);
        if (isRestricted) {
            throw new CustomException(ExceptionCode.USER_RESTRICTED_FOR_MATCH);
        }
    }

}
