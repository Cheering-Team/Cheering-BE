package com.cheering.meet;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomResponse;
import com.cheering.fan.Fan;
import com.cheering.match.Match;
import com.cheering.match.MatchResponse;
import com.cheering.fan.FanResponse;
import com.cheering.team.Team;
import com.cheering.user.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

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
            ChatRoomResponse.ChatRoomDTO chatRoom,
            Integer currentCount,
            Integer max,
            Boolean hasTicket,
            MeetGender gender,
            Integer ageMin,
            Integer ageMax,
            @Schema(description = "경기 상세 정보")
            MeetMatchDTO match,
            String place
    ) {

        public MeetInfoDTO(Meet meet, Integer currentCount, ChatRoomResponse.ChatRoomDTO chatRoom, Team curTeam) { //currentCount
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
                    meet.getMatch() != null ? new MeetMatchDTO(meet.getMatch(), curTeam) : null,
                    meet.getPlace().isEmpty() ? null : meet.getPlace()
            );
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
            Boolean isManager
    ) {

        public MeetDetailDTO(Meet meet, Integer currentCount, ChatRoomResponse.ChatRoomDTO chatRoom, MatchResponse.MatchDetailDTO matchDetailDTO, MeetWriterDTO writer, Boolean isManager) {
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
                    isManager
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

}
