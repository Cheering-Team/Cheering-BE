package com.cheering.chat;

import com.cheering.BaseTimeEntity;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.fan.Fan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_tb", indexes = {
        @Index(name = "idx_room_group", columnList = "chat_room_id, group_key")
})
@NoArgsConstructor
@Getter
@Setter
public class Chat extends BaseTimeEntity{
    @Id
    @GeneratedValue
    @Column(name = "chat_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Fan writer;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String groupKey;

    @Builder
    public Chat(ChatType type, ChatRoom chatRoom, Fan writer, String content, String groupKey) {
        this.type = type;
        this.chatRoom = chatRoom;
        this.writer = writer;
        this.content = content;
        this.groupKey = groupKey;
    }
}
