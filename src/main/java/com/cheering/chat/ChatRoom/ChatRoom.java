package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import com.cheering.player.relation.PlayerUser;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @Column(nullable = false)
    private String name;

    @Column
    private String image;

    @Column
    private String description;

    @Column
    private Integer max;

    @Enumerated(EnumType.STRING)
    @Column
    private ChatRoomType type;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser creator;

    @Builder
    public ChatRoom(Long chatRoomId, String name, String image, String description, Player player, Integer max, ChatRoomType type, PlayerUser creator) {
        this.id = chatRoomId;
        this.name = name;
        this.image = image;
        this.description = description;
        this.player = player;
        this.max = max;
        this.type = type;
        this.creator = creator;
    }
}
