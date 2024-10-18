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

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Builder
    public Message(String content, Chat chat) {
        this.content = content;
        this.chat = chat;
    }
}
