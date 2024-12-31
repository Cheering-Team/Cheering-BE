package com.cheering.meet;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomResponse;
import com.cheering.match.Match;
import com.cheering.match.MatchResponse;
import com.cheering.fan.FanResponse;
import com.cheering.team.Team;
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
            MeetType meetType,
            ChatRoomResponse.ChatRoomDTO chatRoomDTO,
            Integer currentCount,
            Integer max,
            boolean hasTicket,
            MeetGender gender,
            Integer ageMin,
            Integer ageMax,
            MeetMatchDTO match
    ) {

        public MeetInfoDTO(Meet meet, Integer currentCount, ChatRoomResponse.ChatRoomDTO chatRoomDTO, Team curTeam) { //currentCount
            this(
                    meet.getId(),
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
                    meet.getMatch() != null ? new MeetMatchDTO(meet.getMatch(), curTeam) : null
            );
        }
    }

    // Match 정보 DTO
    @Schema(description = "모임 매치 정보")
    public record MeetMatchDTO(Long id, Boolean isHome, String opponentShortName, String opponentImage, LocalDateTime time) {
        public MeetMatchDTO(Match match, Team curTeam) {
            this(
                    match.getId(),
                    match.getHomeTeam().equals(curTeam),
                    match.getHomeTeam().equals(curTeam) ? match.getAwayTeam().getShortName() : match.getHomeTeam().getShortName(),
                    match.getHomeTeam().equals(curTeam) ? match.getAwayTeam().getImage() : match.getHomeTeam().getImage(),
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
            String title,
            String description,
            MeetType meetType,
            ChatRoomResponse.ChatRoomDTO chatRoomDTO,
            Integer currentCount,
            Integer max,
            boolean hasTicket,
            MeetGender gender,
            Integer minAge,
            Integer maxAge,
            MeetWriterDTO writer,
            MeetMatchDTO match
    ) {
        // 작성자 정보 DTO
        //public record MeetWriterDTO(Long id, int age, String gender) { }
        @Schema(description = "모임 작성자")
        public record MeetWriterDTO(Long id) { }
    }

}
