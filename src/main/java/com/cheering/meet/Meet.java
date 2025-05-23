package com.cheering.meet;

import com.cheering.BaseTimeEntity;
import com.cheering.fan.CommunityType;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.fan.Fan;
import com.cheering.match.Match;
import com.cheering.meetfan.MeetFan;
import com.cheering.notification.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @Enumerated(value = EnumType.STRING)
    private MeetType type;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Fan manager;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CommunityType communityType;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @Column
    private String place;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetGender gender;

    @Column(nullable = false)
    private Integer max;

    @Column
    private Integer ageMin;

    @Column
    private Integer ageMax;

    @Column
    private Boolean hasTicket;

    @OneToMany(mappedBy = "meet", cascade = CascadeType.REMOVE)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "meet", cascade = CascadeType.REMOVE)
    private List<MeetFan> meetFans = new ArrayList<>();

    @OneToMany(mappedBy = "meet", cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public Meet(MeetType type, String title, String description, Fan manager, CommunityType communityType, Long communityId, Match match,
                String place, MeetGender gender, Integer max, Integer ageMin, Integer ageMax, Boolean hasTicket) {

        this.type = type;
        this.title = title;
        this.manager = manager;
        this.description = description;
        this.communityType = communityType;
        this.communityId = communityId;
        this.match = match;
        this.place = place;
        this.gender = gender;
        this.max = max;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.hasTicket = hasTicket;
    }
}

