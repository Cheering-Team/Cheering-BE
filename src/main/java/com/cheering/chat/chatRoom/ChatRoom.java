package com.cheering.chat.chatRoom;

import com.cheering.BaseTimeEntity;
import com.cheering.chat.Chat;
import com.cheering.chat.session.ChatSession;
import com.cheering.fan.CommunityType;
import com.cheering.meet.Meet;
import com.cheering.fan.Fan;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "chat_room_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityType communityType;

    @Column(nullable = false)
    private String name;

    @Column
    private String image;

    @Column
    private String description;

    @Column
    private Integer max;

    @Column(nullable = false)
    private Long communityId;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Fan manager;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<Chat> chats = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatSession> chatSessions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meet_id")
    private Meet meet;

    @Builder
    public ChatRoom(Long chatRoomId, String name, String image, String description, Long communityId,
                    Integer max, ChatRoomType type, CommunityType communityType, Fan manager, Meet meet) {
        this.id = chatRoomId;
        this.name = name;
        this.image = image;
        this.description = description;
        this.max = max;
        this.type = type;
        this.communityType = communityType;
        this.communityId = communityId;
        this.manager = manager;
        this.meet = meet;
    }

    public ChatRoom(Long chatRoomId) {
        this.communityId = chatRoomId;
    }
}
