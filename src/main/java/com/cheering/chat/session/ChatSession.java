package com.cheering.chat.session;

import com.cheering.BaseTimeEntity;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.fan.Fan;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_session_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class ChatSession extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "chat_session_id")
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    @Column
    private LocalDateTime lastExitTime;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "fan_id")
    private Fan fan;

    @Column(nullable = false)
    private Boolean notificationsEnabled = true;

    @Builder
    public ChatSession(String sessionId, ChatRoom chatRoom, Fan fan, LocalDateTime lastExitTime) {
        this.sessionId = sessionId;
        this.chatRoom = chatRoom;
        this.fan = fan;
        this.lastExitTime = lastExitTime;
        this.notificationsEnabled = true;
    }
}
