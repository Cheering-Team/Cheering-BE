package com.cheering.chat.message;

import com.cheering.BaseTimeEntity;
import com.cheering.chat.Chat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message extends BaseTimeEntity{
    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    private String message;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Builder
    public Message(String message, Chat chat) {
        this.message = message;
        this.chat = chat;
    }
}
