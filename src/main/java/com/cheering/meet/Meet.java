package com.cheering.meet;

import com.cheering.BaseTimeEntity;
import com.cheering.fan.CommunityType;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.match.Match;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meet_tb")
@RequiredArgsConstructor
@Getter
@Setter
public class Meet extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "meet_id")
    private Long id;

    @Column(nullable = false)
    private MeetType type;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CommunityType communityType;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) //
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Column
    private String place;

    @Column
    private LocalDateTime time;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetGender gender;

    @Column(nullable = false)
    private Integer max;

    @Column
    private Integer ageMin;

    @Column
    private Integer ageMax;

    @Column(nullable = false)
    private boolean hasTicket;


    @Builder
    public Meet(MeetType type, String title, String description, CommunityType communityType, Long communityId, Match match, ChatRoom chatRoom,
                String place, LocalDateTime time, MeetGender gender, Integer max, Integer ageMin, Integer ageMax, boolean hasTicket) {

        this.type = type;
        this.title = title;
        this.description = description;
        this.communityType = communityType;
        this.communityId = communityId;
        this.match = match;
        this.chatRoom = chatRoom;
        this.place = place;
        this.time = time;
        this.gender = gender;
        this.max = max;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.hasTicket = hasTicket;
    }
}

