package com.cheering.meet;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.ApiUtils;
import com.cheering.chat.ChatResponse;
import com.cheering.chat.chatRoom.*;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.match.Match;
import com.cheering.match.MatchRepository;
import com.cheering.meet.MeetFan;
import com.cheering.meetfan.MeetFanRepository;
import com.cheering.meetfan.MeetFanRole;
import com.cheering.post.PostResponse;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cheering.meet.MeetRequest.TicketOption.HAS;
import static com.cheering.meet.MeetRequest.TicketOption.NOT;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final MatchRepository matchRepository;
    private final FanRepository fanRepository;
    private final TeamRepository teamRepository;
    private final MeetFanRepository meetFanRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;

    @Transactional
    public MeetResponse.MeetIdDTO createMeet(Long communityId, MeetRequest.CreateMeetDTO requestDto, User user) {

        Fan fan = fanRepository.findByCommunityIdAndUser(communityId, user)
                .orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        boolean alreadyExists = meetRepository.existsByMatchIdAndFanIdAsManager(requestDto.matchId(), fan.getId());
        if (alreadyExists) {
            throw new CustomException(ExceptionCode.DUPLICATE_MEET);
        }

        Match match = matchRepository.findById(requestDto.matchId())
                .orElseThrow(() -> new CustomException(ExceptionCode.MATCH_NOT_FOUND));

        // Match가 해당 커뮤니티와 관련이 있는지 확인
        if (!isMatchRelatedToCommunity(match, communityId)) {
            throw new CustomException(ExceptionCode.MATCH_NOT_RELATED_TO_COMMUNITY);
        }

        // Meet 생성
        Meet meet = Meet.builder()
                .communityId(communityId)
                .communityType(requestDto.communityType())
                .title(requestDto.title())
                .description(requestDto.description())
                .max(requestDto.max())
                .gender(requestDto.gender())
                .ageMin(requestDto.ageMin())
                .ageMax(requestDto.ageMax())
                .place(requestDto.place())
                .type(requestDto.type())
                .hasTicket(requestDto.hasTicket() != null && requestDto.hasTicket())
                .match(match)
                .chatRoom(null)
                .build();

        meetRepository.save(meet);

        ChatRoomResponse.IdDTO confirmedChatRoomIdDTO = chatRoomService.createConfirmedChatRoom(communityId, meet, requestDto.max(), user);

        ChatRoom confirmedChatRoom = chatRoomRepository.findById(confirmedChatRoomIdDTO.id())
                .orElseThrow(() -> new CustomException(ExceptionCode.CHATROOM_NOT_FOUND));

        meet.setChatRoom(confirmedChatRoom);
        meetRepository.save(meet);

        MeetFan meetFan = MeetFan.builder()
                .role(MeetFanRole.MANAGER)
                .meet(meet)
                .fan(fan)
                .build();

        meetFanRepository.save(meetFan);

        return new MeetResponse.MeetIdDTO(meet.getId());
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

        Team curTeam = teamRepository.findById(meet.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Team opponentTeam = meet.getMatch().getHomeTeam().equals(curTeam)
                ? meet.getMatch().getAwayTeam()
                : meet.getMatch().getHomeTeam();

        // 현재 참가자 수 계산
        int currentCount = meetFanRepository.countByMeet(meet);

        Fan writer = managerFan.getFan();

        ChatRoomResponse.ChatRoomDTO chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                meet.getChatRoom(),
                currentCount, // 현재 참가자 수
                true // 사용자 참여 여부를 true로 설정 (필요 시 로직으로 변경 가능)
        );

        return new MeetResponse.MeetDetailDTO(
                meet.getTitle(),
                meet.getDescription(),
                meet.getType(),
                chatRoomDTO,
                currentCount,
                meet.getMax(),
                meet.isHasTicket(),
                meet.getGender(),
                meet.getAgeMin(),
                meet.getAgeMax(),
                new MeetResponse.MeetDetailDTO.MeetWriterDTO(
                        writer.getId()
                        //writer.getAge(),
                        //writer.getGender().toString()
                ),
                meet.getMatch() != null
                        ? new MeetResponse.MeetMatchDTO(
                        meet.getMatch().getId(),
                        meet.getMatch().getHomeTeam().equals(curTeam),
                        opponentTeam.getShortName(),
                        opponentTeam.getImage(),
                        meet.getMatch().getTime()
                )
                        : null
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

        Page<Meet> meetPage = meetRepository.findByFilters(
                request.getType(),
                request.getGender(),
                request.getMinAge(),
                request.getMaxAge(),
                request.getMatchId(),
                hasTicket,
                request.getLocation(),
                pageRequest
        );

        List<MeetResponse.MeetInfoDTO> meetInfoDTOs = meetPage.getContent().stream()
                .map(meet -> {
                    int currentCount = calculateCurrentCount(meet.getId());
                    ChatRoomResponse.ChatRoomDTO chatRoomDTO = null;

                    if (meet.getChatRoom() != null) {
                        chatRoomDTO = new ChatRoomResponse.ChatRoomDTO(
                                meet.getChatRoom(),
                                currentCount,
                                true
                        );
                    }

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

        ChatRoom chatRoom = meet.getChatRoom();
        if (chatRoom != null) {
            chatRoomRepository.delete(chatRoom);
        }

        meetFanRepository.deleteByMeet(meet);

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

}
