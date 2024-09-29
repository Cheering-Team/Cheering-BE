package com.cheering.chat.session;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.player.relation.PlayerUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_session_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class ChatSession {
    @Id
    @GeneratedValue
    @Column(name = "chat_session_id")
    private Long id;

    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "player_user_id")
    private PlayerUser playerUser;

    @Builder
    public ChatSession(String sessionId, ChatRoom chatRoom, PlayerUser playerUser) {
        this.sessionId = sessionId;
        this.chatRoom = chatRoom;
        this.playerUser = playerUser;
    }
}
