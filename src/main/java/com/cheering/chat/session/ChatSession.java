package com.cheering.chat.session;

import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.fan.Fan;
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

    @Column(nullable = false)
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "fan_id")
    private Fan fan;

    @Builder
    public ChatSession(String sessionId, ChatRoom chatRoom, Fan fan) {
        this.sessionId = sessionId;
        this.chatRoom = chatRoom;
        this.fan = fan;
    }
}
