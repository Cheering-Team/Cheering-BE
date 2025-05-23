package com.cheering.meet;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomResponse;
import com.cheering.fan.Fan;
import com.cheering.match.Match;
import com.cheering.match.MatchResponse;
import com.cheering.fan.FanResponse;
import com.cheering.meetfan.MeetFan;
import com.cheering.meetfan.MeetFanRole;
import com.cheering.team.Team;
import com.cheering.user.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MeetResponse {

    @Schema(description = "모임 생성 응답")
    public record MeetIdDTO (@Schema(description = "생성된 모임 ID", example = "1") Long id) { }

    // Meet 상세 정보 DTO
    @Schema(description = "모임 상세 정보")
    public record MeetInfoDTO(
            Long id,
            String title,
            String description,
            MeetType type,
            MeetStatus status,
            ChatRoomResponse.ChatRoomDTO chatRoom,
            Integer currentCount,
            Integer max,
            Boolean hasTicket,
            MeetGender gender,
            Integer ageMin,
            Integer ageMax,
            @Schema(description = "경기 상세 정보")
            MeetMatchDTO match,
            String place,
            Long communityId
    ) {

        public MeetInfoDTO(Meet meet, Integer currentCount, ChatRoomResponse.ChatRoomDTO chatRoom, Team curTeam, MeetStatus status) { //currentCount
            this(
                    meet.getId(),
                    meet.getTitle(),
                    meet.getDescription(),
                    meet.getType(),
                    status,
                    chatRoom,
                    currentCount,
                    meet.getMax(),
                    meet.getHasTicket(),
                    meet.getGender(),
                    meet.getAgeMin(),
                    meet.getAgeMax(),
                    meet.getMatch() != null ? new MeetMatchDTO(meet.getMatch(), curTeam) : null,
                    meet.getPlace().isEmpty() ? null : meet.getPlace(),
                    meet.getCommunityId()
            );
        }

        public String getMatchDateFormatted() {
            if (match == null || match.time() == null) {
                return null;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREA);
            return match.time().format(formatter);
        }
    }

    // Match 정보 DTO
    @Schema(description = "모임 매치 정보")
    public record MeetMatchDTO(Long id, Boolean isHome, String opponentShortName, String opponentImage, String opponentColor, LocalDateTime time) {
        public MeetMatchDTO(Match match, Team curTeam) {
            this(
                    match.getId(),
                    match.getHomeTeam().equals(curTeam),
                    match.getHomeTeam().equals(curTeam) ? match.getAwayTeam().getShortName() : match.getHomeTeam().getShortName(),
                    match.getHomeTeam().equals(curTeam) ? match.getAwayTeam().getImage() : match.getHomeTeam().getImage(),
                    match.getHomeTeam().equals(curTeam) ? match.getAwayTeam().getColor() : match.getHomeTeam().getColor(),
                    match.getTime()
            );
        }
    }



    // Meet 목록 반환 DTO
    @Schema(description = "모임 목록")
    public record MeetListDTO(List<MeetInfoDTO> meets, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public MeetListDTO(Page<?> page, List<MeetInfoDTO> meets) {
            this(meets, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }
    }
    @Schema(description = "모임 섹션 기반 응답")
    public record MeetSectionResponse(List<MeetSectionDTO> meets, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public MeetSectionResponse(Page<?> page, List<MeetSectionDTO> meets) {
            this(meets, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }
    }

    @Schema(description = "모임 섹션")
    public record MeetSectionDTO(
            String title,
            List<MeetInfoDTO> data
    ) {}

    // Meet 상세 정보 DTO
    public record MeetDetailDTO(
            Long id,
            String title,
            String description,
            MeetType type,
            ChatRoomResponse.ChatRoomDTO chatRoom,
            Integer currentCount,
            Integer max,
            Boolean hasTicket,
            MeetGender gender,
            Integer minAge,
            Integer maxAge,
            MeetWriterDTO writer,
            MatchResponse.MatchDetailDTO match,
            String place,
            Boolean isManager,
            Boolean isMember,
            Long privateChatRoomId
    ) {

        public MeetDetailDTO(Meet meet, Integer currentCount, ChatRoomResponse.ChatRoomDTO chatRoom, MatchResponse.MatchDetailDTO matchDetailDTO, MeetWriterDTO writer, Boolean isManager, Boolean isMember, Long privateChatRoomId) {
            this(
                    meet.getId(),
                    meet.getTitle(),
                    meet.getDescription(),
                    meet.getType(),
                    chatRoom,
                    currentCount,
                    meet.getMax(),
                    meet.getHasTicket(),
                    meet.getGender(),
                    meet.getAgeMin(),
                    meet.getAgeMax(),
                    writer,
                    matchDetailDTO,
                    meet.getPlace().isEmpty() ? null : meet.getPlace(),
                    isManager,
                    isMember,
                    privateChatRoomId
            );
        }

        // 작성자 정보 DTO
        @Schema(description = "모임 작성자")
        public record MeetWriterDTO(
                Long id, // 작성자 ID
                Integer age, // 작성자 나이
                Gender gender // 작성자 성별
        ) {
            public MeetWriterDTO(Fan fan) {
                this(
                        fan.getId(),
                        fan.getUser().getAge(),
                        fan.getUser().getGender()
                );
            }
        }
    }

    public record MeetMemberDTO(
            Long meetFanId,
            Long userId,
            Long fanId,
            Integer userAge,
            Gender userGender,
            String role,
            String name,
            String image,
            Boolean isManager
    ) {
        public MeetMemberDTO(MeetFan meetFan) {
            this(
                    meetFan.getId(),
                    meetFan.getFan().getUser().getId(),
                    meetFan.getFan().getId(),
                    meetFan.getFan().getUser().getAge(),
                    meetFan.getFan().getUser().getGender(),
                    meetFan.getRole().toString(),
                    meetFan.getFan().getMeetName(),
                    meetFan.getFan().getMeetImage(),
                    meetFan.getRole() == MeetFanRole.MANAGER
            );
        }
    }

    public record MeetTitleDTO(
            Long id, // 모임 id
            String title
    ) {
        public MeetTitleDTO(Meet meet) {
            this(
                    meet.getId(),
                    meet.getTitle()
            );
        }
    }


}
