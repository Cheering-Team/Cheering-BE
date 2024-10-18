package com.cheering.chat.chatRoom;

import com.cheering.chat.Chat;
import com.cheering.chat.session.ChatSession;
import com.cheering.community.Community;
import com.cheering.community.relation.Fan;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "chat_room_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ChatRoom {
    @Id
    @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private ChatRoomType type;

    @Column(nullable = false)
    private String name;

    @Column
    private String image;

    @Column
    private String description;

    @Column
    private Integer max;

    @ManyToOne
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Fan manager;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<Chat> chats = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatSession> chatSessions = new ArrayList<>();

    @Builder
    public ChatRoom(Long chatRoomId, String name, String image, String description, Community community, Integer max, ChatRoomType type, Fan manager) {
        this.id = chatRoomId;
        this.name = name;
        this.image = image;
        this.description = description;
        this.community = community;
        this.max = max;
        this.type = type;
        this.manager = manager;
    }
}
