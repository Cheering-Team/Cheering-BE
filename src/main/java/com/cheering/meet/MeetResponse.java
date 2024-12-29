package com.cheering.meet;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomResponse;
import com.cheering.match.Match;
import com.cheering.match.MatchResponse;
import com.cheering.fan.FanResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class MeetResponse {

    public record MeetIdDTO (Long id) { }

    // Meet 상세 정보 DTO
    public record MeetInfoDTO(Long id,
                              String title,
                              String description,
                              ChatRoomResponse.ChatRoomDTO chatRoomDTO,
                              Integer currentCount,
                              Integer max,
                              boolean hasTicket,
                              MeetGender gender,
                              Integer ageMin,
                              Integer ageMax,
                              MeetMatchDTO match
    ) {

        public MeetInfoDTO(Meet meet, Integer currentCount, ChatRoomResponse.ChatRoomDTO chatRoomDTO) { //currentCount
            this(
                    meet.getId(),
                    meet.getTitle(),
                    meet.getDescription(),
                    chatRoomDTO,
                    currentCount,
                    meet.getMax(),
                    meet.isHasTicket(),
                    meet.getGender(),
                    meet.getAgeMin(),
                    meet.getAgeMax(),
                    meet.getMatch() != null ? new MeetMatchDTO(meet.getMatch()) : null
            );
        }
    }

    // Match 정보 DTO
    public record MeetMatchDTO(Long id, String opponentImage, LocalDateTime time) {
        public MeetMatchDTO(Match match) {
            this(match.getId(),match.getAwayTeam().getImage(), match.getTime());
        }
    }

    // Meet 목록 반환 DTO
    public record MeetListDTO(List<MeetInfoDTO> meets, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public MeetListDTO(Page<?> page, List<MeetInfoDTO> meets) {
            this(meets, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }
    }

    // Meet 상세 정보 DTO
    public record MeetDetailDTO(
            String title,
            String description,
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
        public record MeetWriterDTO(Long id) { }
    }

}
