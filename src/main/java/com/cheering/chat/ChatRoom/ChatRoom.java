package com.cheering.chat.ChatRoom;

import com.cheering.player.Player;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Builder
    public ChatRoom(Long chatRoomId, String name, Player player) {
        this.id = chatRoomId;
        this.name = name;
        this.player = player;
    }
}
