package com.cheering.meet;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.community.CommunityResponse;
import com.cheering.fan.CommunityType;
import com.cheering.match.Match;
import com.google.firebase.database.annotations.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class MeetRequest {
    public record CreateMeetDTO(
        @NotNull
        String title,

        String description,

        @NotNull
        Integer max,

        @NotNull
        MeetGender gender, // 성별 (MALE, FEMALE, ANY)

        Integer ageMin,

        Integer ageMax,

        String place,

        @NotNull
        MeetType type, // 모임 종류 (LIVE, BOOK)

        Boolean hasTicket,

        Long matchId,

        @NotNull
        CommunityType communityType

        //ChatRoom confirmChatRoom
    ) { }

    public record UpdateMeetDTO(
            @NotNull
            String title,

            String description,

            @NotNull
            Integer max,

            @NotNull
            MeetGender gender, // 성별 (MALE, FEMALE, ANY)

            Integer ageMin,

            Integer ageMax,

            String place,

            Boolean hasTicket
    ) { }

    @Getter
    @Setter
    public class MeetSearchRequest {

        private MeetType type;        // 모임 타입 (LIVE, BOOK)
        private MeetGender gender;    // 성별 (MALE, FEMALE, ANY)
        private Integer minAge;       // 최소 나이
        private Integer maxAge;       // 최대 나이
        private Long matchId;         // 매치 ID
        private TicketOption ticketOption = TicketOption.ALL;
        private String location;      // 장소 (nullable)
        private Integer page = 0;     // 페이지 번호 (기본값 0)
        private Integer size = 10;    // 페이지 크기 (기본값 10)
    }
    public enum TicketOption {
        ALL, HAS, NOT
    }

}
